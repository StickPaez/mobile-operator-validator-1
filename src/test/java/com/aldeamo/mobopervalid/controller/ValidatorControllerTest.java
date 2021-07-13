package com.aldeamo.mobopervalid.controller;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.aldeamo.mobopervalid.dto.Response;
import com.aldeamo.mobopervalid.dto.ValidationRequest;
import com.aldeamo.mobopervalid.enumerator.ResponseStatusEnum;
import com.aldeamo.mobopervalid.enumerator.ValidationGsmStatusEnum;
import com.aldeamo.mobopervalid.model.ValidationDetail;
import com.aldeamo.mobopervalid.model.ValidationResult;
import com.aldeamo.mobopervalid.service.ValidatorService;
import com.aldeamo.mobopervalid.util.TransactionId;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebAppConfiguration
class ValidatorControllerTest {
    
	private MockMvc mvc;
	
    @InjectMocks
    ValidatorController validatorController;
    
    @Mock
	ValidatorService validatorService;
    
	@BeforeEach
    public void prepareMock() {
        mvc = MockMvcBuilders.standaloneSetup(validatorController).build();
    }
	
	public static String asJsonString(final Object obj) {
	    try {
	        final ObjectMapper mapper = new ObjectMapper();
	        final String jsonContent = mapper.writeValueAsString(obj);
	        return jsonContent;
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
	
    @Test
    void whenDefaultRoute_thenReturn404() throws Exception {
    	mvc.perform(MockMvcRequestBuilders.get("/")).andExpect(status().isNotFound());
    }
    
    @Test
    void whenRequestValidationAndNoParams_thenReturn400() throws Exception {
    	mvc.perform(MockMvcRequestBuilders.post("/requestValidation")).andExpect(status().isBadRequest());
    }
    
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
    		
	    	MvcResult result = mvc.perform(
	    			MockMvcRequestBuilders.post("/requestValidation")
	    				.content(asJsonString(request))
	    				.contentType(MediaType.APPLICATION_JSON)
	    				.accept(MediaType.APPLICATION_JSON)
	    			)
	    			.andExpect(status().isOk())
	    			.andReturn();
	    	
	    	ArgumentCaptor<ValidationRequest> validationRequestCaptor = ArgumentCaptor.forClass(ValidationRequest.class);
	    	verify(validatorService, times(1)).processRequest(validationRequestCaptor.capture(),eq("transactionIdTest"));
	    	
	    	ValidationRequest validationRequest = validationRequestCaptor.getValue();
	    	assertNotNull(validationRequest);
	    	assertEquals(123456, validationRequest.getUserId());
	    	assertEquals("57", validationRequest.getLand());
	    	assertEquals(1, validationRequest.getGsmList().size());
	    	assertEquals("3001234567", validationRequest.getGsmList().get(0));
	    	
	    	String actualResponse = result.getResponse().getContentAsString();
	    	
	    	JSONAssert.assertEquals("{\"status\":1,\"reason\":\"Request Received\",\"data\":{\"validationResult\":"
	    			+ "\"{\\\"totalValid\\\":1,\\\"totalInvalid\\\":0,\\\"totalNull\\\":0,\\\"totalEmpty\\\":0,\\\"totalPorted\\\":0,"
	    			+ "\\\"validList\\\":[{\\\"gsm\\\":\\\"3001234567\\\",\\\"status\\\":0,\\\"operatorId\\\":2,\\\"operatorName\\\":\\\"Claro\\\"}],"
	    			+ "\\\"invalidList\\\":[]}\"}}",
	    			actualResponse, JSONCompareMode.LENIENT);
    	}
    }
}
