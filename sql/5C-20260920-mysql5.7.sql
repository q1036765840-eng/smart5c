-- 处理旧 nbk_inspection_review_result 的 marked_regions 数据格式（MySQL 5.7 版）
-- 旧格式: ["604, 564, 793, 627"]
-- 新格式: [{"coord": "604,564,793,627", "source": 1, "defectType": "异物"}]
-- source 统一为 1，defectType 取 problem_desc
-- 仅处理旧格式数据（不含 "coord" 键），可重复执行
-- 说明: MySQL 5.7 不支持 JSON_TABLE（8.0 才引入），
--       此处改用序号表 + GROUP_CONCAT 拼接

SET SESSION group_concat_max_len = 1048576;

UPDATE nbk_inspection_review_result t
SET marked_regions = (
    SELECT CONCAT(
        '[',
        GROUP_CONCAT(
            JSON_OBJECT(
                'coord',      REPLACE(JSON_UNQUOTE(JSON_EXTRACT(t.marked_regions, CONCAT('$[', n.i, ']'))), ' ', ''),
                'source',     1,
                'defectType', t.problem_desc
            )
            ORDER BY n.i SEPARATOR ','
        ),
        ']'
    )
    FROM (
        SELECT a.N + b.N * 10 AS i
        FROM (SELECT 0 AS N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
              UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a,
             (SELECT 0 AS N UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
              UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b
    ) n
    WHERE n.i < JSON_LENGTH(t.marked_regions)
)
WHERE t.marked_regions IS NOT NULL
  AND JSON_VALID(t.marked_regions)
  AND JSON_LENGTH(t.marked_regions) > 0
  AND t.marked_regions NOT LIKE '%"coord"%';

-- 验证
-- SELECT id, problem_desc, marked_regions FROM nbk_inspection_review_result WHERE marked_regions LIKE '%"coord"%' LIMIT 10;
