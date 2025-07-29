// 文件路径: src/main/java/com/example/esproject/constants/WorkOrderStatus.java
package com.example.esproject.constants;

/**
 * 工单状态常量
 */
public final class WorkOrderStatus {

    private WorkOrderStatus() {}

    /** 新建 */
    public static final String NEW = "新建";

    /** 待分派 */
    public static final String PENDING_ASSIGN = "待分派";

    /** 处理中 */
    public static final String PROCESSING = "处理中";

    /** 已办结 */
    public static final String FINISHED = "已办结";

    /** 已废单 */
    public static final String ABANDONED = "已废单";
}