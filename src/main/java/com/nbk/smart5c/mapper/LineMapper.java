package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.Line;
import com.nbk.smart5c.pojo.qo.LineQO;
import com.nbk.smart5c.pojo.vo.DropDownListVO;
import com.nbk.smart5c.pojo.vo.LineVO;
import com.nbk.smart5c.pojo.vo.TreeVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 线路信息表持久层
 * @since 2026-01-27 15:25:38
 */
public interface LineMapper extends MPJBaseMapper<Line> {

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果集
     */
    List<LineVO> queryAndPaging(@Param("query") LineQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 查询结果集
     */
    List<LineVO> queryAll(LineQO query);


    @Select("select id,line_name as name from nbk_line where section_id=#{sectionId}")
    List<DropDownListVO> getLineDownList(@Param("sectionId") Long sectionId);

    @Select("select l.id, l.line_name as label, 2 as type from nbk_line l where l.section_id=#{sectionId} and exists (select 1 from nbk_inspection_data i where i.line_id = l.id)")
    List<TreeVO> getLineTreeList(@Param("sectionId") Long sectionId);

    @Select({
            "<script>",
            "select * from nbk_line where line_name=#{lineName}",
            "<if test=\"sectionId!=null\">",
            "and section_id=#{sectionId}",
            "</if>",
            "for update",
            "</script>"
    })
    Line getLineBySectionIdAndName(@Param("sectionId") Long sectionId, @Param("lineName") String lineName);
}
