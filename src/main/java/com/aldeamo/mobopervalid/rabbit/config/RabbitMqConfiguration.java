package com.aldeamo.mobopervalid.rabbit.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aldeamo.mobopervalid.rabbit.ValidatorConsumer;

@Configuration
public class RabbitMqConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(RabbitMqConfiguration.class);
	@Value("${application.rabbit.addresses}")
	private String addresses;
	@Value("${application.rabbit.user}")
	private String rabbitUser;
	@Value("${application.rabbit.pass}")
	private String rabbitPass;
	@Value("${application.rabbit.queueNames}")
	private String queueNames;
	@Value("${application.rabbit.producerQueueNames}")
	private String producerQueueNames;
	@Value("${application.rabbit.concurrentConsumers}")
	private String concurrentConsumers;


	@Bean
	ValidatorConsumer receiver() {
		return new ValidatorConsumer();
	}

	@Bean
	MessageListenerAdapter listenerAdapter(ValidatorConsumer receiver) {
		return new MessageListenerAdapter(receiver, jsonMessageConverter());
	}

	@Bean
	SimpleMessageListenerContainer container(MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory());

		String[] queues = queueNames.split("\\|", -1);
		String[] producerQueues = producerQueueNames.split("\\|", -1);
		Map<String, Object> argumentsQueue = new HashMap<>();
		argumentsQueue.put("x-max-priority", 10);
		AmqpAdmin adm = new RabbitAdmin(connectionFactory());
		
		for (String queueName : queues) {
			Queue queue = new Queue(queueName, true, false, false, argumentsQueue);
			adm.declareQueue(queue);
			logger.info("CREATED QUEUE : {} ", queue.getName());
		}

		for (String queueName : producerQueues) {
			Queue queue = new Queue(queueName, true, false, false, argumentsQueue);
			adm.declareQueue(queue);
			logger.info("CREATED PRODUCER QUEUE : {} ",queue.getName());
		}
		container.setQueueNames(queues);
		container.setConcurrentConsumers(Integer.parseInt(concurrentConsumers));
		container.setMessageListener(listenerAdapter);
		container.setPrefetchCount(1);
		return container;
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setUsername(rabbitUser);
		connectionFactory.setPassword(rabbitPass);
		connectionFactory.setAddresses(addresses);
		logger.debug("Rabbit Connection Factory STARTED - Adresses : {} User : {} Pass :{}",addresses,rabbitUser,rabbitPass);
		return connectionFactory;
	}

	@Bean
	public AmqpAdmin amqpAdmin() {		
		return new RabbitAdmin(connectionFactory());
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
	
    @Bean
    public RabbitTemplate rabbitTemplate()
    {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());      
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
