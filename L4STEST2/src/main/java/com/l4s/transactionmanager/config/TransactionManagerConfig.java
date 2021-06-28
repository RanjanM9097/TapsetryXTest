package com.l4s.transactionmanager.config;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.l4s.transactionmanager.dao.TransactionNodeInfo;
import com.l4s.transactionmanager.dto.ALDCompleteOwnerShipLots;
import com.l4s.transactionmanager.dto.BroadcastContraTransactionDelData;
import com.l4s.transactionmanager.dto.BroadcastContraTransactionRecData;
import com.l4s.transactionmanager.dto.CompleteOwnerShipLots;
import com.l4s.transactionmanager.dto.ContraTransactionDelDetails;
import com.l4s.transactionmanager.dto.ContraTransactionRecDetails;
import com.l4s.transactionmanager.dto.ErrorTxnInfo;
import com.l4s.transactionmanager.dto.FLAddress;
import com.l4s.transactionmanager.dto.FLAddressErrorUpdateDto;
import com.l4s.transactionmanager.dto.FutureDatedTx;
import com.l4s.transactionmanager.dto.HashingData;
import com.l4s.transactionmanager.dto.KeyEntities;
import com.l4s.transactionmanager.dto.LocalDateTimes;
import com.l4s.transactionmanager.dto.NewAssetsDetails;
import com.l4s.transactionmanager.dto.PublicKeyEntities;
import com.l4s.transactionmanager.dto.ReceivedTransactionData;
import com.l4s.transactionmanager.dto.TransactionDetails;
import com.l4s.transactionmanager.dto.TransactionProcessDetails;
import com.l4s.transactionmanager.dto.TranxInterimData;
import com.l4s.transactionmanager.dto.ValidationProcessDetails;
import com.l4s.transactionmanager.service.CarryForwardFutureTx;
import com.l4s.transactionmanager.service.ErrorTransactions;

@Configuration
public class TransactionManagerConfig {
	@Bean
	public HashingData hashingData() {
		return new HashingData();
	}

	@Bean
	public KeyEntities keyEntities() {
		return new KeyEntities();
	}

//	@Bean
//	public CustomerKeyEntities customerkeyEntities() {
//		return new CustomerKeyEntities();
//	}

	@Bean
	@Scope("prototype")
	public TranxInterimData tranxInterimData() {
		return new TranxInterimData();
	}

	@Bean
	public TransactionDetails transactionDetails() {
		return new TransactionDetails();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public FLAddress flAddress() {
		return new FLAddress();
	}

	@Bean
	public PublicKeyEntities publicKeys() {
		return new PublicKeyEntities();
	}

	@Bean
	public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		return factory;
	}

	@Bean
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}

	@Bean
	public TransactionNodeInfo transactionNodeInfo() {
		return new TransactionNodeInfo();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public TransactionProcessDetails transactionProcessDaetails() {
		return new TransactionProcessDetails();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public ALDCompleteOwnerShipLots getALDCOLRInfo() {
		return new ALDCompleteOwnerShipLots();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public CompleteOwnerShipLots getCOLRInfo() {
		return new CompleteOwnerShipLots();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public ContraTransactionDelDetails getContraDelTxinfo() {
		return new ContraTransactionDelDetails();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public ContraTransactionRecDetails getContraRecTxinfo() {
		return new ContraTransactionRecDetails();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public BroadcastContraTransactionDelData getContraTxDelinfo() {
		return new BroadcastContraTransactionDelData();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public BroadcastContraTransactionRecData getContraTxRecinfo() {
		return new BroadcastContraTransactionRecData();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public ReceivedTransactionData getReceivedTransactionData() {
		return new ReceivedTransactionData();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public LocalDateTimes getLocalDateTimes() {
		return new LocalDateTimes();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public ValidationProcessDetails getValidationProcessDetails() {
		return new ValidationProcessDetails();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public ErrorTxnInfo getErrorTxnInfo() {
		return new ErrorTxnInfo();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public ErrorTransactions getErrorTransactions() {
		return new ErrorTransactions();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public NewAssetsDetails getNewAssetsDetails() {
		return new NewAssetsDetails();
	}
	
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public FLAddressErrorUpdateDto getFLAddressErrorUpdateDto() {
		return new FLAddressErrorUpdateDto();
	}
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public FutureDatedTx getFutureDatedTx() {
		return new FutureDatedTx();
	}
	
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public CarryForwardFutureTx getCarryForwardFutureTx() {
		return new CarryForwardFutureTx();
	}
}
