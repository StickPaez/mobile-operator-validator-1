package com.aldeamo.mobopervalid.rabbit;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.aldeamo.mobopervalid.dto.Response;
import com.aldeamo.mobopervalid.dto.ValidationRequest;
import com.aldeamo.mobopervalid.service.ValidatorService;
import com.aldeamo.mobopervalid.util.TransactionId;



public class ValidatorConsumer {
	private static final Logger logger = LoggerFactory.getLogger(ValidatorConsumer.class);
	
	@Autowired
	ValidatorService validatorService;
	
	public void handleMessage(ValidationRequest request) {
		logger.debug("STARTED - Received request {}", request);
		String transactionId = TransactionId.getTransactionId("", request.getLand());
		MDC.put(TransactionId.STRING_TRANSACTION_ID, transactionId);
		Response response = validatorService.processRequest(request, transactionId);
		logger.debug("ENDED - Received request result {}", response);
		MDC.clear();
	}
}