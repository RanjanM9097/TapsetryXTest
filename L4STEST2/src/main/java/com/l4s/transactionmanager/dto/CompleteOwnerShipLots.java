package com.l4s.transactionmanager.dto;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "complete_owner_ship_lot")
public class CompleteOwnerShipLots {

	@Id
	private String colrid;
	private String nodeid;
	private String assetid;
	private String Asset_name;
	private String extTxnId;
	private String nDelID;
	private String txDEL;
	private String txREC;
	private Float created_Quantity;
	private String nDelFLID;
	private String nDelFLAddress;
	private String txDelhash;
	private String tPRecALRID;
	private String nRecID;
	private String nRecFLID;
	private String NRecFLAddress;
	private String tx_Rechash;
	private Timestamp TransactedDate;
	private Timestamp PostedDate;
	private char al;
	private char linkedAl;
	private String DelTxID;
	private String RecTxID;
	private String periodId;
	private String airlocknode;
	private String FDPeriodId;

	public String getColrid() {
		return colrid;
	}

	public void setColrid(String colrid) {
		this.colrid = colrid;
	}

	public String getAssetid() {
		return assetid;
	}

	public void setAssetid(String assetid) {
		this.assetid = assetid;
	}

	public String getnDelID() {
		return nDelID;
	}

	public void setnDelID(String nDelID) {
		this.nDelID = nDelID;
	}

	public Float getCreated_Quantity() {
		return created_Quantity;
	}

	public void setCreated_Quantity(Float created_Quantity) {
		this.created_Quantity = created_Quantity;
	}

	public String getnDelFLID() {
		return nDelFLID;
	}

	public void setnDelFLID(String nDelFLID) {
		this.nDelFLID = nDelFLID;
	}

	public String getnDelFLAddress() {
		return nDelFLAddress;
	}

	public void setnDelFLAddress(String nDelFLAddress) {
		this.nDelFLAddress = nDelFLAddress;
	}

	public String getTxDelhash() {
		return txDelhash;
	}

	public void setTxDelhash(String txDelhash) {
		this.txDelhash = txDelhash;
	}

	public String gettPRecALRID() {
		return tPRecALRID;
	}

	public void settPRecALRID(String tPRecALRID) {
		this.tPRecALRID = tPRecALRID;
	}

	public String getnRecID() {
		return nRecID;
	}

	public void setnRecID(String nRecID) {
		this.nRecID = nRecID;
	}

	public String getnRecFLID() {
		return nRecFLID;
	}

	public void setnRecFLID(String nRecFLID) {
		this.nRecFLID = nRecFLID;
	}

	public String getNRecFLAddress() {
		return NRecFLAddress;
	}

	public void setNRecFLAddress(String nRecFLAddress) {
		NRecFLAddress = nRecFLAddress;
	}

	public String getTx_Rechash() {
		return tx_Rechash;
	}

	public void setTx_Rechash(String tx_Rechash) {
		this.tx_Rechash = tx_Rechash;
	}

	public Timestamp getTransactedDate() {
		return TransactedDate;
	}

	public void setTransactedDate(Timestamp transactedDate) {
		TransactedDate = transactedDate;
	}

	public Timestamp getPostedDate() {
		return PostedDate;
	}

	public void setPostedDate(Timestamp postedDate) {
		PostedDate = postedDate;
	}

	public char getAl() {
		return al;
	}

	public void setAl(char al) {
		this.al = al;
	}

	public char getLinkedAl() {
		return linkedAl;
	}

	public void setLinkedAl(char linkedAl) {
		this.linkedAl = linkedAl;
	}

	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public String getTxDEL() {
		return txDEL;
	}

	public void setTxDEL(String txDEL) {
		this.txDEL = txDEL;
	}

	public String getAsset_name() {
		return Asset_name;
	}

	public void setAsset_name(String asset_name) {
		Asset_name = asset_name;
	}

	public String getTxREC() {
		return txREC;
	}

	public void setTxREC(String txREC) {
		this.txREC = txREC;
	}

	public String getExtTxnId() {
		return extTxnId;
	}

	public void setExtTxnId(String extTxnId) {
		this.extTxnId = extTxnId;
	}

	public String getDelTxID() {
		return DelTxID;
	}

	public void setDelTxID(String delTxID) {
		DelTxID = delTxID;
	}

	public String getRecTxID() {
		return RecTxID;
	}

	public void setRecTxID(String recTxID) {
		RecTxID = recTxID;
	}

	public String getPeriodId() {
		return periodId;
	}

	public void setPeriodId(String periodId) {
		this.periodId = periodId;
	}

	public String getAirlocknode() {
		return airlocknode;
	}

	public void setAirlocknode(String airlocknode) {
		this.airlocknode = airlocknode;
	}

	public String getFDPeriodId() {
		return FDPeriodId;
	}

	public void setFDPeriodId(String fDPeriodId) {
		FDPeriodId = fDPeriodId;
	}

}
