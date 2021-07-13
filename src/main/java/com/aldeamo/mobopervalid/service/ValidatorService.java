package com.aldeamo.mobopervalid.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aldeamo.mobopervalid.cache.CoreDataCache;
import com.aldeamo.mobopervalid.dto.Response;
import com.aldeamo.mobopervalid.dto.ValidationRequest;
import com.aldeamo.mobopervalid.entity.core.Country;
import com.aldeamo.mobopervalid.entity.core.Operator;
import com.aldeamo.mobopervalid.entity.portability.PortedNumber;
import com.aldeamo.mobopervalid.enumerator.ResponseStatusEnum;
import com.aldeamo.mobopervalid.enumerator.ValidationGsmStatusEnum;
import com.aldeamo.mobopervalid.model.ValidationDetail;
import com.aldeamo.mobopervalid.model.ValidationResult;
import com.aldeamo.mobopervalid.rabbit.MessagePublisher;
import com.aldeamo.mobopervalid.repository.portability.IPortedNumber;
import com.google.gson.Gson;

@Service
public class ValidatorService {
	private static final Logger logger = LoggerFactory.getLogger(ValidatorService.class);
	
	@Value("${application.rabbit.resultQueueName:test.mobileoperatorvalidator.producer}")
	private String resultQueueName;

	@Autowired
	CoreDataCache coreDataCache;
	
	@Autowired
	IPortedNumber portedNumberPersistence;
	
	@Autowired
	MessagePublisher publisher;

	public Response processRequest(ValidationRequest validationRequest, String transactionId) {
		try {			
			Response response = new Response(ResponseStatusEnum.SUCCESS);
			if (!isValidRequest(validationRequest)) {
				logger.error("Invalid request received : {}", validationRequest);
				return new Response(ResponseStatusEnum.BAD_REQUEST);
			}
			logger.info("STARTED validation request process");

			// Valid land code in core DB
			Country coreCountry = coreDataCache.findCountryByCallCode(Integer.parseInt(validationRequest.getLand()));
			if (coreCountry == null) {
				logger.warn("No country found in core DB by land : {}", validationRequest.getLand());
				return new Response(ResponseStatusEnum.BAD_REQUEST);
			}
			ValidationResult result = iterateGsmList(validationRequest, coreCountry);
			publishResult(result);
			saveResult(result, transactionId);
			response.setDataPair("validationResult", new Gson().toJson(result));
			return response;
			
		} catch (Exception e) {
			logger.error("Error in process", e);
			return new Response(ResponseStatusEnum.TRANSACTION_ERROR);
		}
	}
	
	private ValidationResult iterateGsmList(ValidationRequest validationRequest, Country coreCountry) {
		logger.info("INIT iterate to: {}", validationRequest.getGsmList().size());
		List<ValidationDetail> validList = new ArrayList<>();
		List<ValidationDetail> invalidList = new ArrayList<>();
		int totalNull = 0;
		int totalEmpty = 0;
		int totalInvalid = 0;
		int totalPorted = 0;
		for (String gsm : validationRequest.getGsmList()) {
			if (gsm == null) {
				totalNull += 1;
				totalInvalid += 1;
				logger.debug("INVALID GSM - GSM is null");
			} else if (gsm.trim().isEmpty()) {
				totalEmpty += 1;
				totalInvalid += 1;
				logger.debug("INVALID GSM - GSM is empty");
			} else if (!gsm.matches("[0-9]+")) {
				ValidationDetail tmpDetail = new ValidationDetail();
				tmpDetail.setGsm(gsm);
				tmpDetail.setStatus(ValidationGsmStatusEnum.INVALID_FORMAT.getStatus());
				invalidList.add(tmpDetail);
				totalInvalid += 1;
				logger.debug("INVALID GSM - GSM is not numeric [gsm={}]", gsm);
			} else {
				// Validate portability
				ValidationDetail validationDetail = validatePortedNumber(coreCountry, gsm);
				if(validationDetail.getStatus() == ValidationGsmStatusEnum.VALID_GSM.getStatus()) {
					validList.add(validationDetail);
				} else if (validationDetail.getStatus() == ValidationGsmStatusEnum.VALID_GSM_PORTED.getStatus()) {
					validList.add(validationDetail);
					totalPorted += 1;
				} else {
					invalidList.add(validationDetail);
					totalInvalid += 1;
				}
			}
		}
		ValidationResult result = new ValidationResult();
		result.setTotalNull(totalNull);
		result.setTotalEmpty(totalEmpty);
		result.setTotalInvalid(totalInvalid);
		result.setTotalPorted(totalPorted);
		result.setTotalValid(result.getValidList().size());
		result.setValidList(validList);
		result.setInvalidList(invalidList);
		logger.info("Result validation [TotalValid:{}][TotalInvalid:{}]", result.getTotalValid(), result.getTotalInvalid());
		return result;
	}
	
