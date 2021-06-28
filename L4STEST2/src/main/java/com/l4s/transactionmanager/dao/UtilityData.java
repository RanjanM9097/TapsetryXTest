package com.l4s.transactionmanager.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.l4s.transactionmanager.dto.ALDCompleteOwnerShipLots;
import com.l4s.transactionmanager.dto.CompleteOwnerShipLots;
import com.l4s.transactionmanager.dto.ContraTransactionDelDetails;
import com.l4s.transactionmanager.dto.ContraTransactionRecDetails;
import com.l4s.transactionmanager.dto.FLAddress;
import com.l4s.transactionmanager.dto.FLAddressErrorUpdateDto;
import com.l4s.transactionmanager.dto.FLInput;
import com.l4s.transactionmanager.dto.FLMAddress;
import com.l4s.transactionmanager.dto.FutureDatedTx;
import com.l4s.transactionmanager.dto.HashingData;
import com.l4s.transactionmanager.dto.LocalDateTimes;
import com.l4s.transactionmanager.dto.ReceivedTransactionData;
import com.l4s.transactionmanager.dto.TransactionDetails;
import com.l4s.transactionmanager.dto.TranxInterimData;
import com.l4s.transactionmanager.dto.ValidationProcessDetails;

@Service
public class UtilityData {
	@Autowired
	HashingData hd;
	@Autowired
	TempDb tmpdb;
	@Autowired
	TransactionNodeInfo transactionNodeInfo;
	@Autowired
	CompleteOwnerShipLots aldCOLR;
	@Autowired
	ALDCompleteOwnerShipLots aldCOLRObj;
	@Autowired
	CompleteOwnerShipLots alrCOLR;
	@Autowired
	CompleteOwnerShipLots alnCOLR;
	@Autowired
	CompleteOwnerShipLotRepo objCOLRRepo;
	@Autowired
	ALDCompleteOwnerShipLotRepo objALDRepo;
	@Autowired
	ContraTransactionDelDetails contraTxDel;
	@Autowired
	ContraTransactionRecDetails contraTxRec;
	@Autowired
	ReceviedTransactionRepo receivedTrnsactionRepo;
	@Autowired
	ValidationProcessDetails validationProcessTime;
	@Autowired
	FutureDatedTx futureTxData;
	@Autowired
	ValidationProcessRepo validationProcRepo;
	@Autowired
	ReceivedTransactionData receivedTxData;
	private static Logger log = LogManager.getLogger(UtilityData.class);
	@Autowired
	LocalDateTimes localDateTime;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	FuturedatedTxsRepo futTxRepo;

	public TranxInterimData updateTransactionDetails(TransactionDetails trans, TranxInterimData intrim) {
		intrim.setNodeId(this.transactionNodeInfo.getNodeid());
		intrim.setTransactionId(trans.getTransactionId());
		intrim.setContraIdDel(
				String.valueOf(ThreadLocalRandom.current().nextInt(100000) + this.transactionNodeInfo.getPeriod()));
		intrim.setContraIdRec(
				String.valueOf(ThreadLocalRandom.current().nextInt(100000) + this.transactionNodeInfo.getPeriod()));
		intrim.setAssetLotId(trans.getAssetLotId());
		intrim.setTimeStamp(trans.getTimeStamp());
		intrim.setAtype(trans.getAtype());
		intrim.setAsset_name(trans.getAname());
		intrim.setPeriod(trans.getPeriod());
		intrim.setQtyALR(trans.getQtyALR());
		intrim.setQtyALD(trans.getQtyALD());
		intrim.setnREC(trans.getnREC());
		intrim.setTxREC(trans.getTxREC());
		intrim.setnDEL(trans.getnDEL());
		intrim.setTxDEL(trans.getTxDEL());
		intrim.setTxDEL(trans.getTxDEL());
		intrim.setSnodeId(trans.getNodeId());
		intrim.setUseCase(trans.getUseCase());
		intrim.setTxType(trans.getTxType());
		intrim.setAirlocknode(trans.getAirlocknode());
		intrim.setFDPeriodId(trans.getFDPeriodId());
		if (trans.getSubType() != null) {
			intrim.setContraType(trans.getSubType());
		} else {
			intrim.setContraType(null);
		}

		if (trans.getContingentId() != null) {
			intrim.setContingentId(trans.getContingentId());
		} else {
			intrim.setContingentId(null);
		}

		if (trans.getShortTransferId() != null) {
			intrim.setShortTransferId(trans.getShortTransferId());
		} else {
			intrim.setShortTransferId(null);
		}

		return intrim;

	}

