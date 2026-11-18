ALTER TABLE nbk_inspection_review_result ADD misreport TINYINT default 0 NULL COMMENT '是否误报(0:否,1:是)';
ALTER TABLE nbk_inspection_data ADD misreport TINYINT default 0 COMMENT '是否误报(0:否,1:是)';
ALTER TABLE nbk_defect_storage ADD misreport TINYINT default 0 COMMENT '是否误报(0:否,1:是)';
ALTER TABLE nbk_inspection_data COMMENT='巡查数据表(文件夹记录)';
ALTER TABLE nbk_inspection_review_result COMMENT='巡查数据审核结果表(图片记录)';

ALTER TABLE nbk_task ADD living TINYINT default 0 COMMENT '是否实时任务(0:否,1:是)';