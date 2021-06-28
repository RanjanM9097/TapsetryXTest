package com.l4s.transactionmanager.process;

import java.sql.Timestamp;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.l4s.transactionmanager.dao.BroadCastContraTransactionRec;
import com.l4s.transactionmanager.dao.ContraTransactionDel;
import com.l4s.transactionmanager.dao.ContraTransactionRec;
import com.l4s.transactionmanager.dao.NodeStore;
import com.l4s.transactionmanager.dao.PublicKeyStore;
import com.l4s.transactionmanager.dao.TempDb;
import com.l4s.transactionmanager.dao.TransactionNodeInfo;
import com.l4s.transactionmanager.dao.UtilityData;
import com.l4s.transactionmanager.dao.ValidationProcessRepo;
import com.l4s.transactionmanager.dto.BroadcastContraTransactionDelData;
import com.l4s.transactionmanager.dto.BroadcastContraTransactionRecData;
import com.l4s.transactionmanager.dto.ContraTransactionDelDetails;
import com.l4s.transactionmanager.dto.ContraTransactionRecDetails;
import com.l4s.transactionmanager.dto.LocalDateTimes;
import com.l4s.transactionmanager.dto.NodeDetails;
import com.l4s.transactionmanager.dto.PublicKeyEntities;
import com.l4s.transactionmanager.dto.ShareNodeInfoDto;
import com.l4s.transactionmanager.dto.TranxInterimData;
import com.l4s.transactionmanager.dto.UpdateFLStatus;
import com.l4s.transactionmanager.dto.ValidationProcessDetails;
import com.l4s.transactionmanager.service.BroadCastContraTxService;

@Component
public class ContraTxValidation {
	@Autowired
	UrlBuilder urlBuilder;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	BroadCastContraTxService contraTxDelServiceRepo;
	@Autowired
	BroadCastContraTransactionRec contraTxRecRepo;
//	@Autowired
//	BroadcastContraTransactionDelData contraTxDelInfo;
	@Autowired
	BroadcastContraTransactionRecData contraTxRecInfo;
	@Autowired
	TempDb tmpdb;
	@Autowired
	PublicKeyStore publicKeyStore;
	@Autowired
	TransactionManagerImpl tranManagerImpl;
	@Autowired
	NodeStore nodeStore;
	@Autowired
	ContraTransactionDel contraDelRepo;
	@Autowired
	ContraTransactionRec contraRecRepo;
	@Autowired
	UtilityData util;
	@Autowired
	TransactionNodeInfo transactionNodeInfo;
	@Autowired
	LocalDateTimes localDateTime;
	@Autowired
	ValidationProcessDetails validationProcessTime;
	@Autowired
	ValidationProcessRepo validationProcRepo;

	@Autowired
	ContraTransactionDelDetails contraTxDel;
	@Autowired
	ContraTransactionRecDetails contraTxRec;
	private static Logger log = LogManager.getLogger(ContraTxValidation.class);

