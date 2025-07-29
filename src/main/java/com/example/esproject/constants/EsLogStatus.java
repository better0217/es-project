// 文件路径: src/main/java/com/example/esproject/constants/EsLogStatus.java
package com.example.esproject.constants;

/**
 * ES同步日志状态常量
 */
public final class EsLogStatus {

    private EsLogStatus() {
        // 私有构造函数，防止实例化
    }

    /** 待处理 - 刚创建的记录 */
    public static final String PENDING = "PENDING";

    /** 已发送 - 消息已发送到MQ */
    public static final String SENT = "SENT";

    /** 重试已发送 - 定时任务重新发送 */
    public static final String RETRY_SENT = "RETRY_SENT";

    /** 成功 - ES写入成功 */
    public static final String SUCCESS = "SUCCESS";

    /** 错误 - 处理失败 */
    public static final String ERROR = "ERROR";
}