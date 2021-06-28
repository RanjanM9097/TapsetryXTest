package com.l4s.transactionmanager.dto;

public class FLMAddress {
	private String flAddress;
	private String parentHashLink;
	private String nodeId;
	private String flId;
	private String errorMsg;
	private String flag;

	public FLMAddress() {
		// TODO Auto-generated constructor stub
	}

	public FLMAddress(String flAddress, String parentHash, String nodeId, String flId, String errorMsg, String flag) {
		this.flAddress = flAddress;
		this.parentHashLink = parentHash;
		this.nodeId = nodeId;
		this.flId = flId;
		this.errorMsg = errorMsg;
		this.flag = flag;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getNodeId() {
		return this.nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getFlAddress() {
		return this.flAddress;
	}

	public void setFlAddress(String flAddress) {
		this.flAddress = flAddress;
	}

	public String getParentHashLink() {
		return this.parentHashLink;
	}

	public void setParentHashLink(String parentHashLink) {
		this.parentHashLink = parentHashLink;
	}

	public String getFlId() {
		return this.flId;
	}

	public void setFlId(String flId) {
		this.flId = flId;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		return "FLMAddress [flAddress=" + flAddress + ", parentHashLink=" + parentHashLink + ", nodeId=" + nodeId
				+ ", flId=" + flId + ", errorMsg=" + errorMsg + ", flag=" + flag + "]";
	}

}
