package com.l4s.transactionmanager.dto;

public class FLInput {
	public String nodeId;
	public String period;
	public String txnId;
	public String shareNodeId;
	public String shareFlAddress;
	public String shareFlStatus;
	public String shareTxnId;
	public String FDPeriodId;
//	public String extTxnId;

	public String getNodeId() {
		return this.nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getPeriod() {
		return this.period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getTxnId() {
		return this.txnId;
	}

	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	public String getShareNodeId() {
		return shareNodeId;
	}

	public void setShareNodeId(String shareNodeId) {
		this.shareNodeId = shareNodeId;
	}

	public String getShareFlAddress() {
		return shareFlAddress;
	}

	public void setShareFlAddress(String shareFlAddress) {
		this.shareFlAddress = shareFlAddress;
	}

	public String getShareFlStatus() {
		return shareFlStatus;
	}

	public void setShareFlStatus(String shareFlStatus) {
		this.shareFlStatus = shareFlStatus;
	}

	public String getShareTxnId() {
		return shareTxnId;
	}

	public void setShareTxnId(String shareTxnId) {
		this.shareTxnId = shareTxnId;
	}

	public String getFDPeriodId() {
		return FDPeriodId;
	}

	public void setFDPeriodId(String fDPeriodId) {
		FDPeriodId = fDPeriodId;
	}

	@Override
	public String toString() {
		return "FLInput [nodeId=" + nodeId + ", period=" + period + ", txnId=" + txnId + ", shareNodeId=" + shareNodeId
				+ ", shareFlAddress=" + shareFlAddress + ", shareFlStatus=" + shareFlStatus + ", shareTxnId="
				+ shareTxnId + "]";
	}

}
