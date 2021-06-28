package com.l4s.transactionmanager.process;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.l4s.transactionmanager.businessrule.BusinessRuleManagerImpl;
import com.l4s.transactionmanager.dao.ContraTransactionDel;
import com.l4s.transactionmanager.dao.KeyStore;
import com.l4s.transactionmanager.dao.NodeStore;
import com.l4s.transactionmanager.dao.PublicKeyStore;
import com.l4s.transactionmanager.dao.TempDb;
import com.l4s.transactionmanager.dao.TransactionNodeInfo;
import com.l4s.transactionmanager.dao.UtilityData;
import com.l4s.transactionmanager.dto.ContraTransactionDelDetails;
import com.l4s.transactionmanager.dto.FLAddress;
import com.l4s.transactionmanager.dto.FLAddressErrorUpdateDto;
import com.l4s.transactionmanager.dto.FLInput;
import com.l4s.transactionmanager.dto.FLMAddress;
import com.l4s.transactionmanager.dto.FutureDatedTx;
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
import com.l4s.transactionmanager.service.ErrorTransactions;
import com.l4s.transactionmanager.service.TransactionOutbound;

@Component
public class IntraTransactionDelImpl implements TransactionManager {
	@Autowired
	UrlBuilder urlBuilder;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	TempDb tmpdb;
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
	IntraTransactionRecImpl intraTransactionRecImpl;
	@Autowired
	TransactionManagerImpl tranManagerImpl;
	@Autowired
	InternalShortTransferImpl internalShortTransferImpl;
	@Autowired
	NodeStore nodeStore;
	private static Logger log = LogManager.getLogger(IntraTransactionDelImpl.class);
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	TransactionNodeInfo transactionNodeInfo;
	@Autowired
	TransactionOutbound tx_outbound;
	@Autowired
	ContraTransactionDel contraTxDelRepo;
	@Autowired
	LocalDateTimes localDateTime;
	@Autowired
	ErrorTransactions errorTransactionObj;
	@Autowired
	FLAddressErrorUpdateDto flErrorObj;
	HashMap<String, Integer> periodmap = new HashMap();
	public int count = 1;

	public String calculateALN(float qty, float ald) throws Exception {
		return RandomStringUtils.randomAlphanumeric(5);
	}

	public FLAddress getFLAdress(FLInput flInput, TransactionDetails transdet) throws Exception {
		FLAddress fl = new FLAddress();
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(flInput.nodeId);
		log.trace("Requesting URI :" + "http://" + ((NodeDetails) nodeOptional.get()).getDnsname()
				+ ":8092/flam/requestFlAddr" + "for FL Address with details : "
				+ this.objectMapper.writeValueAsString(flInput));
		FLMAddress flm = this.urlBuilder.getFLUpdated(
				"http://" + ((NodeDetails) nodeOptional.get()).getDnsname() + ":8092/flam/requestFlAddr",
				this.objectMapper.writeValueAsString(flInput).getBytes());
		if (flm != null && flm.getFlAddress() == null && flm.getErrorMsg() != null
				&& flm.getFlag().equalsIgnoreCase("Failed")) {
			this.errorTransactionObj.storeDelErrorTransaction(flm.getErrorMsg(), transdet);

			throw new ApplicationException(" Delivery FL address is null");
		}
		log.info("FLM Address Created--{}", this.objectMapper.writeValueAsString(flm));
		return this.util.updatedFL(fl, flm);
	}

	public void flStatusUpdate(String fdPeriodID, String nodeId, String fLAddress, String status, String ALD,String periodId)
			throws Exception {
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(nodeId);
		log.trace("Requesting URI :" + "http://" + ((NodeDetails) nodeOptional.get()).getDnsname()
				+ ":8092/flam/updateStatus" + "to update FL Address: " + fLAddress + ", status: " + status
				+ " and Node: " + nodeId  +", periodId: "+periodId);

		this.urlBuilder.getResponse(
				"http://" + ((NodeDetails) nodeOptional.get()).getDnsname() + ":8092/flam/updateStatusActiveNode",
				this.objectMapper.writeValueAsString(new UpdateFLStatus(nodeId, fLAddress, status, fdPeriodID, ALD,periodId))
						.getBytes());
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
		try {
			if (!tranxInterim.isEmpty()) {
				log.info("******");
//				log.info("Delivery FL Created");
				TranxInterimData tranxInterimData = (TranxInterimData) tranxInterim.get();
				tranxInterimData = this.util.updateRECFLDetails(flmRec, tranxInterimData);
				this.tmpdb.save(tranxInterimData);
			} else {
				throw new ApplicationException("Transaction is already exists in DB");
			}
		} catch (Exception exception) {
			log.error(exception + "Exception");
		}
	}

