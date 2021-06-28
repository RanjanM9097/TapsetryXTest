package com.l4s.transactionmanager.process;

import java.sql.Timestamp;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.l4s.transactionmanager.dao.ContraTransactionRec;
import com.l4s.transactionmanager.dao.KeyStore;
import com.l4s.transactionmanager.dao.NodeStore;
import com.l4s.transactionmanager.dao.PublicKeyStore;
import com.l4s.transactionmanager.dao.TempDb;
import com.l4s.transactionmanager.dao.UtilityData;
import com.l4s.transactionmanager.dao.ValidationProcessRepo;
import com.l4s.transactionmanager.dto.ContraTransactionRecDetails;
import com.l4s.transactionmanager.dto.FLAddress;
import com.l4s.transactionmanager.dto.FLAddressErrorUpdateDto;
import com.l4s.transactionmanager.dto.FLInput;
import com.l4s.transactionmanager.dto.FutureDatedTx;
import com.l4s.transactionmanager.dto.KeyEntities;
import com.l4s.transactionmanager.dto.LocalDateTimes;
import com.l4s.transactionmanager.dto.NodeDetails;
import com.l4s.transactionmanager.dto.ShareNodeInfoDto;
import com.l4s.transactionmanager.dto.TransactionDetails;
import com.l4s.transactionmanager.dto.TranxInterimData;
import com.l4s.transactionmanager.dto.UpdateFLStatus;
import com.l4s.transactionmanager.dto.ValidationProcessDetails;
import com.l4s.transactionmanager.security.DecryptMsg;
import com.l4s.transactionmanager.security.EncryptDecrypt;
import com.l4s.transactionmanager.security.GenerateKeys;
import com.l4s.transactionmanager.security.VerifySignature;
import com.l4s.transactionmanager.service.ApplicationException;
import com.l4s.transactionmanager.service.ErrorTransactions;
import com.l4s.transactionmanager.service.GenerateK;
import com.l4s.transactionmanager.service.TransactionOutbound;

@Component
public class IntraTransactionRecImpl {
	@Autowired
	KeyStore keystore;
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
	GenerateK generateKeyObj;
	@Autowired
	PublicKeyStore publicKeyStore;
	@Autowired
	VerifySignature verifySignature;
	@Autowired
	NodeStore nodeStore;
	private static Logger log = LogManager.getLogger(IntraTransactionRecImpl.class);
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	ContraTransactionRec contraTxRecRepo;
//	public int count = 1;
	@Autowired
	ValidationProcessDetails validationProcessTime;
	@Autowired
	ValidationProcessRepo validationProcRepo;
	Timestamp txValidateStartTime;
	@Autowired
	LocalDateTimes localDateTime;
	@Autowired
	ErrorTransactions errorTransactionObj;
	@Autowired
	TransactionOutbound tx_outbound;
	@Autowired
	FLAddressErrorUpdateDto flErrorObj;

	public IntraTransactionRecImpl() {
		log.info("User Controller Created");
	}

	public FLAddress shortTransferTemp(TransactionDetails transdet) throws Exception {
		Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(transdet.getTransactionId());
		if (tranxInterim.isPresent() && transdet.getSubType().equalsIgnoreCase("Settlement")) {
			log.info("Zero Down TRANSACTION");
			FLAddress fl = new FLAddress();
			this.flm = this.util.getRecFL(tranxInterim, fl);
		}

		Optional<TranxInterimData> tranxInterimshort = this.tmpdb.findById(transdet.getTransactionId() + "k");
		if (tranxInterimshort.isEmpty()) {
			TranxInterimData tranxInterimData = this.util.updateShortTransactionDetails(transdet,
					new TranxInterimData(), "k");
			tranxInterimData = this.util.updateRECFLDetails(this.flm, tranxInterimData);
			this.tmpdb.save(tranxInterimData);
			return this.flm;
		} else {
			throw new Exception("Transaction id doesn't exists");
		}
	}