	public TranxInterimData updateRecTransactionDetails(TransactionDetails trans, TranxInterimData intrim) {
		intrim.setTransactionId(trans.getTransactionId() + "i");
		intrim.setNodeId(this.transactionNodeInfo.getNodeid());
		intrim.setContraIdDel(
				String.valueOf(ThreadLocalRandom.current().nextInt(100000) + this.transactionNodeInfo.getPeriod()));
		intrim.setContraIdRec(
				String.valueOf(ThreadLocalRandom.current().nextInt(100000) + this.transactionNodeInfo.getPeriod()));
		intrim.setAssetLotId(trans.getAssetLotId());
		intrim.setTimeStamp(trans.getTimeStamp());
		intrim.setAtype(trans.getAtype());
		intrim.setAsset_name(trans.getAname());
		intrim.setPeriod(trans.getPeriod());
		intrim.setQtyALR(trans.getQtyALR());
		intrim.setQtyALD(trans.getQtyALD());
		intrim.setnREC(trans.getnREC());
		intrim.setTxREC(trans.getTxREC());
		intrim.setnDEL(trans.getnDEL());
		intrim.setTxDEL(trans.getTxDEL());
		intrim.setSnodeId(trans.getNodeId());
		intrim.setUseCase(trans.getUseCase());
		intrim.setTxType(trans.getTxType());
		intrim.setAirlocknode(trans.getAirlocknode());
		intrim.setFDPeriodId(trans.getFDPeriodId());
		if (trans.getSubType() != null) {
			intrim.setContraType(trans.getSubType());
		} else {
			intrim.setContraType(null);
		}

		if (trans.getContingentId() != null) {
			intrim.setContingentId(trans.getContingentId());
		} else {
			intrim.setContingentId(null);
		}

		if (trans.getShortTransferId() != null) {
			intrim.setShortTransferId(trans.getShortTransferId());
		} else {
			intrim.setShortTransferId(null);
		}

		return intrim;
	}

	public TranxInterimData updateShortTransactionDetails(TransactionDetails trans, TranxInterimData intrim,
			String extra) {
		String var10001 = trans.getTransactionId();
		intrim.setNodeId(this.transactionNodeInfo.getNodeid());
		intrim.setTransactionId(var10001 + extra);
		intrim.setAssetLotId(trans.getAssetLotId());
		intrim.setContraIdDel(
				String.valueOf(ThreadLocalRandom.current().nextInt(100000) + this.transactionNodeInfo.getPeriod()));
		intrim.setContraIdRec(
				String.valueOf(ThreadLocalRandom.current().nextInt(100000) + this.transactionNodeInfo.getPeriod()));
		intrim.setTimeStamp(trans.getTimeStamp());
		intrim.setAtype(trans.getAtype());
		intrim.setAsset_name(trans.getAname());
		intrim.setPeriod(trans.getPeriod());
		intrim.setQtyALR(trans.getQtyALR());
		intrim.setQtyALD(trans.getQtyALD());
		intrim.setnREC(trans.getnREC());
		intrim.setTxREC(trans.getTxREC());
		intrim.setnDEL(trans.getnDEL());
		intrim.setTxDEL(trans.getTxDEL());
		intrim.setSnodeId(trans.getNodeId());
		intrim.setUseCase(trans.getUseCase());
		intrim.setTxType(trans.getTxType());
		intrim.setAirlocknode(trans.getAirlocknode());
		intrim.setFDPeriodId(trans.getFDPeriodId());
		if (trans.getSubType() != null) {
			intrim.setContraType(trans.getSubType());
		} else {
			intrim.setContraType(null);
		}

		if (trans.getContingentId() != null) {
			intrim.setContingentId(trans.getContingentId());
		} else {
			intrim.setContingentId(null);
		}

		if (trans.getShortTransferId() != null) {
			intrim.setShortTransferId(trans.getShortTransferId());
		} else {
			intrim.setShortTransferId(null);
		}

		return intrim;
	}

