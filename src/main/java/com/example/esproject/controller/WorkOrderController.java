// 文件路径: src/main/java/com/example/esproject/controller/WorkOrderController.java
package com.example.esproject.controller;

import com.example.esproject.document.WorkOrderDocument;
import com.example.esproject.domain.CnsCinfo;
import com.example.esproject.dto.DashboardStatsDTO;
import com.example.esproject.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    @PostMapping
    public ResponseEntity<CnsCinfo> createOrUpdateWorkOrder(@RequestBody CnsCinfo workOrder) {
        // 在真实业务中, ROWGUID和时间可能由其他服务或数据库生成
        // 为了方便测试, 如果传入的工单没有ID, 我们在此处生成一个
        if (Objects.isNull(workOrder.getRowGuid()) || workOrder.getRowGuid().isEmpty()) {
            workOrder.setRowGuid(UUID.randomUUID().toString());
        }
        if (Objects.isNull(workOrder.getRqstTime())) {
            workOrder.setRqstTime(LocalDateTime.now());
        }
        workOrderService.saveOrUpdateWorkOrder(workOrder);

        // 返回 201 Created 状态码和创建的工单数据
        return new ResponseEntity<>(workOrder, HttpStatus.CREATED);
    }
    @GetMapping("/search")
    public ResponseEntity<List<WorkOrderDocument>> search(@RequestParam String keyword) { // <-- 修改返回类型
        List<WorkOrderDocument> results = workOrderService.searchWorkOrders(keyword);
        return ResponseEntity.ok(results);
    }
    /**
     * 【V2版 - 已升级】领导驾驶舱统计数据接口, 支持时间范围
     * @param range 可选参数, "today", "week", "month". 默认为 "today"
     */
    @GetMapping("/stats/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats(
            @RequestParam(defaultValue = "today") String range) {
        DashboardStatsDTO stats = workOrderService.getDashboardStats(range);
        return ResponseEntity.ok(stats);
    }
}