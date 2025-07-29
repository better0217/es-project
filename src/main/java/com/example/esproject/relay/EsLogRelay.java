// 文件路径: src/main/java/com/example/esproject/relay/EsLogRelay.java
package com.example.esproject.relay;

import com.example.esproject.domain.CnsEsLog;
import com.example.esproject.mapper.CnsEsLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EsLogRelay {

    private final CnsEsLogMapper cnsEsLogMapper;
    private final RabbitTemplate rabbitTemplate;
    private static final int BATCH_SIZE = 100; // 每次处理100条

    @Scheduled(fixedDelay = 10000) // 每10秒执行一次
    public void pollAndPublish() {
        // 只处理失败的记录
        List<CnsEsLog> failedRecords = cnsEsLogMapper.findFailedRecords(100);

        if (failedRecords.isEmpty()) {
            return;
        }

        log.info("重试失败记录: {}", failedRecords.size());

        for (CnsEsLog record : failedRecords) {
            try {
                rabbitTemplate.convertAndSend(record.getDestination(), "", record.getPayload());
                cnsEsLogMapper.updateStatus(record.getId(), "RETRY_SENT");
            } catch (Exception e) {
                log.error("重试发送失败: {}", record.getId(), e);
            }
        }
    }
}