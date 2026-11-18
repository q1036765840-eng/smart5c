package com.nbk.smart5c.service.Impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.nbk.smart5c.common.enums.ExportStatusEnum;
import com.nbk.smart5c.entity.ExportInfo;
import com.nbk.smart5c.mapper.ExportInfoMapper;
import com.nbk.smart5c.pojo.bo.ExportInfoSaveBO;
import com.nbk.smart5c.pojo.bo.ExportInfoUpdateBO;
import com.nbk.smart5c.pojo.qo.ExportInfoQO;
import com.nbk.smart5c.pojo.vo.ExportInfoVO;
import com.nbk.smart5c.service.ExportInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 系统导出信息表业务逻辑层
 * @since 2026-10-27
 */
@Service
@Primary
public class ExportInfoServiceImpl extends ServiceImpl<ExportInfoMapper, ExportInfo> implements ExportInfoService<ExportInfoMapper, ExportInfo> {

    /**
     * 新增数据
     *
     * @param info 新增数据信息
     */
    @Override
    public Boolean save(ExportInfoSaveBO info) {
        ExportInfo entity = new ExportInfo();
        BeanUtils.copyProperties(info, entity);
        if (Objects.isNull(entity.getExportStatus())) {
            entity.setExportStatus(ExportStatusEnum.PROCESSING.getCode());
        }
        return super.save(entity);
    }

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     */
    @Override
    public ExportInfo queryById(String id) {
        return super.getById(id);
    }

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     */
    @Override
    public IPage<ExportInfoVO> queryAndPaging(ExportInfoQO query) {
        MPJLambdaWrapper<ExportInfo> exportInfoWrapper = buildQueryWrapper(query);
        exportInfoWrapper.orderByDesc(ExportInfo::getCreatedTime).orderByDesc(ExportInfo::getId);
        return exportInfoWrapper.page(new Page<>(query.getPageNo(), query.getPageSize()), ExportInfoVO.class);
    }

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    @Override
    public List<ExportInfoVO> queryAll(ExportInfoQO query) {
        return super.baseMapper.queryAll(query);
    }

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     */
    @Override
    public Boolean updateById(ExportInfoUpdateBO info) {
        ExportInfo entity = new ExportInfo();
        BeanUtils.copyProperties(info, entity);
        return super.updateById(entity);
    }

    private MPJLambdaWrapper<ExportInfo> buildQueryWrapper(ExportInfoQO query) {
        MPJLambdaWrapper<ExportInfo> exportInfoWrapper = new MPJLambdaWrapper<>(ExportInfo.class);
        exportInfoWrapper.like(CharSequenceUtil.isNotBlank(query.getTaskNo()), ExportInfo::getTaskNo, query.getTaskNo());
        exportInfoWrapper.like(CharSequenceUtil.isNotBlank(query.getSourcePage()), ExportInfo::getSourcePage, query.getSourcePage());
        exportInfoWrapper.eq(CharSequenceUtil.isNotBlank(query.getExportType()), ExportInfo::getExportType, query.getExportType());
        exportInfoWrapper.like(CharSequenceUtil.isNotBlank(query.getFileName()), ExportInfo::getFileName, query.getFileName());
        exportInfoWrapper.eq(Objects.nonNull(query.getExportStatus()), ExportInfo::getExportStatus, query.getExportStatus());
        exportInfoWrapper.like(CharSequenceUtil.isNotBlank(query.getCreatedUser()), ExportInfo::getCreatedUser, query.getCreatedUser());
        exportInfoWrapper.ge(Objects.nonNull(query.getStartTime()), ExportInfo::getCreatedTime, query.getStartTime());
        exportInfoWrapper.le(Objects.nonNull(query.getEndTime()), ExportInfo::getCreatedTime, query.getEndTime());
        return exportInfoWrapper;
    }
}
