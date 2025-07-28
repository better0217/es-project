// 文件路径: src/main/java/com/example/esproject/service/WorkOrderService.java
package com.example.esproject.service;

// 1. 移除旧的、错误的import
// import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

// 2. 导入新版客户端的Query类
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.example.esproject.document.WorkOrderDocument;
import com.example.esproject.domain.CnsCinfo;
import com.example.esproject.domain.CnsEsLog;
import com.example.esproject.mapper.CnsCinfoMapper;
import com.example.esproject.mapper.CnsEsLogMapper;
import com.example.esproject.repository.WorkOrderSearchRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private final CnsCinfoMapper cnsCinfoMapper;
    private final CnsEsLogMapper cnsEsLogMapper;
    private final ObjectMapper objectMapper;
    private final WorkOrderSearchRepository searchRepository;
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

    /**
     * 【已更新】根据关键词在多个字段中进行模糊搜索 (使用新版ES Java Client API)
     * @param keyword 搜索关键词
     * @return 匹配的工单列表
     */
    public List<WorkOrderDocument> searchWorkOrders(String keyword) {
        // 3. 使用新版API的构建器来创建multi_match查询
        Query multiMatchQuery = Query.of(q -> q
                .multiMatch(mmq -> mmq
                        .query(keyword)
                        .fields("rqstPerson", "rqstAddress", "rqstTitle", "rqstContent")
                )
        );

        // 4. 将构建好的Query对象包装到NativeQuery中
        NativeQuery nativeQuery = new NativeQueryBuilder()
                .withQuery(multiMatchQuery)
                .build();

        // 执行查询 (这部分不变)
        SearchHits<WorkOrderDocument> searchHits = elasticsearchOperations.search(nativeQuery, WorkOrderDocument.class);

        return searchHits.getSearchHits().stream()
                         .map(hit -> hit.getContent())
                         .collect(Collectors.toList());
    }
}