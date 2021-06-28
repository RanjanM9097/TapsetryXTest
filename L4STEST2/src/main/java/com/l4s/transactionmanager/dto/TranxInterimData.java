package com.l4s.transactionmanager.dto;

import java.sql.Timestamp;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "transact_table")
public class TranxInterimData {
	@Id
	private String transactionId;
	private Timestamp timeStamp;
	private String atype;
	private String period;
	private String NodeId;
	private String assetLotId;
//	private String extTxnId;
	private float qtyALD;
	private float qtyALR;
	private String nREC;
	private String txREC;
	private String nDEL;
	private String txDEL;
	private String flIdDEL;
	private String flAddressDEL;
	private String flaHashLinkDEL;
	private String aln;
	private String contingentId;
	private String shortTransferId;
	@Column
	private String contraIdDel;
	@Column
	private String tranxHash = null;
	@Column
	private String contraType = null;
	@Column
	private byte[] degiSign = null;
	@Column
	private String encryptedHash = null;
	private String flIdREC;
	private String flAddressREC;
	private String flaHashLinkREC;
	private String alr;
	@Column
	private String contraIdRec;
	@Column
	private String recTranxHash = null;
	@Column
	private byte[] recDegiSign = null;
	@Column
	private String recEncryptedHash = null;
	private String Asset_name;
	private String snodeId;
	private String useCase;
	private String TxType;
	private String FDPeriodId;
	private String airlocknode;

	public String getAsset_name() {
		return Asset_name;
	}

	public void setAsset_name(String asset_name) {
		Asset_name = asset_name;
	}

