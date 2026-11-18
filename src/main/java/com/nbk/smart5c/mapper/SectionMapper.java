package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.Section;
import com.nbk.smart5c.pojo.qo.SectionQO;
import com.nbk.smart5c.pojo.vo.DropDownListVO;
import com.nbk.smart5c.pojo.vo.SectionVO;
import com.nbk.smart5c.pojo.vo.TreeVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 机务段信息表持久层
 * @since 2026-01-27 15:25:38
 */
public interface SectionMapper extends MPJBaseMapper<Section> {

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果集
     */
    List<SectionVO> queryAndPaging(@Param("query") SectionQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 查询结果集
     */
    List<SectionVO> queryAll(SectionQO query);


    @Select("select id, section_name as name from nbk_section")
    List<DropDownListVO> getSectionDownList();


    @Select("select s.id, s.section_name as label, 1 as type from nbk_section s where exists (select 1 from nbk_inspection_data i where i.section_id = s.id)")
    List<TreeVO> getSectionTree();

    @Select("select * from nbk_section where section_name=#{sectionName} for update")
    Section getSectionByName(@Param("sectionName") String sectionName);

}
