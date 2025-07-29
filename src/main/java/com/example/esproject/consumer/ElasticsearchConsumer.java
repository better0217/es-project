// 文件路径: src/main/java/com/example/esproject/consumer/ElasticsearchConsumer.java
package com.example.esproject.consumer;

import com.example.esproject.constants.RabbitMQConstants; // <-- 【修正】导入新的常量类
import com.example.esproject.converter.WorkOrderConverter;
import com.example.esproject.document.WorkOrderDocument;
import com.example.esproject.domain.CnsCinfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticsearchConsumer {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ObjectMapper objectMapper;
    private final WorkOrderConverter workOrderConverter;
    private static final String INDEX_ALIAS = "work-orders";

    // 【修正】将 RabbitMQConfig.QUEUE_NAME 改为 RabbitMQConstants.WORKORDER_ES_QUEUE
    @RabbitListener(queues = RabbitMQConstants.WORKORDER_ES_QUEUE)
    public void handleMessage(String messagePayload) {
        log.info("Received message for indexing: {}", messagePayload);
        try {
            CnsCinfo workOrder = objectMapper.readValue(messagePayload, CnsCinfo.class);
            WorkOrderDocument document = workOrderConverter.toDocument(workOrder);

            IndexQuery indexQuery = new IndexQueryBuilder()
                    .withId(document.getRowGuid())
                    .withObject(document)
                    .build();

            elasticsearchOperations.index(indexQuery, IndexCoordinates.of(INDEX_ALIAS));

            log.info("Successfully indexed work order with ID: {}", document.getRowGuid());

        } catch (Exception e) {
            log.error("Failed to process message. It will be sent to DLQ. Payload: {}", messagePayload, e);
            throw new AmqpRejectAndDontRequeueException("Error processing message", e);
        }
    }
}