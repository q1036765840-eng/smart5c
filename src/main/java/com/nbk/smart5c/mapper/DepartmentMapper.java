package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.Department;
import com.nbk.smart5c.pojo.vo.DepartTreeVO;
import com.nbk.smart5c.pojo.vo.DepartmentVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 部门信息表持久层
 * @since 2026-05-16 17:20:28
 */
public interface DepartmentMapper extends MPJBaseMapper<Department> {

    @Select("select id,department_name,parent_id from nbk_department where parent_id is null or parent_id=0")
    List<DepartTreeVO> getTopDepartList();

    @Select("select id,department_name,parent_id from nbk_department where parent_id=#{parentId}")
    List<DepartTreeVO> getDeptByParentId(@Param("parentId") Long parentId);

    @Select("select path from nbk_department where path like concat(#{path},'%') and CHAR_LENGTH(path)>CHAR_LENGTH(#{path})")
    List<String> getChildDeptPathList(@Param("path") String path);

    @Select("select count(id) from nbk_department where department_name=#{departmentName}")
    Long countByDeptName(@Param("departmentName") String departmentName);

    @Select("select count(id) from nbk_department where department_name=#{departmentName} and id!=#{id}")
    Long countByDeptNameAndId(@Param("departmentName") String departmentName, @Param("id") Long id);

    @Select("select department_name,path from nbk_department where find_in_set(#{deptId},ancestors) or id=#{deptId}")
    List<DepartmentVO> findByAncestors(@Param("deptId") Long deptId);
}
