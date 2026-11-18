package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.RoleInfo;
import com.nbk.smart5c.pojo.qo.RoleInfoQO;
import com.nbk.smart5c.pojo.vo.DropDownListVO;
import com.nbk.smart5c.pojo.vo.RoleInfoVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 角色信息表持久层
 * @since 2026-01-27 15:25:38
 */
public interface RoleInfoMapper extends MPJBaseMapper<RoleInfo> {

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果集
     */
    List<RoleInfoVO> queryAndPaging(@Param("query") RoleInfoQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 查询结果集
     */
    List<RoleInfoVO> queryAll(RoleInfoQO query);

    @Select("select id, role_name name from nbk_role_info")
    List<DropDownListVO> getRoleDownList();

    @Select("select * from nbk_role_info where role_code=#{roleCode}")
    RoleInfo getRoleByCode(@Param("roleCode") String roleCode);
}