	public void validateContraTX() {
		try {
			for (BroadcastContraTransactionDelData contraTxDel : contraTxDelServiceRepo.findByStatus("Pending")) {

				String contraID = contraTxDel.getContraid();
				try {
//				Optional<BroadcastContraTransactionDelData> contraTxDelEntity = this.contraTxDelRepo.findById(contraID);
					if ((contraTxDel.getContraType().equalsIgnoreCase("Settlement")
							&& contraTxDel.getnDel().equalsIgnoreCase(contraTxDel.getnREC())
							&& contraTxDel.getContraid().endsWith("j"))) {
						contraID = contraID.substring(0, contraID.length() - 1) + "k";
					} else if (contraTxDel.getnDel().equalsIgnoreCase(contraTxDel.getnREC())) {
						contraID = contraID + "i";
					}
					Optional<BroadcastContraTransactionRecData> contraTxRecEntity = this.contraTxRecRepo
							.findById(contraID);
					if (!contraTxRecEntity.isEmpty()) {
						Timestamp txValidateStartTime = localDateTime.getTimeStamp();
						Optional<PublicKeyEntities> Delkeyentity = this.publicKeyStore
								.findById(contraTxDel.getNodeid());
						Optional<PublicKeyEntities> Reckeyentity = this.publicKeyStore
								.findById(contraTxRecEntity.get().getNodeid());

//					contraTxDelInfo = contraTxDelEntity.get();
						contraTxRecInfo = contraTxRecEntity.get();
						if (contraTxDel.getAssetid().equalsIgnoreCase(contraTxRecInfo.getAssetid())) {
							log.info("Asset ID: " + contraTxDel.getAssetid()
									+ " is matched in Del and Rec Contra Transactions");
						} else {
							log.debug(contraTxDel.getAssetid() + " is not matched in Del and Rec Contra Transactions");
						}
						if (contraTxDel.getAssetLotId().equalsIgnoreCase(contraTxRecInfo.getAssetLotId())) {
							log.info("AssetLotDel ID: " + contraTxDel.getAssetLotId()
									+ " is matched in Del and Rec Contra Transactions");
						} else {
							log.debug(
									contraTxDel.getAssetLotId() + " is not matched in Del and Rec Contra Transactions");
						}
						if (contraTxDel.getAssetLotReceiverId()
								.equalsIgnoreCase(contraTxRecInfo.getAssetLotReceiverId())) {
							log.info("AssetLotRec ID: " + contraTxDel.getAssetLotReceiverId()
									+ " is matched in Del and Rec Contra Transactions");
						} else {
							log.debug(contraTxDel.getAssetLotReceiverId()
									+ " is not matched in Del and Rec Contra Transactions");
						}
						if (contraTxDel.getTranqty().equals(contraTxRecInfo.getTranqty())) {
							log.info("Quanity: " + contraTxDel.getTranqty()
									+ " is matched in Del and Rec Contra Transactions");
						} else {
							log.debug(contraTxDel.getTranqty() + " is not matched with " + contraTxRecInfo.getTranqty()
									+ "in Del and Rec Contra Transactions");
						}

						String delFLAddress = contraTxDel.getFladdrid();
						String recFLAddress = contraTxRecInfo.getFladdrid();
						String delFLAddressHashlink = contraTxDel.getFladdridparentfk();
						String recFLAddressHashlink = contraTxRecInfo.getFladdridparentfk();
						if (!delFLAddress.isEmpty()) {

							this.passiveFlStatusUpdate(contraTxDel.getFDPeriodId(), contraTxDel.getNodeid(),
									delFLAddress, "CONFIRMED", contraTxDel.getAssetLotId(),contraTxDel.getPostperiodfk());
							if (contraTxDel.getFDPeriodId() == null || contraTxDel.getFDPeriodId().equals("")) {
								String delStatus = this.flLinkStatusUpdate(contraTxDel.getFDPeriodId(),
										contraTxDel.getNodeid(), delFLAddress, delFLAddressHashlink,
										contraTxDel.getAssetLotId());
								if (delStatus.equalsIgnoreCase("Success")
										|| delStatus.equalsIgnoreCase("Pending Update")
										|| delStatus.equalsIgnoreCase("Valid")) {
									log.info("Del Fl Addresss is matched got success from FLM in broadcasting");
								} else {
									log.info("Del Fl Addresss is not mactched got failure from FLM in broadcasting");
									continue;
								}
							}
						} else {
							log.debug("Del Fl Address is empty in broadcasting");
						}
						if (!recFLAddress.isEmpty()) {

							this.passiveFlStatusUpdate(contraTxRecEntity.get().getFDPeriodId(),
									contraTxRecEntity.get().getNodeid(), recFLAddress, "CONFIRMED",
									contraTxRecEntity.get().getAssetLotId(),
									contraTxRecEntity.get().getPostperiodfk());
							if (contraTxDel.getFDPeriodId() == null || contraTxDel.getFDPeriodId().equals("")) {
								String recStatus = this.flLinkStatusUpdate(contraTxRecEntity.get().getFDPeriodId(),
										contraTxRecEntity.get().getNodeid(), recFLAddress, recFLAddressHashlink,
										contraTxRecEntity.get().getAssetLotId());
								if (recStatus.equalsIgnoreCase("Success")
										|| recStatus.equalsIgnoreCase("Pending Update")
										|| recStatus.equalsIgnoreCase("Valid")) {
									log.info("Rec Fl Addresss is matched got success from FLM in broadcasting");
								} else {
									log.info("Rec Fl Addresss is not mactched got failure from FLM in broadcasting");
									continue;
								}
							}
						} else {
							log.debug("Del Fl Address is empty in broadcasting");
						}
						String delEncrptHash = contraTxDel.getEncryptedhash();
						String recEncrptHash = contraTxRecInfo.getEncryptedhash();

						if (delEncrptHash.equalsIgnoreCase(recEncrptHash)) {
							log.info("Del and Rec Encrypted Hash Matched");
						} else {
							log.debug("Del and Rec Encrypted Hash not Matched");
						}
						String txDelHash = contraTxDel.getTrhash();
						String txRecHash = contraTxRecInfo.getTrhash();

						if (txDelHash.equalsIgnoreCase(txRecHash)) {
							log.info("Del and Rec Hash Matched");
						} else {
							log.debug("Del and Rec Hash not Matched");
						}
//					}
//					if (contraTxDelInfo.getAssetid().equalsIgnoreCase(contraTxRecInfo.getAssetid())) {
//						log.info("Asset ID: " + contraTxDelInfo.getAssetid()
//								+ " is matched in Del and Rec Contra Transactions");
//					} else {
//						log.debug(contraTxDelInfo.getAssetid() + " is not matched in Del and Rec Contra Transactions");
//					}
//					if (contraTxDelInfo.getAssetid().equalsIgnoreCase(contraTxRecInfo.getAssetid())) {
//						log.info("Asset ID: " + contraTxDelInfo.getAssetid()
//								+ " is matched in Del and Rec Contra Transactions");
//					} else {
//						log.debug(contraTxDelInfo.getAssetid() + " is not matched in Del and Rec Contra Transactions");
//					}
						contraTxDel.setContramatchStatus(true);
						contraTxRecInfo.setContramatchStatus(true);
						contraTxDel.setStatus("Completed");
						contraTxRecInfo.setStatus("Completed");
//					try {
//						contraTxDel.deleteByContraIDNative(contraID);
//					} catch (Exception e) {
//						log.error(e + " Exception");
//					}
//					try {
//						contraTxRecRepo.deleteByContraIDNative(contraID);
//					} catch (Exception e) {
//						log.error(e + " Exception");
//					}
						Timestamp txValidationEndTime = localDateTime.getTimeStamp();
						this.ledgerProcTime(contraTxDel.getContraid(), txValidateStartTime, txValidationEndTime);

						this.contraDelRepo.save(contraTxDel);
						this.contraRecRepo.save(contraTxRecInfo);
						this.updateContraTransactionDetails(contraTxDel, contraTxRecInfo, new TranxInterimData());
						// Storing BroadCasted Transaction details in to DataBase

//					Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(contraTxDelInfo.getTx_id());
					} else {
						log.debug("Contra Transaction: " + contraID + " not available in Rec DB");
					}
				} catch (Exception exception) {
					log.error(exception + " exception occurred while iterating Contra Transactions");
					continue;
				}

			}

		} catch (Exception exception) {
			log.error(exception + " exception occurred while validating Contra Transactions");
		}
	}

