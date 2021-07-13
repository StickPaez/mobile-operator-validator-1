package com.aldeamo.mobopervalid.rabbit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aldeamo.mobopervalid.model.ValidationResult;
import com.aldeamo.mobopervalid.rabbit.config.RabbitMqConfiguration;

@Component
public class MessagePublisher
{
    @Autowired
    private RabbitMqConfiguration rabbitMqConfiguration;

    public void sendResult(ValidationResult result, String queueName)
    {    	
    	rabbitMqConfiguration.rabbitTemplate().convertAndSend(queueName, result);
    } 
}