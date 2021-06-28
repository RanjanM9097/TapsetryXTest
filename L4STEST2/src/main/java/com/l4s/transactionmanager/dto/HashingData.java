package com.l4s.transactionmanager.dto;

public class HashingData {
	private String nDEL;
	private String nREC;
	private String atype;
	private String qty;
	private String fl;

	public String getnDEL() {
		return this.nDEL;
	}

	public void setnDEL(String nDEL) {
		this.nDEL = nDEL;
	}

	public String getnREC() {
		return this.nREC;
	}

	public void setnREC(String nREC) {
		this.nREC = nREC;
	}

	public String getAtype() {
		return this.atype;
	}

	public void setAtype(String atype) {
		this.atype = atype;
	}

	public String getQty() {
		return this.qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getFl() {
		return this.fl;
	}

	public void setFl(String fl) {
		this.fl = fl;
	}

	@Override
	public String toString() {
		return nDEL + nREC + atype + qty;
	}

}
