package com.l4s.transactionmanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.l4s.transactionmanager.dto.ReceivedTransactionData;

@Repository
public interface ReceviedTransactionRepo extends JpaRepository<ReceivedTransactionData, String> {
}
