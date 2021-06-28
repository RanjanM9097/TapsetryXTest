package com.l4s.transactionmanager.interfacelayer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.l4s.transactionmanager.dao.BroadCastContraTransactionDEL;
import com.l4s.transactionmanager.dao.BroadCastContraTransactionRec;
import com.l4s.transactionmanager.dao.TransactionNodeInfo;
import com.l4s.transactionmanager.dao.UtilityData;
import com.l4s.transactionmanager.dto.BroadcastContraTransactionDelData;
import com.l4s.transactionmanager.dto.BroadcastContraTransactionRecData;
import com.l4s.transactionmanager.dto.LocalDateTimes;
import com.l4s.transactionmanager.dto.TransactionDetails;
import com.l4s.transactionmanager.process.ContraTxValidation;
import com.l4s.transactionmanager.process.IntraTransactionDelImpl;
import com.l4s.transactionmanager.process.ShortTransferImpl;
import com.l4s.transactionmanager.process.TransactionManagerImpl;

@Component
public class InterfaceManagerImpl implements InterfaceManager {
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	TransactionManagerImpl tranManagerImpl;
	@Autowired
	ContraTxValidation contraTxValidation;
	private static Logger log = LogManager.getLogger(InterfaceManagerImpl.class);
//	@Value("${EST.zodeid}")
//	public String estZodeId;
	@Autowired
	TransactionNodeInfo transactionNodeInfo;
	@Autowired
	ShortTransferImpl shortTransferfill;
	@Autowired
	IntraTransactionDelImpl intraTransactionDelImpl;
	@Autowired
	BroadCastContraTransactionDEL objBroadCastContraTransactionDELRepo;
	@Autowired
	BroadCastContraTransactionRec objBroadCastContraTransactionRecRepo;
	@Autowired
	UtilityData util;
	@Autowired
	LocalDateTimes localDateTime;
//	public static Map<String, Integer> txDelsynchronizedHashMap;

	Set<BroadcastContraTransactionDelData> txDelset = new HashSet<BroadcastContraTransactionDelData>();
	Set<BroadcastContraTransactionRecData> txRecset = new HashSet<BroadcastContraTransactionRecData>();

	@JmsListener(destination = "contratx", containerFactory = "myFactory")
	public void receive(String message) {
		log.info("received message='{}'", message);

		try {
//			Callable<String> TxTask = () -> this.txTypeDecisionMake(
//					(TransactionDetails) this.objectMapper.readValue(message, TransactionDetails.class));
//			Future<String> tx = Executors.newCachedThreadPool().submit(TxTask);
			this.txTypeDecisionMake(
					(TransactionDetails) this.objectMapper.readValue(message, TransactionDetails.class));
		} catch (Exception var3) {
			log.error(ExceptionUtils.getStackTrace(var3));
		}

	}

	@JmsListener(destination = "ContraTxDel", containerFactory = "myFactory")
	public void receiveContraTxDel(String message) {
		log.info("received contra Del message='{}'", message);

		try {
//			BroadcastContraTransactionDelData delContraData = (BroadcastContraTransactionDelData) this.objectMapper
//					.readValue(message, BroadcastContraTransactionDelData.class);
//			txDelset.add(delContraData);
//			Callable<String> sendDelDataToDB = () -> this.processDelData();
//			Future<String> txDel = Executors.newCachedThreadPool().submit(sendDelDataToDB);

			this.sendDelDataToDB((BroadcastContraTransactionDelData) this.objectMapper.readValue(message,
					BroadcastContraTransactionDelData.class));
		} catch (Exception var3) {
			log.error(ExceptionUtils.getStackTrace(var3));
		}

	}

