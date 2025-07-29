// 文件路径: src/main/java/com/example/esproject/dto/DashboardStatsDTO.java
package com.example.esproject.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DashboardStatsDTO {
    // 对应右上角 - 核心KPI区
    private long totalTickets;
    private long pendingAcceptance;
    private long inProgress;
    private long feedbackReceived;

    // 【新增】对应左侧 - 按诉求地区统计
    private List<TermCountDTO> statsByArea;

    // 对应左侧 - 诉求分类
    private List<TermCountDTO> statsByType;

    // 对应右下角 - 热门归口TOP5
    private List<TermCountDTO> top5AccordType;
}