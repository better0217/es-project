// 文件路径: src/main/java/com/example/esproject/service/WorkOrderService.java
package com.example.esproject.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.example.esproject.document.WorkOrderDocument;
import com.example.esproject.domain.CnsCinfo;
import com.example.esproject.domain.CnsEsLog;
import com.example.esproject.dto.DashboardStatsDTO;
import com.example.esproject.dto.TermCountDTO;
import com.example.esproject.mapper.CnsCinfoMapper;
import com.example.esproject.mapper.CnsEsLogMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.AggregationsContainer; // <-- 【修正】导入新的聚合容器类
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private final CnsCinfoMapper cnsCinfoMapper;
    private final CnsEsLogMapper cnsEsLogMapper;
    private final ObjectMapper objectMapper;
    private final ElasticsearchOperations elasticsearchOperations;

    @Transactional
    public void saveOrUpdateWorkOrder(CnsCinfo workOrder) {
        try {
            cnsCinfoMapper.save(workOrder);
            String payload = objectMapper.writeValueAsString(workOrder);
            CnsEsLog logEntry = CnsEsLog.builder()
                                        .aggregateId(workOrder.getRowGuid())
                                        .aggregateType("WorkOrder")
                                        .destination("workorder.exchange")
                                        .payload(payload)
                                        .status("PENDING")
                                        .build();
            cnsEsLogMapper.save(logEntry);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize work order to JSON", e);
        }
    }

    public List<WorkOrderDocument> searchWorkOrders(String keyword) {
        Query multiMatchQuery = Query.of(q -> q
                .multiMatch(mmq -> mmq
                        .query(keyword)
                        .fields("rqstPerson", "rqstAddress", "rqstTitle", "rqstContent")
                )
        );

        NativeQuery nativeQuery = new NativeQueryBuilder()
                .withQuery(multiMatchQuery)
                .build();

        SearchHits<WorkOrderDocument> searchHits = elasticsearchOperations.search(nativeQuery, WorkOrderDocument.class);

        return searchHits.getSearchHits().stream()
                         .map(hit -> hit.getContent())
                         .collect(Collectors.toList());
    }

    /**
     * 【V4版 - 最终修正】获取领导驾驶舱的聚合统计数据
     * @param dateRange 时间范围 ("today", "week", "month")
     */
    public DashboardStatsDTO getDashboardStats(String dateRange) {
        // ... (时间范围过滤器的代码无变化)
        Query timeFilterQuery;
        LocalDate now = LocalDate.now();
        switch (dateRange.toLowerCase()) {
            case "week":
                LocalDate startOfWeek = now.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
                timeFilterQuery = Query.of(q -> q.range(r -> r.field("rqstTime").gte(json -> json.stringValue(startOfWeek.toString()))));
                break;
            case "month":
                LocalDate startOfMonth = now.withDayOfMonth(1);
                timeFilterQuery = Query.of(q -> q.range(r -> r.field("rqstTime").gte(json -> json.stringValue(startOfMonth.toString()))));
                break;
            case "today":
            default:
                timeFilterQuery = Query.of(q -> q.range(r -> r.field("rqstTime").gte(json -> json.stringValue(now.toString()))));
                break;
        }

        // ... (聚合查询的构建代码无变化)
        NativeQuery query = new NativeQueryBuilder()
                .withQuery(timeFilterQuery)
                .withAggregations(agg -> agg
                        .put("stats_by_area", a -> a.terms(t -> t.field("rqstAreaCode.keyword").size(10)))
                        .put("stats_by_type", a -> a.terms(t -> t.field("rqstType.keyword").size(10)))
                        .put("stats_by_accord_type", a -> a.terms(t -> t.field("accordType.keyword").size(5)))
                        .put("stats_by_status", a -> a
                                .filters(f -> f
                                        .filters(
                                                Map.of(
                                                        "pending_acceptance", Query.of(q -> q.term(t -> t.field("cstatus.keyword").value("待签收"))),
                                                        "in_progress", Query.of(q -> q.term(t -> t.field("cstatus.keyword").value("处理中"))),
                                                        "feedback_received", Query.of(q -> q.term(t -> t.field("cstatus.keyword").value("已反馈")))
                                                )
                                        )
                                )
                        )
                )
                .withMaxResults(0)
                .build();

        // 3. 【修正】执行查询并使用新的API解析结果
        SearchHits<WorkOrderDocument> searchHits = elasticsearchOperations.search(query, WorkOrderDocument.class);

        // 使用新的 AggregationsContainer API
        AggregationsContainer<?> aggregationsContainer = searchHits.getAggregationsContainer();
        if (aggregationsContainer == null) {
            return DashboardStatsDTO.builder().build();
        }

        var statusBuckets = aggregationsContainer.get("stats_by_status").getAggregate().asFilters().buckets().keyed();

        return DashboardStatsDTO.builder()
                                .totalTickets(searchHits.getTotalHits())
                                .pendingAcceptance(statusBuckets.get("pending_acceptance").docCount())
                                .inProgress(statusBuckets.get("in_progress").docCount())
                                .feedbackReceived(statusBuckets.get("feedback_received").docCount())
                                .statsByArea(parseTermCounts(aggregationsContainer, "stats_by_area"))
                                .statsByType(parseTermCounts(aggregationsContainer, "stats_by_type"))
                                .top5AccordType(parseTermCounts(aggregationsContainer, "stats_by_accord_type"))
                                .build();
    }

    // 【修正】辅助方法的参数类型已更新为 AggregationsContainer
    private List<TermCountDTO> parseTermCounts(AggregationsContainer<?> aggregationsContainer, String aggName) {
        if (aggregationsContainer == null || aggregationsContainer.get(aggName) == null) {
            return List.of();
        }
        return aggregationsContainer.get(aggName).getAggregate()
                                    .asStringTerms().buckets().array().stream()
                                    .map(bucket -> new TermCountDTO(bucket.key().stringValue(), bucket.docCount()))
                                    .collect(Collectors.toList());
    }
}