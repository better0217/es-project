// 文件路径: src/main/java/com/example/esproject/mapper/CnsEsLogMapper.java
package com.example.esproject.mapper;

import com.example.esproject.domain.CnsEsLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CnsEsLogMapper {
    /**
     * 插入一条新的ES同步日志
     * @param cnsEsLog 日志对象
     */
    void save(CnsEsLog cnsEsLog);

    /**
     * 查询指定数量的待处理(PENDING)的日志记录
     * @param limit 查询数量
     * @return 日志记录列表
     */
    List<CnsEsLog> findPendingRecords(@Param("limit") int limit);

    /**
     * 批量更新日志记录的状态
     * @param ids 要更新的记录ID列表
     * @param status 新的状态, 例如 "SENT"
     */
    void updateStatus(@Param("ids") List<Long> ids, @Param("status") String status);
    List<CnsEsLog> findFailedRecords(@Param("limit") int limit);
    void updateStatusByAggregateId(@Param("aggregateId") String aggregateId,
                                   @Param("status") String status);
}