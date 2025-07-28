// File path: src/main/java/com/example/esproject/document/WorkOrderDocument.java
package com.example.esproject.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "work-orders")
public class WorkOrderDocument {

    @Id
    private String rowGuid;

    // ... other fields remain the same ...
    @Field(type = FieldType.Keyword)
    private String serialNum;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String rqstPerson;

    // UPDATED LINE
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime rqstTime;

    @Field(type = FieldType.Keyword)
    private String rqstNumber;
    @Field(type = FieldType.Keyword)
    private String linkNumber;
    @Field(type = FieldType.Keyword)
    private String rqstAreaCode;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String rqstAddress;
    @Field(type = FieldType.Keyword)
    private String rqstType;
    @Field(type = FieldType.Keyword)
    private String accordType;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String rqstTitle;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String rqstContent;
    @Field(type = FieldType.Keyword)
    private String rqstSource;
    @Field(type = FieldType.Keyword)
    private String registerName;
    @Field(type = FieldType.Keyword)
    private String cStatus;
    @Field(type = FieldType.Keyword)
    private String isImpt;

    // UPDATED LINE
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime finishTime;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String telTextContent;
}