	public TransactionDetails nodeSwitch(TransactionDetails transactionData) {
		String nDel = transactionData.getnREC();
		String nRec = transactionData.getnDEL();
		String txDel = transactionData.getTxREC();
		String txRec = transactionData.getTxDEL();
		transactionData.setnDEL(nDel);
		transactionData.setnREC(nRec);
		transactionData.setTxDEL(txDel);
		transactionData.setTxREC(txRec);
		return transactionData;
	}

	public TransactionDetails assetManagerALD(TransactionDetails transactionData, String ald) {
		transactionData.setAssetLotId(ald);
		return transactionData;
	}

	public TransactionDetails assetManagerALR(Optional<TranxInterimData> intrim,
			TransactionDetails transactionDetails) {
		transactionDetails.setTransactionId(((TranxInterimData) intrim.get()).getTransactionId());
		transactionDetails.setAssetLotId(((TranxInterimData) intrim.get()).getAssetLotId());
		transactionDetails.setTimeStamp(((TranxInterimData) intrim.get()).getTimeStamp());
		transactionDetails.setAtype(((TranxInterimData) intrim.get()).getAtype());
		transactionDetails.setAname(((TranxInterimData) intrim.get()).getAsset_name());
		transactionDetails.setPeriod(((TranxInterimData) intrim.get()).getPeriod());
		transactionDetails.setQtyALR(((TranxInterimData) intrim.get()).getQtyALR());
		transactionDetails.setnREC(((TranxInterimData) intrim.get()).getnREC());
		transactionDetails.setTxREC(((TranxInterimData) intrim.get()).getTxREC());
		transactionDetails.setnDEL(((TranxInterimData) intrim.get()).getnDEL());
		transactionDetails.setTxDEL(((TranxInterimData) intrim.get()).getTxDEL());
		transactionDetails.setNodeId(((TranxInterimData) intrim.get()).getSnodeId());
		transactionDetails.setTxType(((TranxInterimData) intrim.get()).getTxType());
		transactionDetails.setUseCase(((TranxInterimData) intrim.get()).getUseCase());
		transactionDetails.setAirlocknode(((TranxInterimData) intrim.get()).getAirlocknode());
		transactionDetails.setFDPeriodId(((TranxInterimData) intrim.get()).getFDPeriodId());
		if (((TranxInterimData) intrim.get()).getContraType() != null) {
			transactionDetails.setSubType(((TranxInterimData) intrim.get()).getContraType());
		}

		if (((TranxInterimData) intrim.get()).getShortTransferId() != null) {
			transactionDetails.setShortTransferId(((TranxInterimData) intrim.get()).getShortTransferId());
		}

		return transactionDetails;
	}

	public FLInput getFLInputDel(TransactionDetails trans, FLInput flInput) {
		flInput.setNodeId(trans.getnDEL());
		flInput.setPeriod(trans.getPeriod());
		flInput.setTxnId(trans.getTransactionId());
		flInput.setFDPeriodId(trans.getFDPeriodId());
//		flInput.setExtTxnId(extTxnId);
		return flInput;
	}

	public FLInput getFLInputRec(TransactionDetails trans, FLInput flInput, String flAddress) {
		try {
//			log.error("Received FLM details from Del" + trans.toString());
			flInput.setNodeId(trans.getnREC());
			flInput.setPeriod(trans.getPeriod());
			flInput.setTxnId(trans.getTransactionId());
			flInput.setFDPeriodId(trans.getFDPeriodId());
			if (!flAddress.equalsIgnoreCase("empty")) {
				flInput.setShareFlAddress(flAddress);
				flInput.setShareNodeId(trans.getnDEL());
				flInput.setShareFlStatus("UTILIZED");
				flInput.setShareTxnId(trans.getTransactionId().replace("i", ""));
//				log.error("Receival Side Setting FLM details Del" + this.objectMapper.writeValueAsString(flInput));
			}

//			log.error("Receival Side Setting FLM Out details Del" + this.objectMapper.writeValueAsString(flInput));
//		flInput.setExtTxnId(extTxnId);
		} catch (Exception e) {
			log.error(e + "----------");
		}
		return flInput;
	}

