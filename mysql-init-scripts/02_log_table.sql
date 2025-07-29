-- 文件路径: mysql-init-scripts/02_log_table.sql
CREATE TABLE IF NOT EXISTS `cns_es_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `aggregate_id` varchar(50) NOT NULL COMMENT '聚合根ID, 此处为工单的ROWGUID',
  `aggregate_type` varchar(100) NOT NULL COMMENT '聚合根类型, 例如 WorkOrder',
  `destination` varchar(255) NOT NULL COMMENT '消息目的地, 例如RabbitMQ的Exchange',
  `payload` json NOT NULL COMMENT '消息体, 完整的业务数据JSON',
  `status` varchar(10) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING, SENT',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status_created_at` (`status`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ES同步日志表（事务性发件箱）';
