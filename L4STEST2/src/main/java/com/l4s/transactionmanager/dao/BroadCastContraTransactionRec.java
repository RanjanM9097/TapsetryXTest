package com.l4s.transactionmanager.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.l4s.transactionmanager.dto.BroadcastContraTransactionRecData;

@Repository
public interface BroadCastContraTransactionRec extends CrudRepository<BroadcastContraTransactionRecData, String> {
//	@Modifying
//	@Query(value = "DELETE FROM contra_transaction_rec  WHERE contraid = ?1", nativeQuery = true)
//	@Transactional
//	void deleteByContraIDNative(String contraid);
}

