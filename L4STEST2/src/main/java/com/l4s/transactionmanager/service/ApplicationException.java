package com.l4s.transactionmanager.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApplicationException extends Exception {
   private static Logger log = LogManager.getLogger(ApplicationException.class);

   public ApplicationException(String msg) {
      log.error("Exception occured {}", msg);
   }
}
