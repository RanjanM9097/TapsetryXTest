package com.l4s.transactionmanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FLAddress {
	@JsonProperty
	private String flID;
	@JsonProperty
	private String flAddress;
	@JsonProperty
	private String flaHashLink;
	@JsonProperty
	private String flag;

	public FLAddress() {
	}

	public FLAddress(String flID, String flAddress, String flaHashLink) {
		this.flID = flID;
		this.flAddress = flAddress;
		this.flaHashLink = flaHashLink;
	}

	public String getFlID() {
		return this.flID;
	}

	public void setFlID(String flID) {
		this.flID = flID;
	}

	public String getFlAddress() {
		return this.flAddress;
	}

	public void setFlAddress(String flAddress) {
		this.flAddress = flAddress;
	}

	public String getFlaHashLink() {
		return this.flaHashLink;
	}

	public void setFlaHashLink(String flaHashLink) {
		this.flaHashLink = flaHashLink;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		return "FLAddress [flID=" + flID + ", flAddress=" + flAddress + ", flaHashLink=" + flaHashLink + ", flag="
				+ flag + "]";
	}

}
