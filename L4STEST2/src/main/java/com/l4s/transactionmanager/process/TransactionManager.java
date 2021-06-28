package com.l4s.transactionmanager.process;

import com.l4s.transactionmanager.dto.TransactionDetails;

public interface TransactionManager {
	String calculateALN(float qty, float ald) throws Exception;

	void updateLedger(String id, String Proc) throws Exception;

	void processTransaction(TransactionDetails transactionDetails) throws Exception;
}