	public String createzeroDownALR(FLAddress fldetails, String id) throws Exception {
		Optional<TranxInterimData> flInterim = this.tmpdb.findById(id);
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(((TranxInterimData) flInterim.get()).getnREC());
		String ip = ((NodeDetails) nodeOptional.get()).getDnsname();
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
//			this.urlBuilder.getResponse("http://" + ipDel + ":8092/flam/updateStatusPassiveNode",
//					this.objectMapper
//							.writeValueAsString(new UpdateFLStatus(((TranxInterimData) flInterim.get()).getnREC(),
//									((TranxInterimData) flInterim.get()).getFlAddressREC(), "CONFIRMED",
//									((TranxInterimData) flInterim.get()).getFDPeriodId(),
//									((TranxInterimData) flInterim.get()).getAssetLotId(),
//									((TranxInterimData) flInterim.get()).getPeriod()))
//							.getBytes());

			if (((TranxInterimData) flInterim.get()).getFDPeriodId() == null
					|| ((TranxInterimData) flInterim.get()).getFDPeriodId().equals("")) {
				this.tranManagerImpl.flStatusUpdate(((TranxInterimData) flInterim.get()).getFDPeriodId(),
						((TranxInterimData) flInterim.get()).getnREC(),
						((TranxInterimData) flInterim.get()).getFlAddressREC(), "LINKED",
						((TranxInterimData) flInterim.get()).getAssetLotId(),
						((TranxInterimData) flInterim.get()).getPeriod()
						);
//				String status = this.urlBuilder
//						.getResponse("http://" + ipDel + ":8092/flam/shareInfo",
//								this.objectMapper
//										.writeValueAsString(
//												new ShareNodeInfoDto(((TranxInterimData) flInterim.get()).getnREC(),
//														((TranxInterimData) flInterim.get()).getFlAddressREC(),
//														((TranxInterimData) flInterim.get()).getFlaHashLinkREC(),
//														((TranxInterimData) flInterim.get()).getFDPeriodId(),
//														((TranxInterimData) flInterim.get()).getAssetLotId()))
//										.getBytes());
//				log.info("Linked Status Updation with : " + status);
			}
			Alr = this.urlBuilder.getResponse("http://" + ip + ":8082/asset/zerodowndel", this.objectMapper
					.writeValueAsString(this.util.assetManagerALR(flInterim, new TransactionDetails())).getBytes());
			TranxInterimData tranxInterimData = (TranxInterimData) flInterim.get();
//			this.util.updateClosedFutureTxDetails(tranxInterimData.getShortTransferId());
			tranxInterimData.setAlr(Alr);
			tranxInterimData = this.util.updateDELFLDetails(fldetails, tranxInterimData);
			this.tmpdb.save(tranxInterimData);
			
			if(Alr==null ) {
				log.info("Getting alr for zerodowndel api null from AM :"+Alr);
				//this.errorTransactionObj.createDelErrorContraTx(transactionDetails);
				throw new ApplicationException(
						"Dropping Transaction due to ALR   is null ");
				
			}
			log.info("ALR Created " + Alr);
			return Alr;
		} else {
			throw new Exception("Transaction id doesnt exists");
		}
	}

	public FLAddress createTemp(TransactionDetails transdet, String id, String flAddress) throws Exception {
		Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(id);
		log.info(this.objectMapper.writeValueAsString(transdet));
		if (tranxInterim.isEmpty()) {
			log.info("NEW TRANSACTION");
			FLInput flInput = new FLInput();
			FutureDatedTx futureTxObj = new FutureDatedTx();
//			if (!transdet.getFlId().equalsIgnoreCase("5432E")) {
			if (transdet.getSubType().equalsIgnoreCase("Reversal")) {
				futureTxObj = this.util.findPayRecID(transdet.getShortTransferId());
				if (futureTxObj.getFDPeriodId() != null && !futureTxObj.getFDPeriodId().isEmpty()) {
					String flRecAddress = this.util.findFLAddDEL(futureTxObj.getTxid());
//				flmDel.setFlAddress(flAddress);
					this.flm.setFlAddress(flRecAddress);

				} else {
					this.flm = this.generateKeyObj.getRecFLAdress(this.util.getFLInputRec(transdet, flInput, flAddress),
							transdet);
					this.tranManagerImpl.flStatusUpdate(transdet.getFDPeriodId(), transdet.getnREC(),
							this.flm.getFlAddress(), "UTILIZED", transdet.getAssetLotId(),transdet.getPeriod());
				}
			} else {
				this.flm = this.generateKeyObj.getRecFLAdress(this.util.getFLInputRec(transdet, flInput, flAddress),
						transdet);
				this.tranManagerImpl.flStatusUpdate(transdet.getFDPeriodId(), transdet.getnREC(),
						this.flm.getFlAddress(), "UTILIZED", transdet.getAssetLotId(),transdet.getPeriod());
			}
//			this.flm = this.generateKeyObj.getRecFLAdress(this.util.getFLInputRec(transdet, flInput, flAddress),
//					transdet);
			if (flm.getFlAddress() == null && flm.getFlag().equalsIgnoreCase("Failed")) {
				return this.flm;
			}

//			}
			TranxInterimData tranxInterimData = this.util.updateRecTransactionDetails(transdet, new TranxInterimData());
			tranxInterimData = this.util.updateRECFLDetails(this.flm, tranxInterimData);
			this.tmpdb.save(tranxInterimData);
			return this.flm;
		} else {
			throw new Exception("Transaction id already exists");
		}
	}

	public String createALR(FLAddress fldetails, String id) throws Exception {
		Optional<TranxInterimData> flInterim = this.tmpdb.findById(id);
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(((TranxInterimData) flInterim.get()).getnREC());
		String ip = ((NodeDetails) nodeOptional.get()).getDnsname();
		Optional<NodeDetails> nodeOptionalDel = this.nodeStore.findById(((TranxInterimData) flInterim.get()).getnDEL());
		String ipDel = ((NodeDetails) nodeOptionalDel.get()).getDnsname();
		log.info(this.objectMapper.writeValueAsString(fldetails));
		String Alr = null;
		if (!flInterim.isEmpty()) {
			log.info("Receive DEL FL ");
//			if (count == 1) {
//				this.tranManagerImpl.flStatusUpdate(((TranxInterimData) flInterim.get()).getnREC(),
//						((TranxInterimData) flInterim.get()).getFlAddressREC(), "LINKED");
//				count++;
//			} else {
			this.tranManagerImpl.flStatusUpdate(((TranxInterimData) flInterim.get()).getFDPeriodId(),
					((TranxInterimData) flInterim.get()).getnREC(),
					((TranxInterimData) flInterim.get()).getFlAddressREC(), "CONFIRMED",
					((TranxInterimData) flInterim.get()).getAssetLotId(),
					((TranxInterimData) flInterim.get()).getPeriod());

//			this.tx_outbound.updateConfirmFLAddressPassiveNodes(flInterim.get(),
//					((TranxInterimData) flInterim.get()).getFlAddressREC(), "CONFIRMED", flInterim.get().getnREC());

			this.tranManagerImpl.flStatusUpdate(((TranxInterimData) flInterim.get()).getFDPeriodId(),
					((TranxInterimData) flInterim.get()).getnREC(),
					((TranxInterimData) flInterim.get()).getFlAddressREC(), "LINKED",
					((TranxInterimData) flInterim.get()).getAssetLotId(),
					((TranxInterimData) flInterim.get()).getPeriod());
//			}
//			if (((TranxInterimData) flInterim.get()).getContraType().equalsIgnoreCase("Obligation")
//					|| ((TranxInterimData) flInterim.get()).getContraType().equalsIgnoreCase("Loan")
//					|| ((TranxInterimData) flInterim.get()).getContraType().equalsIgnoreCase("Collateral")) {
//
//				this.util.updateFutureTxDetails(((TranxInterimData) flInterim.get()).getUseCase(), id,
//						((TranxInterimData) flInterim.get()).getShortTransferId(),
//						((TranxInterimData) flInterim.get()).getnDEL(), ((TranxInterimData) flInterim.get()).getnREC(),
//						((TranxInterimData) flInterim.get()).getContingentId(),
//						((TranxInterimData) flInterim.get()).getFDPeriodId());
//			}
//			String status = this.urlBuilder
//					.getResponse("http://" + ipDel + ":8092/flam/shareInfo",
//							this.objectMapper
//									.writeValueAsString(
//											new ShareNodeInfoDto(((TranxInterimData) flInterim.get()).getnREC(),
//													((TranxInterimData) flInterim.get()).getFlAddressREC(),
//													((TranxInterimData) flInterim.get()).getFlaHashLinkREC()))
//									.getBytes());
//			log.info("Linked Status Updation w;ith Status: " + status);
			log.info("ALR Created ");
//			if (((TranxInterimData) flInterim.get()).getContraType().equalsIgnoreCase("Obligation")) {
//
//				this.util.updateFutureTxDetails(id, ((TranxInterimData) flInterim.get()).getShortTransferId());
//			}
			TransactionDetails AlrData = this.urlBuilder.getTransaction("http://" + ip + ":8082/asset/assetlotALR",
					this.objectMapper.writeValueAsString(this.util.assetManagerALR(flInterim, new TransactionDetails()))
							.getBytes());

			if (AlrData != null && AlrData.getFlag() != null && AlrData.getFlag().equalsIgnoreCase("Failed")) {
				String ErrorFlStatus = this.urlBuilder.getResponse("http://" + ip + ":8092/flam/updateErrorFLStatus",
						this.objectMapper.writeValueAsString(this.util.updatedErrorFL(flErrorObj,
								((TranxInterimData) flInterim.get()).getFlAddressREC())).getBytes());
				log.debug("Update status of Fl address in error Transaction: " + ErrorFlStatus);

				this.errorTransactionObj.storeRecErrorTransaction(AlrData.getErrorMessage(), AlrData);
				return AlrData.getAssetLotId();
//				throw new ApplicationException("AssetLot ID Invalid");
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

	public boolean shareALN(String aln, String id) throws Exception {
		Optional<TranxInterimData> flInterim = this.tmpdb.findById(id);
		boolean alnResponse = false;
		if (!flInterim.isEmpty()) {
			log.info("Receive ALN ");
			TranxInterimData tranxInterimData = (TranxInterimData) flInterim.get();
			tranxInterimData.setAln(aln);
			this.tmpdb.save(tranxInterimData);
			alnResponse = true;
			return alnResponse;
		} else {
			throw new Exception("Transaction id doesnt exists");
		}
	}

	public String getEncryptedHash(String encryption, String id) throws Exception {
		Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(id);
		Optional<KeyEntities> keyentity = this.keystore.findById(((TranxInterimData) tranxInterim.get()).getTxDEL());
		String RecEncrptedHash = null;
		if (!keyentity.isEmpty()) {
			if (this.decryptMsg
					.decryptText(encryption, this.decryptMsg.getPublic(((KeyEntities) keyentity.get()).getPublikKey()))
					.equals(((TranxInterimData) tranxInterim.get()).getTranxHash())) {
				RecEncrptedHash = this.tranManagerImpl.encrypt(
						this.util.encrptyRec((TranxInterimData) tranxInterim.get()), id,
						((TranxInterimData) tranxInterim.get()).getTxREC());
				log.info("Received Delivery E# and verified Successfully");
				log.info("Sharing Rec E#");
				TranxInterimData tranxInterimData = (TranxInterimData) tranxInterim.get();
				tranxInterimData.setEncryptedHash(encryption);
				tranxInterimData.setRecEncryptedHash(RecEncrptedHash);
				this.tmpdb.save(tranxInterimData);
				return RecEncrptedHash;
			} else {
				throw new Exception("E# is not valid");
			}
		} else {
			throw new Exception("KeyEntity doesnot Exists");
		}
	}

	public String getTxHash(String txHash, String id) {
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

	public byte[] getDigiSign(byte[] digisign, String id) throws Exception {
		Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(id);
		Optional<KeyEntities> keyentity = this.keystore.findById(((TranxInterimData) tranxInterim.get()).getTxDEL());
		byte[] RecDigisign = null;
		ContraTransactionRecDetails contra_txRec = this.util.createRecContraTransaction(tranxInterim.get(),
				((TranxInterimData) tranxInterim.get()).getnREC());
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(((TranxInterimData) tranxInterim.get()).getnREC());
		String ip = ((NodeDetails) nodeOptional.get()).getDnsname();
		if (!keyentity.isEmpty() && !tranxInterim.isEmpty()) {
			if (this.verifySignature.verifySignature(this.util.encrptyDel((TranxInterimData) tranxInterim.get()),
					digisign, ((KeyEntities) keyentity.get()).getPublikKey())) {
				RecDigisign = this.tranManagerImpl.digisign(this.util.encrptyRec((TranxInterimData) tranxInterim.get()),
						id, ((TranxInterimData) tranxInterim.get()).getTxREC());
				Timestamp txValidateEndTime = localDateTime.getTimeStamp();
				this.generateKeyObj.ledgerProcTimeRec(((TranxInterimData) tranxInterim.get()).getTransactionId(),
						txValidateStartTime, txValidateEndTime);
				log.info("Receive DigiSign and verified Successfully");
				TranxInterimData tranxInterimData = (TranxInterimData) tranxInterim.get();
				tranxInterimData.setDegiSign(digisign);
				tranxInterimData.setRecDegiSign(RecDigisign);
				this.tmpdb.save(tranxInterimData);
				log.info("******************************************************");
				log.info("Transaction is completed successfully");
				log.info(((TranxInterimData) tranxInterim.get()).toString());
				log.info("******************************************************");
				contra_txRec.setContramatchStatus(true);
			} else {
				contra_txRec.setContramatchStatus(false);
				log.info(" Receival DigiSign is not valid");
			}
			this.contraTxRecRepo.save(contra_txRec);
			log.info("Receival Contra Transaction Created Successfully");
			log.info(contra_txRec);
			// MutiCast Rec ContraTransaction
			String staus = this.urlBuilder.getResponse(
					"http://" + "localhost" + ":7442/LComm/TxMulticasting/contraTxRec",
					this.objectMapper.writeValueAsString(contra_txRec).getBytes());
			log.info("MultiCast Status: " + staus);

			return RecDigisign;
		} else {
			throw new Exception("KeyEntity doesnot Exists");
		}
	}

	public void updateLedger(String id, String procedure) throws Exception {
		StoredProcedureQuery query = this.entityManager.createStoredProcedureQuery(procedure);
		query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
		query.setParameter(1, id);
		query.execute();
	}
}
