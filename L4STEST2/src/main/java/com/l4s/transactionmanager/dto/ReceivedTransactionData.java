package com.l4s.transactionmanager.dto;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "recevied_transactions")
public class ReceivedTransactionData {
	@Id
	private String transactionId;
//	private Timestamp timeStamp;
	private String atype;
	private String period;
	private float qty;
	private String nREC;
//	private String txREC;
	private String nDEL;
//	private String txDEL;
	private String txType;

	public String getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

//	public Timestamp getTimeStamp() {
//		return this.timeStamp;
//	}
//
//	public void setTimeStamp(Timestamp timeStamp) {
//		this.timeStamp = timeStamp;
//	}

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

	public float getQty() {
		return this.qty;
	}

	public void setQty(float qty) {
		this.qty = qty;
	}

	public String getnREC() {
		return this.nREC;
	}

	public void setnREC(String nREC) {
		this.nREC = nREC;
	}

//	public String getTxREC() {
//		return this.txREC;
//	}
//
//	public void setTxREC(String txREC) {
//		this.txREC = txREC;
//	}

	public String getnDEL() {
		return this.nDEL;
	}

	public void setnDEL(String nDEL) {
		this.nDEL = nDEL;
	}

//	public String getTxDEL() {
//		return this.txDEL;
//	}
//
//	public void setTxDEL(String txDEL) {
//		this.txDEL = txDEL;
//	}

	public String getTxType() {
		return this.txType;
	}

	public void setTxType(String txType) {
		this.txType = txType;
	}
}
