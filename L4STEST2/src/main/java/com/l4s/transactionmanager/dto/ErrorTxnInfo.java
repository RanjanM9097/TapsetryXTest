package com.l4s.transactionmanager.dto;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Err_Txn_Record")
public class ErrorTxnInfo {

	@Id
	private String errId;
	private String timeStamp;
	private String periodId;
	private String txId;
	private String module;
	private String errorMessage;

//	public ErrorTxnInfo(String errId, String timeStamp, String periodId, String txId, String module,
//			String errorMessage) {
//		super();
//		this.errId = errId;
//		this.timeStamp = timeStamp;
//		this.periodId = periodId;
//		this.txId = txId;
//		this.module = module;
//		this.errorMessage = errorMessage;
//	}

	public String getErrId() {
		return errId;
	}

	public void setErrId(String errId) {
		this.errId = errId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getPeriodId() {
		return periodId;
	}

	public void setPeriodId(String periodId) {
		this.periodId = periodId;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