	@JmsListener(destination = "ContraTxRec", containerFactory = "myFactory")
	public void receiveContraTRec(String message) {
		log.info("received contra Rec message='{}'", message);

		try {
//			BroadcastContraTransactionRecData recContraData = (BroadcastContraTransactionRecData) this.objectMapper
//					.readValue(message, BroadcastContraTransactionRecData.class);
//
//			txRecset.add(recContraData);
//			Callable<String> sendRecDataToDB = () -> this.processRecData();
//
//			Future<String> txRec = Executors.newCachedThreadPool().submit(sendRecDataToDB);

			this.sendRecDataToDB((BroadcastContraTransactionRecData) this.objectMapper.readValue(message,
					BroadcastContraTransactionRecData.class));
		} catch (Exception var3) {
			log.error(ExceptionUtils.getStackTrace(var3));
		}

	}

	public synchronized String processDelData() {

		try {
			for (BroadcastContraTransactionDelData delObj : txDelset) {
				this.sendDelDataToDB(delObj);
			}
			txDelset.clear();
		} catch (Exception exception) {
			log.error(exception + " exception occurred in synchronization block");
			return "null";
		}
		return "success";

	}

	public synchronized String processRecData() {
		try {
			for (BroadcastContraTransactionRecData recObj : txRecset) {
				this.sendRecDataToDB(recObj);
			}
			txRecset.clear();

		} catch (Exception exception) {
			log.error(exception + " exception occurred in synchronization block");
			return "null";
		}
		return "success";
	}

	public String txTypeDecisionMake(TransactionDetails transactionData) {

		try {
			// String period = "" + LocalDateTime.now(ZoneId.of(estZodeId)).getYear()
			// + LocalDateTime.now(ZoneId.of(estZodeId)).getMonthValue()
			// + LocalDateTime.now(ZoneId.of(estZodeId)).getDayOfMonth();
//			String sPeriodId= "2020091001";
//			transactionData.setPeriod(this.transactionNodeInfo.getPeriod());
//			this.transactionNodeInfo.setNodeid("NodeC");
//			this.transactionNodeInfo.setPeriod("2020091001");
			transactionData.setPeriod(this.transactionNodeInfo.getPeriod());
			log.info("Period Id: " + this.transactionNodeInfo.getPeriod());
			transactionData.setTransactionId(
					String.valueOf(ThreadLocalRandom.current().nextInt(100000) + this.transactionNodeInfo.getPeriod()));
			log.info("transaction ID: " + transactionData.getTransactionId());
			transactionData.setnDEL(this.transactionNodeInfo.getNodeid());
			this.util.updateReceivedTxData(transactionData);
			// transactionData.setPeriod(period);
			if (transactionData.getnDEL().equalsIgnoreCase(transactionData.getnREC())) {
				this.intraTransactionDelImpl.processTransaction(transactionData);
			} else {
				this.tranManagerImpl.processTransaction(transactionData);

			}
		} catch (Exception exception) {
			log.error(exception + " exception occurred while seding transaction details");
			return "null";
		}
		return "success";
	}

	public String sendDelDataToDB(BroadcastContraTransactionDelData contraTransactionData) {

		try {
			contraTransactionData.setStatus("Pending");
			this.objBroadCastContraTransactionDELRepo.save(contraTransactionData);
			log.info("BroadCasted Contra Del Data Stored In in Database");
			// Calling Contra Transaction Validation Process
			this.contraTxValidation.validateContraTX();
			log.info("Contra Transaction Validation done from Del");

		} catch (Exception exception) {
			log.error(exception + " exception occurred while storing Contra Del transaction details");
			return "null";
		}
		return "success";
	}

	public String sendRecDataToDB(BroadcastContraTransactionRecData contraTransactionData) {

		try {
			contraTransactionData.setStatus("Pending");
			this.objBroadCastContraTransactionRecRepo.save(contraTransactionData);
			log.info("BroadCasted Contra Rec Data Stored In in Database");
			this.contraTxValidation.validateContraTX();
			log.info("Contra Transaction Validation done from Rec");
		} catch (Exception exception) {
			log.error(exception + " exception occurred while storing Contra Rec transaction details");
			return "null";
		}
		return "success";
	}

	public void transactionDepository() {

	}
}
