package com.l4s.transactionmanager.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.l4s.transactionmanager.dao.KeyStore;
import com.l4s.transactionmanager.dao.PublicKeyStore;
import com.l4s.transactionmanager.dao.TempDb;
import com.l4s.transactionmanager.dao.UtilityData;
import com.l4s.transactionmanager.dto.FLAddress;
import com.l4s.transactionmanager.dto.PublicKeyEntities;
import com.l4s.transactionmanager.dto.TranxInterimData;
import com.l4s.transactionmanager.process.TransactionManagerImpl;
import com.l4s.transactionmanager.process.UrlBuilder;
import com.l4s.transactionmanager.security.DecryptMsg;
import com.l4s.transactionmanager.security.EncryptDecrypt;
import com.l4s.transactionmanager.security.GenerateKeys;
import com.l4s.transactionmanager.security.VerifySignature;

@RestController
public class SignatureController {
   @Autowired
   KeyStore keystore;
   @Autowired
   UrlBuilder urlBuilder;
   @Autowired
   TempDb tmpdb;
   @Autowired
   TranxInterimData tranxInterimData;
   @Autowired
   UtilityData util;
   @Autowired
   ObjectMapper objectMapper;
   @Autowired
   TransactionManagerImpl tranManagerImpl;
   @Autowired
   FLAddress flm;
   @Autowired
   DecryptMsg decryptMsg;
   @Autowired
   EncryptDecrypt edt;
   @Autowired
   GenerateKeys generateKeys;
   @Autowired
   PublicKeyStore publicKeyStore;
   @Autowired
   VerifySignature verifySignature;
   private static Logger log = LogManager.getLogger(SignatureController.class);

   public SignatureController() {
      System.out.println("User Controller Created");
   }

   @PostMapping
   @RequestMapping({"checkDigisign/{id}"})
   public String digiSign(@RequestBody byte[] digisign, @PathVariable String id) throws Exception {
      Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(id);
      Optional<PublicKeyEntities> keyentity = this.publicKeyStore.findById(((TranxInterimData)tranxInterim.get()).getTxDEL());
      byte[] RecDigisign = null;
      String status = "false";
      if (!keyentity.isEmpty()) {
         if (this.verifySignature.verifySignature(this.util.encrptyDel((TranxInterimData)tranxInterim.get()), digisign, ((PublicKeyEntities)keyentity.get()).getPublikKey())) {
            log.info("Delivery DigiSign verified successfully");
            RecDigisign = this.tranManagerImpl.digisign(this.util.encrptyRec((TranxInterimData)tranxInterim.get()), id, ((TranxInterimData)tranxInterim.get()).getTxREC());
            String verifyStatus = this.urlBuilder.getResponse("http://"+tranxInterim.get().getnREC()+":8080/verifyDigisign/" + id, RecDigisign);
            log.info(verifyStatus);
            if (verifyStatus.equalsIgnoreCase("true")) {
               log.info("DigiSign is valid");
               status = "true";
            } else {
               log.info("DigiSign is not valid");
            }
         } else {
            log.info("DigiSign is not valid");
         }
      }

      return status;
   }

   @PostMapping
   @RequestMapping({"verify"})
   public String verification(@RequestBody String id) throws Exception {
      Optional<TranxInterimData> tranxInterim = this.tmpdb.findById(id);
      byte[] digisign = this.tranManagerImpl.digisign(this.util.encrptyDel((TranxInterimData)tranxInterim.get()), id, ((TranxInterimData)tranxInterim.get()).getTxDEL());
      String recdigiSign = this.urlBuilder.getResponse("http://192.168.130.96:8080/checkDigisign/" + id, digisign);
      log.info(recdigiSign);
      String status = null;
      if (!recdigiSign.equalsIgnoreCase("true")) {
         log.info("DigiSign is not valid");
         status = "DigiSign is not valid";
      } else {
         log.info("DigiSign is valid");
         status = "DigiSign is valid";
      }

      return status;
   }
}
