package com.l4s.transactionmanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.l4s.transactionmanager.dto.BroadcastContraTransactionDelData;
import com.l4s.transactionmanager.dto.ContraTransactionDelDetails;

@Repository
public interface ContraTransactionDel extends JpaRepository<ContraTransactionDelDetails, String> {

//	void save(BroadcastContraTransactionRecData contraTxRecInfo);

	void save(BroadcastContraTransactionDelData contraTxDelInfo);

	@Transactional
	@Modifying(flushAutomatically = true)
	@Query(value = "update del_contra_transaction set postperiodfk = :peroiodid  where contraid= :transactionid", nativeQuery = true)
	void updatePeriodDelContraTransaction(@Param("peroiodid") String peroiodid,
			@Param("transactionid") String transactionid);
}
