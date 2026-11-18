package com.nbk.smart5c.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nbk.smart5c.entity.RealtimeAnalysis;
import org.springframework.stereotype.Service;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 实时分析表业务逻辑层
 * @since 2026-11-03
 */
@Service
public interface RealtimeAnalysisService<M extends BaseMapper<T>, T> extends IService<T> {

    /**
     * 按源数据目录路径查询实时分析记录
     *
     * @param sourceFilepath 源数据目录绝对路径
     * @return 实时分析记录，不存在时返回 null
     */
    RealtimeAnalysis queryBySourceFilepath(String sourceFilepath);

    /**
     * 清理创建时间早于指定小时的记录
     *
     * @param hours 保留时长（小时）
     * @return 清理条数
     */
    long removeExpired(int hours);
}
