-- 巡查数据查询按受电工状态过滤性能优化
-- 背景：多选受电工状态时，原实现为每个缺陷类型生成一个相关 EXISTS 子查询，
--       MySQL 无法改写为半连接，导致主表逐行回表匹配 marked_regions，百万级数据耗时数秒。
-- 现改为一次 IN 半连接，并补充 (pantograph_status, inspection_id) 索引支撑该半连接的定位。

-- MySQL
ALTER TABLE nbk_inspection_review_result ADD INDEX idx_ps_status_inspection (pantograph_status, inspection_id);

-- KingbaseES
-- CREATE INDEX idx_ps_status_inspection ON nbk_inspection_review_result(pantograph_status, inspection_id);
