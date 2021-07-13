package com.aldeamo.mobopervalid.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aldeamo.mobopervalid.cache.CoreDataCache;
import com.aldeamo.mobopervalid.dto.Response;
import com.aldeamo.mobopervalid.dto.ValidationRequest;
import com.aldeamo.mobopervalid.entity.core.Country;
import com.aldeamo.mobopervalid.entity.core.Operator;
import com.aldeamo.mobopervalid.entity.portability.PortedNumber;
import com.aldeamo.mobopervalid.model.ValidationDetail;
import com.aldeamo.mobopervalid.model.ValidationResult;
import com.aldeamo.mobopervalid.rabbit.MessagePublisher;
import com.aldeamo.mobopervalid.repository.portability.IPortedNumber;
import com.google.gson.Gson;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ValidatorServiceTest {
    @InjectMocks
    ValidatorService validatorService;
    
    @Mock
	CoreDataCache coreDataCache;
    
    @Mock
	IPortedNumber portedNumberPersistence;
    
    @Mock
	MessagePublisher publisher;
    
    String queueName;
    
    ValidationRequest validationRequest;
    
    Country country;
    
    @BeforeEach
    public void init(){
    	queueName = "testQueue";
    	validatorService.setResultQueueName(queueName);
    	
		validationRequest = new ValidationRequest();
		validationRequest.setGsmList(Arrays.asList("3001234567"));
		validationRequest.setLand("57");
		validationRequest.setUserId(123456);
		
		country = new Country();
		country.setName("Colombia");
		country.setId(1);
		country.setStatus(1);
		country.setCallCode(57);
		country.setDescription("Colombia");
    }
    
    @Test
    void processRequest_whenValidationRequestIsNull_ReturnBadRequestAndNotSendToRabbitAndNotSaveInMongo(){
    	Response response = validatorService.processRequest(null, "transactionIdTest");
    	
    	assertNotNull(response);
    	assertEquals(-8, response.getStatus());
    	assertEquals("Bad Request", response.getReason());
    }
    
    @Test
    void processRequest_whenLandInValidationRequestIsNull_ReturnBadRequestAndNotSendToRabbitAndNotSaveInMongo(){
    	validationRequest.setLand(null);
    	
    	Response response = validatorService.processRequest(validationRequest, "transactionIdTest");
    	
    	assertNotNull(response);
    	assertEquals(-8, response.getStatus());
    	assertEquals("Bad Request", response.getReason());
    }
    
    @Test
    void processRequest_whenLandInValidationRequestIsEmpty_ReturnBadRequestAndNotSendToRabbitAndNotSaveInMongo(){
    	validationRequest.setLand("");
    	
    	Response response = validatorService.processRequest(validationRequest, "transactionIdTest");
    	
    	assertNotNull(response);
    	assertEquals(-8, response.getStatus());
    	assertEquals("Bad Request", response.getReason());
    }
    
    @Test
    void processRequest_whenGsmListInValidationRequestIsNull_ReturnBadRequestAndNotSendToRabbitAndNotSaveInMongo(){
    	validationRequest.setGsmList(null);
    	
    	Response response = validatorService.processRequest(validationRequest, "transactionIdTest");
    	
    	assertNotNull(response);
    	assertEquals(-8, response.getStatus());
    	assertEquals("Bad Request", response.getReason());
    }
    
    @Test
    void processRequest_whenGsmListInValidationRequestIsEmpty_ReturnBadRequestAndNotSendToRabbitAndNotSaveInMongo(){
    	validationRequest.setGsmList(new ArrayList<>());
    	
    	Response response = validatorService.processRequest(validationRequest, "transactionIdTest");
    	
    	assertNotNull(response);
    	assertEquals(-8, response.getStatus());
    	assertEquals("Bad Request", response.getReason());
    }
    
    @Test
    void processRequest_whenUserIdInValidationRequestIsZero_ReturnBadRequestAndNotSendToRabbitAndNotSaveInMongo(){
    	validationRequest.setUserId(0);
    	
    	Response response = validatorService.processRequest(validationRequest, "transactionIdTest");
    	
    	assertNotNull(response);
    	assertEquals(-8, response.getStatus());
    	assertEquals("Bad Request", response.getReason());
    }
    
    @Test
    void processRequest_whenLandIsNotValid_ReturnBadRequestAndNotSendToRabbitAndNotSaveInMongo(){
    	when(coreDataCache.findCountryByCallCode(57)).thenReturn(null);
    	
    	Response response = validatorService.processRequest(validationRequest, "transactionIdTest");
    	
    	assertNotNull(response);
    	assertEquals(-8, response.getStatus());
    	assertEquals("Bad Request", response.getReason());
    }
    
    @Test
    void processRequest_whenGsmIsNull_ReturnTotalNullAndTotalInvalidAndSendToRabbitAndSaveInMongo(){
    	String gsm1 = null;
    	validationRequest.setGsmList(Arrays.asList(gsm1));
    	
    	when(coreDataCache.findCountryByCallCode(57)).thenReturn(country);
    	
    	Response response = validatorService.processRequest(validationRequest, "transactionIdTest");
    	
    	assertNotNull(response);
    	assertEquals(1, response.getStatus());
    	assertEquals("Request Received", response.getReason());
    	assertNotNull(response.getData());
    	assertTrue(response.getData().containsKey("validationResult"));
    	assertNotNull(response.getData().get("validationResult"));
    	
    	ValidationResult validationResult = new Gson().fromJson(response.getData().get("validationResult").toString(),ValidationResult.class);
    	assertNotNull(validationResult);
    	assertEquals(1, validationResult.getTotalInvalid());
    	assertEquals(1, validationResult.getTotalNull());
    	assertEquals(0, validationResult.getTotalValid());
    	assertEquals(0, validationResult.getTotalEmpty());
    	assertEquals(0, validationResult.getTotalPorted());
    	assertTrue(validationResult.getInvalidList().isEmpty());
    	assertTrue(validationResult.getValidList().isEmpty());
    	
    	verify(publisher, times(1)).sendResult(any(ValidationResult.class), eq(queueName));
    }
    
    @Test
    void processRequest_whenGsmIsEmpty_ReturnTotalEmptyAndTotalInvalidAndSendToRabbitAndSaveInMongo(){
    	String gsm1 = "";
    	validationRequest.setGsmList(Arrays.asList(gsm1));
    	
    	when(coreDataCache.findCountryByCallCode(57)).thenReturn(country);
    	
    	Response response = validatorService.processRequest(validationRequest, "transactionIdTest");
    	
    	assertNotNull(response);
    	assertEquals(1, response.getStatus());
    	assertEquals("Request Received", response.getReason());
    	assertNotNull(response.getData());
    	assertTrue(response.getData().containsKey("validationResult"));
    	assertNotNull(response.getData().get("validationResult"));
    	
    	ValidationResult validationResult = new Gson().fromJson(response.getData().get("validationResult").toString(),ValidationResult.class);
    	assertNotNull(validationResult);
    	assertEquals(1, validationResult.getTotalInvalid());
    	assertEquals(0, validationResult.getTotalNull());
    	assertEquals(0, validationResult.getTotalValid());
    	assertEquals(1, validationResult.getTotalEmpty());
    	assertEquals(0, validationResult.getTotalPorted());
    	assertTrue(validationResult.getInvalidList().isEmpty());
    	assertTrue(validationResult.getValidList().isEmpty());
    	
    	verify(publisher, times(1)).sendResult(any(ValidationResult.class), eq(queueName));
    }
    
    @Test
    void processRequest_whenGsmHasInvalidFormat_ReturnTotalInvalidAndSendToRabbitAndSaveInMongo(){
    	String gsm1 = "gsm3001234567";
    	validationRequest.setGsmList(Arrays.asList(gsm1));
    	
    	when(coreDataCache.findCountryByCallCode(57)).thenReturn(country);
    	
    	Response response = validatorService.processRequest(validationRequest, "transactionIdTest");
    	
    	assertNotNull(response);
    	assertEquals(1, response.getStatus());
    	assertEquals("Request Received", response.getReason());
    	assertNotNull(response.getData());
    	assertTrue(response.getData().containsKey("validationResult"));
    	assertNotNull(response.getData().get("validationResult"));
    	
    	ValidationResult validationResult = new Gson().fromJson(response.getData().get("validationResult").toString(),ValidationResult.class);
    	assertNotNull(validationResult);
    	assertEquals(1, validationResult.getTotalInvalid());
    	assertEquals(0, validationResult.getTotalNull());
    	assertEquals(0, validationResult.getTotalValid());
    	assertEquals(0, validationResult.getTotalEmpty());
    	assertEquals(0, validationResult.getTotalPorted());
    	assertTrue(validationResult.getValidList().isEmpty());
    	assertEquals(1, validationResult.getInvalidList().size());
    	ValidationDetail validationDetail1 = validationResult.getInvalidList().get(0);
    	assertNotNull(validationDetail1);
    	assertEquals("gsm3001234567",validationDetail1.getGsm());
    	assertEquals(4, validationDetail1.getStatus());
    	assertEquals(0, validationDetail1.getOperatorId());
    	assertEquals(null, validationDetail1.getOperatorName());
    	
    	verify(publisher, times(1)).sendResult(any(ValidationResult.class), eq(queueName));
    }
    
    @Test
    void processRequest_whenGsmIsPorted_ReturnTotalValidAndSendToRabbitAndSaveInMongo(){
    	when(coreDataCache.findCountryByCallCode(57)).thenReturn(country);
    	
    	PortedNumber portedNumber = new PortedNumber();
    	portedNumber.setCountryId(1);
    	portedNumber.setOperatorId(3);
    	portedNumber.setGsm(3001234567l);
    	when(portedNumberPersistence.findFirstByGsmAndCountryId(3001234567l, 1)).thenReturn(portedNumber);
    	
    	Operator operatorTo = new Operator();
    	operatorTo.setId(3);
    	operatorTo.setDescription("tigo");
    	when(coreDataCache.findOperatorById(3)).thenReturn(operatorTo);
    	
    	Response response = validatorService.processRequest(validationRequest, "transactionIdTest");
    	
    	assertNotNull(response);
    	assertEquals(1, response.getStatus());
    	assertEquals("Request Received", response.getReason());
    	assertNotNull(response.getData());
    	assertTrue(response.getData().containsKey("validationResult"));
    	assertNotNull(response.getData().get("validationResult"));
    	
    	ValidationResult validationResult = new Gson().fromJson(response.getData().get("validationResult").toString(),ValidationResult.class);
    	assertNotNull(validationResult);
    	assertEquals(0, validationResult.getTotalInvalid());
    	assertEquals(0, validationResult.getTotalNull());
    	assertEquals(1, validationResult.getTotalValid());
    	assertEquals(0, validationResult.getTotalEmpty());
    	assertEquals(1, validationResult.getTotalPorted());
    	assertTrue(validationResult.getInvalidList().isEmpty());
    	assertEquals(1, validationResult.getValidList().size());
    	ValidationDetail validationDetail1 = validationResult.getValidList().get(0);
    	assertNotNull(validationDetail1);
    	assertEquals("3001234567",validationDetail1.getGsm());
    	assertEquals(1, validationDetail1.getStatus());
    	assertEquals(3, validationDetail1.getOperatorId());
    	assertEquals("tigo", validationDetail1.getOperatorName());
    	
    	verify(publisher, times(1)).sendResult(any(ValidationResult.class), eq(queueName));
    }
    
    @Test
    void processRequest_whenGsmIsPortedButOperatorIdIsZero_ReturnTotalInvalidAndSendToRabbitAndSaveInMongo(){
    	when(coreDataCache.findCountryByCallCode(57)).thenReturn(country);
    	
    	PortedNumber portedNumber = new PortedNumber();
    	portedNumber.setCountryId(1);
    	portedNumber.setOperatorId(0);
    	portedNumber.setGsm(3001234567l);
    	when(portedNumberPersistence.findFirstByGsmAndCountryId(3001234567l, 1)).thenReturn(portedNumber);
    	
    	Response response = validatorService.processRequest(validationRequest, "transactionIdTest");
    	
    	assertNotNull(response);
    	assertEquals(1, response.getStatus());
    	assertEquals("Request Received", response.getReason());
    	assertNotNull(response.getData());
    	assertTrue(response.getData().containsKey("validationResult"));
    	assertNotNull(response.getData().get("validationResult"));
    	
    	ValidationResult validationResult = new Gson().fromJson(response.getData().get("validationResult").toString(),ValidationResult.class);
    	assertNotNull(validationResult);
    	assertEquals(1, validationResult.getTotalInvalid());
    	assertEquals(0, validationResult.getTotalNull());
    	assertEquals(0, validationResult.getTotalValid());
    	assertEquals(0, validationResult.getTotalEmpty());
    	assertEquals(0, validationResult.getTotalPorted());
    	assertTrue(validationResult.getValidList().isEmpty());
    	assertEquals(1, validationResult.getInvalidList().size());
    	ValidationDetail validationDetail1 = validationResult.getInvalidList().get(0);
    	assertNotNull(validationDetail1);
    	assertEquals("3001234567",validationDetail1.getGsm());
    	assertEquals(3, validationDetail1.getStatus());
    	assertEquals(0, validationDetail1.getOperatorId());
    	assertEquals(null, validationDetail1.getOperatorName());
    	
    	verify(publisher, times(1)).sendResult(any(ValidationResult.class), eq(queueName));
    }
    
    @Test
    void processRequest_whenGsmIsNotPortedAndDoesNotMatchWithOperatorsRegEx_ReturnTotalInvalidAndSendToRabbitAndSaveInMongo(){
    	when(coreDataCache.findCountryByCallCode(57)).thenReturn(country);
    	
    	when(portedNumberPersistence.findFirstByGsmAndCountryId(3001234567l, 1)).thenReturn(null);
    	
    	Operator operator1 = new Operator();
    	operator1.setId(2);
    	operator1.setGsmRegExIn("^310[0-9]{7}$");
    	List<Operator> operators = Arrays.asList(operator1);
    	when(coreDataCache.findActiveOperatorsByCountryId(1)).thenReturn(operators);
    	
    	Response response = validatorService.processRequest(validationRequest, "transactionIdTest");
    	
    	assertNotNull(response);
    	assertEquals(1, response.getStatus());
    	assertEquals("Request Received", response.getReason());
    	assertNotNull(response.getData());
    	assertTrue(response.getData().containsKey("validationResult"));
    	assertNotNull(response.getData().get("validationResult"));
    	
    	ValidationResult validationResult = new Gson().fromJson(response.getData().get("validationResult").toString(),ValidationResult.class);
    	assertNotNull(validationResult);
    	assertEquals(1, validationResult.getTotalInvalid());
    	assertEquals(0, validationResult.getTotalNull());
    	assertEquals(0, validationResult.getTotalValid());
    	assertEquals(0, validationResult.getTotalEmpty());
    	assertEquals(0, validationResult.getTotalPorted());
    	assertTrue(validationResult.getValidList().isEmpty());
    	assertEquals(1, validationResult.getInvalidList().size());
    	ValidationDetail validationDetail1 = validationResult.getInvalidList().get(0);
    	assertNotNull(validationDetail1);
    	assertEquals("3001234567",validationDetail1.getGsm());
    	assertEquals(3, validationDetail1.getStatus());
    	assertEquals(0, validationDetail1.getOperatorId());
    	assertEquals(null, validationDetail1.getOperatorName());
    	
    	verify(publisher, times(1)).sendResult(any(ValidationResult.class), eq(queueName));
    }
    
    @Test
    void processRequest_whenGsmIsNotPortedAndMatchesWithRegExOfOperator_ReturnTotalInvalidAndSendToRabbitAndSaveInMongo(){
    	when(coreDataCache.findCountryByCallCode(57)).thenReturn(country);
    	
    	when(portedNumberPersistence.findFirstByGsmAndCountryId(3001234567l, 1)).thenReturn(null);
    	
    	Operator operator1 = new Operator();
    	operator1.setId(2);
    	operator1.setDescription("claro");
    	operator1.setGsmRegExIn("^300[0-9]{7}$");
    	List<Operator> operators = Arrays.asList(operator1);
    	when(coreDataCache.findActiveOperatorsByCountryId(1)).thenReturn(operators);

    	when(coreDataCache.findOperatorById(2)).thenReturn(operator1);
    	
    	Response response = validatorService.processRequest(validationRequest, "transactionIdTest");
    	
    	assertNotNull(response);
    	assertEquals(1, response.getStatus());
    	assertEquals("Request Received", response.getReason());
    	assertNotNull(response.getData());
    	assertTrue(response.getData().containsKey("validationResult"));
    	assertNotNull(response.getData().get("validationResult"));
    	
    	ValidationResult validationResult = new Gson().fromJson(response.getData().get("validationResult").toString(),ValidationResult.class);
    	assertNotNull(validationResult);
    	assertEquals(0, validationResult.getTotalInvalid());
    	assertEquals(0, validationResult.getTotalNull());
    	assertEquals(1, validationResult.getTotalValid());
    	assertEquals(0, validationResult.getTotalEmpty());
    	assertEquals(0, validationResult.getTotalPorted());
    	assertTrue(validationResult.getInvalidList().isEmpty());
    	assertEquals(1, validationResult.getValidList().size());
    	ValidationDetail validationDetail1 = validationResult.getValidList().get(0);
    	assertNotNull(validationDetail1);
    	assertEquals("3001234567",validationDetail1.getGsm());
    	assertEquals(0, validationDetail1.getStatus());
    	assertEquals(2, validationDetail1.getOperatorId());
    	assertEquals("claro", validationDetail1.getOperatorName());
    	
    	verify(publisher, times(1)).sendResult(any(ValidationResult.class), eq(queueName));
    }
}
