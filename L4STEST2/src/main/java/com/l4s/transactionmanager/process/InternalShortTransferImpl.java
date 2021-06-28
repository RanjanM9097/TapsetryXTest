package com.l4s.transactionmanager.process;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.sql.Timestamp;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.l4s.transactionmanager.dao.ContraTransactionDel;
import com.l4s.transactionmanager.dao.KeyStore;
import com.l4s.transactionmanager.dao.NodeStore;
import com.l4s.transactionmanager.dao.PublicKeyStore;
import com.l4s.transactionmanager.dao.TempDb;
import com.l4s.transactionmanager.dao.TransactionNodeInfo;
import com.l4s.transactionmanager.dao.UtilityData;
import com.l4s.transactionmanager.dto.ContraTransactionDelDetails;
import com.l4s.transactionmanager.dto.FLAddress;
import com.l4s.transactionmanager.dto.FLInput;
import com.l4s.transactionmanager.dto.FLMAddress;
import com.l4s.transactionmanager.dto.KeyEntities;
import com.l4s.transactionmanager.dto.LocalDateTimes;
import com.l4s.transactionmanager.dto.NodeDetails;
import com.l4s.transactionmanager.dto.TransactionDetails;
import com.l4s.transactionmanager.dto.TranxInterimData;
import com.l4s.transactionmanager.dto.UpdateFLStatus;
import com.l4s.transactionmanager.security.DecryptMsg;
import com.l4s.transactionmanager.security.DigitalSignature;
import com.l4s.transactionmanager.security.EncryptDecrypt;
import com.l4s.transactionmanager.security.GenerateKeys;
import com.l4s.transactionmanager.security.VerifySignature;
import com.l4s.transactionmanager.service.ApplicationException;

@Service
public class InternalShortTransferImpl implements TransactionManager {
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
	ContraTransactionDelDetails shortContraTxDel;
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
	NodeStore nodeStore;
	@Autowired
	DigitalSignature digitalSignature;
	@Autowired
	IntraTransactionRecImpl intraTransactionRecImpl;
	private static Logger log = LogManager.getLogger(InternalShortTransferImpl.class);
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	TransactionNodeInfo transactionNodeInfo;
	@Autowired
	LocalDateTimes localDateTime;
	@Autowired
	ContraTransactionDel contraTxDelRepo;

	public FLAddress getFLAdress(FLInput flInput) throws Exception {

		FLAddress fl = new FLAddress();
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(flInput.nodeId);
		log.trace("Requesting URI :" + "http://" + ((NodeDetails) nodeOptional.get()).getDnsname()
				+ ":8092/flam/requestFlAddr" + "for FL Address with details : "
				+ this.objectMapper.writeValueAsString(flInput));
		// del ip = nodeOptional.get()).getDnsname()
		FLMAddress flm = this.urlBuilder.getFLUpdated(
				"http://" + ((NodeDetails) nodeOptional.get()).getDnsname() + ":8092/flam/requestFlAddr",
				this.objectMapper.writeValueAsString(flInput).getBytes());
		log.trace("Requesting URI :" + "http://" + ((NodeDetails) nodeOptional.get()).getDnsname()
				+ ":8092/flam/requestFlAddr" + "for FL Address with details : "
				+ this.objectMapper.writeValueAsString(flInput));
		log.info("FLM Address Created--{}", this.objectMapper.writeValueAsString(flm));
		return this.util.updatedFL(fl, flm);
	}

	public void flStatusUpdate(String fdPeriodID, String nodeId, String fLAddress, String status, String ALD,String periodId)
			throws Exception {

		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(nodeId);

		log.trace("Requesting URI :" + "http://" + ((NodeDetails) nodeOptional.get()).getDnsname()
				+ ":8092/flam/updateStatus" + "to update FL Address: " + fLAddress + ", status: " + status
				+ " and Node: " + nodeId +", periodId : " + periodId);
		// ip is del side

		this.urlBuilder
				.getResponse("http://" + ((NodeDetails) nodeOptional.get()).getDnsname() + ":8092/flam/updateStatus",
						this.objectMapper
								.writeValueAsString(new UpdateFLStatus(nodeId, fLAddress, status, fdPeriodID, ALD,periodId))
								.getBytes());
	}

