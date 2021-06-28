package com.l4s.transactionmanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.l4s.transactionmanager.dto.TranxInterimData;

public interface TempDb extends JpaRepository<TranxInterimData, String> {
	
	@Transactional
	@Modifying
	@Query(value = "update transact_table set period = :peroiodid  where transaction_id= :transactionid", nativeQuery = true)
	void updatePeriodTransaction(@Param("peroiodid") String peroiodid,@Param("transactionid") String transactionid);
}
