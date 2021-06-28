package com.l4s.transactionmanager.service;

import java.sql.Timestamp;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.l4s.transactionmanager.dao.CompleteOwnerShipLotRepo;
import com.l4s.transactionmanager.dao.ContraTransactionDel;
import com.l4s.transactionmanager.dao.ContraTransactionRec;
import com.l4s.transactionmanager.dao.FuturedatedTxsRepo;
import com.l4s.transactionmanager.dao.TempDb;
import com.l4s.transactionmanager.dao.ValidationProcessRepo;
import com.l4s.transactionmanager.dto.CompleteOwnerShipLots;
import com.l4s.transactionmanager.dto.ContraTransactionDelDetails;
import com.l4s.transactionmanager.dto.ContraTransactionRecDetails;
import com.l4s.transactionmanager.dto.FutureDatedTx;
import com.l4s.transactionmanager.dto.TranxInterimData;
import com.l4s.transactionmanager.dto.ValidationProcessDetails;
import com.l4s.transactionmanager.process.TransactionManagerImpl;
import com.l4s.transactionmanager.process.UrlBuilder;

public class CarryForwardFutureTx {
	@Autowired
	FuturedatedTxsRepo futureTxRepo;
	@Autowired
	UrlBuilder urlBuilder;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	CompleteOwnerShipLotRepo objCOLRRepo;
	@Autowired
	BroadCastContraTxService contraTxDelServiceRepo;
//	@PersistenceContext
//	private EntityManager entityManager;
	@Autowired
	TransactionManagerImpl tranManagerImpl;
	@Autowired
	ContraTransactionDel contraTxDelRepo;
	@Autowired
	ContraTransactionRec contraTxRecRepo;
	@Autowired
	TempDb tmpdb;
	@Autowired
	ValidationProcessRepo validationProcRepo;

//	@Autowired
	private static Logger log = LogManager.getLogger(CarryForwardFutureTx.class);

	public void carryForwardTx(String periodid) {
		String transactionId = "";
		try {
//			List<String> txids = futureTxRepo.findByStatus("Open");
			for (FutureDatedTx futureTx : futureTxRepo.findByStatus("Open")) {
				transactionId = futureTx.getTxid();
//				String transactionId= futureTx.getnDel();
				log.info("processing Future Tx for :" + transactionId);
				this.updateperiodIdInDB(periodid, transactionId);
				String result = this.updateCOLRPeriodId(transactionId, periodid);
				log.info("COLR Update for Future Transaction successfully with result: " + result);
//				this.urlBuilder.getResponse("http://"+"localhost"+":8080/futureJouranalPair",
//						this.objectMapper.writeValueAsString(futureTx).getBytes());
				// this.createjournals(futureTx);
				log.info("Future Transaction processed successfully");
			}
		} catch (Exception e) {
			log.error(e + "occurred while processing Future Tx for :" + transactionId);
		}
	}

	public void updateperiodIdInDB(String periodId, String txId) {
		try {
			this.updatePeriodTransaction(periodId, txId);
			this.updatePeriodDelContraTransaction(periodId, txId);
			this.updatePeriodRecContraTransaction(periodId, txId);
			this.updatePeriodIDLedgerProcTime(txId, periodId);
			log.info("Future Transaction updated successfully in transaction tables");
		} catch (Exception e) {
			log.error(e + "occurred while update period id in DB for Future Tx");
		}
	}

//	public void createjournals(FutureDatedTx transactionDetails) {
//		try {
////			if (transactionDetails.getContingentId() != null
////					&& transactionDetails.getnDel().equalsIgnoreCase(transactionDetails.getnRec())
////					&& (transactionDetails.getPayrecid() != null)) {
////				this.updateLedger(transactionDetails.getTxid(), "IntraShortContingentTransferJournalPairProc");
////			} else if (transactionDetails.getContingentId() != null && transactionDetails.getPayrecid() != null) {
////				this.updateLedger(transactionDetails.getContingentId(), "ContingentShortTransferJournalPairProc");
////			} else 
//			if (transactionDetails.getnDel().equalsIgnoreCase(transactionDetails.getnRec())
//					&& transactionDetails.getPayrecid() != null) {
//				this.tranManagerImpl.updateLedger(transactionDetails.getTxid(), "IntraShortTransferJournalPairProc");
//			} else if (transactionDetails.getPayrecid() != null) {
//				this.tranManagerImpl.updateLedger(transactionDetails.getTxid(), "ShortTransferJournalPairProc");
//			}
//			log.info("Future Transaction journals created successfully");
//		} catch (Exception e) {
//			log.error(e + "occurred while creating journal entries for Future Tx");
//		}
//	}

//	public void updateLedger(String id, String procedure) throws Exception {
////		Timestamp startTime = localDateTime.getTimeStamp();
//		log.info("Executing " + procedure + " for Journal Pairs with Tx ID " + id);
//		StoredProcedureQuery query = this.entityManager.createStoredProcedureQuery(procedure);
//		query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
//		query.setParameter(1, id);
//		query.execute();
////		Timestamp endTime = localDateTime.getTimeStamp();
////		this.util.ledgerProcTime(TxnId, startTime, endTime);
////		log.debug("Ledger Process Completed");
//
//	}

//	public void updateCOLRForFutureTx(String txid, String periodid) throws Exception {
////		Timestamp startTime = localDateTime.getTimeStamp();
////		log.info("Executing " + procedure + " for Journal Pairs with Tx ID " + id);
//		StoredProcedureQuery query = this.entityManager.createStoredProcedureQuery("UpdateCOLRForFutureTX");
//		query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
//		query.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);
//		query.setParameter(1, txid);
//		query.setParameter(2, periodid);
//		query.execute();
////		Timestamp endTime = localDateTime.getTimeStamp();
////		this.util.ledgerProcTime(TxnId, startTime, endTime);
////		log.debug("Ledger Process Completed");
//
//	}
	public String updateCOLRPeriodId(String txid, String periodid) {
		String assetlotid = "";
		String ALR = "";
		String status = "";
		int result = 0;
		Optional<TranxInterimData> tranxdata = this.tmpdb.findById(txid);
		if (!tranxdata.isEmpty()) {
			assetlotid = tranxdata.get().getAssetLotId();
			this.updateCOLRPid(periodid, assetlotid);
			ALR = tranxdata.get().getAlr();
			this.updateCOLRPid(periodid, ALR);
			result = 1;
		}

		if (result != 0) {
			status = "success";
		}
		return status;
	}

