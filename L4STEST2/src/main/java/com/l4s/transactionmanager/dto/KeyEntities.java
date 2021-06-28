package com.l4s.transactionmanager.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(
   name = "key_table"
)
public class KeyEntities {
   @Id
   private String customerId;
   @Column(
      name = "publikKey",
      length = 1024
   )
   @Lob
   private byte[] publikKey;
   @Column(
      name = "privateKey",
      length = 1024
   )
   @Lob
   private byte[] privateKey;
   
   public KeyEntities() {
	   
   }

   public String getCustomerId() {
      return this.customerId;
   }

   public void setCustomerId(String customerId) {
      this.customerId = customerId;
   }

   public byte[] getPublikKey() {
      return this.publikKey;
   }

   public void setPublikKey(byte[] publikKey) {
      this.publikKey = publikKey;
   }

   public byte[] getPrivateKey() {
      return this.privateKey;
   }

   public void setPrivateKey(byte[] privateKey) {
      this.privateKey = privateKey;
   }

   public KeyEntities(String customerId, byte[] publikKey, byte[] privateKey) {
      this.customerId = customerId;
      this.publikKey = publikKey;
      this.privateKey = privateKey;
   }

  
}
