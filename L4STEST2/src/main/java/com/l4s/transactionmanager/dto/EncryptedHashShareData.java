package com.l4s.transactionmanager.dto;

import java.util.Arrays;

import org.springframework.stereotype.Component;

@Component
public class EncryptedHashShareData {

	private byte[] publicKey;
	private String encrptedhash;
	private String nRec;

	public String getnRec() {
		return nRec;
	}

	public void setnRec(String nRec) {
		this.nRec = nRec;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}

	public String getEncrptedhash() {
		return encrptedhash;
	}

	public void setEncrptedhash(String encrptedhash) {
		this.encrptedhash = encrptedhash;
	}

	@Override
	public String toString() {
		return "EncryptedHashShareData [publicKey=" + Arrays.toString(publicKey) + ", encrptedhash=" + encrptedhash
				+ ", nRec=" + nRec + "]";
	}
	
}
