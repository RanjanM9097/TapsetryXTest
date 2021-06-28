package com.l4s.transactionmanager.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.l4s.transactionmanager.dao.ALDCompleteOwnerShipLotRepo;
import com.l4s.transactionmanager.dao.CompleteOwnerShipLotRepo;
import com.l4s.transactionmanager.dao.ContraTransactionDel;
import com.l4s.transactionmanager.dao.ContraTransactionRec;
import com.l4s.transactionmanager.dao.KeyStore;
import com.l4s.transactionmanager.dao.NodeStore;
import com.l4s.transactionmanager.dao.PublicKeyStore;
import com.l4s.transactionmanager.dao.TempDb;
import com.l4s.transactionmanager.dao.TransactionNodeInfo;
import com.l4s.transactionmanager.dao.UtilityData;
import com.l4s.transactionmanager.dao.ValidationProcessRepo;
import com.l4s.transactionmanager.dto.ALDCompleteOwnerShipLots;
import com.l4s.transactionmanager.dto.CompleteOwnerShipLots;
import com.l4s.transactionmanager.dto.ContraTransactionDelDetails;
import com.l4s.transactionmanager.dto.ContraTransactionRecDetails;
import com.l4s.transactionmanager.dto.DigiSignature;
import com.l4s.transactionmanager.dto.EncryptedHashShareData;
import com.l4s.transactionmanager.dto.FLAddress;
import com.l4s.transactionmanager.dto.FLAddressErrorUpdateDto;
import com.l4s.transactionmanager.dto.FLInput;
import com.l4s.transactionmanager.dto.FLMAddress;
import com.l4s.transactionmanager.dto.FutureDatedTx;
import com.l4s.transactionmanager.dto.KeyEntities;
import com.l4s.transactionmanager.dto.LocalDateTimes;
import com.l4s.transactionmanager.dto.NodeDetails;
import com.l4s.transactionmanager.dto.PublicKeyEntities;
import com.l4s.transactionmanager.dto.ShareNodeInfoDto;
import com.l4s.transactionmanager.dto.TransactionDetails;
import com.l4s.transactionmanager.dto.TranxInterimData;
import com.l4s.transactionmanager.dto.UpdateFLStatus;
import com.l4s.transactionmanager.dto.ValidationProcessDetails;
import com.l4s.transactionmanager.process.IntraTransactionDelImpl;
import com.l4s.transactionmanager.process.ShortTransferImpl;
import com.l4s.transactionmanager.process.TransactionManagerImpl;
import com.l4s.transactionmanager.process.UrlBuilder;
import com.l4s.transactionmanager.security.DecryptMsg;
import com.l4s.transactionmanager.security.EncryptDecrypt;
import com.l4s.transactionmanager.security.GenerateKeys;
import com.l4s.transactionmanager.security.VerifySignature;

@RestController
public class GenerateK {
	@Autowired
	KeyStore keystore;
	@Autowired
	TransactionNodeInfo transactionNodeInfo;
	@Autowired
	UrlBuilder urlBuilder;
	@Autowired
	TempDb tmpdb;
	@Autowired
	UtilityData util;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	TransactionManagerImpl tranManagerImpl;
	@Autowired
	FLAddress flm;
	@Autowired
	DecryptMsg decryptMsg;
	@Autowired
	EncryptDecrypt edt;
	@Autowired
	GenerateKeys generateKeys;
	@Autowired
	PublicKeyStore publicKeyStore;
	@Autowired
	VerifySignature verifySignature;
	@Autowired
	IntraTransactionDelImpl intraTransactionDelImpl;
	@Autowired
	ShortTransferImpl shortTransferImpl;
	@Autowired
	NodeStore nodeStore;
	@Autowired
	TransactionOutbound tx_outbound;
	private static Logger log = LogManager.getLogger(GenerateK.class);
	@Autowired
	CompleteOwnerShipLotRepo objCOLRRepo;
	@Autowired
	ALDCompleteOwnerShipLotRepo objALDRepo;
	@Autowired
	ContraTransactionDel contraTxDelRepo;
	@Autowired
	ContraTransactionRec contraTxRecRepo;
	@Autowired
	EncryptedHashShareData encryptedHashObj;
	@Autowired
	DigiSignature digiSignObj;
	@Autowired
	CompleteOwnerShipLots aldCOLR;
	@Autowired
	CompleteOwnerShipLots alrCOLR;
	@Autowired
	CompleteOwnerShipLots alnCOLR;
	@Autowired
	ContraTransactionDelDetails contraTxDel;
	@Autowired
	ContraTransactionRecDetails contraTxRec;
	@Autowired
	LocalDateTimes localDateTime;
//	@Autowired
	Timestamp txValidateStartTime;
	@Autowired
	ValidationProcessDetails recValidationProcessTime;
	@Autowired
	ValidationProcessRepo recValidationProcRepo;
	@Autowired
	ErrorTransactions errorTransactionObj;
	@Autowired
	FLAddressErrorUpdateDto flErrorObj;
	@Autowired
	CarryForwardFutureTx forwardTxObj;
	int count = 1;

	public GenerateK() {
		log.info("User Controller Created");
	}

	@PostMapping
	@RequestMapping({ "genrateKey" })
	public String genrateKey(@RequestBody String customer) throws Exception {
		Optional<KeyEntities> keyentity = this.keystore.findById(customer);
		if (keyentity.isEmpty()) {
			KeyEntities keyEntities = this.generateKeys.keygen(customer);
			this.keystore.save(keyEntities);
		}
		return customer + " Is added Successfully";
	}

	@PostMapping
	@RequestMapping({ "zeroDown" })
	public FLAddress shortTransferTemp(@RequestBody TransactionDetails transdet) throws Exception {
		Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(transdet.getTransactionId());
		if (tranxInterim.isPresent() && transdet.getSubType().equalsIgnoreCase("Settlement")) {
			log.info("Zero Down TRANSACTION");
			FLAddress fl = new FLAddress();
			this.flm = this.util.getRecFL(tranxInterim, fl);
		}

		Optional<TranxInterimData> tranxInterimshort = this.tmpdb.findById(transdet.getTransactionId() + "i");
		if (tranxInterimshort.isEmpty()) {
			TranxInterimData tranxInterimData = this.util.updateRecTransactionDetails(transdet, new TranxInterimData());
			tranxInterimData = this.util.updateRECFLDetails(this.flm, tranxInterimData);
			this.tmpdb.save(tranxInterimData);
			return this.flm;
		} else {
			throw new Exception("Transaction id doesn't exists");
		}
	}

