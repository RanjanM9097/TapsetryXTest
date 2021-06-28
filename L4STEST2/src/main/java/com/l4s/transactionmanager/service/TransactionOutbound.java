
package com.l4s.transactionmanager.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.l4s.transactionmanager.dao.CustomerInfo;
import com.l4s.transactionmanager.dao.KeyStore;
import com.l4s.transactionmanager.dao.NodeStore;
import com.l4s.transactionmanager.dao.PublicKeyStore;
import com.l4s.transactionmanager.dao.TransactionNodeInfo;
import com.l4s.transactionmanager.dto.CustomerDetails;
import com.l4s.transactionmanager.dto.KeyEntities;
import com.l4s.transactionmanager.dto.NewAssetsDetails;
import com.l4s.transactionmanager.dto.NodeDetails;
import com.l4s.transactionmanager.dto.PublicKeyEntities;
import com.l4s.transactionmanager.dto.TranxInterimData;
import com.l4s.transactionmanager.dto.UpdateFLStatus;
import com.l4s.transactionmanager.process.TransactionManagerImpl;
import com.l4s.transactionmanager.process.UrlBuilder;
import com.l4s.transactionmanager.security.GenerateKeys;

@RestController
public class TransactionOutbound {
	private static Logger log = LogManager.getLogger(TransactionOutbound.class);
	@Autowired
	CustomerInfo customerInfo;
	@Autowired
	GenerateKeys generateKeys;
	@Autowired
	PublicKeyStore publicKeyStore;
	@Autowired
	PublicKeyEntities keyEntity;
	@Autowired
	KeyStore keystore;
	@Autowired
	TransactionNodeInfo transactionNodeInfo;
	@Autowired
	UrlBuilder urlBuilder;
	@Autowired
	NodeStore nodeStore;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	TransactionManagerImpl objTransactionMgrImpl;

	@PostMapping
	@RequestMapping({ "tx_prereq_response/{period}" })
	public String preReqResponse(@RequestBody String Node, @PathVariable String period) {
		String customerID = "";
		String Nodeid = Node;
		try {
			if (!Nodeid.isBlank()) {
				this.transactionNodeInfo.setNodeid(Nodeid);
				this.transactionNodeInfo.setPeriod(period);
				log.info("Node Id from Node Manager Module: " + Nodeid);
				log.info("Period Id from Node Manager Module: " + period);
//				switch (Nodeid) {
//				case "NodeA":
//					customerlist.add("0001A");
//					customerlist.add("0002A");
//					customerlist.add("0003A");
//					break;
//				case "NodeB":
//					customerlist.add("0001B");
//					customerlist.add("0002B");
//					customerlist.add("0003B");
//					break;
//				case "NodeC":
//					customerlist.add("0001C");
//					customerlist.add("0002C");
//					customerlist.add("0003C");
//					break;
//				case "NodeD":
//					customerlist.add("0001D");
//					customerlist.add("0002D");
//					customerlist.add("0003D");
//					break;
//				}
				
//				this.createAssetAccount(period);
				
				Optional<NodeDetails> nodeOptional = this.nodeStore.findById(Nodeid);
				String ip = "";
				if (nodeOptional.isPresent()) {
					ip = ((NodeDetails) nodeOptional.get()).getDnsname();
					log.info("IP address for " + Nodeid + " is " + ip);
				} else {
					log.debug("Node Info is empty from Databse");
				}

				for (CustomerDetails customer : customerInfo.findAll()) {
//					if (customer.getNode_id().equalsIgnoreCase(Nodeid)) {
					customerID = customer.getCustomer_id();
					/*
					 * String status = this.urlBuilder.getResponse("http://" + ip +
					 * ":8082/asset/prepopulate/",
					 * this.objectMapper.writeValueAsString(customer).getBytes());
					 * log.info("URI for Asset Manager " + "http://" + ip +
					 * ":8082/asset/prepopulate/" +
					 * this.objectMapper.writeValueAsString(customer).getBytes());
					 * 
					 * log.info("Received Asset manager staus for " + customerID +
					 * " with ALD status " + status);
					 */
					Optional<KeyEntities> keyentity = this.keystore.findById(customerID);
//						if (keyentity.isEmpty()) {
					KeyEntities keyEntities = this.generateKeys.keygen(customerID);
					this.keystore.save(keyEntities);
					log.info("Keys are generated for customer: " + customerID);
					this.storePubKeys(customerID, keyEntities);
//						} else {
//							log.debug("Key entity already existed for " + customerID);
//						}
//					}
				}
			}

		} catch (Exception e) {
			log.error(e + " occurred while generationg keys for customer: " + customerID);

		}
		return "Success";
	}

