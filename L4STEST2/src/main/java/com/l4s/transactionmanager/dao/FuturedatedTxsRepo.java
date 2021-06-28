package com.l4s.transactionmanager.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.l4s.transactionmanager.dto.FutureDatedTx;

@Repository
public interface FuturedatedTxsRepo extends JpaRepository<FutureDatedTx, String> {

	List<FutureDatedTx> findByStatus(String status);
	@Transactional
	@Modifying(flushAutomatically = true)
	@Query(value = "update futuredatetx set status = 'Closed' where status='Open' and payrecid= :payrecid", nativeQuery = true)
	void updateStatusClosedTx(@Param("payrecid") String payrecid);
	@Query(value = "SELECT * FROM futuredatetx WHERE payrecid=?1", nativeQuery = true)
	public String findPayRecId(String payRecId);

}
