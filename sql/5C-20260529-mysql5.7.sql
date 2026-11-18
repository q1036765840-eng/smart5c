ALTER TABLE nbk_inspection_review_result ADD net_path varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文件网络路径';
ALTER TABLE nbk_line MODIFY COLUMN section_id int NULL COMMENT '段id';
ALTER TABLE nbk_inspection_data MODIFY COLUMN section_id bigint NULL COMMENT '机务段id';


