package com.l4s.transactionmanager.process;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.sql.Timestamp;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
import com.l4s.transactionmanager.dao.FuturedatedTxsRepo;
import com.l4s.transactionmanager.dao.KeyStore;
import com.l4s.transactionmanager.dao.NodeStore;
import com.l4s.transactionmanager.dao.PublicKeyStore;
import com.l4s.transactionmanager.dao.TempDb;
import com.l4s.transactionmanager.dao.TransactionNodeInfo;
import com.l4s.transactionmanager.dao.TransactionProcess;
import com.l4s.transactionmanager.dao.UtilityData;
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
import com.l4s.transactionmanager.dto.NewAssetsDetails;
import com.l4s.transactionmanager.dto.NodeDetails;
import com.l4s.transactionmanager.dto.PublicKeyEntities;
import com.l4s.transactionmanager.dto.ShareNodeInfoDto;
import com.l4s.transactionmanager.dto.TransactionDetails;
import com.l4s.transactionmanager.dto.TransactionProcessDetails;
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

@Service
public class TransactionManagerImpl implements TransactionManager {
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
	ShortTransferImpl shortTransferImpl;
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
	@Autowired
	TransactionNodeInfo transactionNodeInfo;
	@Autowired
	TransactionProcess transactionProcess;
	@Autowired
	TransactionOutbound tx_outbound;
	private static Logger log = LogManager.getLogger(TransactionManagerImpl.class);
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	ContraTransactionDel contraTxDelRepo;
	@Autowired
	ContraTransactionRec contraTxRecRepo;
	@Autowired
	EncryptedHashShareData encryptedHashObj;
	@Autowired
	DigiSignature digiSignObj;
	@Autowired
	LocalDateTimes localDateTime;
	@Autowired
	ErrorTransactions errorTransactionObj;
	@Autowired
	FLAddressErrorUpdateDto flErrorObj;

	int count = 1;

	public FLAddress getDelFLAdress(FLInput flInput, TransactionDetails transdet) throws Exception {
		FLAddress fl = new FLAddress();
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(flInput.nodeId);
		log.info("Calling FL Address URI ");
		// del side ip
		FLMAddress flm = this.urlBuilder.getFLUpdated(
				"http://" + ((NodeDetails) nodeOptional.get()).getDnsname() + ":8092/flam/requestFlAddr",
				this.objectMapper.writeValueAsString(flInput).getBytes());
		log.trace("Requesting URI :" + "http://" + ((NodeDetails) nodeOptional.get()).getDnsname()
				+ ":8092/flam/requestFlAddr" + "for FL Address with details : "
				+ this.objectMapper.writeValueAsString(flInput));
		if (flm != null && flm.getFlAddress() == null && flm.getErrorMsg() != null
				&& flm.getFlag().equalsIgnoreCase("Failed")) {
			this.errorTransactionObj.storeDelErrorTransaction(flm.getErrorMsg(), transdet);
			throw new ApplicationException(" Delivery FL address is null");
		}
		log.info("FLM Address Created--{}", this.objectMapper.writeValueAsString(flm));
		return this.util.updatedFL(fl, flm);
	}