	@PostMapping
	@RequestMapping({ "getzeroDownALR/{id}" })
	public String createzeroDownALR(@RequestBody FLAddress fldetails, @PathVariable String id) throws Exception {
		Optional<TranxInterimData> flInterim = this.tmpdb.findById(id);
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(((TranxInterimData) flInterim.get()).getnREC());
		String ip = ((NodeDetails) nodeOptional.get()).getDnsname();
		// ip is rec
		Optional<NodeDetails> nodeOptionalDel = this.nodeStore.findById(((TranxInterimData) flInterim.get()).getnDEL());
		String ipDel = ((NodeDetails) nodeOptionalDel.get()).getDnsname();
		log.info(this.objectMapper.writeValueAsString(fldetails));
		String Alr = null;
		if (!flInterim.isEmpty()) {
			log.info("Receive DEL FL ");

			this.tranManagerImpl.flStatusUpdate(((TranxInterimData) flInterim.get()).getFDPeriodId(),
					((TranxInterimData) flInterim.get()).getnREC(),
					((TranxInterimData) flInterim.get()).getFlAddressREC(), "CONFIRMED",
					((TranxInterimData) flInterim.get()).getAssetLotId(),
					((TranxInterimData) flInterim.get()).getPeriod());
			// Updating Confirmed FLAddress to Passive Nodes
//			this.updateConfirmFLAddressPassiveNodes(flInterim.get(),
//					((TranxInterimData) flInterim.get()).getFlAddressREC(), "CONFIRMED", flInterim.get().getnREC());
			this.urlBuilder.getResponse("http://" + ipDel + ":8092/flam/updateStatusPassiveNode",
					this.objectMapper
							.writeValueAsString(new UpdateFLStatus(((TranxInterimData) flInterim.get()).getnREC(),
									((TranxInterimData) flInterim.get()).getFlAddressREC(), "CONFIRMED",
									((TranxInterimData) flInterim.get()).getFDPeriodId(),
									((TranxInterimData) flInterim.get()).getAssetLotId(),
									((TranxInterimData) flInterim.get()).getPeriod()))
							.getBytes());

			if (((TranxInterimData) flInterim.get()).getFDPeriodId() == null
					|| ((TranxInterimData) flInterim.get()).getFDPeriodId().equals("")) {
				this.tranManagerImpl.flStatusUpdate(((TranxInterimData) flInterim.get()).getFDPeriodId(),
						((TranxInterimData) flInterim.get()).getnREC(),
						((TranxInterimData) flInterim.get()).getFlAddressREC(), "LINKED",
						((TranxInterimData) flInterim.get()).getAssetLotId(),
						((TranxInterimData) flInterim.get()).getPeriod());
				String status = this.urlBuilder
						.getResponse("http://" + ipDel + ":8092/flam/shareInfo",
								this.objectMapper
										.writeValueAsString(
												new ShareNodeInfoDto(((TranxInterimData) flInterim.get()).getnREC(),
														((TranxInterimData) flInterim.get()).getFlAddressREC(),
														((TranxInterimData) flInterim.get()).getFlaHashLinkREC(),
														((TranxInterimData) flInterim.get()).getFDPeriodId(),
														((TranxInterimData) flInterim.get()).getAssetLotId()))
										.getBytes());
				log.info("Linked Status Updation with : " + status);
			}
			Alr = this.urlBuilder.getResponse("http://" + ip + ":8082/asset/zerodowndel", this.objectMapper
					.writeValueAsString(this.util.assetManagerALR(flInterim, new TransactionDetails())).getBytes());
			TranxInterimData tranxInterimData = (TranxInterimData) flInterim.get();
			this.util.updateClosedFutureTxDetails(tranxInterimData.getShortTransferId());
			tranxInterimData.setAlr(Alr);
			tranxInterimData = this.util.updateDELFLDetails(fldetails, tranxInterimData);
			this.tmpdb.save(tranxInterimData);
			
			if(Alr==null ) {
				log.info("Getting alr for zerodowndel api null from AM :"+Alr);
				//this.errorTransactionObj.createDelErrorContraTx(transactionDetails);
				throw new ApplicationException(
						"Dropping Transaction due to ALR   is null ");
				
			}
			log.info("ALR Created " +Alr);
			return Alr;
		} else {
			throw new Exception("Transaction id doesnt exists");
		}
	}

	@PostMapping
	@RequestMapping({ "temp/{flAddress}" })
	public FLAddress createTemp(@RequestBody TransactionDetails transdet, @PathVariable String flAddress)
			throws Exception {

		log.info("rec data from Lcomm rec cont side" + flAddress);

		Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(transdet.getTransactionId());
//		log.info(this.objectMapper.writeValueAsString(transdet));
		FLAddress flmrec= new FLAddress();
		if (tranxInterim.isEmpty()) {
			log.info("NEW TRANSACTION in Receival Node");
			FLInput flInput = new FLInput();
			FutureDatedTx futureTxObj = new FutureDatedTx();
//			if (!transdet.getFlId().equalsIgnoreCase("5432E")) {
			if (transdet.getSubType().equalsIgnoreCase("Reversal")) {
				futureTxObj = this.util.findPayRecID(transdet.getShortTransferId());
				if (futureTxObj.getFDPeriodId() != null && !futureTxObj.getFDPeriodId().isEmpty()) {
					String flRecAddress = this.util.findFLAddDEL(futureTxObj.getTxid());
//				flmDel.setFlAddress(flAddress);
					flmrec.setFlAddress(flRecAddress);

				} else {
					flmrec = this.getRecFLAdress(this.util.getFLInputRec(transdet, flInput, flAddress), transdet);
					this.tranManagerImpl.flStatusUpdate(transdet.getFDPeriodId(), transdet.getnREC(),
							flmrec.getFlAddress(), "UTILIZED", transdet.getAssetLotId(), transdet.getPeriod());
				}
			} else {
				flmrec = this.getRecFLAdress(this.util.getFLInputRec(transdet, flInput, flAddress), transdet);
//				if (transdet.getFDPeriodId() != null && !transdet.getFDPeriodId().isEmpty()) {
//
//					if (count == 1) {
//						this.tranManagerImpl.flStatusUpdate(transdet.getFDPeriodId(), transdet.getnDEL(),
//								this.flm.getFlAddress(), "CONFIRMED", transdet.getAssetLotId(), transdet.getPeriod());
//						count++;
//					} else {
//
//						this.tranManagerImpl.flStatusUpdate(transdet.getFDPeriodId(), transdet.getnREC(),
//								this.flm.getFlAddress(), "UTILIZED", transdet.getAssetLotId(), transdet.getPeriod());
//					}
//
//				} else {

					this.tranManagerImpl.flStatusUpdate(transdet.getFDPeriodId(), transdet.getnREC(),
							flmrec.getFlAddress(), "UTILIZED", transdet.getAssetLotId(), transdet.getPeriod());
				
			}
			if (flmrec.getFlAddress() == null && flmrec.getFlag().equalsIgnoreCase("Failed")) {
				return flmrec;
			}
//			this.tranManagerImpl.flStatusUpdate(transdet.getFDPeriodId(), transdet.getnREC(), this.flm.getFlAddress(),
//					"UTILIZED", transdet.getAssetLotId());
//			}

			TranxInterimData tranxInterimData = this.util.updateTransactionDetails(transdet, new TranxInterimData());
			tranxInterimData = this.util.updateRECFLDetails(flmrec, tranxInterimData);
//			flmrec
			log.info("Flm Rec address: "+flmrec.getFlAddress());
			this.tmpdb.save(tranxInterimData);
			log.info("Transaction Details of ID: " + transdet.getTransactionId() + " stored in DataBase");

			return flmrec;
		} else {
			throw new Exception("Transaction id already exists");
		}
	}

