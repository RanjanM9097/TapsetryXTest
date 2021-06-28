package com.l4s.transactionmanager.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

@Service
public class EncryptDecrypt {
   private Cipher cipher = Cipher.getInstance("RSA");

   public EncryptDecrypt() throws NoSuchAlgorithmException, NoSuchPaddingException {
   }

   public PrivateKey getPrivate(byte[] privateKey) throws Exception {
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKey);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePrivate(spec);
   }

   public String encryptText(String msg, PrivateKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
      this.cipher.init(Cipher.ENCRYPT_MODE, key);
      return Base64.encodeBase64String(this.cipher.doFinal(msg.getBytes("UTF-8")));
   }

   public String applySha256(String input) {
      try {
         MessageDigest digest = MessageDigest.getInstance("SHA-256");
         byte[] hash = digest.digest(input.getBytes("UTF-8"));
         StringBuffer hexString = new StringBuffer();

         for(int i = 0; i < hash.length; ++i) {
            String hex = Integer.toHexString(255 & hash[i]);
            if (hex.length() == 1) {
               hexString.append('0');
            }

            hexString.append(hex);
         }

         return hexString.toString();
      } catch (Exception var7) {
         throw new RuntimeException(var7);
      }
   }
}
