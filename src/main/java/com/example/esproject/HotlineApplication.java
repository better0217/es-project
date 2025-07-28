// 文件路径: src/main/java/com/example/esproject/HotlineApplication.java
package com.example.esproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // <-- 导入并添加这个注解

@SpringBootApplication
@EnableScheduling // <-- 添加这个注解来启用Spring的定时任务功能
public class HotlineApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotlineApplication.class, args);
    }
}