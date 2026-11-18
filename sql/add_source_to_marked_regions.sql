-- nbk_inspection_review_result.marked_regions 为 JSON 数组，
-- 对数组不为空的记录，在每个数组元素中追加 "source": 1
-- 适用：MySQL 8.0+（JSON_TABLE 需要 8.0.4+）
-- 执行前请先备份：
--   CREATE TABLE nbk_inspection_review_result_bak_20260830 AS
--   SELECT id, marked_regions FROM nbk_inspection_review_result;

-- MySQL 8.0
UPDATE nbk_inspection_review_result r
JOIN JSON_TABLE(
       CONCAT('{"arr":', r.marked_regions, '}'),
       '$.arr[*]' COLUMNS (ord FOR ORDINALITY)
     ) jt ON TRUE
SET r.marked_regions = JSON_SET(
    r.marked_regions,
    CONCAT('$[', jt.ord - 1, '].source'),
    1
)
WHERE r.marked_regions IS NOT NULL
  AND JSON_LENGTH(r.marked_regions) > 0;

-- 验证：检查是否还有未追加 source 的记录（应为 0）
SELECT COUNT(*) AS remaining
FROM nbk_inspection_review_result
WHERE marked_regions IS NOT NULL
  AND JSON_LENGTH(marked_regions) > 0
  AND JSON_SEARCH(JSON_KEYS(marked_regions->'$[0]'), 'one', 'source') IS NULL;
