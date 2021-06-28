package com.l4s.transactionmanager.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(
   name = "publicKeys"
)
public class PublicKeyEntities {
   @Id
   private String customerId;
   @Column(
      name = "publikKey",
      length = 1024
   )
   @Lob
   private byte[] publikKey;

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

   public PublicKeyEntities(String customerId, byte[] publikKey) {
      this.customerId = customerId;
      this.publikKey = publikKey;
   }

   public PublicKeyEntities() {
   }
}