	private ValidationDetail validatePortedNumber(Country coreCountry, String gsm) {
		logger.info("INIT validatePortedNumber to: {}", gsm);
		ValidationDetail tmpDetail = new ValidationDetail();
		tmpDetail.setGsm(gsm);
		PortedNumber portedNumber = portedNumberPersistence.findFirstByGsmAndCountryId(Long.valueOf(gsm),coreCountry.getId());
		logger.info("PortedNumber: {}", portedNumber);
		if (portedNumber != null) {
			if(portedNumber.getOperatorId() <= 0) {
				tmpDetail.setStatus(ValidationGsmStatusEnum.NO_OPERATOR_MATCH.getStatus());
			} else {
				tmpDetail.setOperatorId(portedNumber.getOperatorId());
				tmpDetail.setStatus(ValidationGsmStatusEnum.VALID_GSM_PORTED.getStatus());
				logger.info("Operator to set: {}", portedNumber.getOperatorId());
				tmpDetail.setOperatorName(coreDataCache.findOperatorById(portedNumber.getOperatorId()).getDescription());
			}
		} else {
			// Get Core Operators
			List<Operator> operatorList = coreDataCache.findActiveOperatorsByCountryId(coreCountry.getId());

			int tmpOperatorId = getOperator(gsm, operatorList);

			if (tmpOperatorId > 0) {
				tmpDetail.setOperatorId(tmpOperatorId);
				tmpDetail.setOperatorName(coreDataCache.findOperatorById(tmpOperatorId).getDescription());
				tmpDetail.setStatus(ValidationGsmStatusEnum.VALID_GSM.getStatus());
			} else {
				tmpDetail.setStatus(ValidationGsmStatusEnum.NO_OPERATOR_MATCH.getStatus());
			}
		}
		logger.debug("VALIDATION RESULT [detail={}]", tmpDetail);
		return tmpDetail;
	}	

	private int getOperator(String gsm, List<Operator> operatorList) {
		int tmpOperatorId = 0;
		for (Operator operator : operatorList) {
			if (gsm.matches(operator.getGsmRegExIn())) {
				return operator.getId();
			}
		}
		return tmpOperatorId;
	}

	private boolean isValidRequest(ValidationRequest validationRequest) {
		return validationRequestIsNull(validationRequest) && validationRequest.getGsmList() != null 
				&& validationRequestIsEmpty(validationRequest) && validationRequest.getUserId() > 0;
	}

	private boolean validationRequestIsNull(ValidationRequest validationRequest) {
		return validationRequest != null && validationRequest.getLand() != null;
	}

	private boolean validationRequestIsEmpty(ValidationRequest validationRequest) {
		return !validationRequest.getLand().trim().isEmpty() && !validationRequest.getGsmList().isEmpty();
	}
	
	private void publishResult(ValidationResult validationResult) {
		try {
			publisher.sendResult(validationResult, resultQueueName);
			logger.info("Send result: {}, queueName : {}", validationResult, resultQueueName);
		} catch (Exception e) {
			logger.error("Error sending message to result RabbitMQ: {}",e.getMessage(), e);
		}
	}
	
	private void saveResult(ValidationResult validationResult, String transactionId) {
		try {
			//TODO: Connection to mongo
			logger.info("save validationResult: {}, transactionId : {}", validationResult, transactionId);
		} catch (Exception e) {
			logger.error("Error save result in mongo: {}",e.getMessage(), e);
		}
	}
}
