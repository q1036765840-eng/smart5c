CREATE TABLE `nbk_folder_create` (
 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
 `folder_name` varchar(100) DEFAULT NULL COMMENT '文件夹名',
 `folder_path` varchar(255) DEFAULT NULL COMMENT '文件夹路径',
 `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 `updated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
 `created_user` varchar(100) DEFAULT NULL COMMENT '创建人',
 `updated_user` varchar(100) DEFAULT NULL COMMENT '修改人',
 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文件夹创建记录表';