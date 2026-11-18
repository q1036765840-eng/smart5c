package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.InspectionReviewResult;
import com.nbk.smart5c.pojo.qo.InspectionReviewResultQO;
import com.nbk.smart5c.pojo.vo.InspectionResultExportVO;
import com.nbk.smart5c.pojo.vo.InspectionReviewResultVO;
import com.nbk.smart5c.pojo.vo.ReviewPictureVO;
import com.nbk.smart5c.pojo.vo.TaskReviewDetailVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 巡查数据审核结果表持久层
 * @since 2026-01-29 10:35:00
 */
public interface InspectionReviewResultMapper extends MPJBaseMapper<InspectionReviewResult> {

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果集
     */
    List<InspectionReviewResultVO> queryAndPaging(@Param("query") InspectionReviewResultQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 查询结果集
     */
    List<InspectionReviewResultVO> queryAll(InspectionReviewResultQO query);

    /**
     * 根据条件查询审核结果
     *
     * @param inspectionId 查询条件
     * @return 查询结果
     */
    @Select("select * from nbk_inspection_review_result where inspection_id=#{inspectionId} and image_path=#{imagePath}")
    InspectionReviewResultVO getByInspectionIdAndImagePath(@Param("inspectionId") Long inspectionId, @Param("imagePath") String imagePath);

    /**
     * 根据条件查询审核结果
     *
     * @param inspectionId 查询条件
     * @return 查询结果
     */
    @Select("select * from nbk_inspection_review_result where inspection_id=#{inspectionId}")
    List<InspectionReviewResultVO> getImageVOByInspectionId(@Param("inspectionId") Long inspectionId);


    /**
     * 根据条件文件夹id查询审核结果
     *
     * @param inspectionId 查询条件
     * @return 查询结果
     */
    @Select("select * from nbk_inspection_review_result where inspection_id=#{inspectionId}")
    List<InspectionReviewResultVO> getDetailListByInspectionId(@Param("inspectionId") Long inspectionId);

    /**
     * 巡查数据审核
     *
     * @param inspectionId 查询条件
     * @return 查询结果
     */
    @Select("select count(id) from nbk_inspection_review_result where inspection_id=#{inspectionId} and pantograph_status=2")
    int countErrorStatusByInspectionId(@Param("inspectionId") Long inspectionId);

    /**
     * 根据条件查询审核结果
     *
     * @param inspectionId 查询条件
     * @return 查询结果
     */
    @Select("select * from nbk_inspection_review_result where inspection_id=#{inspectionId} and image_path=#{imagePath}")
    InspectionReviewResult getEntityByInspectionId(@Param("inspectionId") Long inspectionId, @Param("imagePath") String imagePath);

    /**
     * 原子写入（依赖 inspection_id+image_path 唯一索引）：存在则更新，不存在则插入。
     * 相比先查再写，可避免多线程并发处理同一巡检图片时的锁等待，从而规避 OceanBase ob_query_timeout 超时。
     */
    @Insert({"<script>",
            "INSERT INTO nbk_inspection_review_result ",
            "( inspection_id, image_path, problem_desc, pantograph_status, defect_level, image_valid, marked_regions, ",
            "  locomotive_number, reliability, confirm_status, task_id, net_path, misreport, ",
            "  created_user, created_time, updated_user, updated_time ) ",
            "VALUES ",
            "( #{e.inspectionId}, #{e.imagePath}, #{e.problemDesc}, #{e.pantographStatus}, #{e.defectLevel}, #{e.imageValid}, #{e.markedRegions}, ",
            "  #{e.locomotiveNumber}, #{e.reliability}, #{e.confirmStatus}, #{e.taskId}, #{e.netPath}, #{e.misreport}, ",
            "  COALESCE(#{e.createdUser},'system'), COALESCE(#{e.createdTime},NOW()), COALESCE(#{e.updatedUser},'system'), COALESCE(#{e.updatedTime},NOW()) ) ",
            "ON DUPLICATE KEY UPDATE ",
            "problem_desc=VALUES(problem_desc), pantograph_status=VALUES(pantograph_status), defect_level=VALUES(defect_level), ",
            "image_valid=VALUES(image_valid), marked_regions=VALUES(marked_regions), locomotive_number=VALUES(locomotive_number), ",
            "reliability=VALUES(reliability), confirm_status=VALUES(confirm_status), task_id=VALUES(task_id), net_path=VALUES(net_path), ",
            "misreport=VALUES(misreport), updated_user=VALUES(updated_user), updated_time=VALUES(updated_time)",
            "</script>"})
    int upsertNormalResult(@Param("e") InspectionReviewResult e);

    /**
     * 原子写入（依赖 inspection_id+image_path 唯一索引）：存在则更新，不存在则插入。
     * 相比先查再写，可避免多线程并发处理同一巡检图片时的锁等待，从而规避 OceanBase ob_query_timeout 超时。
     */
    @Insert({"<script>",
            "INSERT INTO nbk_inspection_review_result ",
            "( inspection_id, image_path, problem_desc, pantograph_status, defect_level, image_valid, marked_regions, ",
            "  locomotive_number, reliability, confirm_status, net_path, misreport, ",
            "  created_user, created_time, updated_user, updated_time ) ",
            "VALUES ",
            "( #{e.inspectionId}, #{e.imagePath}, #{e.problemDesc}, #{e.pantographStatus}, #{e.defectLevel}, #{e.imageValid}, #{e.markedRegions}, ",
            "  #{e.locomotiveNumber}, #{e.reliability}, #{e.confirmStatus}, #{e.netPath}, #{e.misreport}, ",
            "  COALESCE(#{e.createdUser},'system'), COALESCE(#{e.createdTime},NOW()), COALESCE(#{e.updatedUser},'system'), COALESCE(#{e.updatedTime},NOW()) ) ",
            "ON DUPLICATE KEY UPDATE ",
            "problem_desc=VALUES(problem_desc), pantograph_status=VALUES(pantograph_status), defect_level=VALUES(defect_level), ",
            "image_valid=VALUES(image_valid), marked_regions=VALUES(marked_regions), locomotive_number=VALUES(locomotive_number), ",
            "reliability=VALUES(reliability), confirm_status=VALUES(confirm_status), net_path=VALUES(net_path), ",
            "misreport=VALUES(misreport), updated_user=VALUES(updated_user), updated_time=VALUES(updated_time)",
            "</script>"})
    int upsertNormalResultNoTask(@Param("e") InspectionReviewResult e);

    @Select("select count(distinct locomotive_number) from nbk_inspection_review_result where date(created_time)=current_date()")
    Long defectLocomotiveNum(String today);


    @Select("select count(1) from nbk_inspection_review_result where task_id=#{taskId} and confirm_status=2")
    Long countConfirmDefect(Long taskId);

    @Select("select count(*) from nbk_inspection_review_result where date(created_time)=current_date()")
    Long processTotalPictures();

    @Select("select count(*) from nbk_inspection_review_result where date(created_time)=current_date() and pantograph_status=2")
    Long defectPictures();

    @Delete("delete from nbk_inspection_review_result where inspection_id=#{inspectionId}")
    void deleteByInspectionId(@Param("inspectionId") Long inspectionId);

    @Delete({"<script>",
            "delete from nbk_inspection_review_result where inspection_id in",
            "<foreach collection='inspectIds' item='inspectId' open='(' separator=',' close=')'>",
            "#{inspectId}",
            "</foreach>",
            "</script>",
    })
    void deleteByInspectionIds(@Param("inspectIds") List<Long> inspectIds);

    List<InspectionResultExportVO> getInpectionReviewData(@Param("inspectIds") List<Long> inspectIds);

    @Select({"select '图片数量' as category, (select count(id) from nbk_inspection_review_result where task_id=#{taskId}) as identifiedNum,",
            "(select count(id) from nbk_inspection_review_result where task_id=#{taskId} and confirm_status in(1,2)) as confirmNum"})
    TaskReviewDetailVO.DefectCountVO reviewPicNumCount(@Param("taskId") Long taskId);

    @Select({
            "SELECT jt.defectType AS category, COUNT(*) AS identifiedNum,",
            "SUM(CASE WHEN rr.confirm_status = 2 THEN 1 ELSE 0 END) AS confirmNum",
            "FROM nbk_inspection_review_result rr,",
            "JSON_TABLE(rr.marked_regions, '$[*]' COLUMNS (defectType VARCHAR(255) PATH '$.defectType')) jt",
            "WHERE rr.task_id = #{taskId} AND jt.defectType IS NOT NULL AND jt.defectType <> ''",
            "GROUP BY jt.defectType"
    })
    List<TaskReviewDetailVO.DefectCountVO> taskDefectTypeCount(@Param("taskId") Long taskId);

    @Select("select group_concat(problem_desc,',') from nbk_inspection_review_result where inspection_id=#{checkId}")
    String getProblemDescForFolder(@Param("checkId") Long checkId);

    @Select({"select rr.id inspectionResultId, rr.inspection_id,rr.pantograph_status, s.station_name,d.pillar_no poleNo,d.km_sign,ida.inspection_time,ifnull(rr.confirm_status,0) confirm_status,",
            "rr.net_path,rr.marked_regions,ln.line_name,st.section_name,rr.problem_desc from nbk_inspection_review_result rr left join nbk_inspection_data ida on rr.inspection_id=ida.id",
            "left join nbk_station s on ida.station_id=s.id left join nbk_device d on ida.device_id=d.id left join nbk_line ln on ida.line_id=ln.id",
            "left join nbk_section st on ida.section_id=st.id where rr.inspection_id=#{checkId}"})
    List<ReviewPictureVO> getCheckPictureInfoList(@Param("checkId") Long checkId);


    @Select({"select null inspectionResultId, ida.id inspection_id,0 pantograph_status, s.station_name,d.pillar_no poleNo,d.km_sign,ida.inspection_time,",
            "ln.line_name,st.section_name,0 confirmStatus from nbk_inspection_data ida",
            "left join nbk_station s on ida.station_id=s.id left join nbk_device d on ida.device_id=d.id left join nbk_line ln on ida.line_id=ln.id",
            "left join nbk_section st on ida.section_id=st.id where ida.id=#{checkId}"
    })
    ReviewPictureVO getUnCheckPictureInfo(@Param("checkId") Long checkId);

    @Select("select problem_desc from nbk_inspection_review_result where task_id=#{taskId}")
    List<String> getDefectTypeByTaskId(@Param("taskId") Long taskId);

    @Delete("delete from nbk_inspection_review_result where task_id=#{taskId}")
    void deleteByTaskId(@Param("taskId") Long taskId);

    @Delete({
            "<script>",
            "delete from nbk_inspection_review_result where task_id in",
            "<foreach collection='taskIds' item='taskId' open='(' separator=',' close=')'>",
            "#{taskId}",
            "</foreach>",
            "</script>"
    })
    void deleteByTaskIds(@Param("taskIds") List<Long> taskIds);

    @Select("select count(id) from nbk_inspection_review_result where inspection_id=#{inspectionId} and (confirm_status=0 or confirm_status is null)")
    Long countUnViewPicture(@Param("inspectionId") Long inspectionId);

    @Select("select count(id) from nbk_inspection_review_result where inspection_id=#{inspectionId} and JSON_VALID(marked_regions) AND JSON_LENGTH(marked_regions) > 0 and confirm_status in(0,1)")
    Long countUnConfirmPicture(@Param("inspectionId") Long inspectionId);

    @Select("select count(id) from nbk_inspection_review_result where inspection_id=#{inspectionId} and pantograph_status=2")
    long judgeFolderSdgStatus(Long inspectionId);

    @Select({"<script>",
            "select task_id, count(1) as cnt from nbk_inspection_review_result",
            "where confirm_status=2 and task_id in",
            "<foreach collection='taskIds' item='taskId' open='(' separator=',' close=')'>",
            "#{taskId}",
            "</foreach>",
            "group by task_id",
            "</script>"
    })
    List<Map<String, Object>> batchCountConfirmDefect(@Param("taskIds") List<Long> taskIds);

    @Select("select count(id) from nbk_inspection_review_result where task_id=#{taskId}")
    int countByTaskId(@Param("taskId") Long taskId);

    @Select("select count(id) from nbk_inspection_review_result where inspection_id=#{inspectionId}")
    long countByInspectionId(@Param("inspectionId") Long inspectionId);

    @Select("select count(id) from nbk_inspection_review_result where inspection_id=#{inspectionId} and misreport=1")
    int getMisreportByInspectionId(@Param("inspectionId") Long inspectionId);
}
