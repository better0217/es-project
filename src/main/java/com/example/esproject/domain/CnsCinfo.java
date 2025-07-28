// 文件路径: src/main/java/com/example/esproject/domain/CnsCinfo.java
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
public class CnsCinfo {
    // 移除了所有ES注解
    private String rowGuid;
    private String serialNum;
    private String rqstPerson;
    private LocalDateTime rqstTime;
    private String rqstNumber;
    private String linkNumber;
    private String rqstAreaCode;
    private String rqstAddress;
    private String rqstType;
    private String accordType;
    private String rqstTitle;
    private String rqstContent;
    private String rqstSource;
    private String registerName;
    private String cStatus;
    private String isImpt;
    private LocalDateTime finishTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // telTextContent字段在数据库实体中不需要，已移除
}