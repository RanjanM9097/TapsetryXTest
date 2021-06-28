package com.l4s.transactionmanager.process;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
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
import com.l4s.transactionmanager.businessrule.BusinessRuleManagerImpl;
import com.l4s.transactionmanager.dao.ContraTransactionDel;
import com.l4s.transactionmanager.dao.ContraTransactionRec;
import com.l4s.transactionmanager.dao.KeyStore;
import com.l4s.transactionmanager.dao.NodeStore;
import com.l4s.transactionmanager.dao.PublicKeyStore;
import com.l4s.transactionmanager.dao.TempDb;
import com.l4s.transactionmanager.dao.UtilityData;
import com.l4s.transactionmanager.dto.ContraTransactionDelDetails;
import com.l4s.transactionmanager.dto.ContraTransactionRecDetails;
import com.l4s.transactionmanager.dto.DigiSignature;
import com.l4s.transactionmanager.dto.EncryptedHashShareData;
import com.l4s.transactionmanager.dto.FLAddress;
import com.l4s.transactionmanager.dto.FLInput;
import com.l4s.transactionmanager.dto.FLMAddress;
import com.l4s.transactionmanager.dto.KeyEntities;
import com.l4s.transactionmanager.dto.LocalDateTimes;
import com.l4s.transactionmanager.dto.NodeDetails;
import com.l4s.transactionmanager.dto.PublicKeyEntities;
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
public class ShortTransferImpl {
	@Autowired
	UrlBuilder urlBuilder;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	TempDb tmpdb;
	@Autowired
	TranxInterimData tranxInterimData;
	@Autowired
	UtilityData util;
	@Autowired
	FLAddress flm;
	@Autowired
	GenerateKeys generateKeys;
	@Autowired
	KeyStore keystore;
	@Autowired
	DigitalSignature digitalSignature;
	@Autowired
	BusinessRuleManagerImpl brm;
	@Autowired
	EncryptDecrypt edt;
	@Autowired
	DecryptMsg decryptMsg;
	@Autowired
	PublicKeyStore publicKeyStore;
	@Autowired
	VerifySignature verifySignature;
	@Autowired
	NodeStore nodeStore;
	private static Logger log = LogManager.getLogger(ShortTransferImpl.class);
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	TransactionManagerImpl tranManagerImpl;
	@Autowired
	EncryptedHashShareData encryptedHashObj;
	@Autowired
	DigiSignature digiSignObj;
	@Autowired
	ContraTransactionDel contraTxDelRepo;
	@Autowired
	ContraTransactionRec contraTxRecRepo;
	@Autowired
	ContraTransactionDelDetails contraTxDel;
	@Autowired
	ContraTransactionRecDetails contraTxRec;
	@Autowired
	LocalDateTimes localDateTime;

	public FLAddress getFLAdress(FLInput flInput) throws Exception {
		FLAddress fl = new FLAddress();
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(flInput.nodeId);
		log.trace("Requesting URI :" + "http://" + ((NodeDetails) nodeOptional.get()).getDnsname()
				+ ":8092/flam/requestFlAddr" + "for FL Address with details : "
				+ this.objectMapper.writeValueAsString(flInput));
		// del side ip
		FLMAddress flm = this.urlBuilder.getFLUpdated(
				"http://" + ((NodeDetails) nodeOptional.get()).getDnsname() + ":8092/flam/requestFlAddr",
				this.objectMapper.writeValueAsString(flInput).getBytes());
		log.info("FLM Address Created--{}", this.objectMapper.writeValueAsString(flm));
		return this.util.updatedFL(fl, flm);
	}