	public FLAddress updateFLDel(TransactionDetails transactionDetails) {
//		Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(transactionDetails.getTransactionId());
//		FLAddress flmDel = null;
//
//		try {
//			FLInput flInput = new FLInput();
//			flmDel = this.getFLAdress(this.util.getFLInputDel(transactionDetails, flInput));
//			this.flStatusUpdate(transactionDetails.getnDEL(), flmDel.getFlAddress(), "UTILIZED");
//			if (!tranxInterim.isEmpty()) {
//				log.info("Delivery FL Created");
//				TranxInterimData tranxInterimData = (TranxInterimData) tranxInterim.get();
//				tranxInterimData = this.util.updateDELFLDetails(flmDel, tranxInterimData);
//				this.tmpdb.save(tranxInterimData);
//			}
//		} catch (Exception var6) {
//			var6.printStackTrace();
//		}
//
//		return flmDel;
		Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(transactionDetails.getTransactionId());
		FLAddress flmDel = new FLAddress();

		try {
			FLInput flInput = new FLInput();
			FutureDatedTx futureTxObj = new FutureDatedTx();
//			if (transactionDetails.getFlId() == null) {
			if (transactionDetails.getSubType().equalsIgnoreCase("Reversal")) {
				futureTxObj = this.util.findPayRecID(transactionDetails.getShortTransferId());
				if (futureTxObj.getFDPeriodId() != null && !futureTxObj.getFDPeriodId().isEmpty()) {
					String flAddress = this.util.findFLAddRec(futureTxObj.getTxid());
					flmDel.setFlAddress(flAddress);
					this.util.updateClosedFutureTxDetails(transactionDetails.getShortTransferId());
				} else {
					flmDel = this.getFLAdress(this.util.getFLInputDel(transactionDetails, flInput), transactionDetails);
					this.flStatusUpdate(transactionDetails.getFDPeriodId(), transactionDetails.getnDEL(),
							flmDel.getFlAddress(), "UTILIZED", transactionDetails.getAssetLotId(),transactionDetails.getPeriod());
				}
			} else {
				flmDel = this.getFLAdress(this.util.getFLInputDel(transactionDetails, flInput), transactionDetails);
				this.flStatusUpdate(transactionDetails.getFDPeriodId(), transactionDetails.getnDEL(),
						flmDel.getFlAddress(), "UTILIZED", transactionDetails.getAssetLotId(),transactionDetails.getPeriod());
			}

//			flmDel = this.getFLAdress(this.util.getFLInputDel(transactionDetails, flInput), transactionDetails);
//			if (count == 1) {
//				this.tranManagerImpl.flStatusUpdate(transactionDetails.getnDEL(), flmDel.getFlAddress(), "CONFIRMED");
//				this.tx_outbound.updateConfirmFLAddressPassiveNodes(tranxInterim.get(), flmDel.getFlAddress(),
//						"CONFIRMED", tranxInterim.get().getnDEL());
//			} else {
//				this.tranManagerImpl.flStatusUpdate(transactionDetails.getnDEL(), flmDel.getFlAddress(), "UTILIZED");
//			}
//				this.flStatusUpdate(transactionDetails.getnDEL(), flmDel.getFlAddress(), "UTILIZED");
//			} else {
//				flmDel.setFlID(transactionDetails.getFlId());
//			}
//			if (!tranxInterim.isEmpty()) {
//				log.info("Delivery FL Created");
//				TranxInterimData tranxInterimData = (TranxInterimData) tranxInterim.get();
//				tranxInterimData = this.util.updateDELFLDetails(flmDel, tranxInterimData);
//				this.tmpdb.save(tranxInterimData);
//			}
			if (flmDel.getFlAddress() != null) {
				if (tranxInterim.isEmpty()) {
					log.info("NEW TRANSACTION in Delivery Node");
					TranxInterimData tranxInterimData = this.util.updateTransactionDetails(transactionDetails,
							new TranxInterimData());
					log.info("Delivery FL Created");
					tranxInterimData = this.util.updateDELFLDetails(flmDel, tranxInterimData);
					this.tmpdb.save(tranxInterimData);
					log.info("Transaction Details of ID: " + transactionDetails.getTransactionId()
							+ " stored in DatBase");
					if (tranxInterimData.getFDPeriodId() != null && !tranxInterimData.getFDPeriodId().isEmpty()) {
						if (!periodmap.containsKey(tranxInterimData.getFDPeriodId())) {
							log.info("Sending confirmation for Future tx of period: " + tranxInterimData.getFDPeriodId()
									+ "");
							this.tranManagerImpl.flStatusUpdate(transactionDetails.getFDPeriodId(),
									transactionDetails.getnDEL(), flmDel.getFlAddress(), "CONFIRMED",
									transactionDetails.getAssetLotId(),transactionDetails.getPeriod());
							periodmap.put(tranxInterimData.getFDPeriodId(), count);
//					this.tx_outbound.updateConfirmFLAddressPassiveNodes(tranxInterimData, flmDel.getFlAddress(),
//							"CONFIRMED", tranxInterimData.getnDEL());

						} else {

							this.tranManagerImpl.flStatusUpdate(transactionDetails.getFDPeriodId(),
									transactionDetails.getnDEL(), flmDel.getFlAddress(), "UTILIZED",
									transactionDetails.getAssetLotId(),transactionDetails.getPeriod());
						}
					} else if (count == 1) {
						this.tranManagerImpl.flStatusUpdate(transactionDetails.getFDPeriodId(),
								transactionDetails.getnDEL(), flmDel.getFlAddress(), "CONFIRMED",
								transactionDetails.getAssetLotId(),transactionDetails.getPeriod());
					} else {
						this.tranManagerImpl.flStatusUpdate(transactionDetails.getFDPeriodId(),
								transactionDetails.getnDEL(), flmDel.getFlAddress(), "UTILIZED",
								transactionDetails.getAssetLotId(),transactionDetails.getPeriod());
						count++;
					}
				} else {
					log.debug(transactionDetails.getTransactionId() + " Transaction is already exists in DB");
				}
			} else {
				throw new ApplicationException(
						"FLAM ERROR: Dropping Transaction due to  FL Address Del is null from Flam");
			}
		} catch (Exception var6) {
			var6.printStackTrace();
		}

		return flmDel;
	}

