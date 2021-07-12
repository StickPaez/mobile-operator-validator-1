package com.aldeamo.mobopervalid.rabbit.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagePublisherConfiguration extends RabbitMqConfiguration {
	@Bean
    public RabbitTemplate rabbitTemplate()
    {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
