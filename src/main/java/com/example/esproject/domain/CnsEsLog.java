// 文件路径: src/main/java/com/example/esproject/domain/CnsEsLog.java
package com.example.esproject.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CnsEsLog {
    private Long id;
    private String aggregateId;
    private String aggregateType;
    private String destination;
    private String payload; // 存储业务数据的JSON字符串
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}