package com.l4s.transactionmanager.dto;

import java.util.Arrays;

import org.springframework.stereotype.Component;

@Component
public class DigiSignature {

	private byte[] publicKey;
	private byte[] digiSign;
	public String nodeRec;

	public String getNodeRec() {
		return nodeRec;
	}

	public void setNodeRec(String nodeRec) {
		this.nodeRec = nodeRec;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}

	public byte[] getDigiSign() {
		return digiSign;
	}

	public void setDigiSign(byte[] digiSign) {
		this.digiSign = digiSign;
	}

	@Override
	public String toString() {
		return "DigiSignature [publicKey=" + Arrays.toString(publicKey) + ", digiSign=" + Arrays.toString(digiSign)
				+ ", nodeRec=" + nodeRec + "]";
	}
	
}