	public TransactionDetails getALD(TransactionDetails transactionData) throws Exception {
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(transactionData.getnDEL());
		TransactionDetails ald = new TransactionDetails();

		try {
			if (transactionData.getSubType().equalsIgnoreCase("Obligation")
					|| transactionData.getSubType().equalsIgnoreCase("Loan")
					|| transactionData.getSubType().equalsIgnoreCase("Collateral")) {
				ald = this.urlBuilder.getTransaction(
						"http://" + ((NodeDetails) nodeOptional.get()).getDnsname() + ":8082/asset/payreceivablelots",
						this.objectMapper.writeValueAsString(transactionData).getBytes());
				this.util.updateFutureTxDetails(transactionData.getUseCase(), transactionData.getTransactionId(),
						transactionData.getShortTransferId(), transactionData.getnDEL(), transactionData.getnREC(),
						transactionData.getContingentId(), transactionData.getFDPeriodId());
			} else {
				ald = this.urlBuilder.getTransaction(
						"http://" + ((NodeDetails) nodeOptional.get()).getDnsname() + ":8082/asset/assetlot",
						this.objectMapper.writeValueAsString(transactionData).getBytes());
			}
			if(ald.getAssetLotId()== null  ) {
				log.info("Getting assetlot id null from AM :"+ald +"Transaction Details"+transactionData.toString());
				//this.errorTransactionObj.createDelErrorContraTx(transactionDetails);
				throw new ApplicationException(
						"Dropping Transaction due to Assetlot id  is null");
				
			}
		} catch (JsonProcessingException var5) {
			var5.printStackTrace();
		}

		return ald;
	}