	public void updatePeriodTransaction(String periodId, String txId) {
		try {
			Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(txId);
			if (!tranxInterim.isEmpty()) {
				tranxInterim.get().setPeriod(periodId);
				this.tmpdb.save(tranxInterim.get());
				log.info("Future Transaction updated successfully in Transact Table");
			} else {
				log.info("Future Transaction not existed in Transact Table");
			}
//			this.tmpdb.updatePeriodTransaction(periodId, txId);
//			this.contraTxDelRepo.updatePeriodDelContraTransaction(periodId, txId);
//			this.contraTxRecRepo.updatePeriodRecContraTransaction(periodId, txId);
			log.info("Future Transaction updated successfully");
		} catch (Exception e) {
			log.error(e + "occurred while update period id in Transact Table for Future Tx");
		}
	}

	public void updatePeriodDelContraTransaction(String periodId, String txId) {
		try {
			Optional<ContraTransactionDelDetails> tranxInterim = this.contraTxDelRepo.findById(txId);
			if (!tranxInterim.isEmpty()) {
				tranxInterim.get().setPostperiodfk(periodId);
				this.contraTxDelRepo.save(tranxInterim.get());
				log.info("Future Transaction updated successfully in Contra Tx Del Table");
			} else {
				log.info("Future Transaction not existed in Contra Tx Del Table");
			}
//			this.tmpdb.updatePeriodTransaction(periodId, txId);
//			this.contraTxDelRepo.updatePeriodDelContraTransaction(periodId, txId);
//			this.contraTxRecRepo.updatePeriodRecContraTransaction(periodId, txId);
			log.info("Future Transaction updated successfully");
		} catch (Exception e) {
			log.error(e + "occurred while update period id in Contra Tx Del Table for Future Tx");
		}
	}

	public void updatePeriodRecContraTransaction(String periodId, String txId) {
		try {
			Optional<ContraTransactionRecDetails> tranxInterim = this.contraTxRecRepo.findById(txId);
			if (!tranxInterim.isEmpty()) {
				tranxInterim.get().setPostperiodfk(periodId);
				this.contraTxRecRepo.save(tranxInterim.get());
				log.info("Future Transaction updated successfully in Contra Tx Rec Table");
			} else {
				log.info("Future Transaction not existed in Contra Tx Rec Table");
			}
//			this.tmpdb.updatePeriodTransaction(periodId, txId);
//			this.contraTxDelRepo.updatePeriodDelContraTransaction(periodId, txId);
//			this.contraTxRecRepo.updatePeriodRecContraTransaction(periodId, txId);
//			log.info("Future Transaction updated successfully");
		} catch (Exception e) {
			log.error(e + "occurred while update period id in Contra Tx Rec Table for Future Tx");
		}
	}

	public void updateCOLRPid(String periodId, String assetlotId) {
		try {
			Optional<CompleteOwnerShipLots> tranxInterim = this.objCOLRRepo.findById(assetlotId);
			if (!tranxInterim.isEmpty()) {
				tranxInterim.get().setPeriodId(periodId);
				this.objCOLRRepo.save(tranxInterim.get());
				log.info("Future Transaction updated successfully in COLR Table");
			} else {
				log.info("Future Transaction not existed in COLR Table");
			}
//			this.tmpdb.updatePeriodTransaction(periodId, txId);
//			this.contraTxDelRepo.updatePeriodDelContraTransaction(periodId, txId);
//			this.contraTxRecRepo.updatePeriodRecContraTransaction(periodId, txId);
//			log.info("Future Transaction updated successfully");
		} catch (Exception e) {
			log.error(e + "occurred while update period id in COLR Table for Future Tx");
		}
	}

	public void updatePeriodIDLedgerProcTime(String tx_ID, String period_id) {
		try {
//			long diffInmillSeconds = end.getTime() - start.getTime();
//			log.info("Processing Time for transaction " + tx_ID + "in secs: " + diffInmillSeconds);
			Optional<ValidationProcessDetails> validationProcessInfo = this.validationProcRepo.findById(tx_ID);
			if (!validationProcessInfo.isEmpty()) {
				log.info("Updating period id for validaion Prcessing Data into Database");
				validationProcessInfo.get().setTransactionId(tx_ID);

				validationProcessInfo.get().setPeriod_id(period_id);
//				ledgerProcessTIme.setTotalProcessTime(diffInmillSeconds);
				this.validationProcRepo.save(validationProcessInfo.get());
			} else {
				log.debug(tx_ID + " already existed in Ledger Process DB");
			}
		} catch (Exception e) {
			log.error("Error while storing Validation pocessing time " + e);
		}
	}
}
