package com.aldeamo.mobopervalid.rabbit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aldeamo.mobopervalid.model.ValidationResult;
import com.aldeamo.mobopervalid.rabbit.config.MessagePublisherConfiguration;

@Component
public class MessagePublisher
{
    @Autowired
    private MessagePublisherConfiguration messagePublisherConfiguration;

    public void sendResult(ValidationResult result, String queueName)
    {    	
    	messagePublisherConfiguration.rabbitTemplate().convertAndSend(queueName, result);
    } 
}