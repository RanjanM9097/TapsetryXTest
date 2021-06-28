package com.l4s.transactionmanager.dto;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(
   name = "allnodes"
)
public class NodeDetails {
   @Id
   private String nodeid;
   private String nodelogicalname;
   private String networkid;
   private String airlockyn;
   private String dnsname;

   public String getNodeid() {
      return this.nodeid;
   }

   public void setNodeid(String nodeid) {
      this.nodeid = nodeid;
   }

   public String getNodelogicalname() {
      return this.nodelogicalname;
   }

   public void setNodelogicalname(String nodelogicalname) {
      this.nodelogicalname = nodelogicalname;
   }

   public String getNetworkid() {
      return this.networkid;
   }

   public void setNetworkid(String networkid) {
      this.networkid = networkid;
   }

   public String getAirlockyn() {
      return this.airlockyn;
   }

   public void setAirlockyn(String airlockyn) {
      this.airlockyn = airlockyn;
   }

   public String getDnsname() {
      return this.dnsname;
   }

   public void setDnsname(String dnsname) {
      this.dnsname = dnsname;
   }
}