	public void flStatusUpdate(String fdPeriodID, String nodeId, String fLAddress, String status, String ALD,
			String periodId) throws Exception {
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(nodeId);
		log.info("Requesting URI :" + "http://" + ((NodeDetails) nodeOptional.get()).getDnsname()
				+ ":8092/flam/updateStatusActiveNode" + "to update FL Address: " + fLAddress + ", status: " + status
				+ " , Node: " + nodeId + " and fdperiod: " + fdPeriodID + "periodId: " + periodId);

		this.urlBuilder.getResponse(
				"http://" + ((NodeDetails) nodeOptional.get()).getDnsname() + ":8092/flam/updateStatusActiveNode",
				this.objectMapper
						.writeValueAsString(new UpdateFLStatus(nodeId, fLAddress, status, fdPeriodID, ALD, periodId))
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
		log.info("Executing " + procedure + " for Journal Pairs with Tx ID " + id);
		StoredProcedureQuery query = this.entityManager.createStoredProcedureQuery(procedure);
		query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
		query.setParameter(1, id);
		query.execute();
//		Timestamp endTime = localDateTime.getTimeStamp();
//		this.util.ledgerProcTime(TxnId, startTime, endTime);
//		log.debug("Ledger Process Completed");

	}

	public void createAssetAccount(NewAssetsDetails trans) throws Exception {
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("NewAssetLotAccountsProc");
		query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
		query.setParameter(1, trans.getAsset_type());
		query.execute();
	}

	public String shareDigiSign(String Txid, String ip) throws Exception {
		log.info("calling lcom shareDigiSign with " + Txid + " ," + digiSignObj.toString());

		Callable<String> sendDelDataToDB = () -> this.urlBuilder.getResponse(
				"http://" + "localhost" + ":7442/LComm/shareDigisign/" + Txid,
				this.objectMapper.writeValueAsString(digiSignObj).getBytes());
		Future<String> object = Executors.newCachedThreadPool().submit(sendDelDataToDB);
		log.info("Rec digisign from lcom " + sendDelDataToDB);
//		String recdigiSign = this.urlBuilder.getResponse("http://" + ip + ":8080/shareDigisign/" + Txid,
//				this.objectMapper.writeValueAsString(digiSignObj).getBytes());
		return object.get();

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
//			System.out.println(data+"Data Size: ------"+hash.getBytes().length);
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
		Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(transactionDetails.getTransactionId());
		FLAddress flmDel = new FLAddress();
		FutureDatedTx futureTxObj = new FutureDatedTx();
		try {
			FLInput flInput = new FLInput();
			if (transactionDetails.getFlId() == null) {
				if (transactionDetails.getSubType().equalsIgnoreCase("Reversal")) {
					futureTxObj = this.util.findPayRecID(transactionDetails.getShortTransferId());
					
					if (futureTxObj.getFDPeriodId() != null && !futureTxObj.getFDPeriodId().isEmpty()) {
						String flAddress = this.util.findFLAddRec(futureTxObj.getTxid());
						flmDel.setFlAddress(flAddress);
						this.util.updateClosedFutureTxDetails(transactionDetails.getShortTransferId());
					} else {
						flmDel = this.getDelFLAdress(this.util.getFLInputDel(transactionDetails, flInput),
								transactionDetails);
						this.flStatusUpdate(transactionDetails.getFDPeriodId(), transactionDetails.getnDEL(),
								flmDel.getFlAddress(), "UTILIZED", transactionDetails.getAssetLotId(),
								transactionDetails.getPeriod());
					}

				} else {
					flmDel = this.getDelFLAdress(this.util.getFLInputDel(transactionDetails, flInput),
							transactionDetails);
//					if (transactionDetails.getFDPeriodId() != null && !transactionDetails.getFDPeriodId().isEmpty()) {
//
//						if (count == 1) {
//							this.flStatusUpdate(transactionDetails.getFDPeriodId(), transactionDetails.getnDEL(),
//									flmDel.getFlAddress(), "CONFIRMED", transactionDetails.getAssetLotId(),
//									transactionDetails.getPeriod());
//							count++;
//						}
//						 else {
//								this.flStatusUpdate(transactionDetails.getFDPeriodId(), transactionDetails.getnDEL(),
//										flmDel.getFlAddress(), "UTILIZED", transactionDetails.getAssetLotId(),
//										transactionDetails.getPeriod());
//							}
//
//					} 
					
						this.flStatusUpdate(transactionDetails.getFDPeriodId(), transactionDetails.getnDEL(),
								flmDel.getFlAddress(), "UTILIZED", transactionDetails.getAssetLotId(),
								transactionDetails.getPeriod());
//					
				}

			} else {
				flmDel.setFlID(transactionDetails.getFlId());
			}
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
		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(this.transactionNodeInfo.getNodeid());
		TransactionDetails ald = new TransactionDetails();
		log.info("Del Node Ip: " + nodeOptional.get().getDnsname());

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
		} catch (NoSuchElementException var5) {
			var5.printStackTrace();
		}

		return ald;
	}

	public void journalPairs(TransactionDetails transactionDetails) throws Exception {

		if (transactionDetails.getSubType().equalsIgnoreCase("AirLockMarket")) {
			this.updateLedger(transactionDetails.getTransactionId(), "MarketJournalPairProc");
		} else if (transactionDetails.getSubType().equalsIgnoreCase("AirLockMarketOut")) {
			this.updateLedger(transactionDetails.getTransactionId(), "MarketOutJournalPairProc");
		} else if (transactionDetails.getContingentId() != null
				&& transactionDetails.getnDEL().equalsIgnoreCase(transactionDetails.getnREC())
				&& transactionDetails.getSubType().equalsIgnoreCase("Transfer")) {
			this.updateLedger(transactionDetails.getTransactionId(), "ContingentIntraTransferJournalPairProc");
		} else if (transactionDetails.getnDEL().equalsIgnoreCase(transactionDetails.getnREC())
				&& transactionDetails.getSubType().equalsIgnoreCase("Transfer")) {
			this.updateLedger(transactionDetails.getTransactionId(), "InternalTransferJournalPairProc");
		} else if (transactionDetails.getContingentId() != null
				&& transactionDetails.getSubType().equalsIgnoreCase("Transfer")) {
			this.updateLedger(transactionDetails.getContingentId(), "ContingentJournalPairProc");
		} else if (transactionDetails.getContingentId() != null
				&& transactionDetails.getnDEL().equalsIgnoreCase(transactionDetails.getnREC())
				&& (transactionDetails.getShortTransferId() != null
						&& (transactionDetails.getSubType().equalsIgnoreCase("Obligation")
								|| transactionDetails.getSubType().equalsIgnoreCase("Loan")
								|| transactionDetails.getSubType().equalsIgnoreCase("Collateral")))) {
			this.updateLedger(transactionDetails.getTransactionId(), "IntraShortContingentTransferJournalPairProc");
		} else if (transactionDetails.getContingentId() != null && transactionDetails.getShortTransferId() != null
				&& (transactionDetails.getSubType().equalsIgnoreCase("Obligation")
						|| transactionDetails.getSubType().equalsIgnoreCase("Loan")
						|| transactionDetails.getSubType().equalsIgnoreCase("Collateral"))) {
			this.updateLedger(transactionDetails.getContingentId(), "ContingentShortTransferJournalPairProc");
		} else if (transactionDetails.getnDEL().equalsIgnoreCase(transactionDetails.getnREC())
				&& transactionDetails.getShortTransferId() != null
				&& (transactionDetails.getSubType().equalsIgnoreCase("Obligation")
						|| transactionDetails.getSubType().equalsIgnoreCase("Loan")
						|| transactionDetails.getSubType().equalsIgnoreCase("Collateral"))) {
			this.updateLedger(transactionDetails.getTransactionId(), "IntraShortTransferJournalPairProc");
		} else if (transactionDetails.getSubType().equalsIgnoreCase("Obligation")
				|| transactionDetails.getSubType().equalsIgnoreCase("Loan")
				|| transactionDetails.getSubType().equalsIgnoreCase("Collateral")
						&& transactionDetails.getShortTransferId() != null) {
			this.updateLedger(transactionDetails.getTransactionId(), "ShortTransferJournalPairProc");
		} else if (transactionDetails.getContingentId() != null && transactionDetails.getShortTransferId() != null
				&& transactionDetails.getnDEL().equalsIgnoreCase(transactionDetails.getnREC())
				&& transactionDetails.getSubType().equalsIgnoreCase("Settlement")) {
			this.updateLedger(transactionDetails.getTransactionId(), "IntraShortContingentTransferFillJournalPairProc");
		} else if (transactionDetails.getnDEL().equalsIgnoreCase(transactionDetails.getnREC())
				&& transactionDetails.getShortTransferId() != null
				&& transactionDetails.getSubType().equalsIgnoreCase("Settlement")) {
			this.updateLedger(transactionDetails.getTransactionId(), "IntraShortTransferFillJournalPairProc");
		} else if (transactionDetails.getSubType().equalsIgnoreCase("Settlement")
				&& transactionDetails.getShortTransferId() != null && transactionDetails.getContingentId() != null) {
			this.updateLedger(transactionDetails.getContingentId(), "ContingentShortTransferFillJournalPairProc");
		} else if (transactionDetails.getSubType().equalsIgnoreCase("Settlement")
				&& transactionDetails.getShortTransferId() != null) {
			this.updateLedger(transactionDetails.getTransactionId(), "ShortTransferFillJournalPairProc");
		} else if (transactionDetails.getContingentId() != null && transactionDetails.getShortTransferId() != null
				&& transactionDetails.getnDEL().equalsIgnoreCase(transactionDetails.getnREC())
				&& transactionDetails.getSubType().equalsIgnoreCase("Reversal")) {
			this.updateLedger(transactionDetails.getTransactionId(), "IntraContingentPayRecReversalJournalPairProc");
		} else if (transactionDetails.getnDEL().equalsIgnoreCase(transactionDetails.getnREC())
				&& transactionDetails.getShortTransferId() != null
				&& transactionDetails.getSubType().equalsIgnoreCase("Reversal")) {
			this.updateLedger(transactionDetails.getTransactionId(), "IntraPayRecReversalJournalPairProc");
		} else if (transactionDetails.getSubType().equalsIgnoreCase("Reversal")
				&& transactionDetails.getShortTransferId() != null && transactionDetails.getContingentId() != null) {
			this.updateLedger(transactionDetails.getContingentId(), "ContingentPayRecReversalJournalPairProc");
		} else if (transactionDetails.getSubType().equalsIgnoreCase("Reversal")
				&& transactionDetails.getShortTransferId() != null) {
			this.updateLedger(transactionDetails.getTransactionId(), "PayRecReversalJournalPairProc");
		} else if (transactionDetails.getSubType().equalsIgnoreCase("Transfer")) {
			this.updateLedger(transactionDetails.getTransactionId(), "JournalPairProc");
		}

		log.info("Journal Pairs are created successfully");
	}

	public void processTransaction(TransactionDetails transactionDetails) {
//		String txStartTime=localDateTime.getDateTime();
//		Date txStartTime = new Date();
		String alr = "";
		Timestamp txStartTime = localDateTime.getTimeStamp();
		FLAddress flmDel = new FLAddress();
		log.info("Transaction started at " + txStartTime);
		try {
//			transactionData.setTimeStamp(Timestamp.valueOf(localDateTime.getDateTime()));
			transactionDetails.setTimeStamp(txStartTime);
//			if (transactionData.getTxType() == "Short Transfer Fill"
//					|| transactionData.getTxType() == "Short Transfer") {
//				transactionData.setShortTransferId(transactionData.getTxDEL() + transactionData.getAtype()
//						+ transactionData.getQtyALR() + transactionData.getTxREC());
//			}
			log.info("Started Process with transaction details {}" + transactionDetails.toString());
//			log.info("Sending Data to Asset Manager for ALD:");
			String errorAssetLotId = transactionDetails.getAssetLotId();
			if (transactionDetails.getAssetLotId() == null
					&& transactionDetails.getSubType().equalsIgnoreCase("Reversal")) {
				transactionDetails = this.shortTransferImpl.getALD(transactionDetails);
				
				
			} else if (transactionDetails.getAssetLotId() == null) {
				transactionDetails = this.getALD(transactionDetails);
			
//				transactionDetails.setFlId("5432E");
				log.debug("AssetLotID: " + errorAssetLotId + " available in trnsaction generator");
			} else {
//				transactionDetails.setFlId("5432E");
				log.debug("AssetLotID: " + errorAssetLotId + " available in trnsaction generator");
			}
			if(transactionDetails.getAssetLotId()== null && transactionDetails.getAssetLotId().isEmpty() ) {
				log.info("Getting assetlot id null from AM :"+ transactionDetails.getAssetLotId() +"Transaction Details"+transactionDetails.toString());
				//this.errorTransactionObj.createDelErrorContraTx(transactionDetails);
				throw new ApplicationException(
						"Dropping Transaction due to Assetlot id  is null ");
				
			}
//			TransactionDetails transactionDetails = this.getALD(transactionData);
			Optional<NodeDetails> nodeOptionaldel = this.nodeStore.findById(this.transactionNodeInfo.getNodeid());
			String ipDel = ((NodeDetails) nodeOptionaldel.get()).getDnsname();
			Optional<NodeDetails> nodeOptional = this.nodeStore.findById(transactionDetails.getnREC());
			String ip = ((NodeDetails) nodeOptional.get()).getDnsname();
			log.info("Transaction Delilvery Node: " + transactionDetails.getnDEL() + "and Deliver IP: " + ipDel);
//			log.info("Transaction Receival Node: " + transactionDetails.getnREC() + " and Receival Ip: " + ip);

//			FLAddress flmRec = this.urlBuilder.getFL("http://" + ip + ":8080/temp",
//					this.objectMapper.writeValueAsString(transactionDetails).getBytes());
			if (transactionDetails.getFlId() == null) {

				flmDel = this.updateFLDel(transactionDetails);

				transactionDetails.setFlId("empty");

			} else {
				log.debug("FlAddress: " + transactionDetails.getFlId() + " available in trnsaction generator");
				flmDel.setFlAddress(transactionDetails.getFlId());
			}
//			if (flmDel == null) {
//				throw new ApplicationException(" Delivery FL address is null");
//			} else {

			// ip to localhost
			log.info("calling LCOmm/temp api with " + transactionDetails.getFlId() + " and "
					+ transactionDetails.toString());
			FLAddress flmRec = this.urlBuilder.getFL(
					"http://" + "localhost" + ":7442/LComm/temp/" + transactionDetails.getFlId(),
					this.objectMapper.writeValueAsString(transactionDetails).getBytes());
			log.info("FLM details to Receival Node   LComm/temp/" + transactionDetails.toString() + "," + flmRec);
			if (flmRec.getFlAddress() == null && flmRec.getFlag().equalsIgnoreCase("Failed")) {
				this.errorTransactionObj.createDelErrorContraTx(transactionDetails);
				throw new ApplicationException(
						"Dropping Transaction due to Duplicate FL Address and Received FL address is null");
			} else {
				log.info("sending new Transaction");
				this.newTransaction(transactionDetails, flmRec);
				log.info("Receival FL Received");
				Optional<TranxInterimData> tranxInterimHash = this.tmpdb
						.findById(transactionDetails.getTransactionId());
				TranxInterimData tranxInterimData = new TranxInterimData();
				tranxInterimData = (TranxInterimData) tranxInterimHash.get();
				log.info("calling  alr LComm/getALR " + transactionDetails.getTransactionId()+" and "+transactionDetails.getnREC(), flmDel.toString());
						
				if (transactionDetails.getSubType().equalsIgnoreCase("Reversal")) {
					alr = this.urlBuilder.getResponse(
							"http://" + "localhost" + ":7442/LComm/getzeroDownALR/"
									+ transactionDetails.getTransactionId() + "/" + transactionDetails.getnREC(),
							this.objectMapper.writeValueAsString(flmDel).getBytes());
					log.info("reacive  alr getzeroDownALR" + alr);

				} else {
					alr = this.urlBuilder.getResponse(
							"http://" + "localhost" + ":7442/LComm/getALR/" + transactionDetails.getTransactionId()
									+ "/" + transactionDetails.getnREC(),
							this.objectMapper.writeValueAsString(flmDel).getBytes());
				}
				log.info("reacive  alr LComm/getALR" + alr);

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

				TransactionDetails alnData;
				String txDelHash = "";
				String alnResponse;
				if ((!transactionDetails.getSubType().equalsIgnoreCase("Obligation")
						|| !transactionDetails.getSubType().equalsIgnoreCase("Loan")
						|| !transactionDetails.getSubType().equalsIgnoreCase("Collateral")
								&& !transactionDetails.getSubType().equalsIgnoreCase("Reversal"))
						&& transactionDetails.getQtyALD() > transactionDetails.getQtyALR()) {
					alnData = this.urlBuilder.getTransaction("http://" + ipDel + ":8082/asset/assetlotALN",
							this.objectMapper.writeValueAsString(transactionDetails).getBytes());
					log.info("After receving assetlotALN " + alnData);
					if (tranxInterimData.getTransactionId() != null) {
						log.info("Created ALN ->{}", alnData.getAssetLotId());
						tranxInterimData.setAln(alnData.getAssetLotId());
						this.tmpdb.save(tranxInterimData);
					}

					alnResponse = this.urlBuilder.getResponse("http://" + "localhost" + ":7442/LComm/shareALN/"
							+ transactionDetails.getTransactionId() + "/" + transactionDetails.getnREC(),
							alnData.getAssetLotId().getBytes());
					log.info("After reacive alnResponse from shareALN" + alnResponse);
					if (alnResponse.equals("false")) {
						throw new Exception(" ALN not shared");
					}
				}

				txDelHash = this.edt.applySha256(this.util.encrptyDel((TranxInterimData) tranxInterimHash.get()));
				Timestamp txValidateStartTime = localDateTime.getTimeStamp();
				log.info("rec LComm/shareTxHash " + txDelHash);
				alnResponse = this.urlBuilder
						.getResponse(
								"http://" + "localhost" + ":7442/LComm/shareTxHash/"
										+ transactionDetails.getTransactionId() + "/" + transactionDetails.getnREC(),
								txDelHash.getBytes());
				log.info("rec LComm/shareTxHash " + alnResponse);
				if (alnResponse != null && txDelHash != null) {
					log.info("Transaction Hash is created");
					tranxInterimData.setTranxHash(txDelHash);
					tranxInterimData.setRecTranxHash(alnResponse);
					this.tmpdb.save(tranxInterimData);
				}
				Optional<KeyEntities> Delkeyentity = null;
				Delkeyentity = this.keystore.findById(transactionDetails.getTxDEL());
				String delencrptedHash = this.encrypt(this.util.encrptyDel((TranxInterimData) tranxInterimHash.get()),
						transactionDetails.getTransactionId(), transactionDetails.getTxDEL());
				log.info("Delivery Encrypted# is created");
				encryptedHashObj.setEncrptedhash(delencrptedHash);
				encryptedHashObj.setPublicKey(Delkeyentity.get().getPublikKey());
				encryptedHashObj.setnRec(transactionDetails.getnREC());
				log.info("calling LComm for getEncryptedHash ");
				EncryptedHashShareData recencrptedHash = this.urlBuilder.getEnryptedHash(
						"http://" + "localhost" + ":7442/LComm/getEncryptedHash" + "/"
								+ transactionDetails.getTransactionId(),
						this.objectMapper.writeValueAsString(encryptedHashObj).getBytes());
				log.info("return data from del lcom encrypted hash txmgr " + recencrptedHash);
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

					byte[] digisign = this.digisign(this.util.encrptyDel((TranxInterimData) tranxInterimHash.get()),
							transactionDetails.getTransactionId(), transactionDetails.getTxDEL());

					if (digisign.length == 0) {
						log.error("\"DigiSign is null\"");
						throw new Exception("DigiSign is null");
					} else {
						log.info("Delivery Digi-sign is created");
						tranxInterimData.setDegiSign(digisign);
						ContraTransactionDelDetails contra_txDel = this.util
								.createDelContraTransaction(tranxInterimData, transactionDetails.getnDEL());
						this.tmpdb.save(tranxInterimData);
						digiSignObj.setDigiSign(digisign);
						digiSignObj.setPublicKey(Delkeyentity.get().getPublikKey());
						digiSignObj.setNodeRec(tranxInterimHash.get().getnREC());

						String TxId = tranxInterimData.getTransactionId();
						String recdigiSign = this.shareDigiSign(TxId, "localhost");
//						Future<String> object = Executors.newCachedThreadPool().submit(sendDelDataToDB);
//							String recdigiSign = this.shareDigiSign(transactionDetails);

						ContraTransactionRecDetails contra_txRec = this.util
								.createRecContraTransaction(tranxInterimData, transactionDetails.getnREC());
//						String recdigiSign = object.get();
						if (!recdigiSign.equalsIgnoreCase("true")) {
//								String txEndTime = localDateTime.getDateTime();
							Timestamp txEndTime = localDateTime.getTimeStamp();
							log.info("Transaction Suspended at " + txEndTime);
							contra_txDel.setContramatchStatus(false);
							contra_txRec.setContramatchStatus(false);
							this.contraTxDelRepo.save(contra_txDel);
							this.contraTxRecRepo.save(contra_txRec);
							log.info("Contra Transactions Created Successfully");
							// Creation of ALD COLR
							this.util.storeALDCOLRData(tranxInterimData);
							this.calculateTxProcessingTime(transactionDetails.getTransactionId(), txStartTime,
									txEndTime);
							throw new Exception("Digital Signature not matched");
						} else {
							log.info("Digi-sign is received and verified Successfully");
							contra_txDel.setContramatchStatus(true);
							contra_txRec.setContramatchStatus(true);
							log.info(contra_txDel);
							this.contraTxDelRepo.save(contra_txDel);
							this.contraTxRecRepo.save(contra_txRec);
							log.info(contra_txDel);
							log.info("Contra Transactions Created Successfully");
							if (transactionDetails.getSubType().equalsIgnoreCase("Settlement")) {
								this.urlBuilder.getResponse("http://" + "localhost" + ":7442/LComm/stzerodown",
										this.objectMapper.writeValueAsString(transactionDetails).getBytes());
							}
							log.info("txmgr side /LComm/stzerodown");
							// contra_txDel.setContramatchStatus(true);
//							contra_txRec.setContramatchStatus(true);

//							this.contraTxDelRepo.save(contra_txDel);
//							this.contraTxRecRepo.save(contra_txRec);
//							log.info("Contra Transactions Created Successfully");
							Timestamp txValidationEndTime = localDateTime.getTimeStamp();
							this.util.ledgerProcTime(transactionDetails.getTransactionId(), txValidateStartTime,
									txValidationEndTime);
							this.flStatusUpdate(transactionDetails.getFDPeriodId(), transactionDetails.getnDEL(),
									flmDel.getFlAddress(), "CONFIRMED", transactionDetails.getAssetLotId(),
									transactionDetails.getPeriod());
							Optional<TranxInterimData> tranxInterimfin = this.tmpdb
									.findById(transactionDetails.getTransactionId());
							// Updating Confirmed FLAddress to Passive Nodes
//							this.tx_outbound.updateConfirmFLAddressPassiveNodes(tranxInterimfin.get(),
//									flmDel.getFlAddress(), "CONFIRMED", tranxInterimfin.get().getnDEL());
							this.urlBuilder.getResponse("http://" + ip + ":8092/flam/updateStatusPassiveNode",
									this.objectMapper
											.writeValueAsString(new UpdateFLStatus(tranxInterimfin.get().getnDEL(),
													flmDel.getFlAddress(), "CONFIRMED",
													transactionDetails.getFDPeriodId(),
													transactionDetails.getAssetLotId(), transactionDetails.getPeriod()))
											.getBytes());
							if (transactionDetails.getFDPeriodId() == null
									|| transactionDetails.getFDPeriodId().equals("")) {
								this.flStatusUpdate(transactionDetails.getFDPeriodId(), transactionDetails.getnDEL(),
										flmDel.getFlAddress(), "LINKED", transactionDetails.getAssetLotId(),
										transactionDetails.getPeriod());
								String status = this.urlBuilder
										.getResponse("http://" + ip + ":8092/flam/shareInfo",
												this.objectMapper
														.writeValueAsString(
																new ShareNodeInfoDto(transactionDetails.getnDEL(),
																		flmDel.getFlAddress(), flmDel.getFlaHashLink(),
																		transactionDetails.getFDPeriodId(),
																		transactionDetails.getAssetLotId()))
														.getBytes());

								log.info("Linked Status Updation with Comm/shareInfo\",: " + status);
							}
							this.urlBuilder.getResponse(
									"http://" + "localhost" + ":7442/LComm/journal" + "/"
											+ transactionDetails.getnREC(),
									transactionDetails.getTransactionId().getBytes());
							// Update COLR Data
							this.util.storeCOLRData(tranxInterimfin.get());
							this.urlBuilder.getResponse("http://" + "localhost" + ":7442/LComm/updateCLOR",
									this.objectMapper.writeValueAsString(tranxInterimfin.get()).getBytes());
							log.info("txmgr 7442/LComm/updateCLOR ");
							log.info("******************************************************");
							log.info("Transaction is completed successfully");
//								String txEndTime = localDateTime.getDateTime();
							Timestamp txEndTime = localDateTime.getTimeStamp();
							log.info("Transaction Ended at " + txEndTime);
							this.calculateTxProcessingTime(transactionDetails.getTransactionId(), txStartTime,
									txEndTime);
							this.log.info(((TranxInterimData) tranxInterimfin.get()).toString());
							log.info("******************************************************");
							this.journalPairs(transactionDetails);
							// MutiCast DEL ContraTransaction
							Optional<ContraTransactionDelDetails> broadCastContraTx = this.contraTxDelRepo
									.findById(transactionDetails.getTransactionId());
							if (!broadCastContraTx.isEmpty()) {
								log.info(broadCastContraTx.get());
								String staus = this.urlBuilder.getResponse(
										"http://" + "localhost" + ":7442/LComm/TxMulticasting/contraTxDel",
										this.objectMapper.writeValueAsString(broadCastContraTx.get()).getBytes());
								log.info("MultiCast Status: " + staus);
							} else {
								this.log.info("Contra Tx Del not Existed in DB " + broadCastContraTx.get().toString());
							}
//								localhost:7442/TxMulticasting/contraTx
						}
					}
					tranxInterimHash = null;
				} else {
					throw new Exception("KeyEntity doesnot Exists");
				}
			}
//			}
		} catch (Exception exception) {
			log.error(exception + "Exception occussred while Processing Transaction Request");
		}
	}

