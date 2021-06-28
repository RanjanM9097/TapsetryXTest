package com.l4s.transactionmanager.businessrule;

public interface BusinessRuleManager {
   String calculateALN(String qty, String ald) throws Exception;

   boolean validateALD(String ald) throws Exception;
}
