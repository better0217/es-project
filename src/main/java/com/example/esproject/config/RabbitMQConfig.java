// 文件路径: src/main/java/com/example/esproject/config/RabbitMQConfig.java
package com.example.esproject.config;

import com.example.esproject.constants.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // ===== 核心业务交换机和队列 =====
    @Bean
    public FanoutExchange workOrderExchange() {
        return new FanoutExchange(RabbitMQConstants.WORKORDER_EXCHANGE);
    }

    @Bean
    public Queue esQueue() {
        // 【新增】为业务队列配置死信交换机
        Map<String, Object> args = new HashMap<>();
        // 当消息被拒绝(rejected)或过期时, 将其路由到下面的死信交换机
        args.put("x-dead-letter-exchange", RabbitMQConstants.WORKORDER_DLQ + ".exchange");
        return new Queue(RabbitMQConstants.WORKORDER_ES_QUEUE, true, false, false, args);
    }

    @Bean
    public Binding binding(Queue esQueue, FanoutExchange workOrderExchange) {
        return BindingBuilder.bind(esQueue).to(workOrderExchange);
    }

    // ===== 死信队列 (Dead-Letter Queue) 的交换机和队列 =====
    // 用于接收处理失败的消息, 以便后续分析和手动处理
    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(RabbitMQConstants.WORKORDER_DLQ + ".exchange");
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(RabbitMQConstants.WORKORDER_DLQ);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, FanoutExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange);
    }
}