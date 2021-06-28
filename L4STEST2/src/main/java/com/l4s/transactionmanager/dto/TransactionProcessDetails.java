package com.l4s.transactionmanager.dto;



import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "transaction_process_time_info")
public class TransactionProcessDetails {
	@Id
	@Column(name="transaction_id")
	private String transactionId;
	@Column(name="start_time")
	private Timestamp startTime;
	@Column(name="end_time")
	private Timestamp endTime;
	@Column(name="total_process_time")
	private long totalProcessTime;
	@Column(name="contra_mtach_status")

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

	public long getTotalProcessTime() {
		return totalProcessTime;
	}

	public void setTotalProcessTime(long totalProcessTime) {
		this.totalProcessTime = totalProcessTime;
	}	

}
