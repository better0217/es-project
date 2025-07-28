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
        // 1. 从数据库捞取待发送的记录
        List<CnsEsLog> pendingRecords = cnsEsLogMapper.findPendingRecords(BATCH_SIZE);

        if (pendingRecords.isEmpty()) {
            return; // 没有待处理记录, 结束本次任务
        }

        log.info("Found {} pending records to publish.", pendingRecords.size());

        // 2. 遍历记录并发送到RabbitMQ
        for (CnsEsLog record : pendingRecords) {
            // 参数: exchange名字, routingKey (fanout模式下为空), 消息体
            rabbitTemplate.convertAndSend(record.getDestination(), "", record.getPayload());
        }

        // 3. 批量更新已发送记录的状态
        List<Long> sentIds = pendingRecords.stream().map(CnsEsLog::getId).collect(Collectors.toList());
        cnsEsLogMapper.updateStatus(sentIds, "SENT");

        log.info("Successfully published {} records.", sentIds.size());
    }
}