package com.l4s.transactionmanager.dto;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "validation_process")
public class ValidationProcessDetails {
	@Id
	@Column(name = "transaction_id")
	private String transactionId;
	@Column(name = "contra_type")
	private String contra_type;
	@Column(name = "start_time")
	private Timestamp startTime;
	@Column(name = "end_time")
	private Timestamp endTime;
	@Column(name = "period_id")
	private String period_id;


	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	
	public String getPeriod_id() {
		return period_id;
	}

	public void setPeriod_id(String period_id) {
		this.period_id = period_id;
	}

	public String getContra_type() {
		return contra_type;
	}

	public void setContra_type(String contra_type) {
		this.contra_type = contra_type;
	}
	
}
