package com.l4s.transactionmanager.dto;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "all_customer_info")
public class CustomerDetails {
	@Id
	private int id;
	private String a_type;
	private String node_id;
	private String customer_id;
	private float qty;
	private String Field1;
	private String Field2;
	private String Field3;
	private String Field4;
	private String Field5;
	private String UserAssetId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getA_type() {
		return a_type;
	}

	public void setA_type(String a_type) {
		this.a_type = a_type;
	}

	public String getNode_id() {
		return node_id;
	}

	public void setNode_id(String node_id) {
		this.node_id = node_id;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public float getQty() {
		return qty;
	}

	public void setQty(float qty) {
		this.qty = qty;
	}

	public String getField1() {
		return Field1;
	}

	public void setField1(String field1) {
		Field1 = field1;
	}

	public String getField2() {
		return Field2;
	}

	public void setField2(String field2) {
		Field2 = field2;
	}

	public String getField3() {
		return Field3;
	}

	public void setField3(String field3) {
		Field3 = field3;
	}

	public String getField4() {
		return Field4;
	}

	public void setField4(String field4) {
		Field4 = field4;
	}

	public String getField5() {
		return Field5;
	}

	public void setField5(String field5) {
		Field5 = field5;
	}

	public String getUserAssetId() {
		return UserAssetId;
	}

	public void setUserAssetId(String userAssetId) {
		UserAssetId = userAssetId;
	}
	
	
}
