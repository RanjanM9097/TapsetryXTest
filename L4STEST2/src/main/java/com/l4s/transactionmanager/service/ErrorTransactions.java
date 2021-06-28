package com.l4s.transactionmanager.service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.l4s.transactionmanager.dao.ContraTransactionDel;
import com.l4s.transactionmanager.dao.ContraTransactionRec;
import com.l4s.transactionmanager.dao.ErrorTransactionRepo;
import com.l4s.transactionmanager.dao.TransactionNodeInfo;
import com.l4s.transactionmanager.dao.UtilityData;
import com.l4s.transactionmanager.dto.ContraTransactionDelDetails;
import com.l4s.transactionmanager.dto.ContraTransactionRecDetails;
import com.l4s.transactionmanager.dto.ErrorTxnInfo;
import com.l4s.transactionmanager.dto.LocalDateTimes;
import com.l4s.transactionmanager.dto.TransactionDetails;

public class ErrorTransactions {

	@Autowired
	ErrorTransactionRepo errorTransactionRepo;
	@Autowired
	ErrorTxnInfo errorDelTrnsactionObj;
	@Autowired
	ErrorTxnInfo errorRecTrnsactionObj;
	private static Logger log = LogManager.getLogger(UtilityData.class);
	@Autowired
	LocalDateTimes localDateTime;
	@Autowired
	TransactionNodeInfo transactionNodeInfo;
	@Autowired
	ContraTransactionDelDetails contraTxDel;
	@Autowired
	ContraTransactionRecDetails contraTxRec;
	@Autowired
	ContraTransactionDel contraTxDelRepo;
	@Autowired
	ContraTransactionRec contraTxRecRepo;

	public void storeDelErrorTransaction(String flmErrorMessage, TransactionDetails transdet) {
		try {
			String errorId = "";
			errorId = this.getSaltString();
//			long diffInmillSeconds = end.getTime() - start.getTime();
//			log.info("Processing Time for transaction " + tx_ID + "in secs: " + diffInmillSeconds);
			Optional<ErrorTxnInfo> errrorDelTransactionInfo = this.errorTransactionRepo.findById(errorId);
			if (errrorDelTransactionInfo.isEmpty()) {
				log.info("Storing Error Transaction Data into Database");
				errorDelTrnsactionObj.setErrId(this.getSaltString());
				errorDelTrnsactionObj.setTxId(transdet.getTransactionId());
				errorDelTrnsactionObj.setTimeStamp(localDateTime.getDateTime());
				errorDelTrnsactionObj.setPeriodId(this.transactionNodeInfo.getPeriod());
				errorDelTrnsactionObj.setModule("TransactionManager");
				errorDelTrnsactionObj.setErrorMessage(flmErrorMessage);
//				ledgerProcessTIme.setTotalProcessTime(diffInmillSeconds);
				this.errorTransactionRepo.save(errorDelTrnsactionObj);
				this.createDelErrorContraTx(transdet);
			} else {
				log.debug(transdet.getTransactionId() + " already existed in Ledger Process DB");
			}
		} catch (Exception e) {
			log.error("Error while storing Error Transaction " + e);
		}

	}

	public void storeRecErrorTransaction(String flmErrorMessage, TransactionDetails transdet) {
		try {
			String errorId = "";
			errorId = this.getSaltString();
//			long diffInmillSeconds = end.getTime() - start.getTime();
//			log.info("Processing Time for transaction " + tx_ID + "in secs: " + diffInmillSeconds);
			Optional<ErrorTxnInfo> errrorRecTransactionInfo = this.errorTransactionRepo.findById(errorId);
			if (errrorRecTransactionInfo.isEmpty()) {
				log.info("Storing Error Transaction Data into Database");
				errorRecTrnsactionObj.setErrId(this.getSaltString());
				errorRecTrnsactionObj.setTxId(transdet.getTransactionId());
				errorRecTrnsactionObj.setTimeStamp(localDateTime.getDateTime());
				errorRecTrnsactionObj.setPeriodId(this.transactionNodeInfo.getPeriod());
				errorRecTrnsactionObj.setModule("TransactionManager");
				errorRecTrnsactionObj.setErrorMessage(flmErrorMessage);
//				ledgerProcessTIme.setTotalProcessTime(diffInmillSeconds);
				this.errorTransactionRepo.save(errorRecTrnsactionObj);
				this.createRecErrorContraTx(transdet);
			} else {
				log.debug(transdet.getTransactionId() + " already existed in Ledger Process DB");
			}
		} catch (Exception e) {
			log.error("Error while storing Error Transaction " + e);
		}

	}

	public void createRecErrorContraTx(TransactionDetails transdet) {
		try {
			Optional<ContraTransactionRecDetails> contraTxRecData = this.contraTxRecRepo
					.findById(transdet.getTransactionId());
			if (contraTxRecData.isEmpty()) {
				contraTxRec.setContraid(transdet.getTransactionId());
				contraTxRec.setPostperiodfk(transdet.getPeriod());
				contraTxRec.setnDel(transdet.getnDEL());
				contraTxRec.setnREC(transdet.getnREC());
				contraTxRec.setAssetid(transdet.getAtype());
				contraTxRec.setTxDel(transdet.getTxDEL());
				contraTxRec.setTxREC(transdet.getTxREC());
				contraTxRec.setContraType(transdet.getSubType());
				contraTxRec.setAssetLotId(transdet.getAssetLotId());
				contraTxRec.setShortTransferId(transdet.getShortTransferId());
				contraTxRec.setContingentId(transdet.getContingentId());
				contraTxRec.setQtyALR(transdet.getQtyALR());
				contraTxRec.setExtTxnId(transdet.getTransactionId());
				contraTxRec.setContramatchStatus(false);

				this.contraTxRecRepo.save(contraTxRec);
			} else {
				log.debug("Rec Error Transaction ID already existed in DataBase");
			}
//			
		} catch (Exception exception) {
			log.error(exception + " ecxception occurred while creating Error Rec Contra Transaction");
		}
	}

	public void createDelErrorContraTx(TransactionDetails transdet) {
		try {
			Optional<ContraTransactionDelDetails> contraTxDelData = this.contraTxDelRepo
					.findById(transdet.getTransactionId());
			if (contraTxDelData.isEmpty()) {
				contraTxDel.setContraid(transdet.getTransactionId());
				contraTxDel.setPostperiodfk(transdet.getPeriod());
				contraTxDel.setnDel(transdet.getnDEL());
				contraTxDel.setnREC(transdet.getnREC());
				contraTxDel.setTxDel(transdet.getTxDEL());
				contraTxDel.setAssetid(transdet.getAtype());
				contraTxDel.setTxREC(transdet.getTxREC());
				contraTxDel.setContraType(transdet.getSubType());
				contraTxDel.setAssetLotId(transdet.getAssetLotId());
				contraTxDel.setShortTransferId(transdet.getShortTransferId());
				contraTxDel.setContingentId(transdet.getContingentId());
				contraTxDel.setQtyALR(transdet.getQtyALR());
				contraTxDel.setExtTxnId(transdet.getTransactionId());
				contraTxDel.setContramatchStatus(false);
				this.contraTxDelRepo.save(contraTxDel);
			} else {
				log.debug(" Del Error Transaction ID already existed in DataBase");
			}
		} catch (Exception exception) {
			log.error(exception + " ecxception occurred while creating Error Del Contra Transaction");
		}
	}

	public String getSaltString() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 18) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;
	}

}
