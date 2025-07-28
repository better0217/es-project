// 文件路径: src/main/java/com/example/esproject/repository/WorkOrderSearchRepository.java
package com.example.esproject.repository;

import com.example.esproject.document.WorkOrderDocument; // <-- 导入新的Document类
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

// 将泛型中的CnsCinfo替换为WorkOrderDocument
public interface WorkOrderSearchRepository extends ElasticsearchRepository<WorkOrderDocument, String> {
}