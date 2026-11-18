-- 实时分析表：记录 NFS 源数据目录的复制与解析状态
-- MySQL
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

-- KingbaseES
-- CREATE TABLE nbk_realtime_analysis (
--   id BIGSERIAL PRIMARY KEY,
--   source_filepath VARCHAR(500) NOT NULL,
--   dest_filepath VARCHAR(500),
--   copy_status VARCHAR(10),
--   parse_status VARCHAR(10),
--   created_user VARCHAR(50) DEFAULT 'system',
--   created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--   updated_user VARCHAR(50) DEFAULT 'system',
--   updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );
-- COMMENT ON TABLE nbk_realtime_analysis IS '实时分析表';
-- COMMENT ON COLUMN nbk_realtime_analysis.id IS '主键';
-- COMMENT ON COLUMN nbk_realtime_analysis.source_filepath IS '源数据目录路径';
-- COMMENT ON COLUMN nbk_realtime_analysis.dest_filepath IS '复制后的目标目录路径';
-- COMMENT ON COLUMN nbk_realtime_analysis.copy_status IS '复制状态(Y成功,N失败)';
-- COMMENT ON COLUMN nbk_realtime_analysis.parse_status IS '解析状态(Y成功,N失败)';
-- COMMENT ON COLUMN nbk_realtime_analysis.created_user IS '创建人';
-- COMMENT ON COLUMN nbk_realtime_analysis.created_time IS '创建时间';
-- COMMENT ON COLUMN nbk_realtime_analysis.updated_user IS '修改人';
-- COMMENT ON COLUMN nbk_realtime_analysis.updated_time IS '修改时间';
