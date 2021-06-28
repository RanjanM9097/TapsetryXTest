package com.l4s.transactionmanager.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.l4s.transactionmanager.dao.UtilityData;
import com.l4s.transactionmanager.dto.DigiSignature;
import com.l4s.transactionmanager.dto.EncryptedHashShareData;
import com.l4s.transactionmanager.dto.FLAddress;
import com.l4s.transactionmanager.dto.FLMAddress;
import com.l4s.transactionmanager.dto.TransactionDetails;
import com.l4s.transactionmanager.service.GenerateK;

@Service
public class UrlBuilder {
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	UtilityData util;
	private static Logger log = LogManager.getLogger(UrlBuilder.class);

	public String getResponse(String urlLink, byte[] requestBody) {
		StringBuilder build = new StringBuilder();

		try {
			URL url = new URL(urlLink);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			OutputStream os = conn.getOutputStream();
			os.write(requestBody);
			os.flush();
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//			System.out.println("Output from Server .... \n");

			String output;
			while ((output = br.readLine()) != null) {
				build.append(output);
				log.info("Response output :"  + output);
			}

			conn.disconnect();
		} catch (MalformedURLException var9) {
			var9.printStackTrace();
		} catch (IOException var10) {
			var10.printStackTrace();
		}

		return build.toString();
	}

	public FLMAddress getFLUpdated(String url, byte[] requestBody) {
		FLMAddress fl = null;

		try {
			String getres = this.getResponse(url, requestBody);
//			System.out.println("Get Response--->" + getres);
			fl = (FLMAddress) this.objectMapper.readValue(getres, FLMAddress.class);
		} catch (JsonMappingException var5) {
			var5.printStackTrace();
		} catch (JsonProcessingException var6) {
			var6.printStackTrace();
		}

		return fl;
	}

	public FLAddress getFL(String url, byte[] requestBody) {
		FLAddress fl = new FLAddress();
		FLMAddress flm = null;
		try {
			String getres = this.getResponse(url, requestBody);
//			System.out.println("Get Response--->" + getres);
			flm = (FLMAddress) this.objectMapper.readValue(getres, FLMAddress.class);
		} catch (JsonMappingException var5) {
			var5.printStackTrace();
		} catch (JsonProcessingException var6) {
			var6.printStackTrace();
		}

		return this.util.updatedFL(fl, flm);
	}

	public TransactionDetails getTransaction(String url, byte[] requestBody) {
		TransactionDetails td = null;

		try {
			String getres = this.getResponse(url, requestBody);
//			System.out.println("Get Response--->" + getres);
			td = (TransactionDetails) this.objectMapper.readValue(getres, TransactionDetails.class);
		} catch (JsonMappingException var5) {
			var5.printStackTrace();
		} catch (JsonProcessingException var6) {
			var6.printStackTrace();
		}

		return td;
	}

	public EncryptedHashShareData getEnryptedHash(String url, byte[] requestBody) {
		EncryptedHashShareData encrtptHash = new EncryptedHashShareData();
		try {
			String getres = this.getResponse(url, requestBody);
//			System.out.println("Get Response--->" + getres);
			encrtptHash = (EncryptedHashShareData) this.objectMapper.readValue(getres, EncryptedHashShareData.class);
		} catch (JsonMappingException var5) {
			var5.printStackTrace();
		} catch (JsonProcessingException var6) {
			var6.printStackTrace();
		}

		return encrtptHash;
	}

	public DigiSignature getDigiSign(String url, byte[] requestBody) {
		DigiSignature digiSign = null;

		try {
			String getres = this.getResponse(url, requestBody);
//			System.out.println("Get Response--->" + getres);
			digiSign = (DigiSignature) this.objectMapper.readValue(getres, DigiSignature.class);
		} catch (JsonMappingException var5) {
			var5.printStackTrace();
		} catch (JsonProcessingException var6) {
			var6.printStackTrace();
		}

		return digiSign;
	}
}
