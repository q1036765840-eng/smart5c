package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.Bureau;
import com.nbk.smart5c.pojo.qo.BureauQO;
import com.nbk.smart5c.pojo.vo.BureauVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 供电部信息表持久层
 * @since 2026-01-30 17:29:19
 */
public interface BureauMapper extends MPJBaseMapper<Bureau> {

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果集
     */
    List<BureauVO> queryAndPaging(@Param("query") BureauQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 查询结果集
     */
    List<BureauVO> queryAll(BureauQO query);


}