	@PostMapping
	@RequestMapping({ "getALR/{id}" })
	public String createALR(@RequestBody FLAddress fldetails, @PathVariable String id) throws Exception {
		Optional<TranxInterimData> flInterim = this.tmpdb.findById(id);
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(((TranxInterimData) flInterim.get()).getnREC());
		String ip = ((NodeDetails) nodeOptional.get()).getDnsname();
		Optional<NodeDetails> nodeOptionalDel = this.nodeStore.findById(((TranxInterimData) flInterim.get()).getnDEL());
		String ipDel = ((NodeDetails) nodeOptionalDel.get()).getDnsname();
		log.info(this.objectMapper.writeValueAsString(fldetails));
		String Alr = null;
		if (!flInterim.isEmpty()) {
			log.info("Receive DEL FL ");
			this.tranManagerImpl.flStatusUpdate(((TranxInterimData) flInterim.get()).getFDPeriodId(),
					((TranxInterimData) flInterim.get()).getnREC(),
					((TranxInterimData) flInterim.get()).getFlAddressREC(), "CONFIRMED",
					((TranxInterimData) flInterim.get()).getAssetLotId(),
					((TranxInterimData) flInterim.get()).getPeriod());
			// Updating Confirmed FLAddress to Passive Nodes
//			this.updateConfirmFLAddressPassiveNodes(flInterim.get(),
//					((TranxInterimData) flInterim.get()).getFlAddressREC(), "CONFIRMED", flInterim.get().getnREC());
			this.urlBuilder.getResponse("http://" + ipDel + ":8092/flam/updateStatusPassiveNode",
					this.objectMapper
							.writeValueAsString(new UpdateFLStatus(((TranxInterimData) flInterim.get()).getnREC(),
									((TranxInterimData) flInterim.get()).getFlAddressREC(), "CONFIRMED",
									((TranxInterimData) flInterim.get()).getFDPeriodId(),
									((TranxInterimData) flInterim.get()).getAssetLotId(),
									((TranxInterimData) flInterim.get()).getPeriod()))
							.getBytes());

			if (((TranxInterimData) flInterim.get()).getFDPeriodId() == null
					|| ((TranxInterimData) flInterim.get()).getFDPeriodId().equals("")) {
				this.tranManagerImpl.flStatusUpdate(((TranxInterimData) flInterim.get()).getFDPeriodId(),
						((TranxInterimData) flInterim.get()).getnREC(),
						((TranxInterimData) flInterim.get()).getFlAddressREC(), "LINKED",
						((TranxInterimData) flInterim.get()).getAssetLotId(),
						((TranxInterimData) flInterim.get()).getPeriod());
				String status = this.urlBuilder
						.getResponse("http://" + ipDel + ":8092/flam/shareInfo",
								this.objectMapper
										.writeValueAsString(
												new ShareNodeInfoDto(((TranxInterimData) flInterim.get()).getnREC(),
														((TranxInterimData) flInterim.get()).getFlAddressREC(),
														((TranxInterimData) flInterim.get()).getFlaHashLinkREC(),
														((TranxInterimData) flInterim.get()).getFDPeriodId(),
														((TranxInterimData) flInterim.get()).getAssetLotId()))
										.getBytes());
				log.info("Linked Status Updation with : " + status);
//				log.info("ALR Created ");
			}
//			log.info("ALR Request data: " + flInterim.toString());
			if (((TranxInterimData) flInterim.get()).getContraType().equalsIgnoreCase("Obligation")
					|| ((TranxInterimData) flInterim.get()).getContraType().equalsIgnoreCase("Loan")
					|| ((TranxInterimData) flInterim.get()).getContraType().equalsIgnoreCase("Collateral")) {

				this.util.updateFutureTxDetails(((TranxInterimData) flInterim.get()).getUseCase(), id,
						((TranxInterimData) flInterim.get()).getShortTransferId(),
						((TranxInterimData) flInterim.get()).getnDEL(), ((TranxInterimData) flInterim.get()).getnREC(),
						((TranxInterimData) flInterim.get()).getContingentId(),
						((TranxInterimData) flInterim.get()).getFDPeriodId());
			}

			// ip is rec
			TransactionDetails AlrData = this.urlBuilder.getTransaction("http://" + ip + ":8082/asset/assetlotALR",
					this.objectMapper.writeValueAsString(this.util.assetManagerALR(flInterim, new TransactionDetails()))
							.getBytes());
			if (AlrData != null && AlrData.getFlag() != null && AlrData.getFlag().equalsIgnoreCase("Failed")
					&& AlrData.getErrorMessage() != null) {
				String ErrorFlStatus = this.urlBuilder.getResponse("http://" + ip + ":8092/flam/updateErrorFLStatus",
						this.objectMapper.writeValueAsString(this.util.updatedErrorFL(flErrorObj,
								((TranxInterimData) flInterim.get()).getFlAddressREC())).getBytes());
				log.debug("Update status of Fl address in error Transaction: " + ErrorFlStatus);

				this.errorTransactionObj.storeRecErrorTransaction(AlrData.getErrorMessage(), AlrData);
				return AlrData.getAssetLotId();
			}
			if(AlrData.getAssetLotId()==null) {
				log.info("Getting alr for getALR api null from AM :"+Alr);
				//this.errorTransactionObj.createDelErrorContraTx(transactionDetails);
				throw new ApplicationException(
						"Dropping Transaction due to ALR   is null ");
			}
			TranxInterimData tranxInterimData = (TranxInterimData) flInterim.get();
			tranxInterimData.setAlr(AlrData.getAssetLotId());
			tranxInterimData.setAsset_name(AlrData.getAname());
			tranxInterimData = this.util.updateDELFLDetails(fldetails, tranxInterimData);
			this.tmpdb.save(tranxInterimData);
			return AlrData.getAssetLotId();
		} else {
			throw new Exception("Transaction id doesnt exists");
		}
	}

	@PostMapping
	@RequestMapping({ "shareALN/{id}" })
	public boolean shareALN(@RequestBody String aln, @PathVariable String id) throws Exception {
		Optional<TranxInterimData> flInterim = this.tmpdb.findById(id);
		boolean alnResponse = false;
		if (!flInterim.isEmpty()) {
			System.out.println("Receive ALN ");
			TranxInterimData tranxInterimData = (TranxInterimData) flInterim.get();
			tranxInterimData.setAln(aln);
			this.tmpdb.save(tranxInterimData);
			alnResponse = true;
			return alnResponse;
		} else {
			throw new Exception("Transaction id doesnt exists");
		}
	}

	@PostMapping
	@RequestMapping({ "getEncryptedHash/{id}" })
	public EncryptedHashShareData getEncryptedHash(@RequestBody EncryptedHashShareData encryptionData,
			@PathVariable String id) throws Exception {
		Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(id);
		Optional<KeyEntities> Reckeyentity = null;
		Reckeyentity = this.keystore.findById(((TranxInterimData) tranxInterim.get()).getTxREC());
//		Optional<PublicKeyEntities> keyentity = this.publicKeyStore
//				.findById(((TranxInterimData) tranxInterim.get()).getTxDEL());
		String RecEncrptedHash = null;
		if (!Reckeyentity.isEmpty()) {
			if (this.decryptMsg
					.decryptText(encryptionData.getEncrptedhash(),
							this.decryptMsg.getPublic(encryptionData.getPublicKey()))
					.equals(((TranxInterimData) tranxInterim.get()).getTranxHash())) {
				RecEncrptedHash = this.tranManagerImpl.encrypt(
						this.util.encrptyRec((TranxInterimData) tranxInterim.get()), id,
						((TranxInterimData) tranxInterim.get()).getTxREC());
				log.info("Received Delivery E# and verified Successfully");
				log.info("Sharing Rec E#");
				TranxInterimData tranxInterimData = (TranxInterimData) tranxInterim.get();
				tranxInterimData.setEncryptedHash(encryptionData.getEncrptedhash());
				tranxInterimData.setRecEncryptedHash(RecEncrptedHash);
				this.tmpdb.save(tranxInterimData);
				encryptedHashObj.setEncrptedhash(RecEncrptedHash);
				encryptedHashObj.setPublicKey(Reckeyentity.get().getPublikKey());
				return encryptedHashObj;
			} else {
				throw new Exception("E# is not valid");
			}
		} else {
			throw new Exception("KeyEntity doesnot Exists");
		}
	}

