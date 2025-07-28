// 文件路径: src/main/java/com/example/esproject/config/RabbitMQConfig.java
package com.example.esproject.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "workorder.exchange";
    public static final String QUEUE_NAME = "workorder.es.queue";

    @Bean
    public FanoutExchange workOrderExchange() {
        return new FanoutExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue esQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Binding binding(Queue esQueue, FanoutExchange workOrderExchange) {
        return BindingBuilder.bind(esQueue).to(workOrderExchange);
    }
}