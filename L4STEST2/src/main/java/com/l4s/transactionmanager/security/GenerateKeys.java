package com.l4s.transactionmanager.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.l4s.transactionmanager.dto.KeyEntities;

@Service
public class GenerateKeys {
   @Autowired
   KeyEntities keyEntities;
   private KeyPairGenerator keyGen;
   private KeyPair pair;
   private PrivateKey privateKey;
   private PublicKey publicKey;

   public GenerateKeys() throws NoSuchAlgorithmException, NoSuchProviderException {
      int keylength = 1024;
      this.keyGen = KeyPairGenerator.getInstance("RSA");
      this.keyGen.initialize(keylength);
   }

   public void createKeys() {
      this.pair = this.keyGen.generateKeyPair();
      this.privateKey = this.pair.getPrivate();
      this.publicKey = this.pair.getPublic();
   }

   public PrivateKey getPrivateKey() {
      return this.privateKey;
   }

   public PublicKey getPublicKey() {
      return this.publicKey;
   }

   public KeyEntities keygen(String id) {
      try {
         this.createKeys();
         byte[] publicKey = this.getPublicKey().getEncoded();
         byte[] privateKey = this.getPrivateKey().getEncoded();
         this.keyEntities.setCustomerId(id);
         this.keyEntities.setPublikKey(publicKey);
         this.keyEntities.setPrivateKey(privateKey);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return this.keyEntities;
   }
}
