package com.l4s.transactionmanager.dto;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "futuredatetx")
public class FutureDatedTx {

	@Id
	private String payrecid;
	private String txid;
	private String status;
	private String nDel;
	private String nRec;
	private String contingentId;
	private String FDPeriodId;
	private String useCase;
	private String status1;

	public String getPayrecid() {
		return payrecid;
	}

	public void setPayrecid(String payrecid) {
		this.payrecid = payrecid;
	}

	public String getTxid() {
		return txid;
	}

	public void setTxid(String txid) {
		this.txid = txid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getnDel() {
		return nDel;
	}

	public void setnDel(String nDel) {
		this.nDel = nDel;
	}

	public String getnRec() {
		return nRec;
	}

	public void setnRec(String nRec) {
		this.nRec = nRec;
	}

	public String getContingentId() {
		return contingentId;
	}

	public void setContingentId(String contingentId) {
		this.contingentId = contingentId;
	}

	public String getFDPeriodId() {
		return FDPeriodId;
	}

	public void setFDPeriodId(String fDPeriodId) {
		FDPeriodId = fDPeriodId;
	}

	public String getUseCase() {
		return useCase;
	}

	public void setUseCase(String useCase) {
		this.useCase = useCase;
	}

	public String getStatus1() {
		return status1;
	}

	public void setStatus1(String status1) {
		this.status1 = status1;
	}

	@Override
	public String toString() {
		return "FutureDatedTx [payrecid=" + payrecid + ", txid=" + txid + ", status=" + status + ", nDel=" + nDel
				+ ", nRec=" + nRec + ", contingentId=" + contingentId + ", FDPeriodId=" + FDPeriodId + ", useCase="
				+ useCase + "]";
	}

}