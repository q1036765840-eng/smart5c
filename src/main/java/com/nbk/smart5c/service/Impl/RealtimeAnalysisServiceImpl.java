package com.nbk.smart5c.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbk.smart5c.entity.RealtimeAnalysis;
import com.nbk.smart5c.mapper.RealtimeAnalysisMapper;
import com.nbk.smart5c.service.RealtimeAnalysisService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 实时分析表业务逻辑层
 * @since 2026-11-03
 */
@Service
@Primary
public class RealtimeAnalysisServiceImpl extends ServiceImpl<RealtimeAnalysisMapper, RealtimeAnalysis>
        implements RealtimeAnalysisService<RealtimeAnalysisMapper, RealtimeAnalysis> {

    @Override
    public RealtimeAnalysis queryBySourceFilepath(String sourceFilepath) {
        LambdaQueryWrapper<RealtimeAnalysis> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RealtimeAnalysis::getSourceFilepath, sourceFilepath).last("limit 1");
        return super.getOne(wrapper, false);
    }

    @Override
    public long removeExpired(int hours) {
        LambdaQueryWrapper<RealtimeAnalysis> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(RealtimeAnalysis::getCreatedTime, LocalDateTime.now().minusHours(hours));
        return super.baseMapper.delete(wrapper);
    }
}