	public FLAddress updatedFL(FLAddress fl, FLMAddress flm) {
		fl.setFlID(flm.getFlId());
		fl.setFlAddress(flm.getFlAddress());
		fl.setFlaHashLink(flm.getParentHashLink());
		fl.setFlag(flm.getFlag());
		return fl;
	}

	public FLAddressErrorUpdateDto updatedErrorFL(FLAddressErrorUpdateDto flError, String FlAddress) {
		log.info(flError);
//		flError.setFlID(flm.getFlId());
		flError.setFlAddress(FlAddress);
		flError.setNodeId(this.transactionNodeInfo.getNodeid());
		flError.setPeriod(this.transactionNodeInfo.getPeriod());
		return flError;
	}

	public TranxInterimData updateDELFLDetails(FLAddress flm, TranxInterimData intrim) {
		intrim.setFlIdDEL(flm.getFlID());
		intrim.setFlAddressDEL(flm.getFlAddress());
		intrim.setFlaHashLinkDEL(flm.getFlaHashLink());
		return intrim;
	}

	public TranxInterimData updateRECFLDetails(FLAddress flm, TranxInterimData intrim) {
		intrim.setFlIdREC(flm.getFlID());
		intrim.setFlAddressREC(flm.getFlAddress());
		intrim.setFlaHashLinkREC(flm.getFlaHashLink());
		return intrim;
	}

	public String encrptyDel(TranxInterimData intrim) {
//		this.hd.setnDEL(intrim.getnDEL());
//		this.hd.setFl(intrim.getFlAddressDEL());
//		this.hd.setnREC(intrim.getnREC());
//		this.hd.setAtype(intrim.getAtype());
//		this.hd.setQty(intrim.getAln());
//		return this.hd.toString();
		return intrim.getnDEL();
	}

	public String encrptyRec(TranxInterimData intrim) {
		this.hd.setnDEL(intrim.getnDEL());
//		this.hd.setFl(intrim.getFlAddressREC());
//		this.hd.setnREC(intrim.getnREC());
//		this.hd.setAtype(intrim.getAtype());
//		this.hd.setQty(intrim.getAlr());
//		return this.hd.toString();
		return intrim.getnDEL();
	}

	public FLAddress getDelFL(Optional<TranxInterimData> intrim, FLAddress fl) {
		fl.setFlAddress(((TranxInterimData) intrim.get()).getFlAddressREC());
		fl.setFlID(((TranxInterimData) intrim.get()).getFlIdREC());
		fl.setFlaHashLink(((TranxInterimData) intrim.get()).getFlaHashLinkREC());
		return fl;
	}

	public FLAddress getRecFL(Optional<TranxInterimData> intrim, FLAddress fl) {
		fl.setFlAddress(((TranxInterimData) intrim.get()).getFlAddressDEL());
		fl.setFlID(((TranxInterimData) intrim.get()).getFlIdDEL());
		fl.setFlaHashLink(((TranxInterimData) intrim.get()).getFlaHashLinkDEL());
		return fl;
	}

