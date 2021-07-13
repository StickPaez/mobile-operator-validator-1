package com.aldeamo.mobopervalid.controller;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.aldeamo.mobopervalid.dto.Response;
import com.aldeamo.mobopervalid.dto.ValidationRequest;
import com.aldeamo.mobopervalid.service.ValidatorService;
import com.aldeamo.mobopervalid.util.TransactionId;


@RestController
public class ValidatorController {
	private static final Logger logger = LoggerFactory.getLogger(ValidatorController.class);
	
	@Autowired
	ValidatorService validatorService;
	
	@PostMapping(value = "/requestValidation", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response requestValidation(@RequestBody ValidationRequest validationRequest,
			@RequestHeader(value=TransactionId.STRING_TRANSACTION_ID, defaultValue="") String txId) {
		logger.debug("STARTED - Received request {}", validationRequest);
		String transactionId = TransactionId.getTransactionId(txId, validationRequest.getLand());
		MDC.put(TransactionId.STRING_TRANSACTION_ID, transactionId);
		Response response = validatorService.processRequest(validationRequest, transactionId);
		logger.debug("ENDED - Received request result {}", response);
		MDC.clear();
		return response;
	}
}
