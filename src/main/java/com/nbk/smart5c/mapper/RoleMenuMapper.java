package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.RoleMenu;
import com.nbk.smart5c.pojo.qo.RoleMenuQO;
import com.nbk.smart5c.pojo.vo.RoleMenuVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 角色和菜单关联表持久层
 * @since 2026-01-27 20:44:51
 */
public interface RoleMenuMapper extends MPJBaseMapper<RoleMenu> {

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果集
     */
    List<RoleMenuVO> queryAndPaging(@Param("query") RoleMenuQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 查询结果集
     */
    List<RoleMenuVO> queryAll(RoleMenuQO query);


}