	public String createALR() throws Exception {
		return RandomStringUtils.randomAlphanumeric(5);
	}

	public String calculateALN(float qty, float ald) throws Exception {
		return RandomStringUtils.randomAlphanumeric(5);
	}

	public boolean validateALD(String ald) throws Exception {
		return true;
	}

	public void updateLedger(String id, String procedure) throws Exception {
//		Timestamp startTime = localDateTime.getTimeStamp();
		StoredProcedureQuery query = this.entityManager.createStoredProcedureQuery(procedure);
		query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
		query.setParameter(1, id);
		query.execute();
//		Timestamp endTime = localDateTime.getTimeStamp();
//		this.util.ledgerProcTime(TxnId, startTime, endTime);
//		log.debug("Ledger Process Completed");

	}

	public byte[] digisign(String data, String id, String customerId) throws Exception {
		byte[] privateKey = null;
		Optional<KeyEntities> keyEntity = this.keystore.findById(customerId);
		if (!this.tmpdb.findById(id).isEmpty() && !keyEntity.isEmpty()) {
			privateKey = ((KeyEntities) keyEntity.get()).getPrivateKey();
		}

		return this.digitalSignature.sign(data, privateKey);
	}

	public String encrypt(String data, String id, String txParty) throws InvalidKeyException, NoSuchPaddingException,
			UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, Exception {
		Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(id);
		String hash = null;
		Optional<KeyEntities> keyentity = null;
		if (!tranxInterim.isEmpty()) {
			hash = this.edt.applySha256(data);
			keyentity = this.keystore.findById(txParty);
			return this.edt.encryptText(hash, this.edt.getPrivate(((KeyEntities) keyentity.get()).getPrivateKey()));
		} else {
			throw new Exception("Transaction id is not exists in DB");
		}
	}

	public void newTransaction(TransactionDetails transactionDetails, FLAddress flmRec) throws ApplicationException {
		Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(transactionDetails.getTransactionId());
		if (tranxInterim.isEmpty()) {
			log.info("NEW TRANSACTION");
			TranxInterimData tranxInterimData = this.util.updateTransactionDetails(transactionDetails,
					new TranxInterimData());
			tranxInterimData = this.util.updateTransactionDetails(transactionDetails, tranxInterimData);
			tranxInterimData = this.util.updateRECFLDetails(flmRec, tranxInterimData);
			this.tmpdb.save(tranxInterimData);
		} else {
			throw new ApplicationException("Transaction is already exists in DB");
		}
	}

	public FLAddress updateFLDel(TransactionDetails transactionDetails) {
		Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(transactionDetails.getTransactionId());
		FLAddress flmDel = null;

		try {
			FLInput flInput = new FLInput();
			flmDel = this.getFLAdress(this.util.getFLInputDel(transactionDetails, flInput));
			this.flStatusUpdate(transactionDetails.getFDPeriodId(), this.transactionNodeInfo.getNodeid(),
					flmDel.getFlAddress(), "UTILIZED", transactionDetails.getAssetLotId(),transactionDetails.getPeriod());
			if (!tranxInterim.isEmpty()) {
				log.info("Delivery FL Created");
				TranxInterimData tranxInterimData = (TranxInterimData) tranxInterim.get();
				tranxInterimData = this.util.updateDELFLDetails(flmDel, tranxInterimData);
				this.tmpdb.save(tranxInterimData);
			}
		} catch (Exception var6) {
			var6.printStackTrace();
		}

		return flmDel;
	}

