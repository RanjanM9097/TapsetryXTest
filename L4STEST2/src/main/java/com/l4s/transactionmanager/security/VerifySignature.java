package com.l4s.transactionmanager.security;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

import org.springframework.stereotype.Service;

@Service
public class VerifySignature {
   public boolean verifySignature(String data, byte[] signature, byte[] publicKey) throws Exception {
      Signature sig = Signature.getInstance("SHA1withRSA");
      sig.initVerify(this.getPublic(publicKey));
      sig.update(data.getBytes());
      return sig.verify(signature);
   }

   public PublicKey getPublic(byte[] publicKey) throws Exception {
      X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePublic(spec);
   }
}
