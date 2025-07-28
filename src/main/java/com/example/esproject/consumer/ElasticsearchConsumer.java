// 文件路径: src/main/java/com/example/esproject/consumer/ElasticsearchConsumer.java
package com.example.esproject.consumer;

import com.example.esproject.config.RabbitMQConfig;
import com.example.esproject.converter.WorkOrderConverter; // <-- 导入转换器
import com.example.esproject.document.WorkOrderDocument; // <-- 导入Document
import com.example.esproject.domain.CnsCinfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final WorkOrderConverter workOrderConverter; // <-- 注入转换器
    private static final String INDEX_ALIAS = "work-orders";

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleMessage(String messagePayload) {
        log.info("Received message for indexing: {}", messagePayload);
        try {
            // 1. 反序列化为数据库实体
            CnsCinfo workOrder = objectMapper.readValue(messagePayload, CnsCinfo.class);

            // 2. 将数据库实体转换为ES文档实体
            WorkOrderDocument document = workOrderConverter.toDocument(workOrder);

            // 3. 构建索引请求
            IndexQuery indexQuery = new IndexQueryBuilder()
                    .withId(document.getRowGuid())
                    .withObject(document)
                    .build();

            // 4. 执行索引
            elasticsearchOperations.index(indexQuery, IndexCoordinates.of(INDEX_ALIAS));

            log.info("Successfully indexed work order with ID: {}", document.getRowGuid());

        } catch (JsonProcessingException e) {
            log.error("Failed to parse message payload.", e);
        } catch (Exception e) {
            log.error("Failed to index document to Elasticsearch.", e);
        }
    }
}