	public void flStatusUpdate(String fdPeriodID, String nodeId, String fLAddress, String status, String ALD,String periodId)
			throws Exception {
		// del side ip
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(nodeId);
		log.trace("Requesting URI :" + "http://" + ((NodeDetails) nodeOptional.get()).getDnsname()
				+ ":8092/flam/updateStatus" + "to update FL Address: " + fLAddress + ", status: " + status
				+ " and Node: " + nodeId +", periodId : " + periodId);
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
		StoredProcedureQuery query = this.entityManager.createStoredProcedureQuery(procedure);
		query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
		query.setParameter(1, id);
		query.execute();
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
			this.tranxInterimData = this.util.updateTransactionDetails(transactionDetails, this.tranxInterimData);
			this.tranxInterimData = this.util.updateRECFLDetails(flmRec, this.tranxInterimData);
			this.tmpdb.save(this.tranxInterimData);
		} else {
			throw new ApplicationException("Transaction is already exists in DB");
		}
	}

	public FLAddress updateFLDel(TransactionDetails transactionDetails) {
		FLAddress flmDel = null;
		try {
			FLInput flInput = new FLInput();
			flmDel = this.getFLAdress(this.util.getFLInputDel(transactionDetails, flInput));
			this.flStatusUpdate(transactionDetails.getFDPeriodId(), transactionDetails.getnDEL(), flmDel.getFlAddress(),
					"UTILIZED", transactionDetails.getAssetLotId(),
					transactionDetails.getPeriod());
			if (this.tranxInterimData.getTransactionId() != null) {
				log.info("Delivery FL Created");
				this.tranxInterimData = this.util.updateDELFLDetails(flmDel, this.tranxInterimData);
				this.tmpdb.save(this.tranxInterimData);
			}
		} catch (Exception var4) {
			var4.printStackTrace();
		}

		return flmDel;
	}

	public TransactionDetails getALD(TransactionDetails transactionData) throws Exception{
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(transactionData.getnDEL());
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

		return this.util.assetManagerALD(transactionData, ald);
	}

	public void processTransaction(TransactionDetails transactionData) throws Exception {
		TransactionDetails transactionDetails = this.getALD(transactionData);
		ContraTransactionDelDetails contra_txDel = new ContraTransactionDelDetails();
		ContraTransactionRecDetails contra_txRec = new ContraTransactionRecDetails();
		log.info("Started Process with transaction details {}", transactionDetails.toString());
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(transactionDetails.getnREC());
//		String ip = ((NodeDetails) nodeOptional.get()).getDnsname();
		Optional<TranxInterimData> tranxInterimfl = this.tmpdb.findById(transactionDetails.getTransactionId());
		FLAddress fl = new FLAddress();
		log.info("Calling lLcomm for rec side http:/ localhost" + ":7442/LComm/zeroDown ");
		FLAddress flmRec = this.urlBuilder.getFL("http://" + "localhost" + ":7442/LComm/zeroDown",
				this.objectMapper.writeValueAsString(transactionDetails).getBytes());
		transactionDetails.setTransactionId(transactionDetails.getTransactionId() + "i");
		if (flmRec == null) {
			throw new ApplicationException("Received FL address is null");
		} else {
			this.newTransaction(transactionDetails, flmRec);
			log.info("Rec FL Received");
			FLAddress flmDel = this.util.getDelFL(tranxInterimfl, fl);
			this.tranxInterimData = this.util.updateDELFLDetails(flmDel, this.tranxInterimData);
			this.tmpdb.save(this.tranxInterimData);
			if (flmDel == null) {
				throw new ApplicationException("Delivery FL address is null");
			} else {
				log.info("calling 7442/LComm/getzeroDownALR/");
				String alr = this.urlBuilder.getResponse(
						"http://" + "localhost" + ":7442/LComm/getzeroDownALR/" + transactionDetails.getTransactionId()
								+ "/" + transactionData.getnREC(),
						this.objectMapper.writeValueAsString(flmDel).getBytes());
				if (this.tranxInterimData.getTransactionId() != null) {
					log.info("Received ALR ->{}", alr);
					this.tranxInterimData.setAlr(alr);
					this.tmpdb.save(this.tranxInterimData);
				}

				Optional<TranxInterimData> tranxInterimHash = this.tmpdb
						.findById(transactionDetails.getTransactionId());
				String txDelHash = this.edt
						.applySha256(this.util.encrptyDel((TranxInterimData) tranxInterimHash.get()));

				String txRecHash = this.urlBuilder
						.getResponse(
								"http://" + "localhost" + ":7442/LComm/shareTxHash/"
										+ transactionDetails.getTransactionId() + "/" + transactionDetails.getnREC(),
								txDelHash.getBytes());
				log.info("After call LComm/shareTxHashShort" + txRecHash);
				if (txRecHash != null && txDelHash != null) {
					log.info("Transaction Hash is created");
					this.tranxInterimData.setTranxHash(txDelHash);
					this.tranxInterimData.setRecTranxHash(txRecHash);
					this.tmpdb.save(this.tranxInterimData);
				}

				Optional<TranxInterimData> tranxInterimEncrypt1 = this.tmpdb
						.findById(transactionDetails.getTransactionId());

				Optional<KeyEntities> Delkeyentity = null;
				Delkeyentity = this.keystore.findById(transactionDetails.getTxDEL());
				String delencrptedHash = this.encrypt(
						this.util.encrptyDel((TranxInterimData) tranxInterimEncrypt1.get()),
						transactionDetails.getTransactionId(), transactionDetails.getTxDEL());
				log.info("Delivery Encrypted# is created");
				encryptedHashObj.setEncrptedhash(delencrptedHash);
				encryptedHashObj.setPublicKey(Delkeyentity.get().getPublikKey());
				encryptedHashObj.setnRec(transactionDetails.getnREC());
				EncryptedHashShareData recencrptedHash = this.urlBuilder.getEnryptedHash(
						"http://" + "localhost" + ":7442/LComm/getEncryptedHash/"
								+ transactionDetails.getTransactionId(),
						this.objectMapper.writeValueAsString(encryptedHashObj).getBytes());
				log.info("After calling LComm/getEncryptedHash" + recencrptedHash);
				Optional<PublicKeyEntities> keyentity = this.publicKeyStore.findById(transactionDetails.getTxREC());
				if (!keyentity.isEmpty()) {
					if (this.decryptMsg
							.decryptText(recencrptedHash.getEncrptedhash(),
									this.decryptMsg.getPublic((recencrptedHash.getPublicKey())))
							.equals(((TranxInterimData) tranxInterimHash.get()).getRecTranxHash())) {
						log.info("Recieve Encrypted# and verified Successfully ");
						tranxInterimData.setEncryptedHash(delencrptedHash);
						tranxInterimData.setRecEncryptedHash(recencrptedHash.getEncrptedhash());
						this.tmpdb.save(tranxInterimData);
					}

//				String delencrptedHash = this.encrypt(
//						this.util.encrptyDel((TranxInterimData) tranxInterimEncrypt1.get()),
//						transactionDetails.getTransactionId(), transactionDetails.getTxDEL());
//				log.info("Delivery Encrypted# is created");
//				String recencrptedHash = this.urlBuilder.getResponse(
//						"http://" + ip + ":8080/getEncryptedHash/" + transactionDetails.getTransactionId(),
//						delencrptedHash.getBytes());
//				Optional<TranxInterimData> tranxInterimEncrypt = this.tmpdb
//						.findById(transactionDetails.getTransactionId());
//				Optional<PublicKeyEntities> keyentity = this.publicKeyStore.findById(transactionDetails.getTxREC());
//				if (!keyentity.isEmpty()) {
//					if (this.decryptMsg
//							.decryptText(recencrptedHash,
//									this.decryptMsg.getPublic(((PublicKeyEntities) keyentity.get()).getPublikKey()))
//							.equals(((TranxInterimData) tranxInterimEncrypt.get()).getRecTranxHash())) {
//						log.info("Recieve Encrypted# and verified Successfully ");
//						this.tranxInterimData.setEncryptedHash(delencrptedHash);
//						this.tranxInterimData.setRecEncryptedHash(recencrptedHash);
//						this.tmpdb.save(this.tranxInterimData);
//					}

					Optional<TranxInterimData> tranxInterimdig = this.tmpdb
							.findById(transactionDetails.getTransactionId());
					byte[] digisign = this.digisign(this.util.encrptyDel((TranxInterimData) tranxInterimdig.get()),
							transactionDetails.getTransactionId(), transactionDetails.getTxDEL());
					if (digisign.length == 0) {
						log.error("\"DigiSign is null\"");
						throw new Exception("DigiSign is null");
					} else {
						log.info("Delivery Digi-sign is created");
						tranxInterimData.setDegiSign(digisign);
//						ContraTransactionDelDetails contra_txDel = this.util
//								.createDelContraTransaction(tranxInterimData, transactionDetails.getnDEL());
						this.tmpdb.save(tranxInterimData);
						digiSignObj.setDigiSign(digisign);
						digiSignObj.setPublicKey(Delkeyentity.get().getPublikKey());
						digiSignObj.setNodeRec(tranxInterimdig.get().getnREC());
//						log.info("Delivery Digi-sign is created");
//						this.tranxInterimData.setDegiSign(digisign);
//						this.tmpdb.save(this.tranxInterimData);
						String recdigiSign = this.urlBuilder.getResponse(
								"http://" + "localhost" + ":7442/LComm/shareDigisign/"
										+ transactionDetails.getTransactionId(),
								this.objectMapper.writeValueAsString(digiSignObj).getBytes());
						log.info("After calling LComm /shareDigisignShort" + recdigiSign);
						if (!recdigiSign.equalsIgnoreCase("true")) {
//							tranxInterimData.setRecDegiSign(recdigiSign.getBytes());
							contra_txRec = this.createRecContraTransaction(tranxInterimData,
									transactionDetails.getnREC());
							contra_txDel = this.createDelContraTransaction(tranxInterimData,
									transactionDetails.getnDEL());
							contra_txDel.setContramatchStatus(false);
							contra_txRec.setContramatchStatus(false);
							this.contraTxDelRepo.save(contra_txDel);
							this.contraTxRecRepo.save(contra_txRec);
							throw new Exception("Digital Signature not matched");

						} else {
							contra_txRec = this.util.createRecContraTransaction(tranxInterimData,
									transactionDetails.getnREC());
							contra_txDel = this.util.createDelContraTransaction(tranxInterimData,
									transactionDetails.getnDEL());
							contra_txDel.setContramatchStatus(true);
							contra_txRec.setContramatchStatus(true);
							this.contraTxDelRepo.save(contra_txDel);
							this.contraTxRecRepo.save(contra_txRec);
							log.info("Digi-sign is received and verified Successfully");
						}
						String staus = this.urlBuilder.getResponse(
								"http://" + "localhost" + ":7442/LComm/TxMulticasting/contraTxDel",
								this.objectMapper.writeValueAsString(contra_txDel).getBytes());
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
			contraTxDel.setContraid(intrim.getTransactionId());
			contraTxDel.setTx_id(intrim.getTransactionId());
			contraTxDel.setPostperiodfk(intrim.getPeriod());
			contraTxDel.setAssetid(intrim.getAtype());
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

	public ContraTransactionRecDetails createRecContraTransaction(TranxInterimData intrim, String nodeType) {
		try {
			String contrID = intrim.getTxDEL() + intrim.getTransactionId() + intrim.getTxREC();
//			contraTxRec.setContraid(contrID);
			contraTxRec.setContraid(intrim.getTransactionId());
			contraTxRec.setTx_id(intrim.getTransactionId());
			contraTxRec.setPostperiodfk(intrim.getPeriod());
			contraTxRec.setAssetid(intrim.getAtype());
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
			contraTxRec.setTxType(intrim.getTxType());
			contraTxRec.setUseCase(intrim.getUseCase());
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
}
