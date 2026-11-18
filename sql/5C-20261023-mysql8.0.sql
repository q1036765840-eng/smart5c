-- 新增导出信息表
CREATE TABLE `nbk_export_info` (
   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
   `task_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '导出任务编号',
   `source_page` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '导出所属页面',
   `export_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '导出类型',
   `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '导出文件名',
   `quantity` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '数据量',
   `file_size` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '文件大小',
   `query_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '查询参数',
   `export_status` tinyint DEFAULT NULL COMMENT '导出状态(0:导出中,1:成功,2:失败)',
   `created_user` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'system' COMMENT '创建人',
   `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `updated_user` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'system' COMMENT '修改人',
   `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统导出信息表';

-- 确认状态增加误报选项
ALTER TABLE nbk_inspection_data MODIFY COLUMN confirm_status tinyint DEFAULT 0 NULL COMMENT '确认状态(0:未查看,1:已查看,2:已确认,3:误报)';
ALTER TABLE nbk_inspection_review_result MODIFY COLUMN confirm_status tinyint DEFAULT 0 NULL COMMENT '确认状态(0:未查看,1:已查看,2:已确认,3:误报)';