	public TranxInterimData updateTransactionProcessDetails(TransactionDetails trans, TranxInterimData intrim) {
		intrim.setTransactionId(trans.getTransactionId());
		intrim.setAssetLotId(trans.getAssetLotId());
		intrim.setTimeStamp(trans.getTimeStamp());
		intrim.setAtype(trans.getAtype());
		intrim.setAsset_name(trans.getAname());
		intrim.setPeriod(trans.getPeriod());
		intrim.setQtyALR(trans.getQtyALR());
		intrim.setQtyALD(trans.getQtyALD());
		intrim.setnREC(trans.getnREC());
		intrim.setTxREC(trans.getTxREC());
		intrim.setnDEL(trans.getnDEL());
		intrim.setTxDEL(trans.getTxDEL());
		intrim.setUseCase(trans.getUseCase());
		intrim.setTxType(trans.getTxType());
		intrim.setAirlocknode(trans.getAirlocknode());
		intrim.setFDPeriodId(trans.getFDPeriodId());
		if (trans.getSubType() != null) {
			intrim.setContraType(trans.getSubType());
		} else {
			intrim.setContraType(null);
		}

		if (trans.getContingentId() != null) {
			intrim.setContingentId(trans.getContingentId());
		} else {
			intrim.setContingentId(null);
		}

		if (trans.getSubType().equalsIgnoreCase("Settlement")) {
			trans.setShortTransferId(trans.getTransactionId());
			intrim.setShortTransferId(trans.getShortTransferId());
		}

		return intrim;
	}

