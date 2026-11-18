-- nbk_inspection_review_result.marked_regions 为 JSON 数组，
-- 对数组不为空的记录，在每个数组元素中追加 "source": 1
-- 适用：MySQL 5.7（无 JSON_TABLE，改用存储过程 + 游标逐个下标 JSON_SET）
-- 说明：MySQL 5.7 的 JSON_SET 不支持 '*' 通配路径，必须逐下标处理。
-- 执行前请先备份：
--   CREATE TABLE nbk_inspection_review_result_bak_20260831 AS
--   SELECT id, marked_regions FROM nbk_inspection_review_result;

DROP PROCEDURE IF EXISTS add_source_to_marked_regions;

DELIMITER $$

CREATE PROCEDURE add_source_to_marked_regions()
BEGIN
    DECLARE v_id BIGINT;
    DECLARE v_json JSON;
    DECLARE v_len INT;
    DECLARE v_i INT;
    DECLARE done INT DEFAULT 0;

    -- 游标只取数组非空、且第一个元素还没有 source 键的记录（可重复执行/断点续跑）
    DECLARE cur CURSOR FOR
        SELECT id, marked_regions
        FROM nbk_inspection_review_result
        WHERE marked_regions IS NOT NULL
          AND JSON_LENGTH(marked_regions) > 0
          AND JSON_EXTRACT(marked_regions, '$[0].source') IS NULL;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO v_id, v_json;
        IF done = 1 THEN
            LEAVE read_loop;
        END IF;

        SET v_len = JSON_LENGTH(v_json);
        SET v_i = 0;
        WHILE v_i < v_len DO
            UPDATE nbk_inspection_review_result
            SET marked_regions = JSON_SET(
                marked_regions,
                CONCAT('$[', v_i, '].source'),
                1
            )
            WHERE id = v_id;
            SET v_i = v_i + 1;
        END WHILE;
    END LOOP;
    CLOSE cur;
END$$

DELIMITER ;

CALL add_source_to_marked_regions();
DROP PROCEDURE IF EXISTS add_source_to_marked_regions;

-- 验证：检查是否还有未追加 source 的记录（应为 0）
SELECT COUNT(*) AS remaining
FROM nbk_inspection_review_result
WHERE marked_regions IS NOT NULL
  AND JSON_LENGTH(marked_regions) > 0
  AND JSON_EXTRACT(marked_regions, '$[0].source') IS NULL;