	public void calculateTxProcessingTime(String tx_ID, Timestamp start, Timestamp end) {
		try {
			long diffInmillSeconds = end.getTime() - start.getTime();
			log.info("Processing Time for transaction " + tx_ID + "in secs: " + diffInmillSeconds);
			Optional<TransactionProcessDetails> tranxProcessInfo = this.transactionProcess.findById(tx_ID);
			if (tranxProcessInfo.isEmpty()) {
				log.info("Storing new Transaction Prcessing Data into Database");
				TransactionProcessDetails newTranxProcessInfo = new TransactionProcessDetails();
				newTranxProcessInfo.setTransactionId(tx_ID);
				newTranxProcessInfo.setStartTime(start);
				newTranxProcessInfo.setEndTime(start);
//		         log.info("new Trnsaction Process Info: "+ newTranxProcessInfo.getEndTime());
//		         log.info("new Trnsaction Process Info: "+ newTranxProcessInfo.getTotalProcessTime());
//		         log.info("new Trnsaction Process Info: "+ newTranxProcessInfo.getTransactionId());
//		         log.info("new Trnsaction Process Info: "+ newTranxProcessInfo.getStartTime());
				log.info("Transaction Processing Data Insterted");
			} else {
				log.debug("Transaction is already exists in DB");
			}
		} catch (Exception e) {
			log.error("Error while calculating Tx pocessing time " + e);
		}
	}

	public void updateCOLRForFutureTx(String txid, String periodid) throws Exception {
//		Timestamp startTime = localDateTime.getTimeStamp();
//		log.info("Executing " + procedure + " for Journal Pairs with Tx ID " + id);
		StoredProcedureQuery query = this.entityManager.createStoredProcedureQuery("UpdateCOLRForFutureTX");
		query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
		query.setParameter(1, txid);
		query.setParameter(2, periodid);
		query.execute();
		this.entityManager.flush();
		this.entityManager.clear();
//		Timestamp endTime = localDateTime.getTimeStamp();
//		this.util.ledgerProcTime(TxnId, startTime, endTime);
//		log.debug("Ledger Process Completed");

	}
}