	public String getALD(TransactionDetails transactionData)throws Exception {
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(transactionData.getnDEL());
//		TransactionDetails shortTransactionDetails = new TransactionDetails();
		String ald = null;

		try {
			ald = this.urlBuilder.getResponse(
					"http://" + ((NodeDetails) nodeOptional.get()).getDnsname() + ":8082/asset/zerodownrec",
					this.objectMapper.writeValueAsString(transactionData).getBytes());
			if(ald==null ) {
				log.info("Getting alr for zerodowndel api null from AM :"+ald);
				//this.errorTransactionObj.createDelErrorContraTx(transactionDetails);
				throw new ApplicationException(
						"Dropping Transaction due to ALR   is null ");
				
			}
//			this.util.updateClosedFutureTxDetails(transactionData.getShortTransferId());
		} catch (JsonProcessingException var5) {
			var5.printStackTrace();
		}

//		shortTransactionDetails.setAssetLotId(ald);
//		shortTransactionDetails.setTransactionId(transactionData.getTransactionId() + "j");
//		shortTransactionDetails.setTimeStamp(transactionData.getTimeStamp());
//		shortTransactionDetails.setAtype(transactionData.getAtype());
//		shortTransactionDetails.setAname(transactionData.getAname());
//		shortTransactionDetails.setPeriod(transactionData.getPeriod());
//		shortTransactionDetails.setQtyALR(transactionData.getQtyALR());
//		shortTransactionDetails.setQtyALD(transactionData.getQtyALD());
//		shortTransactionDetails.setnREC(transactionData.getnREC());
//		shortTransactionDetails.setTxREC(transactionData.getTxREC());
//		shortTransactionDetails.setnDEL(transactionData.getnDEL());
//		shortTransactionDetails.setTxDEL(transactionData.getTxDEL());
//		shortTransactionDetails.setNodeId(transactionData.getNodeId());
//		shortTransactionDetails.setTxType(transactionData.getTxType());
//		shortTransactionDetails.setUseCase(transactionData.getUseCase());
//		shortTransactionDetails.setFDPeriodId(transactionData.getFDPeriodId());
//		if (transactionData.getSubType() != null) {
//			shortTransactionDetails.setSubType(transactionData.getSubType());
//		}
//
//		if (transactionData.getShortTransferId() != null) {
//			shortTransactionDetails.setShortTransferId(transactionData.getShortTransferId());
//		}

		return ald;
	}

