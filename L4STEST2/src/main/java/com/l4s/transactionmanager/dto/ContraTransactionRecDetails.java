package com.l4s.transactionmanager.dto;

import java.sql.Timestamp;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rec_contra_transaction")
public class ContraTransactionRecDetails {
	@Id
	private String contraid;
	private String nodeid;
	private String tx_id;
	private String assetid;
	private String Asset_name;
	private String extTxnId;
	private String useCase;
	private String TxType;
	private String FDPeriodId;
	private String tranperiodfk;
	private Timestamp trandt;
	private String postperiodfk;
	private String flid;
	private String fladdrid;
	private Timestamp postdt;
	private String fladdridparentfk;
	private String trhash;
	private float tranqty;
	private float qtyALR;
	private String txDel;
	private String txREC;
	private String nDel;
	private String nREC;

	public String getAsset_name() {
		return Asset_name;
	}

	public void setAsset_name(String asset_name) {
		Asset_name = asset_name;
	}

	public String getUseCase() {
		return useCase;
	}

	public void setUseCase(String useCase) {
		this.useCase = useCase;
	}

	public String getTxType() {
		return TxType;
	}

	public void setTxType(String txType) {
		TxType = txType;
	}

	public String getnDel() {
		return nDel;
	}

	public void setnDel(String nDel) {
		this.nDel = nDel;
	}

	public String getnREC() {
		return nREC;
	}

	public void setnREC(String nREC) {
		this.nREC = nREC;
	}

	private String contingentId;
	private String shortTransferId;
	private String contraType;
	@Column(name = "encrypted#")
	private String encryptedhash;
	private byte[] digitalsig;
	private String assetLotId;
	private String assetLotNetId;
	private String assetLotReceiverId;
	private boolean contramatchStatus;
	private String airlocknode;

	public String getAirlocknode() {
		return airlocknode;
	}

	public void setAirlocknode(String airlocknode) {
		this.airlocknode = airlocknode;
	}

	public String getContraid() {
		return contraid;
	}

	public void setContraid(String contraid) {
		this.contraid = contraid;
	}

	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public String getAssetid() {
		return assetid;
	}

	public void setAssetid(String assetid) {
		this.assetid = assetid;
	}

	public String getTranperiodfk() {
		return tranperiodfk;
	}

	public void setTranperiodfk(String tranperiodfk) {
		this.tranperiodfk = tranperiodfk;
	}

	public Timestamp getTrandt() {
		return trandt;
	}

	public void setTrandt(Timestamp trandt) {
		this.trandt = trandt;
	}

	public String getPostperiodfk() {
		return postperiodfk;
	}

	public void setPostperiodfk(String postperiodfk) {
		this.postperiodfk = postperiodfk;
	}

	public String getFlid() {
		return flid;
	}

	public void setFlid(String flid) {
		this.flid = flid;
	}

	public String getFladdrid() {
		return fladdrid;
	}

	public void setFladdrid(String fladdrid) {
		this.fladdrid = fladdrid;
	}

	public Timestamp getPostdt() {
		return postdt;
	}

	public void setPostdt(Timestamp postdt) {
		this.postdt = postdt;
	}

	public String getFladdridparentfk() {
		return fladdridparentfk;
	}

	public void setFladdridparentfk(String fladdridparentfk) {
		this.fladdridparentfk = fladdridparentfk;
	}

	public String getTrhash() {
		return trhash;
	}

	public void setTrhash(String trhash) {
		this.trhash = trhash;
	}

//	public String getContratype() {
//		return contratype;
//	}
//
//	public void setContratype(String contratype) {
//		this.contratype = contratype;
//	}

	public Float getTranqty() {
		return tranqty;
	}

	public void setTranqty(Float tranqty) {
		this.tranqty = tranqty;
	}

	public String getEncryptedhash() {
		return encryptedhash;
	}

	public void setEncryptedhash(String encryptedhash) {
		this.encryptedhash = encryptedhash;
	}

	public byte[] getDigitalsig() {
		return digitalsig;
	}

	public void setDigitalsig(byte[] digitalsig) {
		this.digitalsig = digitalsig;
	}

	public String getAssetLotId() {
		return assetLotId;
	}

	public void setAssetLotId(String assetLotId) {
		this.assetLotId = assetLotId;
	}

	public String getAssetLotNetId() {
		return assetLotNetId;
	}

	public void setAssetLotNetId(String assetLotNetId) {
		this.assetLotNetId = assetLotNetId;
	}

	public String getAssetLotReceiverId() {
		return assetLotReceiverId;
	}

	public void setAssetLotReceiverId(String assetLotReceiverId) {
		this.assetLotReceiverId = assetLotReceiverId;
	}

	public String getTx_id() {
		return tx_id;
	}

	public void setTx_id(String tx_id) {
		this.tx_id = tx_id;
	}

	public boolean isContramatchStatus() {
		return contramatchStatus;
	}

	public void setContramatchStatus(boolean contramatchStatus) {
		this.contramatchStatus = contramatchStatus;
	}

	public float getQtyALR() {
		return qtyALR;
	}

	public void setQtyALR(float qtyALR) {
		this.qtyALR = qtyALR;
	}

	public void setTranqty(float tranqty) {
		this.tranqty = tranqty;
	}

	public String getTxDel() {
		return txDel;
	}

	public void setTxDel(String txDel) {
		this.txDel = txDel;
	}

	public String getTxREC() {
		return txREC;
	}

	public void setTxREC(String txREC) {
		this.txREC = txREC;
	}

	public String getContingentId() {
		return contingentId;
	}

	public void setContingentId(String contingentId) {
		this.contingentId = contingentId;
	}

	public String getShortTransferId() {
		return shortTransferId;
	}

	public void setShortTransferId(String shortTransferId) {
		this.shortTransferId = shortTransferId;
	}

	public String getContraType() {
		return contraType;
	}

	public void setContraType(String contraType) {
		this.contraType = contraType;
	}

	public String getExtTxnId() {
		return extTxnId;
	}

	public void setExtTxnId(String extTxnId) {
		this.extTxnId = extTxnId;
	}

		
	public String getFDPeriodId() {
		return FDPeriodId;
	}

	public void setFDPeriodId(String fDPeriodId) {
		FDPeriodId = fDPeriodId;
	}

	@Override
	public String toString() {
		return "ContraTransactionRecDetails [contraid=" + contraid + ", nodeid=" + nodeid + ", tx_id=" + tx_id
				+ ", assetid=" + assetid + ", Asset_name=" + Asset_name + ", extTxnId=" + extTxnId + ", tranperiodfk="
				+ tranperiodfk + ", trandt=" + trandt + ", postperiodfk=" + postperiodfk + ", flid=" + flid
				+ ", fladdrid=" + fladdrid + ", postdt=" + postdt + ", fladdridparentfk=" + fladdridparentfk
				+ ", trhash=" + trhash + ", tranqty=" + tranqty + ", qtyALR=" + qtyALR + ", txDel=" + txDel + ", txREC="
				+ txREC + ", nDel=" + nDel + ", nREC=" + nREC + ", contingentId=" + contingentId + ", shortTransferId="
				+ shortTransferId + ", contraType=" + contraType + ", encryptedhash=" + encryptedhash + ", digitalsig="
				+ Arrays.toString(digitalsig) + ", assetLotId=" + assetLotId + ", assetLotNetId=" + assetLotNetId
				+ ", assetLotReceiverId=" + assetLotReceiverId + ", contramatchStatus=" + contramatchStatus + "]";
	}
}