	public void storeCOLRData(TranxInterimData intrim) {
		try {
			Date date = new Date();
			Optional<CompleteOwnerShipLots> aldCOLROptional = this.objCOLRRepo.findById(intrim.getAssetLotId());
			if (aldCOLROptional.isEmpty()) {
				aldCOLR.setColrid(intrim.getAssetLotId());
				aldCOLR.setNodeid(intrim.getnDEL());
				aldCOLR.setPeriodId(intrim.getPeriod());
				aldCOLR.setTxDEL(intrim.getTxDEL());
				aldCOLR.setTxREC(intrim.getTxREC());
				aldCOLR.setDelTxID(intrim.getContraIdDel());
				aldCOLR.setRecTxID(intrim.getContraIdRec());
				aldCOLR.setTxREC(intrim.getTxREC());
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
				// aldCOLR.setAl('L');
				aldCOLR.setLinkedAl(' ');
				aldCOLR.setFDPeriodId(intrim.getFDPeriodId());
				aldCOLR.setTransactedDate(intrim.getTimeStamp());
				aldCOLR.setPostedDate(localDateTime.getTimeStamp());
				aldCOLR.setAirlocknode(intrim.getAirlocknode());
				this.objCOLRRepo.save(aldCOLR);
				log.info("ALD COLR Updated Successfully");
			} else {
				log.debug("ALD :" + intrim.getAssetLotId() + " already existed in Datbase");
			}
			
			this.storeALDCOLRData(intrim);
			
			Optional<CompleteOwnerShipLots> alrCOLROptional = this.objCOLRRepo.findById(intrim.getAlr());
			if (alrCOLROptional.isEmpty()) {
				alrCOLR.setColrid(intrim.getAlr());
				alrCOLR.setNodeid(intrim.getnREC());
				alrCOLR.setPeriodId(intrim.getPeriod());
				alrCOLR.setTxDEL(intrim.getTxDEL());
				alrCOLR.setTxREC(intrim.getTxREC());
				alrCOLR.setDelTxID(intrim.getContraIdDel());
				alrCOLR.setRecTxID(intrim.getContraIdRec());
				alrCOLR.setTxREC(intrim.getTxREC());
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

				if ((!intrim.getContraType().equalsIgnoreCase("Obligation")
						&& !intrim.getContraType().equalsIgnoreCase("Loan")
						&& !intrim.getContraType().equalsIgnoreCase("Collateral"))
								&& !intrim.getContraType().equalsIgnoreCase("Reversal")) {
					alrCOLR.setAl('L');
				} else {
					alrCOLR.setAl('R');
				}
				alrCOLR.setFDPeriodId(intrim.getFDPeriodId());
//				alrCOLR.setLinkedAl(' ');
				alrCOLR.setTransactedDate(intrim.getTimeStamp());
				alrCOLR.setPostedDate(localDateTime.getTimeStamp());
				alrCOLR.setAirlocknode(intrim.getAirlocknode());
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
					alnCOLR.setDelTxID(intrim.getContraIdDel());
					alnCOLR.setRecTxID(intrim.getContraIdRec());
					alnCOLR.setTxDEL(intrim.getTxDEL());
					alnCOLR.setTxREC(intrim.getTxREC());
					alnCOLR.setAssetid(intrim.getAtype());
					alnCOLR.setAsset_name(intrim.getAsset_name());
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
					alnCOLR.setAl('L');
					alnCOLR.setFDPeriodId(intrim.getFDPeriodId());
					alnCOLR.setLinkedAl(' ');
					alnCOLR.setTransactedDate(intrim.getTimeStamp());
					alnCOLR.setPostedDate(localDateTime.getTimeStamp());
					alnCOLR.setAirlocknode(intrim.getAirlocknode());
					this.objCOLRRepo.save(alnCOLR);

					log.info("ALN COLR Updated Successfully");
				} else {
					log.debug("ALN :" + intrim.getAln() + " already existed in Datbase");
				}

			}
			log.info("ALN  COLR is not required due to ALD  and ALR are equal");
		} catch (Exception exception) {
			log.info(exception + " ecxception occurred while inserting data COLR");
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

	public void storeALDCOLRData(TranxInterimData intrim) {
		try {
			Date date = new Date();
			Optional<ALDCompleteOwnerShipLots> aldCOLROptional = this.objALDRepo.findById(intrim.getAssetLotId());
			if (aldCOLROptional.isEmpty()) {
				aldCOLRObj.setColrid(intrim.getAssetLotId());
				aldCOLRObj.setNodeid(intrim.getnDEL());
				aldCOLRObj.setPeriodId(intrim.getPeriod());
				aldCOLRObj.setDelTxID(intrim.getContraIdDel());
				aldCOLRObj.setRecTxID(intrim.getContraIdRec());
				aldCOLRObj.setTxDEL(intrim.getTxDEL());
				aldCOLRObj.setTxREC(intrim.getTxREC());
				aldCOLRObj.setAssetid(intrim.getAtype());
				aldCOLRObj.setAsset_name(intrim.getAsset_name());
				aldCOLRObj.setCreated_Quantity(intrim.getQtyALD());
				aldCOLRObj.setnDelID(intrim.getnDEL());
				aldCOLRObj.setnDelFLID(intrim.getFlIdDEL());
				aldCOLRObj.setnDelFLAddress(intrim.getFlAddressDEL());
				aldCOLRObj.setnRecID(intrim.getnREC());
				aldCOLRObj.setnRecFLID(intrim.getFlIdREC());
				aldCOLRObj.setNRecFLAddress(intrim.getFlAddressREC());
				aldCOLRObj.settPRecALRID(intrim.getAlr());
				aldCOLRObj.setTxDelhash(intrim.getTranxHash());
				aldCOLRObj.setTx_Rechash(intrim.getRecTranxHash());
				if ((!intrim.getContraType().equalsIgnoreCase("Obligation")
						|| !intrim.getContraType().equalsIgnoreCase("Loan")
						|| !intrim.getContraType().equalsIgnoreCase("Collateral"))
								&& !intrim.getContraType().equalsIgnoreCase("Reversal")) {
					aldCOLRObj.setAl('L');
				} else {
					aldCOLRObj.setAl('P');
				}

				aldCOLRObj.setFDPeriodId(intrim.getFDPeriodId());
				aldCOLRObj.setLinkedAl(' ');
				aldCOLRObj.setTransactedDate(intrim.getTimeStamp());
				aldCOLRObj.setAirlocknode(intrim.getAirlocknode());
				aldCOLRObj.setPostedDate(localDateTime.getTimeStamp());
				this.objALDRepo.save(aldCOLRObj);
				log.info("ALD COLR Updated Successfully");
			} else {
				log.debug("ALD :" + intrim.getAssetLotId() + " already existed in Datbase");
			}
		} catch (Exception exception) {
			log.error(exception + " ecxception occurred while Creating ALD COLR for Umatched Tx");
		}
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

	public void updateReceivedTxData(TransactionDetails transactionData) {

		try {
			Optional<ReceivedTransactionData> recTxInfo = this.receivedTrnsactionRepo
					.findById(transactionData.getTransactionId());
			if (recTxInfo.isEmpty()) {
				receivedTxData.setTransactionId(transactionData.getTransactionId());
				receivedTxData.setAtype(transactionData.getAtype());
				receivedTxData.setPeriod(transactionData.getPeriod());
				receivedTxData.setQty(transactionData.getQtyALR());
				receivedTxData.setnDEL(transactionData.getnDEL());
				receivedTxData.setnREC(transactionData.getnREC());
				receivedTxData.setTxType(transactionData.getSubType());
//				receivedTxData.setTimeStamp(Timestamp.valueOf(time));
				receivedTrnsactionRepo.save(receivedTxData);
			} else {
				log.debug(transactionData.getTransactionId() + " already existed in DataBase ");
			}

		} catch (Exception exception) {
			log.error(exception + " exception occurred while storing Received transaction details");
		}
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
				validationProcessTime.setContra_type("OutBound");
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

	public void updateFutureTxDetails(String usecase, String txId, String payRecId, String nDel, String nRec,
			String contingentId, String fdPeriodId) {
		try {
//			long diffInmillSeconds = end.getTime() - start.getTime();
//			log.info("Processing Time for transaction " + tx_ID + "in secs: " + diffInmillSeconds);
			Optional<FutureDatedTx> futureTxInfo = this.futTxRepo.findById(payRecId);
			if (futureTxInfo.isEmpty()) {
				log.info("Storing new Future Transction Data into Database");
				futureTxData.setTxid(txId);
				futureTxData.setPayrecid(payRecId);
				futureTxData.setnDel(nDel);
				futureTxData.setnRec(nRec);
				futureTxData.setContingentId(contingentId);
				futureTxData.setFDPeriodId(fdPeriodId);
				futureTxData.setUseCase(usecase);
				futureTxData.setStatus("Open");
				futureTxData.setStatus1("Open");

//				ledgerProcessTIme.setTotalProcessTime(diffInmillSeconds);
				this.futTxRepo.save(futureTxData);
			} else {
				log.debug(payRecId + " already existed in Ledger Process DB");
			}
		} catch (Exception e) {
			log.error("Error while storing Future Tx Details " + e);
		}
	}

	public void updateClosedFutureTxDetails(String payRecId) {
		try {
//			long diffInmillSeconds = end.getTime() - start.getTime();
//			log.info("Processing Time for transaction " + tx_ID + "in secs: " + diffInmillSeconds);
			Optional<FutureDatedTx> futureTxInfo = this.futTxRepo.findById(payRecId);
			if (!futureTxInfo.isEmpty()) {
				log.info("Storing new Future Transction Data into Database");

				this.futTxRepo.updateStatusClosedTx(payRecId);

//				ledgerProcessTIme.setTotalProcessTime(diffInmillSeconds);

			} else {
				log.debug(payRecId + " not existed in Future Tx table");
			}
		} catch (Exception e) {
			log.error("Error while updating Future Tx Details " + e);
		}
	}

	public FutureDatedTx findPayRecID(String payRecId) {
		Optional<FutureDatedTx> futureTxInfo = this.futTxRepo.findById(payRecId);
		if (!futureTxInfo.isEmpty()) {
			log.info("Getting Future Rusult for Pay/Rec id: " + payRecId);
			if (futureTxInfo.get().getStatus().equalsIgnoreCase("Open")) {
				return futureTxInfo.get();
			}
		}
		log.info("Future Tx result for id: " + payRecId + " is " + futureTxInfo.get().toString());
		return futureTxInfo.get();
	}

	public String findFLAddDEL(String txid) {
		String delfL = "";
		log.info("Going to fetch existing DelFl address for: " + txid);
		Optional<TranxInterimData> tranxdata = this.tmpdb.findById(txid);
		if (!tranxdata.isEmpty()) {
			delfL = tranxdata.get().getFlAddressDEL();
		}
		log.info("DelFl address for: " + txid + " is " + delfL);
		return delfL;

	}

	public String findFLAddRec(String txid) {
		String recfL = "";
		log.info("Going to fetch existing RecFl address for: " + txid);
		Optional<TranxInterimData> tranxdata = this.tmpdb.findById(txid);
		if (!tranxdata.isEmpty()) {
			recfL = tranxdata.get().getFlAddressREC();
		}
		log.info("DelFl address for: " + txid + " is " + recfL);
		return recfL;

	}

}