	public void storePubKeys(String customer, KeyEntities keyentity) {
		try {
			Optional<PublicKeyEntities> existkeyentity = this.publicKeyStore.findById(customer);
			log.info(existkeyentity.toString().toString());
			if (existkeyentity.isEmpty()) {
				keyEntity.setCustomerId(customer);
				keyEntity.setPublikKey(keyentity.getPublikKey());
				this.publicKeyStore.save(keyEntity);
				log.info("Public key is inserted for customer: " + customer + " in database");

			} else {
				log.info("Public key of customer id: " + customer + " already exists in database");
			}

		} catch (Exception exception) {
			log.error(exception + "exception occurred while storing pubic keys in database");
		}
	}

//	public void updateConfirmFLAddressPassiveNodes(TranxInterimData tx_details, String FLAddress, String status,
//			String Node) {
//		try {
//			List<NodeDetails> nodeDetails = this.nodeStore.findAll();
//			List<Optional<NodeDetails>> convertedNodeList = nodeDetails.stream()
//					.map((nodedata) -> Optional.of(nodedata)).collect(Collectors.toList());
//			if (!convertedNodeList.isEmpty()) {
//				convertedNodeList.stream().parallel().forEachOrdered((nodeinfo) -> {
//					if (!nodeinfo.get().getNodeid().equalsIgnoreCase(Node)) {
//						String ip = nodeinfo.get().getDnsname();
//						String nodeid = nodeinfo.get().getNodeid();
//						try {
//							this.urlBuilder.getResponse("http://" + ip + ":8092/flam/updateStatus", this.objectMapper
//									.writeValueAsString(new UpdateFLStatus(Node, FLAddress, status,tx_details.getFDPeriodId())).getBytes());
//						} catch (JsonProcessingException exception) {
//							log.error(exception + "exception occurred in sending fladdress details to status");
//						}
//						log.info("FL Detials sent to FLM to change status of FL Address "+FLAddress+" for Passive Node " + nodeid);
//					}
//				});
//			} else {
//				log.debug("Node info is empty");
//			}
//		} catch (Exception exception) {
//			log.error(exception + " exception occurred while requesting FL Adddress Updation for Passive Nodes");
//		}
//	}

	@PostMapping
	@RequestMapping({ "newcustomer" })
	public String genKeysNewAssets(@RequestBody List<NewAssetsDetails> newCustomerInfo) {
		String customerID = "";
//		String Nodeid = Node;
		log.info("Requested for new assets to create keys");
		try {
			for (NewAssetsDetails customer : newCustomerInfo) {
//				if (customer.getNode_id().equalsIgnoreCase(Nodeid)) {
				customerID = customer.getCustomer_id();
				Optional<KeyEntities> keyentity = this.keystore.findById(customerID);
				if (keyentity.isEmpty()) {
					KeyEntities keyEntities = this.generateKeys.keygen(customerID);
					this.keystore.save(keyEntities);
					log.info("Keys are generated for customer: " + customerID);
					this.storePubKeys(customerID, keyEntities);
				} else {
					log.info("Key entity already existed for " + customerID);
				}
				if (!customerInfo.getAssets().contains(customer.getAsset_type())) {
					this.objTransactionMgrImpl.createAssetAccount(customer);
				} else {
					log.info("ALready Existed Asset Type " + customer.getAsset_type());
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return "Success";

	}

//	@PostMapping
//	@RequestMapping({ "" })
//	public String testpreReqResponse(@RequestBody String Node, @PathVariable String period) {
//		String customerID = "";
//		String Nodeid = Node;
//		try {
//			if (!Nodeid.isBlank()) {
//				this.transactionNodeInfo.setNodeid(Nodeid);
//				this.transactionNodeInfo.setPeriod(period);
//				log.info("Node Id from Node Manager Module: " + Nodeid);
//				log.info("Period Id from Node Manager Module: " + period);
//			}
//		} catch (Exception e) {
//			System.out.println(e);
//		}
//		return "Success";
//	}
	
	public void createAssetAccount(String periodId) throws Exception {
		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("AssetLotAccountsProc");
		query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
		query.setParameter(1, periodId);
		query.execute();
	}
}