	public void processTransaction(TransactionDetails transactionDetails) throws Exception {
//		String txStartTime=localDateTime.getDateTime();
		String alr = "";
		Timestamp txStartTime = localDateTime.getTimeStamp();
		FLAddress flmDel = new FLAddress();
		log.info("Transaction started at " + txStartTime);
		try {
			transactionDetails.setTimeStamp(txStartTime);
			String errorAssetLotId = transactionDetails.getAssetLotId();
			if (transactionDetails.getAssetLotId() == null
					&& transactionDetails.getSubType().equalsIgnoreCase("Reversal")) {
				String ald = this.internalShortTransferImpl.getALD(transactionDetails);
				transactionDetails.setAssetLotId(ald);
			} else if (transactionDetails.getAssetLotId() == null) {
				transactionDetails = this.getALD(transactionDetails);
				log.info("ALD IS :" + transactionDetails.getAssetLotId());
			} else {
//				transactionDetails.setFlId("5432E");
				log.debug("AssetLotID: " + errorAssetLotId + " available in trnsaction generator");
			}
			log.info("Started Process with transaction details {}", transactionDetails.toString());
			Optional<NodeDetails> nodeOptionaldel = this.nodeStore.findById(this.transactionNodeInfo.getNodeid());
			String ipDel = ((NodeDetails) nodeOptionaldel.get()).getDnsname();
			Optional<NodeDetails> nodeOptionalrec = this.nodeStore.findById(transactionDetails.getnREC());
			String iprec = ((NodeDetails) nodeOptionalrec.get()).getDnsname();

			if (transactionDetails.getFlId() == null) {
				flmDel = this.updateFLDel(transactionDetails);
				transactionDetails.setFlId("empty");
			} else {
				log.debug("FlAddress: " + transactionDetails.getFlId() + " available in trnsaction generator");
				flmDel.setFlAddress(transactionDetails.getFlId());
			}
//			if (flmDel.getFlAddress() == null) {
//				flmDel.setFlAddress("");
//			}
			FLAddress flmRec = this.intraTransactionRecImpl.createTemp(transactionDetails,
					transactionDetails.getTransactionId() + "i", transactionDetails.getFlId());
			if (flmRec.getFlAddress() == null && flmRec.getFlag().equalsIgnoreCase("Failed")) {
				this.errorTransactionObj.createDelErrorContraTx(transactionDetails);
				throw new ApplicationException("Received FL address is null");
			} else {
				this.newTransaction(transactionDetails, flmRec);
				Optional<TranxInterimData> tranxInterimHash = this.tmpdb
						.findById(transactionDetails.getTransactionId());
				TranxInterimData tranxInterimData = new TranxInterimData();
				tranxInterimData = (TranxInterimData) tranxInterimHash.get();
				if (transactionDetails.getAssetLotId() == null
						&& transactionDetails.getSubType().equalsIgnoreCase("Reversal")) {
					alr = this.intraTransactionRecImpl.createzeroDownALR(flmDel,
							transactionDetails.getTransactionId() + "i");
				} else {
					alr = this.intraTransactionRecImpl.createALR(flmDel, transactionDetails.getTransactionId() + "i");
				}
				if (alr != null && errorAssetLotId == null) {
					log.info("Received ALR ->{}", alr);
					tranxInterimData.setAlr(alr);
					this.tmpdb.save(tranxInterimData);
				} else {
					String ErrorFlStatus = this.urlBuilder.getResponse(
							"http://" + ipDel + ":8092/flam/updateErrorFLStatus",
							this.objectMapper
									.writeValueAsString(this.util.updatedErrorFL(flErrorObj, flmDel.getFlAddress()))
									.getBytes());
					log.debug("Update status of Fl address in error Transaction: " + ErrorFlStatus);
					this.errorTransactionObj.createDelErrorContraTx(transactionDetails);
					throw new ApplicationException("AssetLot Id Invalid from Receival Node");
				}

				String txDelHash;
				TransactionDetails alnData;
				if ((!transactionDetails.getSubType().equalsIgnoreCase("Obligation")
						|| !transactionDetails.getSubType().equalsIgnoreCase("Loan")
						|| !transactionDetails.getSubType().equalsIgnoreCase("Collateral")
								&& !transactionDetails.getSubType().equalsIgnoreCase("Reversal"))
						&& transactionDetails.getQtyALD() > transactionDetails.getQtyALR()) {
					alnData = this.urlBuilder.getTransaction("http://" + ipDel + ":8082/asset/assetlotALN",
							this.objectMapper.writeValueAsString(transactionDetails).getBytes());
					if (tranxInterimData.getTransactionId() != null) {
						log.info("Created ALN ->{}", alnData.getAssetLotId());
						tranxInterimData.setAln(alnData.getAssetLotId());

						this.tmpdb.save(tranxInterimData);
					}

					boolean alnResponse = this.intraTransactionRecImpl.shareALN(alnData.getAssetLotId(),
							transactionDetails.getTransactionId() + "i");
					if (!alnResponse) {
						throw new Exception(" ALN not shared");
					}
				}

				txDelHash = this.edt.applySha256(this.util.encrptyDel((TranxInterimData) tranxInterimHash.get()));
				Timestamp txValidateStartTime = localDateTime.getTimeStamp();
				String txRecHash = this.intraTransactionRecImpl.getTxHash(txDelHash,
						transactionDetails.getTransactionId() + "i");
				if (txRecHash != null && txDelHash != null) {
					log.info("Transaction Hash is created");
					tranxInterimData.setTranxHash(txDelHash);
					tranxInterimData.setRecTranxHash(txRecHash);
					this.tmpdb.save(tranxInterimData);
				}

				String delencrptedHash = this.encrypt(this.util.encrptyDel((TranxInterimData) tranxInterimHash.get()),
						transactionDetails.getTransactionId(), transactionDetails.getTxDEL());
				log.info("Delivery Encrypted# is created--{}", delencrptedHash);
				String recencrptedHash = this.intraTransactionRecImpl.getEncryptedHash(delencrptedHash,
						transactionDetails.getTransactionId() + "i");
				Optional<KeyEntities> keyentity = this.keystore.findById(transactionDetails.getTxREC());
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
							transactionDetails.getTransactionId(), transactionDetails.getTxDEL());
					ContraTransactionDelDetails contra_txDel = this.util.createDelContraTransaction(tranxInterimData,
							transactionDetails.getnDEL());
					log.info(contra_txDel);
					if (digisign.length == 0) {
						log.error("\"DigiSign is null\"");
						throw new Exception("DigiSign is null");
					} else {
						log.info("Delivery Digi-sign is created");
						tranxInterimData.setDegiSign(digisign);
						this.tmpdb.save(tranxInterimData);
						byte[] recdigiSign = this.intraTransactionRecImpl.getDigiSign(digisign,
								transactionDetails.getTransactionId() + "i");
						Optional<KeyEntities> keyentityDig = this.keystore.findById(transactionDetails.getTxREC());
						if (!keyentityDig.isEmpty()) {
							if (this.verifySignature.verifySignature(
									this.util.encrptyRec((TranxInterimData) tranxInterimHash.get()), recdigiSign,
									((KeyEntities) keyentityDig.get()).getPublikKey())) {
								log.info("Verifyed DigiSign successfully");
								tranxInterimData.setRecDegiSign(recdigiSign);
								this.tmpdb.save(tranxInterimData);
								contra_txDel.setContramatchStatus(true);
							} else {
//									String txEndTime=localDateTime.getDateTime();
								Timestamp txEndTime = localDateTime.getTimeStamp();
								contra_txDel.setContramatchStatus(false);
								log.info("Transaction Ended at " + txEndTime);
								this.tranManagerImpl.calculateTxProcessingTime(transactionDetails.getTransactionId(),
										txStartTime, txEndTime);
								throw new Exception("Digital Signature not matched");
							}
							Timestamp txValidationEndTime = localDateTime.getTimeStamp();
							this.util.ledgerProcTime(transactionDetails.getTransactionId(), txValidateStartTime,
									txValidationEndTime);
							log.info("Digi-sign is received and verified Successfully");
							if (transactionDetails.getSubType().equalsIgnoreCase("Settlement")) {
								this.internalShortTransferImpl
										.processTransaction(this.util.nodeSwitch(transactionDetails));
							}
							this.contraTxDelRepo.save(contra_txDel);
							log.info("Deliver Contra Transaction Created Successfully");

							if (count == 1 && (transactionDetails.getFDPeriodId() == null
									|| transactionDetails.getFDPeriodId().equals(""))) {
								this.tranManagerImpl.flStatusUpdate(transactionDetails.getFDPeriodId(),
										transactionDetails.getnDEL(), flmDel.getFlAddress(), "LINKED",
										transactionDetails.getAssetLotId(),transactionDetails.getPeriod());
							} else {
								this.tranManagerImpl.flStatusUpdate(transactionDetails.getFDPeriodId(),
										transactionDetails.getnDEL(), flmDel.getFlAddress(), "CONFIRMED",
										transactionDetails.getAssetLotId(),transactionDetails.getPeriod());
								if (transactionDetails.getFDPeriodId() == null
										|| transactionDetails.getFDPeriodId().equals("")) {
									this.tranManagerImpl.flStatusUpdate(transactionDetails.getFDPeriodId(),
											transactionDetails.getnDEL(), flmDel.getFlAddress(), "LINKED",
											transactionDetails.getAssetLotId(),transactionDetails.getPeriod());
									count++;
								}
//								this.tx_outbound.updateConfirmFLAddressPassiveNodes(tranxInterimData,
//										flmDel.getFlAddress(), "CONFIRMED", tranxInterimData.getnDEL());
							}
//							this.flStatusUpdate(transactionDetails.getnDEL(), flmDel.getFlAddress(), "CONFIRMED");
							// Updating Confirmed FLAddress to Passive Nodes
//							this.tx_outbound.updateConfirmFLAddressPassiveNodes(tranxInterimData, flmDel.getFlAddress(),
//									"CONFIRMED", tranxInterimData.getnDEL());
//							this.flStatusUpdate(transactionDetails.getnDEL(), flmDel.getFlAddress(), "LINKED");

//							String status = this.urlBuilder
//									.getResponse("http://" + iprec + ":8092/flam/shareInfo",
//											this.objectMapper
//													.writeValueAsString(
//															new ShareNodeInfoDto(transactionDetails.getnDEL(),
//																	flmDel.getFlAddress(), flmDel.getFlaHashLink()))
//													.getBytes());
//							log.info("Linked Status Updation with Status: " + status);
							log.info("******************************************************");
							log.info("Transaction is completed successfully and saved data in database");
							log.info(((TranxInterimData) tranxInterimHash.get()).toString());
//								String txEndTime = localDateTime.getDateTime();
							Timestamp txEndTime = localDateTime.getTimeStamp();
							log.info("Transaction Ended at " + txEndTime);
//							newTranxProcessInfo.setContramtachstatus(true);
							this.tranManagerImpl.calculateTxProcessingTime(transactionDetails.getTransactionId(),
									txStartTime, txEndTime);
							// Update COLR Data
							this.util.storeCOLRData(tranxInterimData);
							log.info("******************************************************");
							this.tranManagerImpl.journalPairs(transactionDetails);
							// MutiCast DEL ContraTransaction

							log.info(contra_txDel);
							String staus = this.urlBuilder.getResponse(
									"http://" + "localhost" + ":7442/LComm/TxMulticasting/contraTxDel",
									this.objectMapper.writeValueAsString(contra_txDel).getBytes());
							log.info("MultiCast Status: ");
						} else {
							throw new Exception("KeyEntity doesnot Exists");
						}
					}
					tranxInterimData = null;
				} else {
					throw new Exception("KeyEntity doesnot Exists");
				}
			}

		} catch (Exception exception) {
			log.error(exception + "Exception occurred while Processing Transaction Request");
		}
	}
}
