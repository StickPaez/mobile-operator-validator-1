package com.aldeamo.mobopervalid.rabbit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.aldeamo.mobopervalid.enumerator.ValidationGsmStatusEnum;
import com.aldeamo.mobopervalid.model.ValidationDetail;
import com.aldeamo.mobopervalid.model.ValidationResult;
import com.aldeamo.mobopervalid.rabbit.config.MessagePublisherConfiguration;

import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class MessagePublisherTest {
    @InjectMocks
    MessagePublisher messagePublisher;

    @Mock
    MessagePublisherConfiguration messagePublConfiguration;

    ValidationResult validationResult;
    String queueName = "testQueue";

    RabbitTemplate rabbitTemplateMock;

    @BeforeEach
    public void init(){
        rabbitTemplateMock = mock(RabbitTemplate.class);

        validationResult = new ValidationResult();
		validationResult.setTotalValid(1);
		ValidationDetail validationDetail1 = new ValidationDetail();
		validationDetail1.setGsm("3001234567");
		validationDetail1.setOperatorId(2);
		validationDetail1.setOperatorName("Claro");
		validationDetail1.setStatus(ValidationGsmStatusEnum.VALID_GSM.getStatus());
		List<ValidationDetail> validList = Arrays.asList(validationDetail1);
		validationResult.setValidList(validList);
		validationResult.setInvalidList(new ArrayList<>());
    }
    
    @Test
    void sendResult_whenValidationResultIsValid_thenSendToRabbit(){
        when(messagePublConfiguration.rabbitTemplate()).thenReturn(rabbitTemplateMock);
        messagePublisher.sendResult(validationResult, queueName);

        ArgumentCaptor<Object> resultCaptor = ArgumentCaptor.forClass(Object.class);
        verify(rabbitTemplateMock, times(1)).convertAndSend(eq(queueName), resultCaptor.capture());

        ValidationResult result = (ValidationResult) resultCaptor.getValue();

        assertNotNull(result);
        assertEquals(validationResult.getTotalValid(), result.getTotalValid());
        assertEquals(validationResult.getTotalInvalid(), result.getTotalInvalid());
        assertEquals(validationResult.getValidList().size(), result.getValidList().size());
        assertEquals(validationResult.getValidList().get(0).getGsm(), result.getValidList().get(0).getGsm());
    }
}
