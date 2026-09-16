package com.example.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
@ConditionalOnProperty(name = "notification.rabbitmq.enabled", havingValue = "true")
public class RabbitNotificationConfig {
    public static final String EXCHANGE = "taskcraft.events";
    public static final String QUEUE = "notification.events";
    public static final String DLX = "taskcraft.events.dlx";
    public static final String DLQ = "notification.events.dlq";

    @Bean
    public TopicExchange taskcraftExchange() { return new TopicExchange(EXCHANGE, true, false); }

    @Bean
    public Queue notificationQueue() { return QueueBuilder.durable(QUEUE).deadLetterExchange(DLX).deadLetterRoutingKey(DLQ).build(); }
    @Bean public DirectExchange notificationDeadLetterExchange(){return new DirectExchange(DLX,true,false);}
    @Bean public Queue notificationDeadLetterQueue(){return QueueBuilder.durable(DLQ).build();}
    @Bean public Binding notificationDeadLetterBinding(@Qualifier("notificationDeadLetterQueue") Queue notificationDeadLetterQueue,DirectExchange notificationDeadLetterExchange){return BindingBuilder.bind(notificationDeadLetterQueue).to(notificationDeadLetterExchange).with(DLQ);}

    @Bean
    public Binding notificationBinding(@Qualifier("notificationQueue") Queue notificationQueue, TopicExchange taskcraftExchange) {
        return BindingBuilder.bind(notificationQueue).to(taskcraftExchange).with("notification.#");
    }

    @Bean
    public MessageConverter notificationMessageConverter() { return new Jackson2JsonMessageConverter(); }
}
