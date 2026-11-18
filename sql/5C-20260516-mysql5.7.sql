ALTER TABLE nbk_department DROP INDEX nbk_department_info_department_type_IDX;
ALTER TABLE nbk_department ADD ancestors varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '祖先';
ALTER TABLE nbk_department ADD `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '机构路径';

ALTER TABLE nbk_section ADD bureau_id BIGINT NULL COMMENT '局Id';

CREATE TABLE `nbk_file_upload_records` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `folder_name` varchar(100) DEFAULT NULL COMMENT '文件夹名',
  `upload_path` varchar(255) DEFAULT NULL COMMENT '上传目录',
  `created_time` timestamp DEFAULT current_timestamp COMMENT '创建时间',
  `updated_time` timestamp DEFAULT current_timestamp COMMENT '更新时间',
  `created_user` varchar(100) DEFAULT NULL COMMENT '创建人',
  `updated_user` varchar(100) DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件上传记录表';

ALTER TABLE nbk_task MODIFY COLUMN task_folder varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '任务文件夹路径';

ALTER TABLE nbk_inspection_data ADD confirm_status TINYINT NULL COMMENT '确认状态(0:未查看,1:已查看,2:已确认)';

ALTER TABLE nbk_inspection_review_result ADD confirm_status TINYINT  DEFAULT '0' COMMENT '确认状态(0:未查看,1:已查看,2:已确认)';

CREATE TABLE `nbk_defect_storage` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `defect_type` varchar(100) DEFAULT NULL COMMENT '缺陷类型',
  `defect_level` tinyint DEFAULT NULL COMMENT '缺陷等级',
  `status` tinyint DEFAULT NULL COMMENT '状态(0:待确认,1:已确认)',
  `inspection_time` timestamp NULL DEFAULT NULL COMMENT '检测时间',
  `locomotive_number` varchar(100) DEFAULT NULL COMMENT '车架号',
  `section_id` bigint DEFAULT NULL COMMENT '段id',
  `section_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '段名',
  `line_id` bigint DEFAULT NULL COMMENT '线路id',
  `line_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '线路名称',
  `station_id` bigint DEFAULT NULL COMMENT '区站id',
  `station_name` varchar(50) DEFAULT NULL COMMENT '区站名',
  `reviewer_id` bigint DEFAULT NULL COMMENT '审核人id',
  `reviewer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '审核人名称',
  `review_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '审核文本',
  `created_user` bigint DEFAULT NULL COMMENT '创建人',
  `created_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated_user` bigint DEFAULT NULL COMMENT '修改人',
  `updated_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='缺陷库表';

ALTER TABLE nbk_inspection_review_result ADD task_id BIGINT NULL COMMENT '任务id';



