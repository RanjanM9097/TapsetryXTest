package com.example.demo.repository;



import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.NodeAInfo;

@Repository

public interface FlamRepository extends JpaRepository<NodeAInfo, Integer> {
	@Transactional
	@Modifying
	@Query("update NodeAInfo ni set ni.status =:status where ni.currentLayer =:currentLayer")
	int updateByStatus(@Param("status") String status,@Param("currentLayer") int layer);

	
	
	
}
