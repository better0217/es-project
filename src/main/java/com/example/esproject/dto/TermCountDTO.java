// 文件路径: src/main/java/com/example/esproject/dto/TermCountDTO.java
package com.example.esproject.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class TermCountDTO {
    private String term;
    private long count;
}