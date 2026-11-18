-- nbk_bureau definition

CREATE TABLE `nbk_bureau` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `bureau_code` varchar(20) NOT NULL COMMENT '供电部编码',
  `bureau_name` varchar(100) NOT NULL COMMENT '供电部名称',
  `description` text COMMENT '供电部描述',
  `created_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_user` varchar(50) DEFAULT NULL COMMENT '更新人',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供电部信息表';


-- nbk_defect_storage definition

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
  `line_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '线路名称',
  `station_id` bigint DEFAULT NULL COMMENT '区站id',
  `station_name` varchar(50) DEFAULT NULL COMMENT '区站名',
  `reviewer_id` bigint DEFAULT NULL COMMENT '审核人id',
  `reviewer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '审核人名称',
  `review_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '审核文本',
  `created_user` bigint DEFAULT NULL COMMENT '创建人',
  `created_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated_user` bigint DEFAULT NULL COMMENT '修改人',
  `updated_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='缺陷库表';


-- nbk_department definition

CREATE TABLE `nbk_department` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `department_code` varchar(50) DEFAULT NULL COMMENT '部门编码',
  `department_name` varchar(100) DEFAULT NULL COMMENT '部门名称',
  `department_type` tinyint(4) DEFAULT NULL COMMENT '部门类型(0:供电部,1:供电段,2:车间)',
  `parent_id` int(11) DEFAULT NULL COMMENT '父机构id',
  `remark` text COMMENT '备注',
  `created_user` varchar(50) NOT NULL DEFAULT 'system' COMMENT '创建人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_user` varchar(50) NOT NULL DEFAULT 'system' COMMENT '修改人',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nbk_department_info_department_code_unique` (`department_code`),
  KEY `nbk_department_info_department_type_IDX` (`department_type`) USING BTREE,
  KEY `nbk_department_info_parent_id_IDX` (`parent_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='部门信息表';


-- nbk_device definition

CREATE TABLE `nbk_device` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `station_id` bigint(20) NOT NULL COMMENT '区站id',
  `device_code` varchar(50) NOT NULL COMMENT '设备编码',
  `device_type` varchar(50) DEFAULT NULL COMMENT '设备类型',
  `installation_date` date DEFAULT NULL COMMENT '安装日期',
  `manufacturer` varchar(100) DEFAULT NULL COMMENT '制造商',
  `pillar_no` varchar(50) DEFAULT NULL COMMENT '杆号',
  `model` varchar(100) DEFAULT NULL COMMENT '型号',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态(0:停用,1:运行,2:维修)',
  `last_maintenance_date` date DEFAULT NULL COMMENT '上次维护日期',
  `next_maintenance_date` date DEFAULT NULL COMMENT '下次维护日期',
  `parameters` json DEFAULT NULL COMMENT '参数',
  `fix_location` varchar(255) DEFAULT NULL COMMENT '安装位置',
  `km_sign` varchar(50) DEFAULT NULL COMMENT '公里标',
  `created_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `updated_user` varchar(50) DEFAULT NULL COMMENT '更新人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nbk_device_device_code_IDX` (`device_code`,`station_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='设备信息表';


-- nbk_export_info definition

CREATE TABLE `nbk_export_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_no` varchar(50) DEFAULT NULL COMMENT '导出任务编号',
  `source_page` varchar(100) DEFAULT NULL COMMENT '导出所属页面',
  `export_type` varchar(50) DEFAULT NULL COMMENT '导出类型',
  `file_name` varchar(255) DEFAULT NULL COMMENT '导出文件名',
  `quantity` varchar(50) DEFAULT NULL COMMENT '数据量',
  `file_size` varchar(20) DEFAULT NULL COMMENT '文件大小',
  `query_param` text COMMENT '查询参数',
  `query_api` varchar(255) DEFAULT NULL COMMENT '查询接口路径',
  `export_api` varchar(255) DEFAULT NULL COMMENT '导出接口路径',
  `export_status` tinyint(4) DEFAULT NULL COMMENT '导出状态(0:导出中,1:成功,2:失败)',
  `created_user` varchar(50) NOT NULL DEFAULT 'system' COMMENT '创建人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_user` varchar(50) NOT NULL DEFAULT 'system' COMMENT '修改人',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='系统导出信息表';


-- nbk_inspection_data definition

CREATE TABLE `nbk_inspection_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `device_id` bigint(20) NOT NULL COMMENT '设备id',
  `section_id` bigint(20) NOT NULL COMMENT '机务段id',
  `line_id` bigint(20) NOT NULL COMMENT '线路id',
  `station_id` bigint(20) NOT NULL COMMENT '区站id',
  `inspection_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '巡查时间',
  `locomotive_number` varchar(50) DEFAULT NULL COMMENT '机车号',
  `analysis_status` tinyint(4) DEFAULT NULL COMMENT '分析状态(未分析:0,待审批:1,已审批:2)',
  `km_sign` varchar(50) DEFAULT NULL COMMENT '公里标',
  `pantograph_status` tinyint(4) DEFAULT NULL COMMENT '受电弓状态(正常:1,异常:2)',
  `locomotive_image` varchar(255) DEFAULT NULL COMMENT '机车图',
  `created_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_user` varchar(50) DEFAULT NULL COMMENT '更新人',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `speed` double DEFAULT NULL COMMENT '机车速度',
  `task_id` bigint(20) DEFAULT NULL COMMENT '任务id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='巡查数据表';


-- nbk_inspection_review_result definition

CREATE TABLE `nbk_inspection_review_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `inspection_id` bigint(20) NOT NULL COMMENT '巡检id',
  `image_path` varchar(255) DEFAULT NULL COMMENT '缺陷图路径',
  `problem_desc` text COMMENT '缺陷描述',
  `pantograph_status` int(11) DEFAULT NULL COMMENT '受电弓状态(0:异常,1:正常)',
  `defect_level` int(11) DEFAULT NULL COMMENT '缺陷等级',
  `image_valid` tinyint(1) DEFAULT NULL COMMENT '图片是否合规',
  `marked_regions` json DEFAULT NULL COMMENT '标记区域',
  `locomotive_number` varchar(50) DEFAULT NULL COMMENT '机车号',
  `reliability` varchar(20) DEFAULT NULL COMMENT '置信度',
  `created_user` varchar(50) NOT NULL DEFAULT 'system' COMMENT '创建人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_user` varchar(50) NOT NULL DEFAULT 'system' COMMENT '修改人',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nbk_inspection_review_result_inspection_id_IDX` (`inspection_id`,`image_path`) USING BTREE,
  KEY `idx_ps_status_inspection` (`pantograph_status`,`inspection_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='巡查数据审核结果表';


-- nbk_line definition

CREATE TABLE `nbk_line` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `section_id` int(11) NOT NULL COMMENT '区域id',
  `line_code` varchar(20) NOT NULL COMMENT '线路编码',
  `line_name` varchar(100) NOT NULL COMMENT '线路名称',
  `description` text COMMENT '描述',
  `created_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_user` varchar(50) DEFAULT NULL COMMENT '更新人',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `direction` tinyint(4) DEFAULT NULL COMMENT '行别(0:上行,1:下行,2:单线,3:其它)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nbk_line_line_name_IDX` (`line_name`,`section_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='线路信息表';


-- nbk_menu definition

CREATE TABLE `nbk_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) DEFAULT '' COMMENT '路由名称',
  `is_frame` int(1) DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `created_user` varchar(50) DEFAULT '' COMMENT '创建者',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_user` varchar(50) DEFAULT '' COMMENT '更新者',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';


-- nbk_operate_log definition

CREATE TABLE `nbk_operate_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `operate_user` varchar(50) DEFAULT NULL COMMENT '操作人',
  `operate_time` timestamp NULL DEFAULT NULL COMMENT '操作时间',
  `operate_type` tinyint(4) DEFAULT NULL COMMENT '操作类型(0:删除,1:新增,2:修改)',
  `class_name` varchar(100) DEFAULT NULL COMMENT '操作类名',
  `method_name` varchar(100) DEFAULT NULL COMMENT '操作方法名',
  `method_params` text COMMENT '操作方法参数',
  `return_value` text COMMENT '操作方法返回值',
  `cost_time` bigint(20) DEFAULT NULL COMMENT '操作耗时',
  `operate_desc` text COMMENT '操作详细描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';


-- nbk_role_info definition

CREATE TABLE `nbk_role_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `role_code` varchar(30) DEFAULT NULL COMMENT '角色编码',
  `role_name` varchar(50) DEFAULT NULL COMMENT '角色名称(系统管理员、技术专工、检测员、只读用户)',
  `role_level` int(11) DEFAULT NULL COMMENT '角色等级',
  `created_user` varchar(50) NOT NULL DEFAULT 'system' COMMENT '创建人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_user` varchar(50) NOT NULL DEFAULT 'system' COMMENT '修改人',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nbk_role_info_role_code_unique` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';


-- nbk_role_menu definition

CREATE TABLE `nbk_role_menu` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';


-- nbk_section definition

CREATE TABLE `nbk_section` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `section_code` varchar(20) NOT NULL COMMENT '机务段编码',
  `section_name` varchar(100) NOT NULL COMMENT '机务段名称',
  `description` text COMMENT '机务段描述',
  `created_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_user` varchar(50) DEFAULT NULL COMMENT '更新人',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nbk_section_section_name_IDX` (`section_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='机务段信息表';


-- nbk_station definition

CREATE TABLE `nbk_station` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  `line_id` bigint(20) NOT NULL COMMENT '线路id',
  `station_code` varchar(20) NOT NULL COMMENT '区站编码',
  `station_name` varchar(100) NOT NULL COMMENT '区站名',
  `description` text COMMENT '区站描述',
  `created_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_user` varchar(50) DEFAULT NULL COMMENT '修改人',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `nbk_station_line_id_IDX` (`line_id`,`station_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='区站信息表';


-- nbk_task definition

CREATE TABLE `nbk_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_name` varchar(100) DEFAULT NULL COMMENT '任务名',
  `task_folder` varchar(200) DEFAULT NULL COMMENT '任务文件夹',
  `priority` tinyint(4) DEFAULT NULL COMMENT '0:低,1:中,2:高',
  `task_status` tinyint(4) DEFAULT NULL COMMENT '任务状态(0:创建中,1:待分析,2:分析中,3:暂停,4:分析完成)',
  `analysis_progress` decimal(10,2) DEFAULT NULL COMMENT '分析进度',
  `in_storage` tinyint(4) DEFAULT NULL COMMENT '是否入库(0:未入库,1:已入库)',
  `file_size` double DEFAULT NULL COMMENT '文件大小',
  `ext_name` varchar(100) DEFAULT NULL COMMENT '扩展名',
  `file_nums` int(11) DEFAULT NULL COMMENT '文件数量',
  `import_nums` int(11) DEFAULT NULL COMMENT '已导入文件数量',
  `analyzed_duration` double DEFAULT NULL COMMENT '分析时长(秒数)',
  `analyzed_pictures` int(11) DEFAULT NULL COMMENT '已分析图片数',
  `data_type` tinyint(4) DEFAULT NULL COMMENT '数据类型(0:图片,1:视频)',
  `item_points` varchar(255) DEFAULT NULL COMMENT '项点',
  `ai_alarms` int(11) DEFAULT NULL COMMENT 'AI报警数',
  `manual_progress` decimal(10,2) DEFAULT NULL COMMENT '人工审核进度',
  `checkout_rate` decimal(10,2) DEFAULT NULL COMMENT '检出率(检出真实缺陷数/真实缺陷总数)',
  `reporting_rate` decimal(10,2) DEFAULT NULL COMMENT '提报率(智能识别疑似缺陷数/总检测图片数)',
  `hit_rate` decimal(10,2) DEFAULT NULL COMMENT '准确率',
  `created_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `created_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated_user` varchar(50) DEFAULT NULL COMMENT '修改人',
  `updated_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='任务表';


-- nbk_user_info definition

CREATE TABLE `nbk_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户名',
  `password` varchar(50) DEFAULT NULL COMMENT '密码',
  `account_no` varchar(50) NOT NULL COMMENT '账号',
  `telephone` varchar(30) DEFAULT NULL COMMENT '电话',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像链接',
  `department_id` int(11) DEFAULT NULL COMMENT '部门id',
  `login_times` int(11) DEFAULT '0' COMMENT '登录错误次数（最大次数）',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱地址',
  `lock_status` tinyint(4) DEFAULT '0' COMMENT '锁定状态（0：未锁定，1：锁定）',
  `position` tinyint(4) DEFAULT NULL COMMENT '岗位(0:系统管理员,1:技术专工,2:检测员,3:只读用户)',
  `last_login` timestamp NULL DEFAULT NULL COMMENT '最后登录时间',
  `created_user` varchar(50) NOT NULL DEFAULT 'system' COMMENT '创建人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_user` varchar(50) NOT NULL DEFAULT 'system' COMMENT '修改人',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `nbk_user_info_department_id_IDX` (`department_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';


-- nbk_user_role definition

CREATE TABLE `nbk_user_role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色id',
  `created_user` varchar(50) NOT NULL DEFAULT 'system' COMMENT '创建人',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_user` varchar(50) NOT NULL DEFAULT 'system' COMMENT '修改人',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户角色信息表';


-- nbk_realtime_analysis definition

CREATE TABLE `nbk_realtime_analysis` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `source_filepath` varchar(500) NOT NULL COMMENT '源数据目录路径',
  `dest_filepath` varchar(500) DEFAULT NULL COMMENT '复制后的目标目录路径',
  `copy_status` varchar(10) DEFAULT NULL COMMENT '复制状态(Y成功,N失败)',
  `parse_status` varchar(10) DEFAULT NULL COMMENT '解析状态(Y成功,N失败)',
  `created_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_user` varchar(50) DEFAULT NULL COMMENT '修改人',
  `updated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_realtime_analysis_source` (`source_filepath`(191))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实时分析表';