	public void processTransaction(TransactionDetails transactionData) throws Exception {
//		TransactionDetails shortTransactionDetails = new TransactionDetails();
		ContraTransactionDelDetails short_contra_txDel = new ContraTransactionDelDetails();
//		TransactionDetails shortTransactionDetails = this.getALD(transactionData);
		log.info("Started Process with transaction details {}", transactionData.toString());
		Optional<TranxInterimData> tranxInterimfl = this.tmpdb.findById(transactionData.getTransactionId() + "i");
		FLAddress fl = new FLAddress();
		FLAddress flmRec = this.intraTransactionRecImpl.shortTransferTemp(transactionData);
		String recTranId = transactionData.getTransactionId() + "k";
//		shortTransactionDetails.setTransactionId(shortTransactionDetails.getTransactionId() + "j");
		if (flmRec == null) {
			throw new ApplicationException("Received FL address is null");
		} else {
			this.newTransaction(transactionData, flmRec);
			log.info("Rec FL Received");
			Optional<TranxInterimData> tranxInterimHash = this.tmpdb
					.findById(transactionData.getTransactionId());
			TranxInterimData tranxInterimData = (TranxInterimData) tranxInterimHash.get();
			FLAddress flmDel = this.util.getDelFL(tranxInterimfl, fl);
			tranxInterimData = this.util.updateDELFLDetails(flmDel, tranxInterimData);
			this.tmpdb.save(tranxInterimData);
			if (flmDel == null) {
				throw new ApplicationException("Delivery FL address is null");
			} else {
				String alr = this.intraTransactionRecImpl.createzeroDownALR(flmDel, recTranId);
				if (tranxInterimData.getTransactionId() != null) {
					log.info("Received ALR ->{}", alr);
					tranxInterimData.setAlr(alr);
					this.tmpdb.save(tranxInterimData);
				}

				String txDelHash = this.edt
						.applySha256(this.util.encrptyDel((TranxInterimData) tranxInterimHash.get()));
				String txRecHash = this.intraTransactionRecImpl.getTxHash(txDelHash, recTranId);
				if (txRecHash != null && txDelHash != null) {
					log.info("Transaction Hash is created");
					tranxInterimData.setTranxHash(txDelHash);
					tranxInterimData.setRecTranxHash(txRecHash);
					this.tmpdb.save(tranxInterimData);
				}

//				String delencrptedHash = this.encrypt(this.util.encrptyDel((TranxInterimData) tranxInterimHash.get()),
//						transactionDetails.getTransactionId(), transactionDetails.getTxDEL());
//				log.info("Delivery Encrypted# is created");
//				String recencrptedHash = this.intraTransactionRecImpl.getEncryptedHash(delencrptedHash, recTranId);
//				Optional<KeyEntities> keyentity = this.keystore.findById(transactionDetails.getTxREC());
//				if (!keyentity.isEmpty()) {
//					if (this.decryptMsg
//							.decryptText(recencrptedHash,
//									this.decryptMsg.getPublic(((KeyEntities) keyentity.get()).getPublikKey()))
//							.equals(((TranxInterimData) tranxInterimHash.get()).getRecTranxHash())) {
//						log.info("Recieve Encrypted# and verified Successfully ");
//						tranxInterimData.setEncryptedHash(delencrptedHash);
//						tranxInterimData.setRecEncryptedHash(recencrptedHash);
//						this.tmpdb.save(tranxInterimData);
//					}
				String delencrptedHash = this.encrypt(this.util.encrptyDel((TranxInterimData) tranxInterimHash.get()),
						transactionData.getTransactionId(), transactionData.getTxDEL());
				log.info("Delivery Encrypted# is created--{}", delencrptedHash);
				String recencrptedHash = this.intraTransactionRecImpl.getEncryptedHash(delencrptedHash, recTranId);
				Optional<KeyEntities> keyentity = this.keystore.findById(transactionData.getTxREC());
				if (!keyentity.isEmpty()) {
					if (this.decryptMsg
							.decryptText(recencrptedHash,
									this.decryptMsg.getPublic(((KeyEntities) keyentity.get()).getPublikKey()))
							.equals(((TranxInterimData) tranxInterimHash.get()).getRecTranxHash())) {
						log.info("Recieve Encrypted# and verified Successfully ");
						tranxInterimData.setEncryptedHash(delencrptedHash);
						tranxInterimData.setRecEncryptedHash(recencrptedHash);
						this.tmpdb.save(tranxInterimData);
					}

					byte[] digisign = this.digisign(this.util.encrptyDel((TranxInterimData) tranxInterimHash.get()),
							transactionData.getTransactionId(), transactionData.getTxDEL());
					if (digisign.length == 0) {
						log.error("\"DigiSign is null\"");
						throw new Exception("DigiSign is null");
					} else {
						log.info("Delivery Digi-sign is created");
						tranxInterimData.setDegiSign(digisign);
						this.tmpdb.save(tranxInterimData);
						short_contra_txDel = this.createDelContraTransaction(tranxInterimData,
								transactionData.getnDEL());
						short_contra_txDel.setDigitalsig(digisign);
						byte[] recdigiSign = this.intraTransactionRecImpl.getDigiSign(digisign, recTranId);
						if (!keyentity.isEmpty()) {
							if (this.verifySignature.verifySignature(
									this.util.encrptyRec((TranxInterimData) tranxInterimHash.get()), recdigiSign,
									((KeyEntities) keyentity.get()).getPublikKey())) {
								log.info("Verifyed DigiSign successfully");
								tranxInterimData.setRecDegiSign(recdigiSign);
								this.tmpdb.save(tranxInterimData);
								short_contra_txDel.setContramatchStatus(true);
								log.info("Digi-sign is received and verified Successfully");
							} else {
								short_contra_txDel.setContramatchStatus(false);
							}
							log.info(short_contra_txDel.toString());
							this.contraTxDelRepo.save(short_contra_txDel);
						} else {
							throw new Exception("KeyEntity doesnot Exists");
						}
						String staus = this.urlBuilder.getResponse(
								"http://" + "localhost" + ":7442/LComm/TxMulticasting/contraTxDel",
								this.objectMapper.writeValueAsString(short_contra_txDel).getBytes());
						log.info("MultiCast Status: " + staus);
					}
				} else {
					throw new Exception("KeyEntity doesnot Exists");
				}
			}
		}
	}

