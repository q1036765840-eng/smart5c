package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.ExportInfo;
import com.nbk.smart5c.pojo.qo.ExportInfoQO;
import com.nbk.smart5c.pojo.vo.ExportInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 系统导出信息表持久层
 * @since 2026-10-27
 */
public interface ExportInfoMapper extends MPJBaseMapper<ExportInfo> {

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果集
     */
    List<ExportInfoVO> queryAndPaging(@Param("query") ExportInfoQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 查询结果集
     */
    List<ExportInfoVO> queryAll(@Param("query") ExportInfoQO query);

}
