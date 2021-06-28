package com.l4s.transactionmanager.dao;

public class TransactionNodeInfo {
	private String nodeId = "";
	private String period = "";

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getNodeid() {
		return nodeId;
	}

	public String setNodeid(String nodeId) {
		return this.nodeId = nodeId;
	}
}
