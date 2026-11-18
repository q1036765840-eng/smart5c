package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.UserInfo;
import com.nbk.smart5c.pojo.qo.UserInfoQO;
import com.nbk.smart5c.pojo.vo.UserInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 用户信息表持久层
 * @since 2026-01-27 15:25:38
 */
public interface UserInfoMapper extends MPJBaseMapper<UserInfo> {

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果集
     */
    List<UserInfoVO> queryAndPaging(@Param("query") UserInfoQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 查询结果集
     */
    List<UserInfoVO> queryAll(UserInfoQO query);


}