	@PostMapping
	@RequestMapping({ "shareTxHash/{id}" })
	public String getTxHash(@RequestBody String txHash, @PathVariable String id) {
		txValidateStartTime = localDateTime.getTimeStamp();
		Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(id);
		String txRecHash = this.edt.applySha256(this.util.encrptyRec((TranxInterimData) tranxInterim.get()));
		if (txRecHash != null && txHash != null) {
			TranxInterimData tranxInterimData = (TranxInterimData) tranxInterim.get();
			tranxInterimData.setTranxHash(txHash);
			tranxInterimData.setRecTranxHash(txRecHash);
			this.tmpdb.save(tranxInterimData);
		}

		log.info("Received tx_del# and sharing tx_rec#");
		return txRecHash;
	}

	@PostMapping
	@RequestMapping({ "shareDigisign/{id}" })
	public synchronized String getDigiSign(@RequestBody DigiSignature digisign, @PathVariable String id)
			throws Exception {
		String verifyStatus = null;
		int i = 0;
		int TIMEOUT = 5;
		Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(id);
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(((TranxInterimData) tranxInterim.get()).getnDEL());
		// String ip = ((NodeDetails) nodeOptional.get()).getDnsname();

		Optional<NodeDetails> nodeOptionalRec = this.nodeStore
				.findById(((TranxInterimData) tranxInterim.get()).getnREC());
		String ipRec = ((NodeDetails) nodeOptionalRec.get()).getDnsname();
		Optional<KeyEntities> Reckeyentity = this.keystore.findById(((TranxInterimData) tranxInterim.get()).getTxREC());
		byte[] RecDigisign = null;
		String status = "false";
		ContraTransactionRecDetails recContra_txRec = new ContraTransactionRecDetails();
		ContraTransactionDelDetails recContra_txDel = this.createDelContraTransactionDel(tranxInterim.get(),
				((TranxInterimData) tranxInterim.get()).getnDEL());
		if (!Reckeyentity.isEmpty() && !tranxInterim.isEmpty()) {
			if (this.verifySignature.verifySignature(this.util.encrptyDel((TranxInterimData) tranxInterim.get()),
					digisign.getDigiSign(), digisign.getPublicKey())) {
				RecDigisign = this.tranManagerImpl.digisign(this.util.encrptyRec((TranxInterimData) tranxInterim.get()),
						id, ((TranxInterimData) tranxInterim.get()).getTxREC());
				recContra_txRec = this.createRecContraTransactionRec(tranxInterim.get(),
						((TranxInterimData) tranxInterim.get()).getnREC());
				digiSignObj.setDigiSign(RecDigisign);
				digiSignObj.setPublicKey(Reckeyentity.get().getPublikKey());
				digiSignObj.setNodeRec(tranxInterim.get().getnREC());
				while (i < TIMEOUT) {
					verifyStatus = this.urlBuilder.getResponse(
							"http://" + "localhost" + ":7442/LComm/verifyDigisign/" + id,
							this.objectMapper.writeValueAsString(digiSignObj).getBytes());
					log.info("After rec verifyDigisign" + verifyStatus);

					if (verifyStatus.equalsIgnoreCase("true")) {
						Timestamp txValidateEndTime = localDateTime.getTimeStamp();
						this.ledgerProcTimeRec(((TranxInterimData) tranxInterim.get()).getTransactionId(),
								txValidateStartTime, txValidateEndTime);
						log.info("Receive DigiSign and verified Successfully");
						TranxInterimData tranxInterimData = (TranxInterimData) tranxInterim.get();
						tranxInterimData.setDegiSign(digisign.getDigiSign());
						tranxInterimData.setRecDegiSign(RecDigisign);
						this.tmpdb.save(tranxInterimData);
						status = "true";
						log.info("******************************************************");
						log.info("Transaction is completed successfully");
						log.info(((TranxInterimData) tranxInterim.get()).toString());
						log.info("******************************************************");
						recContra_txDel.setContramatchStatus(true);
						recContra_txRec.setContramatchStatus(true);
						this.contraTxDelRepo.save(recContra_txDel);
						this.contraTxRecRepo.save(recContra_txRec);
						log.info("Contra Transactions Created Successfully");
						log.info(recContra_txRec);
						String staus = this.urlBuilder.getResponse(
								"http://" + "localhost" + ":7442/LComm/TxMulticasting/contraTxRec",
								this.objectMapper.writeValueAsString(recContra_txRec).getBytes());
						log.info("MultiCast Status: " + staus);
						return status;
					} else if (verifyStatus.equalsIgnoreCase("false")) {
						recContra_txRec.setContramatchStatus(false);
						this.contraTxDelRepo.save(recContra_txDel);
						this.contraTxRecRepo.save(recContra_txRec);
						log.info("Receival DigiSign is not valid");
						this.util.storeALDCOLRData(tranxInterim.get());
						break;
					} else {
						TimeUnit.SECONDS.sleep(1);
						++i;
						log.info("waiting for response from other node");
						if (i == TIMEOUT) {
							throw new TimeoutException("Timed out after waiting for " + i + " seconds");
						}
					}
				}
//				String verifyStatus = this.urlBuilder.getResponse("http://" + ip + ":8080/verifyDigisign/" + id,
//						this.objectMapper.writeValueAsString(digiSignObj).getBytes());
//				if (verifyStatus.equalsIgnoreCase("true")) {
//					Timestamp txValidateEndTime = localDateTime.getTimeStamp();
//					this.ledgerProcTimeRec(((TranxInterimData) tranxInterim.get()).getTransactionId(),
//							txValidateStartTime, txValidateEndTime);
//					log.info("Receive DigiSign and verified Successfully");
//					TranxInterimData tranxInterimData = (TranxInterimData) tranxInterim.get();
//					tranxInterimData.setDegiSign(digisign.getDigiSign());
//					tranxInterimData.setRecDegiSign(RecDigisign);
//					this.tmpdb.save(tranxInterimData);
//					status = "true";
//					log.info("******************************************************");
//					log.info("Transaction is completed successfully");
//					log.info(((TranxInterimData) tranxInterim.get()).toString());
//					log.info("******************************************************");
//					recContra_txDel.setContramatchStatus(true);
//					recContra_txRec.setContramatchStatus(true);
//				} else {
//					recContra_txRec.setContramatchStatus(false);
//					log.info(" Receival DigiSign is not valid");
//					this.util.storeALDCOLRData(tranxInterim.get());
//				}
			} else {
				recContra_txDel.setContramatchStatus(false);
				log.info("Delivery DigiSign is not valid");
				this.util.storeALDCOLRData(tranxInterim.get());
			}
			// Storing ContraTransaction Data into Database
//			this.contraTxDelRepo.save(recContra_txDel);
//			this.contraTxRecRepo.save(recContra_txRec);
//			log.info("Contra Transactions Created Successfully");
//			log.info(recContra_txRec);
//			String staus = this.urlBuilder.getResponse(
//					"http://" + "localhost" + ":7442/LComm/TxMulticasting/contraTxRec",
//					this.objectMapper.writeValueAsString(recContra_txRec).getBytes());
//			log.info("MultiCast Status: " + staus);
			return status;
		} else {
			throw new Exception("KeyEntity doesnot Exists");
		}
	}

