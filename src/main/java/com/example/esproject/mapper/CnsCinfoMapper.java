// 文件路径: src/main/java/com/example/esproject/mapper/CnsCinfoMapper.java
package com.example.esproject.mapper;

import com.example.esproject.domain.CnsCinfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CnsCinfoMapper {
    /**
     * 保存或更新一个工单信息
     * 如果主键 (rowGuid) 已存在, 则更新记录; 否则插入新记录.
     * @param cnsCinfo 工单对象
     */
    void save(CnsCinfo cnsCinfo);
}