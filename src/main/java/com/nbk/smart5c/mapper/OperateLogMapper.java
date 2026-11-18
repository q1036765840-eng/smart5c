package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.OperateLog;
import com.nbk.smart5c.pojo.qo.OperateLogQO;
import com.nbk.smart5c.pojo.vo.OperateLogVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 操作日志表持久层
 * @since 2026-01-27 15:25:38
 */
public interface OperateLogMapper extends MPJBaseMapper<OperateLog> {

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果集
     */
    List<OperateLogVO> queryAndPaging(@Param("query") OperateLogQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 查询结果集
     */
    List<OperateLogVO> queryAll(OperateLogQO query);


}
