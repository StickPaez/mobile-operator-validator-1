package com.aldeamo.mobopervalid.rabbit;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aldeamo.mobopervalid.rabbit.config.RabbitMqConfiguration;

@Component
public class MessagePublisher
{
    @Autowired
    private RabbitMqConfiguration rabbitMqConfiguration;

}