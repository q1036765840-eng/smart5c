package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.Menu;
import com.nbk.smart5c.pojo.qo.MenuQO;
import com.nbk.smart5c.pojo.vo.MenuVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 菜单权限表持久层
 * @since 2026-01-27 20:44:51
 */
public interface MenuMapper extends MPJBaseMapper<Menu> {

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果集
     */
    List<MenuVO> queryAndPaging(@Param("query") MenuQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 查询结果集
     */
    List<MenuVO> queryAll(MenuQO query);


}