	public String getTransactionId() {
		return this.transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Timestamp getTimeStamp() {
		return this.timeStamp;
	}

	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getAtype() {
		return this.atype;
	}

	public void setAtype(String atype) {
		this.atype = atype;
	}

	public String getPeriod() {
		return this.period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getAssetLotId() {
		return this.assetLotId;
	}

	public void setAssetLotId(String assetLotId) {
		this.assetLotId = assetLotId;
	}

	public float getQtyALD() {
		return this.qtyALD;
	}

	public void setQtyALD(float qtyALD) {
		this.qtyALD = qtyALD;
	}

	public float getQtyALR() {
		return this.qtyALR;
	}

	public void setQtyALR(float qtyALR) {
		this.qtyALR = qtyALR;
	}

	public String getnREC() {
		return this.nREC;
	}

	public void setnREC(String nREC) {
		this.nREC = nREC;
	}

	public String getTxREC() {
		return this.txREC;
	}

	public void setTxREC(String txREC) {
		this.txREC = txREC;
	}

	public String getnDEL() {
		return this.nDEL;
	}

	public void setnDEL(String nDEL) {
		this.nDEL = nDEL;
	}

	public String getTxDEL() {
		return this.txDEL;
	}

	public void setTxDEL(String txDEL) {
		this.txDEL = txDEL;
	}

	public String getFlIdDEL() {
		return this.flIdDEL;
	}

	public void setFlIdDEL(String flIdDEL) {
		this.flIdDEL = flIdDEL;
	}

	public String getFlAddressDEL() {
		return this.flAddressDEL;
	}

	public void setFlAddressDEL(String flAddressDEL) {
		this.flAddressDEL = flAddressDEL;
	}

	public String getFlaHashLinkDEL() {
		return this.flaHashLinkDEL;
	}

	public void setFlaHashLinkDEL(String flaHashLinkDEL) {
		this.flaHashLinkDEL = flaHashLinkDEL;
	}

	public String getAln() {
		return this.aln;
	}

	public void setAln(String aln) {
		this.aln = aln;
	}

	public String getContraIdDel() {
		return this.contraIdDel;
	}

	public void setContraIdDel(String contraIdDel) {
		this.contraIdDel = contraIdDel;
	}

	public String getTranxHash() {
		return this.tranxHash;
	}

	public void setTranxHash(String tranxHash) {
		this.tranxHash = tranxHash;
	}

	public String getContraType() {
		return this.contraType;
	}

	public void setContraType(String contraType) {
		this.contraType = contraType;
	}

	public byte[] getDegiSign() {
		return this.degiSign;
	}

	public void setDegiSign(byte[] degiSign) {
		this.degiSign = degiSign;
	}

	public String getEncryptedHash() {
		return this.encryptedHash;
	}

	public void setEncryptedHash(String encryptedHash) {
		this.encryptedHash = encryptedHash;
	}

	public String getFlIdREC() {
		return this.flIdREC;
	}

	public void setFlIdREC(String flIdREC) {
		this.flIdREC = flIdREC;
	}

	public String getFlAddressREC() {
		return this.flAddressREC;
	}

	public void setFlAddressREC(String flAddressREC) {
		this.flAddressREC = flAddressREC;
	}

	public String getFlaHashLinkREC() {
		return this.flaHashLinkREC;
	}

	public void setFlaHashLinkREC(String flaHashLinkREC) {
		this.flaHashLinkREC = flaHashLinkREC;
	}

	public String getAlr() {
		return this.alr;
	}

	public void setAlr(String alr) {
		this.alr = alr;
	}

	public String getContraIdRec() {
		return this.contraIdRec;
	}

	public void setContraIdRec(String contraIdRec) {
		this.contraIdRec = contraIdRec;
	}

	public String getRecTranxHash() {
		return this.recTranxHash;
	}

	public void setRecTranxHash(String recTranxHash) {
		this.recTranxHash = recTranxHash;
	}

	public byte[] getRecDegiSign() {
		return this.recDegiSign;
	}

	public void setRecDegiSign(byte[] recDegiSign) {
		this.recDegiSign = recDegiSign;
	}

	public String getRecEncryptedHash() {
		return this.recEncryptedHash;
	}

	public void setRecEncryptedHash(String recEncryptedHash) {
		this.recEncryptedHash = recEncryptedHash;
	}

	public String getContingentId() {
		return this.contingentId;
	}

	public void setContingentId(String contingentId) {
		this.contingentId = contingentId;
	}

	public String getShortTransferId() {
		return this.shortTransferId;
	}

	public void setShortTransferId(String shortTransferId) {
		this.shortTransferId = shortTransferId;
	}

	public String getNodeId() {
		return NodeId;
	}

	public void setNodeId(String nodeId) {
		NodeId = nodeId;
	}

	public String getSnodeId() {
		return snodeId;
	}

	public void setSnodeId(String snodeId) {
		this.snodeId = snodeId;
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

	public String getFDPeriodId() {
		return FDPeriodId;
	}

	public void setFDPeriodId(String fDPeriodId) {
		FDPeriodId = fDPeriodId;
	}
	
	public String getAirlocknode() {
		return airlocknode;
	}

	public void setAirlocknode(String airlocknode) {
		this.airlocknode = airlocknode;
	}

	@Override
	public String toString() {
		return "TranxInterimData [transactionId=" + transactionId + ", timeStamp=" + timeStamp + ", atype=" + atype
				+ ", period=" + period + ", NodeId=" + NodeId + ", assetLotId=" + assetLotId + ", qtyALD=" + qtyALD
				+ ", qtyALR=" + qtyALR + ", nREC=" + nREC + ", txREC=" + txREC + ", nDEL=" + nDEL + ", txDEL=" + txDEL
				+ ", flIdDEL=" + flIdDEL + ", flAddressDEL=" + flAddressDEL + ", flaHashLinkDEL=" + flaHashLinkDEL
				+ ", aln=" + aln + ", contingentId=" + contingentId + ", shortTransferId=" + shortTransferId
				+ ", contraIdDel=" + contraIdDel + ", tranxHash=" + tranxHash + ", contraType=" + contraType
				+ ", degiSign=" + Arrays.toString(degiSign) + ", encryptedHash=" + encryptedHash + ", flIdREC="
				+ flIdREC + ", flAddressREC=" + flAddressREC + ", flaHashLinkREC=" + flaHashLinkREC + ", alr=" + alr
				+ ", contraIdRec=" + contraIdRec + ", recTranxHash=" + recTranxHash + ", recDegiSign="
				+ Arrays.toString(recDegiSign) + ", recEncryptedHash=" + recEncryptedHash + ", Asset_name=" + Asset_name
				+ ", snodeId=" + snodeId + ", useCase=" + useCase + ", TxType=" + TxType + "]";
	}

//	public String getExtTxnId() {
//		return extTxnId;
//	}
//
//	public void setExtTxnId(String extTxnId) {
//		this.extTxnId = extTxnId;
//	}
}
