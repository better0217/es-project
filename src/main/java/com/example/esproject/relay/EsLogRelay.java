// 文件路径: src/main/java/com/example/esproject/relay/EsLogRelay.java
package com.example.esproject.relay;

import com.example.esproject.constants.EsLogStatus;
import com.example.esproject.constants.RabbitMQConstants;
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
    private static final int BATCH_SIZE = 100;

    /**
     * 【已修正】这个定时任务是数据同步的核心驱动。
     * 它的唯一职责就是轮询 cns_es_log 表中所有 PENDING 状态的记录,
     * 将它们发送到RabbitMQ, 然后将状态更新为 SENT。
     */
    @Scheduled(fixedDelay = 5000) // 每5秒执行一次
    public void pollAndPublish() {
        // 1. 【修正】查询 PENDING 状态的记录, 而不是失败的记录
        List<CnsEsLog> pendingRecords = cnsEsLogMapper.findPendingRecords(BATCH_SIZE);

        if (pendingRecords.isEmpty()) {
            return; // 没有待处理记录, 结束本次任务
        }

        log.info("Found {} pending records to publish.", pendingRecords.size());

        try {
            // 2. 遍历记录并发送到RabbitMQ
            for (CnsEsLog record : pendingRecords) {
                rabbitTemplate.convertAndSend(RabbitMQConstants.WORKORDER_EXCHANGE, "", record.getPayload());
            }

            // 3. 批量更新已发送记录的状态为 SENT
            List<Long> sentIds = pendingRecords.stream().map(CnsEsLog::getId).collect(Collectors.toList());
            cnsEsLogMapper.updateStatus(sentIds, EsLogStatus.SENT);

            log.info("Successfully published {} records.", sentIds.size());
        } catch (Exception e) {
            log.error("An error occurred during message publishing.", e);
            // 发生异常时不更新状态, 下次轮询时会自动重试
        }
    }
}