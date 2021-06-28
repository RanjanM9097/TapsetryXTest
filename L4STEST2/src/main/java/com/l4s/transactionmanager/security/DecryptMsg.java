package com.l4s.transactionmanager.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

@Service
public class DecryptMsg {
   private Cipher cipher = Cipher.getInstance("RSA");

   public DecryptMsg() throws NoSuchAlgorithmException, NoSuchPaddingException {
   }

   public PublicKey getPublic(byte[] publicKey) throws Exception {
      X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePublic(spec);
   }

   public String decryptText(String msg, PublicKey key) throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
    System.out.println("Decrypting*****");
	   this.cipher.init(Cipher.DECRYPT_MODE, key);
      return new String(this.cipher.doFinal(Base64.decodeBase64(msg)), "UTF-8");
   }
}