	public String flLinkStatusUpdate(String fdPeriodID, String nodeId, String fLAddress, String flhashlink, String ALD)
			throws Exception {
		String status = "";
		try {
//		Optional<PublicKeyEntities> publicKeyEntity = this.publicKeyStore
//				.findById(nodeId);
//			Optional<NodeDetails> nodeOptional = this.nodeStore.findById(nodeId);
//			if (!nodeOptional.isEmpty()) {
//			if (fdPeriodID == null || fdPeriodID.equals("")) {
			log.trace("Requesting URI :" + "http://" + "localhost" + ":8092/flam/shareInfo" + "to update FL Address: "
					+ fLAddress + ", status: " + flhashlink + " and Node: " + nodeId);
			status = this.urlBuilder.getResponse("http://" + "localhost" + ":8092/flam/shareInfo",
					this.objectMapper
							.writeValueAsString(new ShareNodeInfoDto(nodeId, fLAddress, flhashlink, fdPeriodID, ALD))
							.getBytes());
//			}
		} catch (Exception exception) {
			log.error(exception + " Exception occurred while sending  FLDetails in Broad Casting");
		}
		return status;
	}

	public TranxInterimData updateContraTransactionDetails(BroadcastContraTransactionDelData delContraInfo,
			BroadcastContraTransactionRecData recContraInfo, TranxInterimData intrim) {
		try {
			Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(delContraInfo.getTx_id());
			if (tranxInterim.isEmpty()) {
//				Date txStartTime = new Date();
				intrim.setTransactionId(delContraInfo.getTx_id());
				intrim.setContraIdDel(delContraInfo.getExtTxnId());
				intrim.setContraIdRec(recContraInfo.getExtTxnId());
				intrim.setAssetLotId(delContraInfo.getAssetLotId());
				intrim.setTimeStamp(delContraInfo.getTrandt());
				intrim.setNodeId(this.transactionNodeInfo.getNodeid());
				intrim.setAtype(delContraInfo.getAssetid());
				intrim.setAsset_name(delContraInfo.getAsset_name());
				intrim.setPeriod(this.transactionNodeInfo.getPeriod());
				intrim.setQtyALR(delContraInfo.getQtyALR());
				intrim.setQtyALD(delContraInfo.getTranqty());
				intrim.setnREC(recContraInfo.getNodeid());
				intrim.setTxREC(delContraInfo.getTxREC());
				intrim.setnDEL(delContraInfo.getNodeid());
				intrim.setTxDEL(delContraInfo.getTxDel());
				intrim.setAln(delContraInfo.getAssetLotNetId());
				intrim.setAlr(delContraInfo.getAssetLotReceiverId());
				intrim.setFlIdDEL(delContraInfo.getFlid());
				intrim.setFlaHashLinkDEL(delContraInfo.getFladdridparentfk());
				intrim.setFlAddressDEL(delContraInfo.getFladdrid());
				intrim.setFlIdREC(recContraInfo.getFlid());
				intrim.setFlaHashLinkREC(recContraInfo.getFladdridparentfk());
				intrim.setFlAddressREC(recContraInfo.getFladdrid());
				intrim.setTranxHash(delContraInfo.getTrhash());
				intrim.setEncryptedHash(delContraInfo.getEncryptedhash());
				intrim.setDegiSign(delContraInfo.getDigitalsig());
				intrim.setRecTranxHash(recContraInfo.getTrhash());
				intrim.setRecEncryptedHash(recContraInfo.getEncryptedhash());
				intrim.setRecDegiSign(recContraInfo.getDigitalsig());
				intrim.setTxType(recContraInfo.getTxType());
				intrim.setUseCase(recContraInfo.getUseCase());
				intrim.setAirlocknode(recContraInfo.getAirlocknode());
				intrim.setFDPeriodId(recContraInfo.getFDPeriodId());
				if (delContraInfo.getContraType() != null) {
					intrim.setContraType(delContraInfo.getContraType());
				}

				if (delContraInfo.getContingentId() != null) {
					intrim.setContingentId(delContraInfo.getContingentId());
				}

				if (delContraInfo.getShortTransferId() != null) {
					intrim.setShortTransferId(delContraInfo.getShortTransferId());

				}
				this.tmpdb.save(intrim);
				// Creating Journal Pairs
				if (!delContraInfo.getContraid().endsWith("i") && !delContraInfo.getContraid().endsWith("j")) {
					this.journalPairs(intrim);
				}
//				
//				log.info("Journal Pairs Created");
				ContraTransactionDelDetails contra_txDel = this.createDelContraTransaction(intrim, intrim.getnDEL());

				ContraTransactionRecDetails contra_txRec = this.createRecContraTransaction(intrim, intrim.getnREC());
				contra_txDel.setContramatchStatus(true);
				contra_txRec.setContramatchStatus(true);
				delContraInfo.setStatus("Completed");
				recContraInfo.setStatus("Completed");
				this.contraDelRepo.save(contra_txDel);
				this.contraRecRepo.save(contra_txRec);

				// Creating COLR
				this.util.storeCOLRData(intrim);

				if (intrim.getContraType().equalsIgnoreCase("Obligation")
						|| intrim.getContraType().equalsIgnoreCase("Loan")
						|| intrim.getContraType().equalsIgnoreCase("Collateral")) {

					this.util.updateFutureTxDetails(intrim.getUseCase(), intrim.getTransactionId(),
							intrim.getShortTransferId(), intrim.getnDEL(), intrim.getnREC(), intrim.getContingentId(),
							intrim.getFDPeriodId());
				} else if (intrim.getContraType().equalsIgnoreCase("Reversal")
						|| intrim.getContraType().equalsIgnoreCase("Settlement")) {
					this.util.updateClosedFutureTxDetails(intrim.getShortTransferId());
				}
			} else {
				log.debug(delContraInfo.getTx_id() + " already existed in DataBase");
			}

		} catch (Exception exception) {
			log.error(exception + " Exception occurred  while updatin Transaction details from Broadcasting");
		}

		return intrim;
	}

