package com.l4s.transactionmanager.dto;

import java.sql.Timestamp;

public class TransactionDetails {

	private String transactionId;
	private Timestamp timeStamp;
	private String atype;
	private String period;
	private String assetLotId;
	private float qtyALD;
	private float qtyALR;
	private String nREC;
	private String txREC;
	private String nDEL;
	private String txDEL;
	private String txType;
	private String contingentId;
	private String shortTransferId;
	private String aname;
	private String nodeId;
	private String flId;
	private String Flag;
	private String errorMessage;
	private String useCase;
	private String subType;
	private String destinationNode;
	private String destinationTxParty;
	private String airlocknode;
	private String FDPeriodId;

	public String getDestinationNode() {
		return destinationNode;
	}

	public void setDestinationNode(String destinationNode) {
		this.destinationNode = destinationNode;
	}

	public String getDestinationTxParty() {
		return destinationTxParty;
	}

	public void setDestinationTxParty(String destinationTxParty) {
		this.destinationTxParty = destinationTxParty;
	}

	public String getFlId() {
		return flId;
	}

	public void setFlId(String flId) {
		this.flId = flId;
	}

	public String getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Timestamp getTimeStamp() {
		return this.timeStamp;
	}

	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getAtype() {
		return this.atype;
	}

	public void setAtype(String atype) {
		this.atype = atype;
	}

	public String getPeriod() {
		return this.period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getAssetLotId() {
		return this.assetLotId;
	}

	public void setAssetLotId(String assetLotId) {
		this.assetLotId = assetLotId;
	}

	public float getQtyALD() {
		return this.qtyALD;
	}

	public void setQtyALD(float qtyALD) {
		this.qtyALD = qtyALD;
	}

	public float getQtyALR() {
		return this.qtyALR;
	}

	public void setQtyALR(float qtyALR) {
		this.qtyALR = qtyALR;
	}

	public String getnREC() {
		return this.nREC;
	}

	public void setnREC(String nREC) {
		this.nREC = nREC;
	}

	public String getTxREC() {
		return this.txREC;
	}

	public void setTxREC(String txREC) {
		this.txREC = txREC;
	}

	public String getnDEL() {
		return this.nDEL;
	}

	public void setnDEL(String nDEL) {
		this.nDEL = nDEL;
	}

	public String getTxDEL() {
		return this.txDEL;
	}

	public void setTxDEL(String txDEL) {
		this.txDEL = txDEL;
	}

	public String getTxType() {
		return this.txType;
	}

	public void setTxType(String txType) {
		this.txType = txType;
	}

	public String getContingentId() {
		return this.contingentId;
	}

	public void setContingentId(String contingentId) {
		this.contingentId = contingentId;
	}

	public String getShortTransferId() {
		return this.shortTransferId;
	}

	public void setShortTransferId(String shortTransferId) {
		this.shortTransferId = shortTransferId;
	}

	public String getAname() {
		return aname;
	}

	public void setAname(String aname) {
		this.aname = aname;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getFlag() {
		return Flag;
	}

	public void setFlag(String flag) {
		Flag = flag;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getUseCase() {
		return useCase;
	}

	public void setUseCase(String useCase) {
		this.useCase = useCase;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getAirlocknode() {
		return airlocknode;
	}

	public void setAirlocknode(String airlocknode) {
		this.airlocknode = airlocknode;
	}

	public String getFDPeriodId() {
		return FDPeriodId;
	}

	public void setFDPeriodId(String fDPeriodId) {
		FDPeriodId = fDPeriodId;
	}

	@Override
	public String toString() {
		return "TransactionDetails [transactionId=" + transactionId + ", timeStamp=" + timeStamp + ", atype=" + atype
				+ ", period=" + period + ", assetLotId=" + assetLotId + ", qtyALD=" + qtyALD + ", qtyALR=" + qtyALR
				+ ", nREC=" + nREC + ", txREC=" + txREC + ", nDEL=" + nDEL + ", txDEL=" + txDEL + ", txType=" + txType
				+ ", contingentId=" + contingentId + ", shortTransferId=" + shortTransferId + ", aname=" + aname
				+ ", nodeId=" + nodeId + ", flId=" + flId + ", Flag=" + Flag + ", errorMessage=" + errorMessage
				+ ", useCase=" + useCase + ", subType=" + subType + "]";
	}

}
