package com.l4s.transactionmanager.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.l4s.transactionmanager.dto.BroadcastContraTransactionDelData;

@Repository
public interface BroadCastContraTransactionDEL extends JpaRepository<BroadcastContraTransactionDelData, String> {

//	@Modifying
//	@Query(value = "DELETE FROM contra_transaction_del  WHERE contraid = ?1", nativeQuery = true)
//	@Transactional
//	void deleteByContraIDNative(String contraid);
	List<BroadcastContraTransactionDelData> findByStatus(String status);
}