	public ContraTransactionDelDetails createDelContraTransaction(TranxInterimData intrim, String nodeType) {
		try {
			String contrID = intrim.getTxDEL() + intrim.getTransactionId() + intrim.getTxREC();
			;
//			contraTxDel.setContraid(contrID);
			shortContraTxDel.setContraid(intrim.getTransactionId());
			shortContraTxDel.setTx_id(intrim.getTransactionId());
			shortContraTxDel.setPostperiodfk(intrim.getPeriod());
			shortContraTxDel.setAssetid(intrim.getAtype());
			shortContraTxDel.setAssetLotId(intrim.getAssetLotId());
			shortContraTxDel.setTxDel(intrim.getTxDEL());
			shortContraTxDel.setTxREC(intrim.getTxREC());
			shortContraTxDel.setTranqty(intrim.getQtyALD());
			shortContraTxDel.setQtyALR(intrim.getQtyALR());
			shortContraTxDel.setAssetLotReceiverId(intrim.getAlr());
			shortContraTxDel.setAssetLotNetId(intrim.getAln());
			shortContraTxDel.setTrandt(intrim.getTimeStamp());
			shortContraTxDel.setPostdt(localDateTime.getTimeStamp());
			shortContraTxDel.setExtTxnId(intrim.getContraIdDel());
//			if (nodeType.equalsIgnoreCase(intrim.getnDEL())) {
			shortContraTxDel.setNodeid(nodeType);
			shortContraTxDel.setFladdrid(intrim.getFlAddressDEL());
			shortContraTxDel.setFlid(intrim.getFlIdDEL());
			shortContraTxDel.setFladdridparentfk(intrim.getFlaHashLinkDEL());
			shortContraTxDel.setEncryptedhash(intrim.getEncryptedHash());
			shortContraTxDel.setTrhash(intrim.getTranxHash());
			shortContraTxDel.setDigitalsig(intrim.getDegiSign());
			shortContraTxDel.setnDel(intrim.getnDEL());
			shortContraTxDel.setnREC(intrim.getnREC());
			shortContraTxDel.setTxType(intrim.getTxType());
			shortContraTxDel.setUseCase(intrim.getUseCase());
			shortContraTxDel.setAirlocknode(intrim.getAirlocknode());
			shortContraTxDel.setFDPeriodId(intrim.getFDPeriodId());
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
				shortContraTxDel.setContraType(intrim.getContraType());
			} else {
				shortContraTxDel.setContraType(null);
			}

			if (intrim.getContingentId() != null) {
				shortContraTxDel.setContingentId(intrim.getContingentId());
			} else {
				shortContraTxDel.setContingentId(null);
			}

			if (intrim.getShortTransferId() != null) {
				shortContraTxDel.setShortTransferId(intrim.getShortTransferId());

			} else {
				shortContraTxDel.setShortTransferId(null);
			}
		} catch (Exception exception) {
			log.info(exception + " ecxception occurred while creating Contra Transaction");
		}
		return shortContraTxDel;
	}

}
