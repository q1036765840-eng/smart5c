-- nbk_export_info 增加查询接口、导出接口路径字段
-- MySQL
ALTER TABLE nbk_export_info ADD COLUMN `query_api` varchar(255) DEFAULT NULL COMMENT '查询接口路径' AFTER `query_param`;
ALTER TABLE nbk_export_info ADD COLUMN `export_api` varchar(255) DEFAULT NULL COMMENT '导出接口路径' AFTER `query_api`;

-- KingbaseES
-- ALTER TABLE nbk_export_info ADD COLUMN query_api VARCHAR(255);
-- ALTER TABLE nbk_export_info ADD COLUMN export_api VARCHAR(255);
-- COMMENT ON COLUMN nbk_export_info.query_api IS '查询接口路径';
-- COMMENT ON COLUMN nbk_export_info.export_api IS '导出接口路径';
