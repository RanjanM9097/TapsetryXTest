package com.l4s.transactionmanager.dto;

public class ShareNodeInfoDto {
	public String nodeId;
	public String flAddress;
	public String parentHash;
//	public String nodeRec;
	private String FDPeriodId;
	private String ALD;

//	public String getNodeRec() {
//		return nodeRec;
//	}
//
//	public void setNodeRec(String nodeRec) {
//		this.nodeRec = nodeRec;
//	}

	public ShareNodeInfoDto() {
	}

//	public ShareNodeInfoDto(String nodeId, String flAddress, String parentHash,String nodeRec) {
//		this.nodeId = nodeId;
//		this.flAddress = flAddress;
//		this.parentHash = parentHash;
//		this.nodeRec=nodeRec;
//	}
	
	public String getFDPeriodId() {
		return FDPeriodId;
	}

	public ShareNodeInfoDto(String nodeId, String flAddress, String parentHash, String fDPeriodId, String aLD) {
	super();
	this.nodeId = nodeId;
	this.flAddress = flAddress;
	this.parentHash = parentHash;
	this.FDPeriodId = fDPeriodId;
	this.ALD = aLD;
}

	public void setFDPeriodId(String fDPeriodId) {
		FDPeriodId = fDPeriodId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getFlAddress() {
		return flAddress;
	}

	public void setFlAddress(String flAddress) {
		this.flAddress = flAddress;
	}

	public String getParentHash() {
		return parentHash;
	}

	public void setParentHash(String parentHash) {
		this.parentHash = parentHash;
	}

	public String getALD() {
		return ALD;
	}

	public void setALD(String aLD) {
		ALD = aLD;
	}
	
}
