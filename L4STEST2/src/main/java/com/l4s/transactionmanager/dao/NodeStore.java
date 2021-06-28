package com.l4s.transactionmanager.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.l4s.transactionmanager.dto.NodeDetails;

@Repository
public interface NodeStore extends JpaRepository<NodeDetails, String> {
	@Query(value="SELECT * FROM allnodes nodeinfo WHERE nodeinfo.node_id !=?1 and nodeinfo.nodeid !=?2",nativeQuery = true)
	public List<NodeDetails> getCustomersOtherNode(String nodeDelId, String nodeRecId);
}
