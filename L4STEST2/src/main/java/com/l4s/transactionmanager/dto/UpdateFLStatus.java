package com.l4s.transactionmanager.dto;

public class UpdateFLStatus {
	public String nodeId;
	public String flAddress;
	public String status;
	private String FDPeriodId;
	private String ALD;
	private String periodId;

	public String getPeriodId() {
		return periodId;
	}

	public void setPeriodId(String periodId) {
		this.periodId = periodId;
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

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFDPeriodId() {
		return FDPeriodId;
	}

	public void setFDPeriodId(String fDPeriodId) {
		this.FDPeriodId = fDPeriodId;
	}

	public String getALD() {
		return ALD;
	}

	public void setALD(String aLD) {
		ALD = aLD;
	}

	public UpdateFLStatus(String nodeId, String flAddress, String status, String fDPeriodId, String aLD,String periodId) {
		super();
		this.nodeId = nodeId;
		this.flAddress = flAddress;
		this.status = status;
		this.FDPeriodId = fDPeriodId;
		this.ALD = aLD;
		this.periodId=periodId;
	}

	

	
}
