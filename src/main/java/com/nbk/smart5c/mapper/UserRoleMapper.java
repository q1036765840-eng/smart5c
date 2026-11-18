package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.UserRole;
import com.nbk.smart5c.pojo.qo.UserRoleQO;
import com.nbk.smart5c.pojo.vo.UserRoleInfoVO;
import com.nbk.smart5c.pojo.vo.UserRoleVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 用户角色信息表持久层
 * @since 2026-01-27 15:25:38
 */
public interface UserRoleMapper extends MPJBaseMapper<UserRole> {

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果集
     */
    List<UserRoleVO> queryAndPaging(@Param("query") UserRoleQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 查询结果集
     */
    List<UserRoleVO> queryAll(UserRoleQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param userId 查询条件
     * @return 查询结果集
     */
    @Select({
            "select ur.user_id,ur.role_id,u.user_name,r.role_name,r.role_code from nbk_user_role ur inner join nbk_user_info u on ur.user_id=u.id",
            "inner join nbk_role_info r on ur.role_id=r.id where ur.user_id=#{userId}"
    })
    List<UserRoleInfoVO> getUserRoleInfo(@Param("userId") Long userId);

    @Delete("delete from nbk_user_role where user_id=#{userId}")
    void deleteByUserId(@Param("userId") Long userId);
}
