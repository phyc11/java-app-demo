package com.example.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "notification.rabbitmq.enabled", havingValue = "true")
public class RabbitNotificationConfig {
    public static final String EXCHANGE = "taskcraft.events";
    public static final String QUEUE = "notification.events";

    @Bean
    public TopicExchange taskcraftExchange() { return new TopicExchange(EXCHANGE, true, false); }

    @Bean
    public Queue notificationQueue() { return QueueBuilder.durable(QUEUE).build(); }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange taskcraftExchange) {
        return BindingBuilder.bind(notificationQueue).to(taskcraftExchange).with("notification.#");
    }

    @Bean
    public MessageConverter notificationMessageConverter() { return new Jackson2JsonMessageConverter(); }
}
