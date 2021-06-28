package com.l4s.transactionmanager.dto;

public class FLAddressErrorUpdateDto {
	
	public String nodeId;
	public String period;
    public String txnId;
    public String flAddress;
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	public String getFlAddress() {
		return flAddress;
	}
	public void setFlAddress(String flAddress) {
		this.flAddress = flAddress;
	}
	@Override
	public String toString() {
		return "FLAddressErrorUpdateDto [nodeId=" + nodeId + ", period=" + period + ", txnId=" + txnId + ", flAddress="
				+ flAddress + "]";
	}
	
}
