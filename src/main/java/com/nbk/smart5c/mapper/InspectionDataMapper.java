package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.InspectionData;
import com.nbk.smart5c.pojo.qo.InspectionDataQO;
import com.nbk.smart5c.pojo.qo.TaskReviewQO;
import com.nbk.smart5c.pojo.vo.InspectionDataVO;
import com.nbk.smart5c.pojo.vo.InspectionReviewDataVO;
import com.nbk.smart5c.pojo.vo.LabelValueVO;
import com.nbk.smart5c.pojo.vo.LatestAlarmListVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 巡查数据表持久层
 * @since 2026-01-27 15:25:38
 */
public interface InspectionDataMapper extends MPJBaseMapper<InspectionData> {

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果集
     */
    List<InspectionDataVO> queryAndPaging(@Param("query") InspectionDataQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 查询结果集
     */
    List<InspectionDataVO> queryAll(InspectionDataQO query);


    @Select({
            "select ida.id,ida.device_id,d.device_code,d.fix_location, ida.line_id,",
            "l.line_name,ida.station_id stationId,s.station_name,d.pillar_no,ida.km_sign,",
            "ida.locomotive_number,ida.pantograph_status,ida.locomotive_image from",
            "nbk_inspection_data ida left join nbk_line l on ida.line_id=l.id",
            "left join nbk_station s on ida.station_id=s.id",
            "left join nbk_device d on ida.device_id=d.id",
            "where ida.device_id=#{deviceId}"
    })
    List<InspectionReviewDataVO> inspectionDataList(@Param("deviceId") Long deviceId);

    @Select("select count(*) from nbk_inspection_data where date(inspection_time)=current_date() and (analysis_status=0 or analysis_status is null)")
    Long countAlarmsToday();

    @Select({"select date(inspection_time) label ,count(*) value from nbk_inspection_data",
            "where date(inspection_time)>=DATE(DATE_SUB(current_date(), INTERVAL 6 DAY))",
            "group by date(inspection_time)"
    })
    List<LabelValueVO> lastWeekAlarms();

    @Select({
            "select i.id,i.locomotive_number,i.inspection_time, d.fix_location, ",
            "i.locomotive_image,i.analysis_status from nbk_inspection_data i",
            "left join nbk_device d on i.device_id=d.id where i.analysis_status>0 order by inspection_time desc limit #{offset},#{pageSize}"
    })
    List<LatestAlarmListVO> latestAlarmPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @Select({
            "select i.id, i.locomotive_number,i.inspection_time, d.fix_location, ",
            "i.locomotive_image,i.analysis_status from nbk_inspection_data i",
            "left join nbk_device d on i.device_id=d.id where i.analysis_status>0 order by inspection_time desc limit 100"
    })
    List<LatestAlarmListVO> latestAlarmList();

    @Select({
            "select count(1) from nbk_inspection_data i",
            "left join nbk_device d on i.device_id=d.id"
    })
    Integer countLatestAlarm();

    @Select({
            "select * from nbk_inspection_data where analysis_status=#{analysisStatus}"
    })
    List<InspectionData> getInspectDataByAnalysisStatus(@Param("analysisStatus") Integer analysisStatus);

    @Select("select count(distinct locomotive_number) from nbk_inspection_data where date(created_time)=current_date()")
    Long defectLocomotiveNum(String today);

    @Select("select sum(image_num) from nbk_inspection_data where date(created_time)=current_date()")
    Long processTotalPictures();

    @Select("select id from nbk_inspection_data where task_id=#{taskId} order by id")
    List<Long> getInspectionIdAscByTaskId(@Param("taskId") Long taskId);

    @Select({"<script>",
            "select distinct da.id,da.inspection_time from nbk_inspection_data da left join nbk_inspection_review_result rr on da.id=rr.inspection_id",
            "where",
            "da.task_id=#{taskId}",
            "<if test=\"locomotiveNumber!=null and locomotiveNumber!=''\">",
            "and lower(da.locomotive_number) like concat ('%',lower(#{locomotiveNumber}),'%')",
            "</if>",
            "<if test=\"startTime!=null\">",
            "and da.inspection_time>=#{startTime}",
            "</if>",
            "<if test=\"endTime!=null\">",
            "and da.inspection_time &lt;=#{endTime}",
            "</if>",
            "<if test=\"confirmStatusList!=null and confirmStatusList.size()>0\">",
            "and da.confirm_status in",
            "<foreach collection='confirmStatusList' item='cs' open='(' separator=',' close=')'>",
            "#{cs}",
            "</foreach>",
            "</if>",
            "<if test=\"defectTypeList!=null and defectTypeList.size()>0\">",
            "and rr.problem_desc in",
            "<foreach collection='defectTypeList' item='dt' open='(' separator=',' close=')'>",
            "#{dt}",
            "</foreach>",
            "</if>",
            "order by da.inspection_time asc, da.id asc",
            "</script>"
    })
    List<Long> getInspectionIdAscByCont(TaskReviewQO taskReviewQO);

    @Delete("delete from nbk_inspection_data where task_id=#{taskId}")
    void deleteByTaskId(@Param("taskId") Long taskId);

    @Delete({"<script>",
            "delete from nbk_inspection_data where task_id in",
            "<foreach collection='taskIds' item='taskId' open='(' separator=',' close=')'>",
            "#{taskId}",
            "</foreach>",
            "</script>"
    })
    void deleteByTaskIds(@Param("taskIds") List<Long> taskIds);

    @Select("select count(id) from nbk_inspection_data where locomotive_image like concat('%',#{filePath},'%')")
    Long countLikeLocomotiveImage(@Param("filePath") String filePath);

    @Select({"<script>",
            "select distinct da.id,da.inspection_time",
            "from nbk_inspection_data da left join nbk_inspection_review_result rr on da.id=rr.inspection_id",
            "where",
            "da.task_id=#{taskId}",
            "<if test=\"locomotiveNumber!=null and locomotiveNumber!=''\">",
            "and lower(da.locomotive_number) like concat ('%',lower(#{locomotiveNumber}),'%')",
            "</if>",
            "<if test=\"startTime!=null\">",
            "and da.inspection_time>=#{startTime}",
            "</if>",
            "<if test=\"endTime!=null\">",
            "and da.inspection_time &lt;= #{endTime}",
            "</if>",
            "<if test=\"pantographStatus!=null\">",
            "and da.pantograph_status = #{pantographStatus}",
            "</if>",
            "<if test=\"confirmStatusList!=null and confirmStatusList.size()>0\">",
            "and da.confirm_status in",
            "<foreach collection='confirmStatusList' item='cs' open='(' separator=',' close=')'>",
            "#{cs}",
            "</foreach>",
            "</if>",
            "<if test=\"defectTypeList!=null and defectTypeList.size()>0\">",
            "AND JSON_VALID(rr.marked_regions) AND JSON_LENGTH(rr.marked_regions) > 0 and (",
            "<foreach collection='defectTypeList' item='dt' open='' separator=' or ' close=''>",
            "JSON_CONTAINS(JSON_EXTRACT(rr.marked_regions, '$[*].defectType'), concat('\"',#{dt},'\"'))",
            "</foreach>",
            ")",
            "</if>",
            "order by da.inspection_time asc, da.id asc",
            "limit #{offset},#{pageSize}",
            "</script>"
    })
    List<Long> getInspectionIdAscPaging(TaskReviewQO taskReviewQO);

    @Select({"<script>",
            "select count(distinct da.id) from nbk_inspection_data da left join nbk_inspection_review_result rr on da.id=rr.inspection_id",
            "where",
            "da.task_id=#{taskId}",
            "<if test=\"locomotiveNumber!=null and locomotiveNumber!=''\">",
            "and lower(da.locomotive_number) like concat ('%',lower(#{locomotiveNumber}),'%')",
            "</if>",
            "<if test=\"startTime!=null\">",
            "and da.inspection_time>=#{startTime}",
            "</if>",
            "<if test=\"endTime!=null\">",
            "and da.inspection_time &lt;=#{endTime}",
            "</if>",
            "<if test=\"pantographStatus!=null\">",
            "and da.pantograph_status = #{pantographStatus}",
            "</if>",
            "<if test=\"confirmStatusList!=null and confirmStatusList.size()>0\">",
            "and da.confirm_status in",
            "<foreach collection='confirmStatusList' item='cs' open='(' separator=',' close=')'>",
            "#{cs}",
            "</foreach>",
            "</if>",
            "<if test=\"defectTypeList!=null and defectTypeList.size()>0\">",
            "AND JSON_VALID(rr.marked_regions) AND JSON_LENGTH(rr.marked_regions) > 0 and (",
            "<foreach collection='defectTypeList' item='dt' open='' separator=' or ' close=''>",
            "JSON_CONTAINS(JSON_EXTRACT(rr.marked_regions, '$[*].defectType'), concat('\"',#{dt},'\"'))",
            "</foreach>",
            ")",
            "</if>",
            "</script>"
    })
    Long countInspectionIdAscPaging(TaskReviewQO taskReviewQO);

    @Select("select confirm_status from nbk_inspection_data where id=#{inspectionId}")
    Integer getConfirmStatusById(@Param("inspectionId") Long inspectionId);

    @Update("update nbk_inspection_data set pantograph_status=#{pantographStatus} where id=#{id}")
    void updateSdgStatusById(@Param("id") Long id, @Param("pantographStatus") Integer pantographStatus);

    @Select("select count(*) from nbk_inspection_data where locomotive_image like concat (#{relativePath}, '%')")
    long countByRelativePath(@Param("relativePath") String relativePath);

    @Select("select max(id) from nbk_inspection_data where locomotive_image=#{relativePath}")
    long getIdByRelativePath(@Param("relativePath") String relativePath);

    @Select("select count(id) from nbk_inspection_data where task_id=#{taskId}")
    int countByTaskId(@Param("taskId") Long taskId);

    @Select("select * from nbk_inspection_data where locomotive_image like concat (#{folderPath}, '%')")
    List<InspectionData> getByFilePath(String folderPath);

    @Select("select max(id) from nbk_inspection_data where task_id is null and analysis_status in(1,2)")
    Long getMaxId();

    @Select("select count(id) from nbk_inspection_data where task_id is null and id > #{userFolderId} and analysis_status in(1,2)")
    long countGreatThanCurr(@Param("userFolderId") Long userFolderId);

    @Select("select count(id) from nbk_inspection_data where date(created_time)=current_date() and pantograph_status=2")
    Long defectCount();
}
