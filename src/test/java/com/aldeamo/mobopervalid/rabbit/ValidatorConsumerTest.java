package com.aldeamo.mobopervalid.rabbit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.aldeamo.mobopervalid.dto.Response;
import com.aldeamo.mobopervalid.dto.ValidationRequest;
import com.aldeamo.mobopervalid.enumerator.ResponseStatusEnum;
import com.aldeamo.mobopervalid.enumerator.ValidationGsmStatusEnum;
import com.aldeamo.mobopervalid.model.ValidationDetail;
import com.aldeamo.mobopervalid.model.ValidationResult;
import com.aldeamo.mobopervalid.service.ValidatorService;
import com.aldeamo.mobopervalid.util.TransactionId;
import com.google.gson.Gson;

@ExtendWith(MockitoExtension.class)
class ValidatorConsumerTest {
    @InjectMocks
    ValidatorConsumer validatorConsumer;

    @Mock
	ValidatorService validatorService;
    
    @Test
    void whenRequestValidationAndValidParams_thenCallServiceAndReturnSuccess() throws Exception { 	
    	try (MockedStatic<TransactionId> transactionIdMock = mockStatic(TransactionId.class)) {
    		transactionIdMock.when(() -> TransactionId.getTransactionId("","57")).thenReturn("transactionIdTest");
    		   		
    		Response responseMock = new Response(ResponseStatusEnum.SUCCESS);
    		ValidationResult validationResult = new ValidationResult();
    		validationResult.setTotalValid(1);
    		ValidationDetail validationDetail1 = new ValidationDetail();
    		validationDetail1.setGsm("3001234567");
    		validationDetail1.setOperatorId(2);
    		validationDetail1.setOperatorName("Claro");
    		validationDetail1.setStatus(ValidationGsmStatusEnum.VALID_GSM.getStatus());
    		List<ValidationDetail> validList = Arrays.asList(validationDetail1);
    		validationResult.setValidList(validList);
    		validationResult.setInvalidList(new ArrayList<>());
    		responseMock.setDataPair("validationResult", new Gson().toJson(validationResult));
    		when(validatorService.processRequest(any(ValidationRequest.class), anyString())).thenReturn(responseMock);
    		
    		ValidationRequest request = new ValidationRequest();
    		request.setGsmList(Arrays.asList("3001234567"));
    		request.setLand("57");
    		request.setUserId(123456);
    		
    		validatorConsumer.handleMessage(request);
    		
	    	ArgumentCaptor<ValidationRequest> validationRequestCaptor = ArgumentCaptor.forClass(ValidationRequest.class);
	    	verify(validatorService, times(1)).processRequest(validationRequestCaptor.capture(),eq("transactionIdTest"));
	    	
	    	ValidationRequest validationRequest = validationRequestCaptor.getValue();
	    	assertNotNull(validationRequest);
	    	assertEquals(123456, validationRequest.getUserId());
	    	assertEquals("57", validationRequest.getLand());
	    	assertEquals(1, validationRequest.getGsmList().size());
	    	assertEquals("3001234567", validationRequest.getGsmList().get(0));
    	}
    }
}
