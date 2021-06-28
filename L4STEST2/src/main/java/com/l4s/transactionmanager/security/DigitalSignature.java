package com.l4s.transactionmanager.security;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class DigitalSignature {
   private static List<String> list;

   public void Message(String data, byte[] privateKey) throws InvalidKeyException, Exception {
      list = new ArrayList();
      list.add(data);
      list.add(new String(this.sign(data, privateKey)));
   }

   public byte[] sign(String data, byte[] privateKey) throws InvalidKeyException, Exception {
      Signature rsa = Signature.getInstance("SHA1withRSA");
      rsa.initSign(this.getPrivate(privateKey));
      rsa.update(data.getBytes());
      return rsa.sign();
   }

   public PrivateKey getPrivate(byte[] privateKey) throws Exception {
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKey);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePrivate(spec);
   }
}