	@PostMapping
	@RequestMapping({ "verifyDigisign/{id}" })
	public String verifyDigiSign(@RequestBody DigiSignature digisign, @PathVariable String id) throws Exception {
		Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(id);
		Optional<PublicKeyEntities> keyentity = this.publicKeyStore
				.findById(((TranxInterimData) tranxInterim.get()).getTxREC());
		String status = "false";
		if (!keyentity.isEmpty()) {
			if (this.verifySignature.verifySignature(this.util.encrptyRec((TranxInterimData) tranxInterim.get()),
					digisign.getDigiSign(), digisign.getPublicKey()) && !tranxInterim.isEmpty()) {
				log.info("Verifyed DigiSign successfully");
				TranxInterimData tranxInterimData = (TranxInterimData) tranxInterim.get();
				tranxInterimData.setRecDegiSign(digisign.getDigiSign());
				this.tmpdb.save(tranxInterimData);
				status = "true";
			}

			return status;
		} else {
			throw new Exception("KeyEntity doesnot Exists");
		}
	}

	@PostMapping
	@RequestMapping({ "journal" })
	public void createJournalpairs(@RequestBody String id) throws Exception {
		Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(id);
//		if (((TranxInterimData) tranxInterim.get()).getContingentId() != null
//				&& ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Del/Rec")) {
//			this.tranManagerImpl.updateLedger(id, "ContingentJournalPairProc");
//		} else if (((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Del/Rec")
//				&& ((TranxInterimData) tranxInterim.get()).getContingentId() == null) {
//			this.tranManagerImpl.updateLedger(id, "JournalPairProc");
//		} else if (((TranxInterimData) tranxInterim.get()).getContingentId() != null
//				&& ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Short Transfer")) {
//			this.tranManagerImpl.updateLedger(id, "ContingentShortTransferJournalPairProc");
//		} else if (((TranxInterimData) tranxInterim.get()).getContingentId() == null
//				&& ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Short Transfer")) {
//			this.tranManagerImpl.updateLedger(id, "ShortTransferJournalPairProc");
//		} else if (((TranxInterimData) tranxInterim.get()).getContingentId() != null
//				&& ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Short Transfer Fill")) {
//			this.tranManagerImpl.updateLedger(id, "ContingentShortTransferFillJournalPairProc");
//		} else if (((TranxInterimData) tranxInterim.get()).getContingentId() == null
//				&& ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Short Transfer Fill")) {
//			this.tranManagerImpl.updateLedger(id, "ShortTransferFillJournalPairProc");
//		}
//		if (((TranxInterimData) tranxInterim.get()).getContingentId() == null) {
		if (((TranxInterimData) tranxInterim.get()).getContingentId() != null
				&& ((TranxInterimData) tranxInterim.get()).getnDEL()
						.equalsIgnoreCase(((TranxInterimData) tranxInterim.get()).getnREC())
				&& ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Transfer")) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getContingentId(),
					"ContingentIntraTransferJournalPairProc");
		} else if (((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("AirLockMarketOut")) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getTransactionId(),
					"MarketOutJournalPairProc");
		} else if (((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("AirLockMarket")) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getTransactionId(),
					"MarketJournalPairProc");
		} else if (((TranxInterimData) tranxInterim.get()).getnDEL()
				.equalsIgnoreCase(((TranxInterimData) tranxInterim.get()).getnREC())
				&& ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Transfer")) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getTransactionId(),
					"InternalTransferJournalPairProc");
		} else if (((TranxInterimData) tranxInterim.get()).getContingentId() != null
				&& ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Transfer")) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getContingentId(),
					"ContingentJournalPairProc");
		} else if (((TranxInterimData) tranxInterim.get()).getContingentId() != null
				&& ((TranxInterimData) tranxInterim.get()).getnDEL()
						.equalsIgnoreCase(((TranxInterimData) tranxInterim.get()).getnREC())
				&& (((TranxInterimData) tranxInterim.get()).getShortTransferId() != null
						&& (((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Obligation")
								|| ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Loan")
								|| ((TranxInterimData) tranxInterim.get()).getContraType()
										.equalsIgnoreCase("Collateral")))) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getContingentId(),
					"IntraShortContingentTransferJournalPairProc");
		} else if (((TranxInterimData) tranxInterim.get()).getContingentId() != null
				&& ((TranxInterimData) tranxInterim.get()).getShortTransferId() != null
				&& ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Obligation")
				|| ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Loan")
				|| ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Collateral")) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getContingentId(),
					"ContingentShortTransferJournalPairProc");
		} else if (((TranxInterimData) tranxInterim.get()).getnDEL()
				.equalsIgnoreCase(((TranxInterimData) tranxInterim.get()).getnREC())
				&& ((TranxInterimData) tranxInterim.get()).getShortTransferId() != null
				&& ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Obligation")
				|| ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Loan")
				|| ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Collateral")) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getTransactionId(),
					"IntraShortTransferJournalPairProc");
		} else if (((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Obligation")
				|| ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Loan")
				|| ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Collateral")
						&& ((TranxInterimData) tranxInterim.get()).getShortTransferId() != null) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getTransactionId(),
					"ShortTransferJournalPairProc");
		} else if (((TranxInterimData) tranxInterim.get()).getContingentId() != null
				&& ((TranxInterimData) tranxInterim.get()).getShortTransferId() != null
				&& ((TranxInterimData) tranxInterim.get()).getnDEL()
						.equalsIgnoreCase(((TranxInterimData) tranxInterim.get()).getnREC())
				&& ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Settlement")) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getContingentId(),
					"IntraShortContingentTransferFillJournalPairProc");
		} else if (((TranxInterimData) tranxInterim.get()).getnDEL()
				.equalsIgnoreCase(((TranxInterimData) tranxInterim.get()).getnREC())
				&& ((TranxInterimData) tranxInterim.get()).getShortTransferId() != null
				&& ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Settlement")) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getTransactionId(),
					"IntraShortTransferFillJournalPairProc");
		} else if (((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Settlement")
				&& ((TranxInterimData) tranxInterim.get()).getShortTransferId() != null
				&& ((TranxInterimData) tranxInterim.get()).getContingentId() != null) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getContingentId(),
					"ContingentShortTransferFillJournalPairProc");
		} else if (((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Settlement")
				&& ((TranxInterimData) tranxInterim.get()).getShortTransferId() != null) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getTransactionId(),
					"ShortTransferFillJournalPairProc");
		} else if (((TranxInterimData) tranxInterim.get()).getContingentId() != null
				&& ((TranxInterimData) tranxInterim.get()).getShortTransferId() != null
				&& ((TranxInterimData) tranxInterim.get()).getnDEL()
						.equalsIgnoreCase(((TranxInterimData) tranxInterim.get()).getnREC())
				&& ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Reversal")) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getTransactionId(),
					"IntraContingentPayRecReversalJournalPairProc");
		} else if (((TranxInterimData) tranxInterim.get()).getnDEL()
				.equalsIgnoreCase(((TranxInterimData) tranxInterim.get()).getnREC())
				&& ((TranxInterimData) tranxInterim.get()).getShortTransferId() != null
				&& ((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Reversal")) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getTransactionId(),
					"IntraPayRecReversalJournalPairProc");
		} else if (((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Reversal")
				&& ((TranxInterimData) tranxInterim.get()).getShortTransferId() != null
				&& ((TranxInterimData) tranxInterim.get()).getContingentId() != null) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getContingentId(),
					"ContingentPayRecReversalJournalPairProc");
		} else if (((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Reversal")
				&& ((TranxInterimData) tranxInterim.get()).getShortTransferId() != null) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getTransactionId(),
					"PayRecReversalJournalPairProc");
		} else if (((TranxInterimData) tranxInterim.get()).getContraType().equalsIgnoreCase("Transfer")) {
			this.tranManagerImpl.updateLedger(((TranxInterimData) tranxInterim.get()).getTransactionId(),
					"JournalPairProc");
		}
		// }

		log.info("Journal Pairs are completed successfully");

//		String staus = this.urlBuilder
//				.getResponse("http://" + tranxInterim.get().getnDEL() + ":7442/TrnsactionMulticasting/",
//						this.objectMapper
//								.writeValueAsString(this.util.assetManagerALR(tranxInterim, new TransactionDetails()))
//								.getBytes());
//		log.info("MultiCast Status: " + staus);
	}

	@PostMapping
	@RequestMapping({ "updateCLOR" })
	public void storeCOLRData(@RequestBody TranxInterimData intrim) {
		try {
			Date date = new Date();
			Optional<CompleteOwnerShipLots> aldCOLROptional = this.objCOLRRepo.findById(intrim.getAssetLotId());
			if (aldCOLROptional.isEmpty()) {
				aldCOLR.setColrid(intrim.getAssetLotId());
				aldCOLR.setNodeid(intrim.getnDEL());
				aldCOLR.setPeriodId(intrim.getPeriod());
				aldCOLR.setDelTxID(intrim.getContraIdDel());
				aldCOLR.setTxDEL(intrim.getTxDEL());
				aldCOLR.setTxREC(intrim.getTxREC());
				aldCOLR.setRecTxID(intrim.getContraIdRec());
				aldCOLR.setAssetid(intrim.getAtype());
				aldCOLR.setAsset_name(intrim.getAsset_name());
				aldCOLR.setCreated_Quantity(intrim.getQtyALD());
				aldCOLR.setnDelID(intrim.getnDEL());
				aldCOLR.setnDelFLID(intrim.getFlIdDEL());
				aldCOLR.setnDelFLAddress(intrim.getFlAddressDEL());
				aldCOLR.setnRecID(intrim.getnREC());
				aldCOLR.setnRecFLID(intrim.getFlIdREC());
				aldCOLR.setNRecFLAddress(intrim.getFlAddressREC());
				aldCOLR.settPRecALRID(intrim.getAlr());
				aldCOLR.setTxDelhash(intrim.getTranxHash());
				aldCOLR.setTx_Rechash(intrim.getRecTranxHash());
				if ((!intrim.getContraType().equalsIgnoreCase("Obligation")
						&& !intrim.getContraType().equalsIgnoreCase("Loan")
						&& !intrim.getContraType().equalsIgnoreCase("Collateral"))
						&& !intrim.getContraType().equalsIgnoreCase("Reversal")) {
					aldCOLR.setAl('L');
				} else {
					aldCOLR.setAl('P');
				}

				aldCOLR.setFDPeriodId(intrim.getFDPeriodId());
				aldCOLR.setLinkedAl(' ');
				aldCOLR.setAirlocknode(intrim.getAirlocknode());
				aldCOLR.setTransactedDate(intrim.getTimeStamp());
				aldCOLR.setPostedDate(new java.sql.Timestamp(date.getTime()));
				this.objCOLRRepo.save(aldCOLR);
				log.info("ALD COLR Updated Successfully");
			} else {
				log.debug("ALD :" + intrim.getAssetLotId() + " already existed in Datbase");
			}

			this.util.storeALDCOLRData(intrim);

			Optional<CompleteOwnerShipLots> alrCOLROptional = this.objCOLRRepo.findById(intrim.getAlr());
			if (alrCOLROptional.isEmpty()) {
				alrCOLR.setColrid(intrim.getAlr());
				alrCOLR.setNodeid(intrim.getnREC());
				alrCOLR.setPeriodId(intrim.getPeriod());
				alrCOLR.setTxDEL(intrim.getTxDEL());
				alrCOLR.setTxREC(intrim.getTxREC());
				alrCOLR.setDelTxID(intrim.getContraIdDel());
				alrCOLR.setRecTxID(intrim.getContraIdRec());
				alrCOLR.setAssetid(intrim.getAtype());
				alrCOLR.setAsset_name(intrim.getAsset_name());
				alrCOLR.setCreated_Quantity(intrim.getQtyALR());
				alrCOLR.setnDelID(intrim.getnDEL());
				alrCOLR.setnDelFLID(intrim.getFlIdDEL());
				alrCOLR.setnDelFLAddress(intrim.getFlAddressDEL());
				alrCOLR.setnRecID(intrim.getnREC());
				alrCOLR.setnRecFLID(intrim.getFlIdREC());
				alrCOLR.setNRecFLAddress(intrim.getFlAddressREC());
				alrCOLR.settPRecALRID(intrim.getAssetLotId());
				alrCOLR.setTxDelhash(intrim.getTranxHash());
				alrCOLR.setTx_Rechash(intrim.getRecTranxHash());
				alrCOLR.setAirlocknode(intrim.getAirlocknode());

				if ((!intrim.getContraType().equalsIgnoreCase("Obligation")
						&& !intrim.getContraType().equalsIgnoreCase("Loan")
						&& !intrim.getContraType().equalsIgnoreCase("Collateral"))
						&& !intrim.getContraType().equalsIgnoreCase("Reversal")) {
					alrCOLR.setAl('L');
				} else {
					alrCOLR.setAl('R');
				}
				alrCOLR.setFDPeriodId(intrim.getFDPeriodId());
				alrCOLR.setLinkedAl(' ');
				alrCOLR.setTransactedDate(intrim.getTimeStamp());
				alrCOLR.setPostedDate(new java.sql.Timestamp(date.getTime()));
				this.objCOLRRepo.save(alrCOLR);
				log.info("ALR COLR Updated Successfully");
			} else {
				log.debug("ALR :" + intrim.getAlr() + " already existed in Datbase");
			}
			if (!intrim.getContraType().equalsIgnoreCase("Obligation")
					&& !intrim.getContraType().equalsIgnoreCase("Loan")
					&& !intrim.getContraType().equalsIgnoreCase("Collateral")) {
				objCOLRRepo.updateALN(intrim.getAssetLotId());
			}
			if (intrim.getQtyALD() > intrim.getQtyALR() && (!intrim.getContraType().equalsIgnoreCase("Obligation")
					&& !intrim.getContraType().equalsIgnoreCase("Loan")
					&& !intrim.getContraType().equalsIgnoreCase("Collateral")
					&& !intrim.getContraType().equalsIgnoreCase("Reversal"))) {

				Optional<CompleteOwnerShipLots> alnCOLROptional = this.objCOLRRepo.findById(intrim.getAln());
				if (alnCOLROptional.isEmpty()) {
					alnCOLR.setColrid(intrim.getAln());
					alnCOLR.setNodeid(intrim.getnDEL());
					alnCOLR.setPeriodId(intrim.getPeriod());
					alnCOLR.setTxDEL(intrim.getTxDEL());
					alnCOLR.setTxREC(intrim.getTxREC());
					alnCOLR.setAssetid(intrim.getAtype());
					alnCOLR.setAsset_name(intrim.getAsset_name());
					alnCOLR.setDelTxID(intrim.getContraIdDel());
					alnCOLR.setRecTxID(intrim.getContraIdRec());
					alnCOLR.setCreated_Quantity(intrim.getQtyALD() - intrim.getQtyALR());
					alnCOLR.setnDelID(intrim.getnDEL());
					alnCOLR.setnDelFLID(intrim.getFlIdDEL());
					alnCOLR.setnDelFLAddress(intrim.getFlAddressDEL());
					alnCOLR.setnRecID(intrim.getnREC());
					alnCOLR.setnRecFLID(intrim.getFlIdREC());
					alnCOLR.setNRecFLAddress(intrim.getFlAddressREC());
					alnCOLR.settPRecALRID(intrim.getAssetLotId());
					alnCOLR.setTxDelhash(intrim.getTranxHash());
					alnCOLR.setTx_Rechash(intrim.getRecTranxHash());
					alnCOLR.setAirlocknode(intrim.getAirlocknode());
					alnCOLR.setAl('L');
					alnCOLR.setFDPeriodId(intrim.getFDPeriodId());
					alnCOLR.setLinkedAl(' ');
					alnCOLR.setTransactedDate(intrim.getTimeStamp());
					alnCOLR.setPostedDate(new java.sql.Timestamp(date.getTime()));
					this.objCOLRRepo.save(alnCOLR);
					log.info("ALN COLR Updated Successfully");
				} else {
					log.debug("ALR :" + intrim.getAlr() + " already existed in Datbase");
				}

			}
			log.info("ALN  COLR is not required due to ALD  and ALR are equal");
		} catch (Exception exception) {
			log.info(exception + " ecxception occurred while inserting data COLR");
		}

	}

	@PostMapping
	@RequestMapping({ "initiate" })
	public String processtrans(@RequestBody TransactionDetails transdet) throws Exception {
		log.info("Initiated Transaction");
		this.tranManagerImpl.processTransaction(transdet);
		return "Success";
	}

	@PostMapping
	@RequestMapping({ "intra" })
	public String processtransIntra(@RequestBody TransactionDetails transdet) throws Exception {
		log.info("Initiated Intra Transaction");
		this.intraTransactionDelImpl.processTransaction(transdet);
		return "Success";
	}

	@PostMapping
	@RequestMapping({ "stzerodown" })
	public String processtranszerodown(@RequestBody TransactionDetails transdet) throws Exception {
		log.info("Initiated Zero Down Transaction");
		this.shortTransferImpl.processTransaction(this.util.nodeSwitch(transdet));
		return "Success";
	}

	@PostMapping
	@RequestMapping({ "test" })
	public String test() throws Exception {
		log.info("Test Transaction");
		String s = "1234567i";
		if (!s.endsWith("i")) {
			return "Success";
		}
//		String s1=
		else {
//		
			return "Failure";
		}
	}

	@PostMapping
	@RequestMapping({ "futuretest/{payrecid}" })
	public String test1(@PathVariable String payrecid) throws Exception {
		log.info("Future Test Transaction");
		this.util.updateClosedFutureTxDetails(payrecid);
		return "Success";
	}

	public ContraTransactionRecDetails createRecContraTransactionRec(TranxInterimData intrim, String nodeType) {
		try {
			String contrID = intrim.getTxDEL() + intrim.getTransactionId() + intrim.getTxREC();
//			contraTxRec.setContraid(contrID);
			contraTxRec.setContraid(intrim.getTransactionId());
			contraTxRec.setTx_id(intrim.getTransactionId());
			contraTxRec.setPostperiodfk(intrim.getPeriod());
			contraTxRec.setAssetid(intrim.getAtype());
			contraTxRec.setAsset_name(intrim.getAsset_name());
			contraTxRec.setAssetLotId(intrim.getAssetLotId());
			contraTxRec.setTxDel(intrim.getTxDEL());
			contraTxRec.setTxREC(intrim.getTxREC());
			contraTxRec.setTranqty(intrim.getQtyALD());
			contraTxRec.setQtyALR(intrim.getQtyALR());
			contraTxRec.setAssetLotReceiverId(intrim.getAlr());
			contraTxRec.setAssetLotNetId(intrim.getAln());
			contraTxRec.setTrandt(intrim.getTimeStamp());
			contraTxRec.setPostdt(localDateTime.getTimeStamp());
			contraTxRec.setnDel(intrim.getnDEL());
			contraTxRec.setnREC(intrim.getnREC());
			contraTxRec.setExtTxnId(intrim.getContraIdRec());
			contraTxRec.setAirlocknode(intrim.getAirlocknode());
//			if (nodeType.equalsIgnoreCase(intrim.getnDEL())) {
//				contraTxDel.setNodeid(nodeType);
//				contraTxDel.setFladdrid(intrim.getFlAddressDEL());
//				contraTxDel.setFlid(intrim.getFlIdDEL());
//				contraTxDel.setFladdridparentfk(intrim.getFlaHashLinkDEL());
//				contraTxDel.setEncryptedhash(intrim.getEncryptedHash());
//				contraTxDel.setTrhash(intrim.getTranxHash());
//				contraTxDel.setDigitalsig(intrim.getDegiSign());
//			} else {
			contraTxRec.setNodeid(nodeType);
			contraTxRec.setFladdrid(intrim.getFlAddressREC());
			contraTxRec.setFlid(intrim.getFlIdREC());
			contraTxRec.setFladdridparentfk(intrim.getFlaHashLinkREC());
			contraTxRec.setEncryptedhash(intrim.getRecEncryptedHash());
			contraTxRec.setTrhash(intrim.getRecTranxHash());
			contraTxRec.setDigitalsig(intrim.getRecDegiSign());
			contraTxRec.setTxType(intrim.getTxType());
			contraTxRec.setUseCase(intrim.getUseCase());
			contraTxRec.setFDPeriodId(intrim.getFDPeriodId());
//			}
			if (intrim.getContraType() != null) {
				contraTxRec.setContraType(intrim.getContraType());
			} else {
				contraTxRec.setContraType(null);
			}

			if (intrim.getContingentId() != null) {
				contraTxRec.setContingentId(intrim.getContingentId());
			} else {
				contraTxRec.setContingentId(null);
			}

			if (intrim.getShortTransferId() != null) {
				contraTxRec.setShortTransferId(intrim.getShortTransferId());

			} else {
				contraTxRec.setShortTransferId(null);
			}
		} catch (Exception exception) {
			log.info(exception + " ecxception occurred while creating Contra Transaction");
		}
		return contraTxRec;
	}

	public ContraTransactionDelDetails createDelContraTransactionDel(TranxInterimData intrim, String nodeType) {
		try {
			String contrID = intrim.getTxDEL() + intrim.getTransactionId() + intrim.getTxREC();
			;
//			contraTxDel.setContraid(contrID);
			contraTxDel.setContraid(intrim.getTransactionId());
			contraTxDel.setTx_id(intrim.getTransactionId());
			contraTxDel.setPostperiodfk(intrim.getPeriod());
			contraTxDel.setAssetid(intrim.getAtype());
			contraTxDel.setAsset_name(intrim.getAsset_name());
			contraTxDel.setAssetLotId(intrim.getAssetLotId());
			contraTxDel.setTxDel(intrim.getTxDEL());
			contraTxDel.setTxREC(intrim.getTxREC());
			contraTxDel.setTranqty(intrim.getQtyALD());
			contraTxDel.setQtyALR(intrim.getQtyALR());
			contraTxDel.setAssetLotReceiverId(intrim.getAlr());
			contraTxDel.setAssetLotNetId(intrim.getAln());
			contraTxDel.setTrandt(intrim.getTimeStamp());
			contraTxDel.setPostdt(localDateTime.getTimeStamp());
			contraTxDel.setExtTxnId(intrim.getContraIdDel());
//			if (nodeType.equalsIgnoreCase(intrim.getnDEL())) {
			contraTxDel.setNodeid(nodeType);
			contraTxDel.setFladdrid(intrim.getFlAddressDEL());
			contraTxDel.setFlid(intrim.getFlIdDEL());
			contraTxDel.setFladdridparentfk(intrim.getFlaHashLinkDEL());
			contraTxDel.setEncryptedhash(intrim.getEncryptedHash());
			contraTxDel.setTrhash(intrim.getTranxHash());
			contraTxDel.setDigitalsig(intrim.getDegiSign());
			contraTxDel.setnDel(intrim.getnDEL());
			contraTxDel.setnREC(intrim.getnREC());
			contraTxDel.setTxType(intrim.getTxType());
			contraTxDel.setUseCase(intrim.getUseCase());
			contraTxDel.setAirlocknode(intrim.getAirlocknode());
			contraTxDel.setFDPeriodId(intrim.getFDPeriodId());
//			} else {
//				contraTxDel.setNodeid(nodeType);
//				contraTxDel.setFladdrid(intrim.getFlAddressREC());
//				contraTxDel.setFlid(intrim.getFlIdREC());
//				contraTxDel.setFladdridparentfk(intrim.getFlaHashLinkREC());
//				contraTxDel.setEncryptedhash(intrim.getRecEncryptedHash());
//				contraTxDel.setTrhash(intrim.getRecTranxHash());
//				contraTxDel.setDigitalsig(intrim.getRecDegiSign());
//			}
			if (intrim.getContraType() != null) {
				contraTxDel.setContraType(intrim.getContraType());
			} else {
				contraTxDel.setContraType(null);
			}

			if (intrim.getContingentId() != null) {
				contraTxDel.setContingentId(intrim.getContingentId());
			} else {
				contraTxDel.setContingentId(null);
			}

			if (intrim.getShortTransferId() != null) {
				contraTxDel.setShortTransferId(intrim.getShortTransferId());

			} else {
				contraTxDel.setShortTransferId(null);
			}
		} catch (Exception exception) {
			log.info(exception + " ecxception occurred while creating Contra Transaction");
		}
		return contraTxDel;
	}

	public void ledgerProcTimeRec(String tx_ID, Timestamp start, Timestamp end) {
		try {
//			long diffInmillSeconds = end.getTime() - start.getTime();
//			log.info("Processing Time for transaction " + tx_ID + "in secs: " + diffInmillSeconds);
			Optional<ValidationProcessDetails> validationProcessInfo = this.recValidationProcRepo.findById(tx_ID);
			if (validationProcessInfo.isEmpty()) {
				log.info("Storing new Transaction Prcessing Data into Database");
				recValidationProcessTime.setTransactionId(tx_ID);
				recValidationProcessTime.setStartTime(start);
				recValidationProcessTime.setEndTime(end);
				recValidationProcessTime.setContra_type("InBound");
				recValidationProcessTime.setPeriod_id(this.transactionNodeInfo.getPeriod());
//				ledgerProcessTIme.setTotalProcessTime(diffInmillSeconds);
				this.recValidationProcRepo.save(recValidationProcessTime);
			} else {
				log.debug(tx_ID + " already existed in Ledger Process DB");
			}
		} catch (Exception e) {
			log.error("Error while storing Validation pocessing time " + e);
		}
	}

	public FLAddress getRecFLAdress(FLInput flInput, TransactionDetails transdet) {
		FLAddress fl = new FLAddress();
		FLMAddress flm = new FLMAddress();
		try {
			Optional<NodeDetails> nodeOptional = this.nodeStore.findById(flInput.nodeId);
			log.info("Calling FL Address URI");
			flm = this.urlBuilder.getFLUpdated(
					"http://" + ((NodeDetails) nodeOptional.get()).getDnsname() + ":8092/flam/requestFlAddr",
					this.objectMapper.writeValueAsString(flInput).getBytes());
			log.trace("Requesting URI :" + "http://" + ((NodeDetails) nodeOptional.get()).getDnsname()
					+ ":8092/flam/requestFlAddr" + "for FL Address with details : "
					+ this.objectMapper.writeValueAsString(flInput));
			if (flm != null && flm.getFlAddress() == null && flm.getErrorMsg() != null
					&& flm.getFlag().equalsIgnoreCase("Failed")) {
				this.errorTransactionObj.storeRecErrorTransaction(flm.getErrorMsg(), transdet);
				log.error("invalid transaction for FlAddress" + flInput.getTxnId());
//			throw new ApplicationException("Receival FL address is null");
			}
			log.info("FLM Address Created--{}", this.objectMapper.writeValueAsString(flm));
		} catch (Exception e) {
			log.error(e + "Exception");
		}
		return this.util.updatedFL(fl, flm);
	}

	public void updateConfirmFLAddressPassiveNodes(TranxInterimData tx_details, String FLAddress, String status,
			String Node) {
		try {
			List<NodeDetails> nodeDetails = this.nodeStore.findAll();
			List<Optional<NodeDetails>> convertedNodeList = nodeDetails.stream()
					.map((nodedata) -> Optional.of(nodedata)).collect(Collectors.toList());
			if (!convertedNodeList.isEmpty()) {
				convertedNodeList.stream().parallel().forEachOrdered((nodeinfo) -> {
					if (!nodeinfo.get().getNodeid().equalsIgnoreCase(Node)) {
						String ip = nodeinfo.get().getDnsname();
						String nodeid = nodeinfo.get().getNodeid();
						try {
							this.urlBuilder
									.getResponse("http://" + ip + ":8092/flam/updateStatus",
											this.objectMapper.writeValueAsString(new UpdateFLStatus(Node, FLAddress,
													status, tx_details.getFDPeriodId(), tx_details.getAssetLotId(),
													tx_details.getPeriod())).getBytes());
						} catch (JsonProcessingException exception) {
							log.error(exception + "exception occurred in sending fladdress details to status");
						}
						log.info("FL Detials sent to FLM to change status of FL Address for Passive Node " + nodeid);
					}
				});
			} else {
				log.debug("Node info is empty");
			}
		} catch (Exception exception) {
			log.error(exception + " exception occurred while requesting FL Adddress Updation for Passive Nodes");
		}
	}

	@PostMapping
	@RequestMapping({ "updatefuturetx/{periodid}" })
	public String updateFutureTx(@PathVariable String periodid) throws Exception {
		log.info("Future Test Transaction");
		this.transactionNodeInfo.setPeriod(periodid);
		this.forwardTxObj.carryForwardTx(periodid);
		return "Success";
	}

	@PostMapping
	@RequestMapping("futureJouranalPair")
	public void createjournals(@RequestBody FutureDatedTx transactionDetails) {
		try {
//			if (transactionDetails.getContingentId() != null
//					&& transactionDetails.getnDel().equalsIgnoreCase(transactionDetails.getnRec())
//					&& (transactionDetails.getPayrecid() != null)) {
//				this.updateLedger(transactionDetails.getTxid(), "IntraShortContingentTransferJournalPairProc");
//			} else if (transactionDetails.getContingentId() != null && transactionDetails.getPayrecid() != null) {
//				this.updateLedger(transactionDetails.getContingentId(), "ContingentShortTransferJournalPairProc");
//			} else 
			if (transactionDetails.getnDel().equalsIgnoreCase(transactionDetails.getnRec())
					&& transactionDetails.getPayrecid() != null) {
				this.tranManagerImpl.updateLedger(transactionDetails.getTxid(), "IntraShortTransferJournalPairProc");
			} else if (transactionDetails.getPayrecid() != null) {
				this.tranManagerImpl.updateLedger(transactionDetails.getTxid(), "ShortTransferJournalPairProc");
			}
			log.info("Future Transaction journals created successfully");
		} catch (Exception e) {
			log.error(e + "occurred while creating journal entries for Future Tx");
		}
	}
}
