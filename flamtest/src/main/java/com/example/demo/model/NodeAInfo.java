package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "FLAM_NodeB")
public class NodeAInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	public String nodeId;
	public String flAddress;
	public String status;
	public String flId;
	public int layer;
	public int currentLayer;
	public String period;
	public int branchNo;
	public String parentFlAddr;
	public String parentHashLink;
	public String childNodesFl;
	public String flamDate;
	public String assignTime;
	public String confirmedTime;
	public String linkedTime;
	public String txId;
	

	public NodeAInfo() {
		
	}
	
	
	public NodeAInfo(String nodeId, int layer, String period, int branch,String date) {
		this.nodeId = nodeId;
		this.layer = layer;
		this.period = period;
		this.branchNo = branch;
		this.flamDate = date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFlId() {
		return flId;
	}

	public void setFlId(String flId) {
		this.flId = flId;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public int getBranchNo() {
		return branchNo;
	}

	public void setBranchNo(int branchNo) {
		this.branchNo = branchNo;
	}

	public String getParentFlAddr() {
		return parentFlAddr;
	}

	public void setParentFlAddr(String parentFlAddr) {
		this.parentFlAddr = parentFlAddr;
	}

	public String getParentHashLink() {
		return parentHashLink;
	}

	public void setParentHashLink(String parentHashLink) {
		this.parentHashLink = parentHashLink;
	}

	public String getChildNodesFl() {
		return childNodesFl;
	}

	public void setChildNodesFl(String childNodesFl) {
		this.childNodesFl = childNodesFl;
	}


	public String getFlamDate() {
		return flamDate;
	}


	public void setFlamDate(String flamDate) {
		this.flamDate = flamDate;
	}


	public String getAssignTime() {
		return assignTime;
	}


	public void setAssignTime(String assignTime) {
		this.assignTime = assignTime;
	}


	public int getCurrentLayer() {
		return currentLayer;
	}


	public void setCurrentLayer(int currentLayer) {
		this.currentLayer = currentLayer;
	}


	public String getConfirmedTime() {
		return confirmedTime;
	}


	public void setConfirmedTime(String confirmedTime) {
		this.confirmedTime = confirmedTime;
	}


	public String getLinkedTime() {
		return linkedTime;
	}


	public void setLinkedTime(String linkedTime) {
		this.linkedTime = linkedTime;
	}


	public String getTxId() {
		return txId;
	}


	public void setTxId(String txId) {
		this.txId = txId;
	}


	@Override
	public String toString() {
		return "NodeAInfo [id=" + id + ", nodeId=" + nodeId + ", flAddress=" + flAddress + ", status=" + status
				+ ", flId=" + flId + ", layer=" + layer + ", currentLayer=" + currentLayer + ", period=" + period
				+ ", branchNo=" + branchNo + ", parentFlAddr=" + parentFlAddr + ", parentHashLink=" + parentHashLink
				+ ", childNodesFl=" + childNodesFl + ", flamDate=" + flamDate + ", assignTime=" + assignTime
				+ ", confirmedTime=" + confirmedTime + ", linkedTime=" + linkedTime + ", txId=" + txId + "]";
	}

}
