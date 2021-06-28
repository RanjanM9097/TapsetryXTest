package com.l4s.transactionmanager.dao;


import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.l4s.transactionmanager.dto.CompleteOwnerShipLots;

@Repository
public interface CompleteOwnerShipLotRepo extends CrudRepository<CompleteOwnerShipLots, String> {
	@Transactional
	@Modifying(flushAutomatically = true)
	@Query("update CompleteOwnerShipLots set created_Quantity=0 where colrid=:colrid")
	int updateALN(@Param("colrid") String colrid);
	
	@Transactional
	@Modifying(flushAutomatically = true)
	@Query("update CompleteOwnerShipLots set period_id=:periodid where colrid =:asset_lot_id or colrid=:alr")
	int updateCOLRPid(@Param("periodid") String  periodid,@Param("asset_lot_id") String  asset_lot_id, @Param("alr") String alr);
}


