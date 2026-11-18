-- mysql
ALTER TABLE nbk_menu CHANGE query query_param varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '路由参数';

-- kingbase
ALTER TABLE nbk_menu RENAME COLUMN query TO query_param;

ALTER TABLE nbk_inspection_data ADD speed DOUBLE NULL COMMENT '机车速度';

ALTER TABLE nbk_line ADD direction TINYINT NULL COMMENT '行别(0:上行,1:下行,2:单线,3:其它)';


