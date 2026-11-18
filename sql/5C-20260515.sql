-- nbk_task definition

CREATE TABLE `nbk_task` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `task_name` varchar(100) DEFAULT NULL COMMENT '任务名',
    `task_folder` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务文件夹路径',
    `priority` tinyint DEFAULT NULL COMMENT '0:低,1:中,2:高',
    `task_status` tinyint DEFAULT NULL COMMENT '任务状态(0:创建中,1:待分析,2:分析中,3:暂停,4:分析完成)',
    `analysis_progress` varchar(65) DEFAULT NULL COMMENT '分析进度',
    `file_size` double DEFAULT NULL COMMENT '文件大小',
    `ext_name` varchar(100) DEFAULT NULL COMMENT '扩展名',
    `file_nums` int DEFAULT '0' COMMENT '文件数量',
    `import_nums` int DEFAULT '0' COMMENT '已导入文件数量',
    `analyzed_duration` bigint DEFAULT '0' COMMENT '分析时长(秒数)',
    `analyzed_folders` int DEFAULT '0' COMMENT '已分析文件夹数',
    `data_type` tinyint DEFAULT NULL COMMENT '数据类型(0:图片,1:视频)',
    `item_points` varchar(255) DEFAULT NULL COMMENT '项点',
    `ai_alarms` int DEFAULT NULL COMMENT 'AI报警数',
    `review_nums` int DEFAULT '0' COMMENT '复核数量',
    `checkout_rate` decimal(10,2) DEFAULT NULL COMMENT '检出率(检出真实缺陷数/真实缺陷总数)',
    `reporting_rate` decimal(10,2) DEFAULT NULL COMMENT '提报率(智能识别疑似缺陷数/总检测图片数)',
    `hit_rate` decimal(10,2) DEFAULT NULL COMMENT '准确率',
    `created_user` varchar(50) DEFAULT NULL COMMENT '创建人',
    `created_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
    `updated_user` varchar(50) DEFAULT NULL COMMENT '修改人',
    `updated_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
    `start_check_time` timestamp NULL DEFAULT NULL COMMENT '开始分析时间',
    `end_check_time` timestamp NULL DEFAULT NULL COMMENT '分析完成时间',
    `pause_time` timestamp NULL DEFAULT NULL COMMENT '暂停时间',
    `deadline` timestamp NULL DEFAULT NULL COMMENT '截止时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务表';

CREATE TABLE `nbk_task_file` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_id` bigint DEFAULT NULL COMMENT 'file_path',
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '文件路径',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务文件表';