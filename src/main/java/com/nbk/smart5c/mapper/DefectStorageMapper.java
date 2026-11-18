package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.DefectStorage;
import com.nbk.smart5c.pojo.qo.DefectStorageDetailQO;
import com.nbk.smart5c.pojo.qo.DefectStorageQO;
import com.nbk.smart5c.pojo.vo.DefectStorageVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 缺陷库表持久层
 * @since 2026-04-07 16:41:49
 */
public interface DefectStorageMapper extends MPJBaseMapper<DefectStorage> {

//    /**
//     * 分页查询
//     *
//     * @param query 查询条件
//     * @return 分页查询结果集
//     */
//    List<DefectStorageVO> queryAndPaging(@Param("query") DefectStorageQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 查询结果集
     */
    List<DefectStorageVO> queryAll(DefectStorageQO query);

    @Select({
            "<script>",
            "select distinct defect_type from nbk_defect_storage ",
            "<where>",
            "<if test=\"reviewStatusList!=null and reviewStatusList.size()>0\">",
            "and review_status in",
            "<foreach collection='reviewStatusList' item='status' open='(' separator=',' close=')'>",
            "#{status}",
            "</foreach>",
            "</if>",
            "<if test=\"taskId!=null\">",
            "and task_id=#{taskId}",
            "</if>",
            "</where>",
            "</script>"
    })
    List<String> getTaskDefectType(@Param("taskId") Long taskId, @Param("reviewStatusList") List<Integer> reviewStatusList);

    @Select({
            "<script>",
            "select max(id) from nbk_defect_storage",
            "<where>",
            "<if test=\"defectType!=null and defectType!=''\">",
            "and defect_type = #{defectType}",
            "</if>",
            "<if test=\"startTime!=null\">",
            "and inspection_time >= #{startTime}",
            "</if>",
            "<if test=\"endTime!=null\">",
            "and inspection_time &lt;= #{endTime}",
            "</if>",
            "<if test=\"id!=null\">",
            "and id &lt; #{id}",
            "</if>",
            "<if test=\"reviewStatusList!=null and reviewStatusList.size()>0\">",
            "and review_status in",
            "<foreach collection='reviewStatusList' item='status' open='(' separator=',' close=')'>",
            "#{status}",
            "</foreach>",
            "</if>",
            "</where>",
            "</script>"
    })
    Long getMinId(DefectStorageDetailQO defectStorageDetailQO);

    @Select({
            "<script>",
            "select min(id) from nbk_defect_storage",
            "<where>",
            "<if test=\"defectType!=null and defectType!=''\">",
            "and defect_type = #{defectType}",
            "</if>",
            "<if test=\"startTime!=null\">",
            "and inspection_time >= #{startTime}",
            "</if>",
            "<if test=\"endTime!=null\">",
            "and inspection_time &lt;= #{endTime}",
            "</if>",
            "<if test=\"id!=null\">",
            "and id > #{id}",
            "</if>",
            "<if test=\"reviewStatusList!=null and reviewStatusList.size()>0\">",
            "and review_status in",
            "<foreach collection='reviewStatusList' item='status' open='(' separator=',' close=')'>",
            "#{status}",
            "</foreach>",
            "</if>",
            "</where>",
            "</script>"
    })
    Long getMaxId(DefectStorageDetailQO defectStorageDetailQO);

    @Select({
            "<script>",
            "select id from nbk_defect_storage",
            "<where>",
            "<if test=\"defectType!=null and defectType!=''\">",
            "and defect_type = #{defectType}",
            "</if>",
            "<if test=\"startTime!=null\">",
            "and inspection_time >= #{startTime}",
            "</if>",
            "<if test=\"endTime!=null\">",
            "and inspection_time &lt;= #{endTime}",
            "</if>",
            "<if test=\"id!=null\">",
            "and id=#{id}",
            "</if>",
            "<if test=\"reviewStatusList!=null and reviewStatusList.size()>0\">",
            "and review_status in",
            "<foreach collection='reviewStatusList' item='status' open='(' separator=',' close=')'>",
            "#{status}",
            "</foreach>",
            "</if>",
            "</where>",
            "order by id desc limit 1",
            "</script>"
    })
    Long getDefectIdByCont(DefectStorageDetailQO defectStorageDetailQO);

    @Select("select * from nbk_defect_storage where marked_regions=#{markedRegions} and inspection_result_id=#{inspectResultId}")
    DefectStorage getOneRecord(@Param("markedRegions") String markedRegions, @Param("inspectResultId") Long inspectResultId);

    @Delete("delete from nbk_defect_storage where inspection_result_id=#{inspectResultId}")
    void deleteByInspectResultId(@Param("inspectResultId") Long inspectResultId);

    @Select("select marked_regions from nbk_defect_storage where inspection_result_id=#{inspectResultId}")
    List<String> getImageAllCoord(@Param("inspectResultId") Long inspectResultId);

    @Select("select * from nbk_defect_storage where inspection_result_id=#{inspectionResultId} limit 1")
    DefectStorage getOneRecordByInspectionResultId(@Param("inspectionResultId") Long inspectionResultId);

    @Delete("delete from nbk_defect_storage where inspection_result_id=#{inspectionResultId}")
    void deleteByInspectionId(@Param("inspectionResultId") Long inspectionResultId);

    @Select("select image_path from nbk_defect_storage where inspection_result_id=#{inspectionResultId} limit 1")
    String getImagePathByInspectionId(@Param("inspectionResultId") Long inspectionResultId);

    @Select("select count(id) from nbk_defect_storage where image_path like concat('%',#{filePath},'%')")
    Long countLikeImagePath(@Param("filePath") String relativeFilePath);

    @Select("select * from nbk_defect_storage where inspection_result_id=#{resultId} and misreport=1 and review_status<>0 and source=1")
    List<DefectStorage> getMisreportListByResultId(@Param("resultId") Long resultId);

    /**
     * 数据导出只取当前页ID
     * @param query
     * @return
     */
    List<Long> queryCurrentPage(DefectStorageQO query);
}
