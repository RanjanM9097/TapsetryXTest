package com.l4s.transactionmanager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.l4s.transactionmanager.process.IntraTransactionDelImpl;

@SpringBootApplication
public class TransactionManagerApplication extends SpringBootServletInitializer {
	private static Class<TransactionManagerApplication> applicationClass = TransactionManagerApplication.class;
	private static Logger log = LogManager.getLogger(IntraTransactionDelImpl.class);

	public static void main(String[] args) {
		SpringApplication.run(applicationClass, args);
		log.info("Transaction Manager Module Started");
	}

	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(new Class[] { applicationClass });
	}
}
