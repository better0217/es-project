// 文件路径: src/main/java/com/example/esproject/constants/RabbitMQConstants.java
package com.example.esproject.constants;

/**
 * RabbitMQ相关常量
 */
public final class RabbitMQConstants {

    private RabbitMQConstants() {}

    /** 工单交换机名称 */
    public static final String WORKORDER_EXCHANGE = "workorder.exchange";

    /** 工单ES队列名称 */
    public static final String WORKORDER_ES_QUEUE = "workorder.es.queue";

    /** 重试队列名称 */
    public static final String WORKORDER_RETRY_QUEUE = "workorder.retry.queue";

    /** 死信队列名称 */
    public static final String WORKORDER_DLQ = "workorder.dlq";
}