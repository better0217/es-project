// 文件路径: src/main/java/com/example/esproject/config/JacksonConfig.java
package com.example.esproject.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 注册JavaTimeModule模块, 以支持LocalDateTime等类型的序列化和反序列化
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}