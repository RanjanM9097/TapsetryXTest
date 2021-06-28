package com.l4s.transactionmanager.businessrule;

import org.springframework.stereotype.Service;

@Service
public class BusinessRuleManagerImpl implements BusinessRuleManager {
   public String calculateALN(String qty, String ald) throws Exception {
      int aldValue = Integer.parseInt(ald);
      int qtyValue = Integer.parseInt(qty);
      return String.valueOf(aldValue - qtyValue);
   }

   public boolean validateALD(String ald) throws Exception {
      return true;
   }
}
