package com.l4s.transactionmanager.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.l4s.transactionmanager.dto.CustomerDetails;

@Repository
public interface CustomerInfo extends JpaRepository<CustomerDetails, String> {
	
	@Query(value="select DISTINCT(a_type) from all_customer_info",nativeQuery=true)
	public Set<String> getAssets();	

}