	public void journalPairs(TranxInterimData tranxInterimData) throws Exception {
		if (tranxInterimData.getContingentId() != null
				&& tranxInterimData.getnDEL().equalsIgnoreCase(tranxInterimData.getnREC())
				&& tranxInterimData.getContraType().equalsIgnoreCase("Transfer")) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getTransactionId(),
					"ContingentIntraTransferJournalPairProc");
		} else if (tranxInterimData.getContraType().equalsIgnoreCase("AirLockMarket")) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getTransactionId(), "MarketJournalPairProc");
		} else if (tranxInterimData.getContraType().equalsIgnoreCase("AirLockMarketOut")) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getTransactionId(), "MarketOutJournalPairProc");
		} else if (tranxInterimData.getnDEL().equalsIgnoreCase(tranxInterimData.getnREC())
				&& tranxInterimData.getContraType().equalsIgnoreCase("Transfer")) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getTransactionId(), "InternalTransferJournalPairProc");
		} else if (tranxInterimData.getContingentId() != null
				&& tranxInterimData.getContraType().equalsIgnoreCase("Transfer")) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getContingentId(), "ContingentJournalPairProc");
		} else if (tranxInterimData.getContingentId() != null
				&& (tranxInterimData.getnDEL().equalsIgnoreCase(tranxInterimData.getnREC())
						&& (tranxInterimData.getShortTransferId() != null
								&& (tranxInterimData.getContraType().equalsIgnoreCase("Obligation"))
								|| tranxInterimData.getContraType().equalsIgnoreCase("Loan")
								|| tranxInterimData.getContraType().equalsIgnoreCase("Collateral")))) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getTransactionId(),
					"IntraShortContingentTransferJournalPairProc");
		} else if (tranxInterimData.getContingentId() != null && tranxInterimData.getShortTransferId() != null
				&& (tranxInterimData.getContraType().equalsIgnoreCase("Obligation")
						|| tranxInterimData.getContraType().equalsIgnoreCase("Loan")
						|| tranxInterimData.getContraType().equalsIgnoreCase("Collateral"))) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getContingentId(),
					"ContingentShortTransferJournalPairProc");
		} else if (tranxInterimData.getnDEL().equalsIgnoreCase(tranxInterimData.getnREC())
				&& tranxInterimData.getShortTransferId() != null
				&& (tranxInterimData.getContraType().equalsIgnoreCase("Obligation"))
				|| tranxInterimData.getContraType().equalsIgnoreCase("Loan")
				|| tranxInterimData.getContraType().equalsIgnoreCase("Collateral")) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getTransactionId(), "IntraShortTransferJournalPairProc");
		} else if ((tranxInterimData.getContraType().equalsIgnoreCase("Obligation")
				|| tranxInterimData.getContraType().equalsIgnoreCase("Loan")
				|| tranxInterimData.getContraType().equalsIgnoreCase("Collateral"))
				&& (tranxInterimData.getShortTransferId() != null)) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getTransactionId(), "ShortTransferJournalPairProc");
		} else if (tranxInterimData.getContingentId() != null && (tranxInterimData.getShortTransferId() != null
				&& (tranxInterimData.getnDEL().equalsIgnoreCase(tranxInterimData.getnREC())
						&& tranxInterimData.getContraType().equalsIgnoreCase("Settlement")))) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getTransactionId(),
					"IntraShortContingentTransferFillJournalPairProc");
		} else if (tranxInterimData.getnDEL().equalsIgnoreCase(tranxInterimData.getnREC())
				&& tranxInterimData.getShortTransferId() != null
				&& tranxInterimData.getContraType().equalsIgnoreCase("Settlement")) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getTransactionId(),
					"IntraShortTransferFillJournalPairProc");
		} else if (tranxInterimData.getContraType().equalsIgnoreCase("Settlement")
				&& tranxInterimData.getShortTransferId() != null && tranxInterimData.getContingentId() != null) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getContingentId(),
					"ContingentShortTransferFillJournalPairProc");
		} else if (tranxInterimData.getContraType().equalsIgnoreCase("Settlement")
				&& (tranxInterimData.getShortTransferId() != null)) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getTransactionId(), "ShortTransferFillJournalPairProc");
		} else if (tranxInterimData.getContingentId() != null && tranxInterimData.getShortTransferId() != null
				&& (tranxInterimData.getnDEL().equalsIgnoreCase(tranxInterimData.getnREC())
						&& (tranxInterimData.getContraType().equalsIgnoreCase("Reversal")))) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getTransactionId(),
					"IntraContingentPayRecReversalJournalPairProc");
		} else if (tranxInterimData.getnDEL().equalsIgnoreCase(tranxInterimData.getnREC())
				&& (tranxInterimData.getShortTransferId() != null
						&& (tranxInterimData.getContraType().equalsIgnoreCase("Reversal")))) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getTransactionId(),
					"IntraPayRecReversalJournalPairProc");
		} else if (tranxInterimData.getContraType().equalsIgnoreCase("Reversal")
				&& (tranxInterimData.getShortTransferId() != null) && (tranxInterimData.getContingentId() != null)) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getContingentId(),
					"ContingentPayRecReversalJournalPairProc");
		} else if (tranxInterimData.getContraType().equalsIgnoreCase("Reversal")
				&& (tranxInterimData.getShortTransferId() != null)) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getTransactionId(), "PayRecReversalJournalPairProc");
		} else if (tranxInterimData.getContraType().equalsIgnoreCase("Transfer")) {
			this.tranManagerImpl.updateLedger(tranxInterimData.getTransactionId(), "JournalPairProc");
		}

		log.info("Journal Pairs are created successfully");
	}

	public void ledgerProcTime(String tx_ID, Timestamp start, Timestamp end) {
		try {
//			long diffInmillSeconds = end.getTime() - start.getTime();
//			log.info("Processing Time for transaction " + tx_ID + "in secs: " + diffInmillSeconds);
			Optional<ValidationProcessDetails> validationProcessInfo = this.validationProcRepo.findById(tx_ID);
			if (validationProcessInfo.isEmpty()) {
				log.info("Storing new Transaction Prcessing Data into Database");
				validationProcessTime.setTransactionId(tx_ID);
				validationProcessTime.setStartTime(start);
				validationProcessTime.setEndTime(end);
				validationProcessTime.setContra_type("BroadCast");
				validationProcessTime.setPeriod_id(this.transactionNodeInfo.getPeriod());
//				ledgerProcessTIme.setTotalProcessTime(diffInmillSeconds);
				this.validationProcRepo.save(validationProcessTime);
			} else {
				log.debug(tx_ID + " already existed in Ledger Process DB");
			}
		} catch (Exception e) {
			log.error("Error while storing Validation pocessing time " + e);
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

	public ContraTransactionRecDetails createRecContraTransaction(TranxInterimData intrim, String nodeType) {
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
			contraTxRec.setTxType(intrim.getTxType());
			contraTxRec.setUseCase(intrim.getUseCase());
			contraTxRec.setAirlocknode(intrim.getAirlocknode());
			contraTxRec.setFDPeriodId(intrim.getFDPeriodId());
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

	public void passiveFlStatusUpdate(String FdPeriodId, String nodeId, String fLAddress, String status, String ALD,String periodId)
			throws Exception {
//		Optional<NodeDetails> nodeOptional = this.nodeStore.findById(nodeId);
		log.trace("Requesting URI :" + "http://" + "localhost" + ":8092/flam/updateStatusPassiveNode"
				+ "to update FL Address: " + fLAddress + ", status: " + status + " and Node: " + nodeId +", periodId" + periodId);

		this.urlBuilder.getResponse("http://" + "localhost" + ":8092/flam/updateStatusPassiveNode", this.objectMapper
				.writeValueAsString(new UpdateFLStatus(nodeId, fLAddress, status, FdPeriodId, ALD,periodId)).getBytes());
	}

}
