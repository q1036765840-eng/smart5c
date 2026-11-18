package com.nbk.smart5c.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbk.smart5c.common.enums.AnalyzeStatusEnum;
import com.nbk.smart5c.common.enums.ConfirmStatusEnum;
import com.nbk.smart5c.common.enums.DefectSourceTypeEnum;
import com.nbk.smart5c.common.enums.LogicStatusEnum;
import com.nbk.smart5c.common.utils.ResultCode;
import com.nbk.smart5c.entity.DefectStorage;
import com.nbk.smart5c.entity.InspectionData;
import com.nbk.smart5c.entity.InspectionReviewResult;
import com.nbk.smart5c.entity.Task;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.mapper.DefectStorageMapper;
import com.nbk.smart5c.mapper.InspectionDataMapper;
import com.nbk.smart5c.mapper.InspectionReviewResultMapper;
import com.nbk.smart5c.mapper.TaskMapper;
import com.nbk.smart5c.pojo.bo.*;
import com.nbk.smart5c.pojo.qo.InspectionReviewResultQO;
import com.nbk.smart5c.pojo.vo.InspectionReviewResultVO;
import com.nbk.smart5c.service.InspectionReviewResultService;
import com.nbk.smart5c.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 巡查数据审核结果表业务逻辑层
 * @since 2026-01-29 10:35:00
 */
@Service
@Primary
@RequiredArgsConstructor
public class InspectionReviewResultServiceImpl extends ServiceImpl<InspectionReviewResultMapper, InspectionReviewResult> implements InspectionReviewResultService<InspectionReviewResultMapper, InspectionReviewResult> {
    @Value("${inspect.host}")
    private String inspectHost;
    private final InspectionDataMapper inspectionDataMapper;
    private final TaskService<TaskMapper, Task> taskService;
    private final DefectStorageMapper defectStorageMapper;

    private final static String FILE_NAME = "nbk_inspection_review_result(巡查数据审核结果表)";

    private final static List<String> TITLE = Arrays.asList(
            "id(主键)",
            "inspection_id(巡检id)",
            "image_path(缺陷图路径)",
            "problem_desc(缺陷描述)",
            "pantograph_status(受电弓状态(0:异常,1:正常))",
            "defect_level(缺陷等级)",
            "image_valid(图片是否合规)",
            "marked_regions(标记区域)",
            "created_user(创建人)",
            "created_time(创建时间)",
            "updated_user(修改人)",
            "updated_time(修改时间)"
    );

    /**
     * 新增数据
     *
     * @param info 新增数据信息
     */
    @Override
    public Boolean save(InspectionReviewResultSaveBO info) {
        InspectionReviewResult entity = new InspectionReviewResult();
        BeanUtils.copyProperties(info, entity);
        return super.save(entity);
    }

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     */
    @Override
    public InspectionReviewResult queryById(String id) {
        return super.getById(id);
    }

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     */
    @Override
    public IPage<InspectionReviewResultVO> queryAndPaging(InspectionReviewResultQO query) {
        return null;
    }

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    @Override
    public List<InspectionReviewResultVO> queryAll(InspectionReviewResultQO query) {
        return super.baseMapper.queryAll(query);
    }

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     */
    @Override
    public Boolean updateById(InspectionReviewResultUpdateBO info) {
        InspectionReviewResult entity = new InspectionReviewResult();
        BeanUtils.copyProperties(info, entity);
        return super.updateById(entity);
    }

    /**
     * 数据导出
     *
     * @param query 查询条件
     */
    @Override
    public void export(InspectionReviewResultQO query, HttpServletResponse response) {
        // 查询用户数据
        List<InspectionReviewResultVO> result = baseMapper.queryAll(query);
        List<List<String>> data = new ArrayList<>(result.size());
        for (InspectionReviewResultVO item : result) {
            data.add(Arrays.asList(
                    item.getId() + "",
                    item.getInspectionId() + "",
                    item.getImagePath(),
                    item.getProblemDesc(),
                    item.getPantographStatus() + "",
                    item.getDefectLevel() + "",
                    item.getImageValid() + "",
                    item.getMarkedRegions(),
                    item.getCreatedUser(),
                    Objects.isNull(item.getCreatedTime()) ? "" : item.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    item.getUpdatedUser(),
                    Objects.isNull(item.getUpdatedTime()) ? "" : item.getUpdatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
    }

    /**
     * 模板导出
     */
    @Override
    public void exportTemplate(HttpServletResponse response) {

    }

    /**
     * 数据导入
     */
    @Override
    public void analysis(MultipartFile file) {

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveReviewData(InspectionDataReviewBO inspectionDataReviewBO) throws ServiceException {
        try {
            InspectionReviewResult reviewResult = super.baseMapper.getEntityByInspectionId(inspectionDataReviewBO.getInspectionId(), inspectionDataReviewBO.getLocomotiveImage().replace(inspectHost, ""));
            if (null == reviewResult) {
                reviewResult = new InspectionReviewResult();
            }
            List<String> markList = new ArrayList<>();
            if (CollUtil.isNotEmpty(inspectionDataReviewBO.getMarkedRegions())) {
                markList = CharSequenceUtil.isNotBlank(reviewResult.getMarkedRegions()) ? JSONUtil.toList(reviewResult.getMarkedRegions(), String.class) : new ArrayList<>();
                markList.addAll(inspectionDataReviewBO.getMarkedRegions());
            }
            reviewResult.setInspectionId(inspectionDataReviewBO.getInspectionId())
                    .setImagePath(inspectionDataReviewBO.getLocomotiveImage().replace(inspectHost, ""))
                    .setImageValid(inspectionDataReviewBO.getImageValid())
                    .setPantographStatus(inspectionDataReviewBO.getPantographStatus())
                    .setProblemDesc(inspectionDataReviewBO.getProblemDesc())
                    .setLocomotiveNumber(inspectionDataReviewBO.getLocomotiveNumber())
                    .setMarkedRegions(JSONUtil.toJsonStr(markList));
            if (null != reviewResult.getId()) {
                super.updateById(reviewResult);
            } else {
                super.save(reviewResult);
            }
            InspectionData inspectionData = inspectionDataMapper.selectById(inspectionDataReviewBO.getInspectionId());
            inspectionData.setAnalysisStatus(AnalyzeStatusEnum.REVIEWED.getCode());
            int pantographStatus = super.baseMapper.countErrorStatusByInspectionId(inspectionData.getId()) > 0 ? 2 : 1;
            inspectionData.setPantographStatus(pantographStatus);
            return inspectionDataMapper.updateById(inspectionData) > 0;
        } catch (Exception e) {
            throw new ServiceException(ResultCode.REVIEW_FAIL);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean misreport(MisreportReqBO misreportReqBO) throws ServiceException {
        InspectionReviewResult reviewResult = super.getById(misreportReqBO.getResultId());
        if (null == reviewResult) {
            throw new ServiceException(ResultCode.VALIDATE_FAILED.getCode(), "审核结果不存在: " + misreportReqBO.getResultId());
        }
        InspectionData inspectionData = inspectionDataMapper.selectById(reviewResult.getInspectionId());
        if (null == inspectionData) {
            throw new ServiceException(ResultCode.VALIDATE_FAILED.getCode(), "巡查数据不存在: " + reviewResult.getInspectionId());
        }
        if (LogicStatusEnum.YES.getCode().equals(misreportReqBO.getMisreport())) {
            // 图片误报：只有图中存在算法识别出的缺陷才允许误报
            List<DefectMarkBO> aiMarkedRegions = parseMarkedRegionsForAI(reviewResult.getMarkedRegions());
            if (CollUtil.isEmpty(aiMarkedRegions)) {
                throw new ServiceException(ResultCode.VALIDATE_FAILED.getCode(), "该图片没有算法识别出的缺陷，不能提交误报");
            }
            reviewResult.setMisreport(LogicStatusEnum.YES.getCode())
                    .setConfirmStatus(ConfirmStatusEnum.MISREPORT.getCode())
                    .setPantographStatus(InspectionData.PANTOGRAPH_STATUS_RIGHT);
            super.updateById(reviewResult);
        } else {
            // 取消误报：缺陷恢复为人工确认状态
            List<DefectMarkBO> markedRegions = parseMarkedRegions(reviewResult.getMarkedRegions());
            reviewResult.setMisreport(LogicStatusEnum.NO.getCode())
                    .setConfirmStatus(ConfirmStatusEnum.CONFIRMED.getCode())
                    .setPantographStatus(CollUtil.isEmpty(markedRegions)
                            ? InspectionData.PANTOGRAPH_STATUS_RIGHT
                            : InspectionData.PANTOGRAPH_STATUS_ERROR);
            super.updateById(reviewResult);
        }

        // 按最新的审核结果重新汇总巡检数据的确认状态、受电弓状态、误报状态
        refreshReviewStatus(inspectionData, reviewResult.getId());
        return true;
    }

    /**
     * 依据审核结果重新汇总巡检数据的聚合状态：
     * 1. 受电弓状态：存在异常记录则为异常，否则正常；
     * 2. 误报状态：存在误报记录则为误报，否则否；
     * 3. 确认状态：算法来源记录已全部确认(含误报)且其余记录已查看时，若还存在已确认记录则置为已确认，否则置为误报；
     *    不满足上述条件但其余记录均已查看时置为已查看；否则保持原确认状态不变。
     */
    private void refreshReviewStatus(InspectionData inspectionData, Long reviewResultId) {
        inspectionData.setPantographStatus(super.baseMapper.countErrorStatusByInspectionId(inspectionData.getId()) > 0
                ? InspectionData.PANTOGRAPH_STATUS_ERROR
                : InspectionData.PANTOGRAPH_STATUS_RIGHT);
        inspectionData.setMisreport(super.baseMapper.getMisreportByInspectionId(inspectionData.getId()) > 0
                ? LogicStatusEnum.YES.getCode()
                : LogicStatusEnum.NO.getCode());

        if (taskService.isAllAiConfirmedAndOthersViewed(inspectionData.getId())) {
            inspectionData.setConfirmStatus(existsConfirmedRecord(inspectionData.getId(), reviewResultId)
                    ? ConfirmStatusEnum.CONFIRMED.getCode()
                    : ConfirmStatusEnum.MISREPORT.getCode());
        } else if (isAllOthersViewed(inspectionData.getId())) {
            inspectionData.setConfirmStatus(ConfirmStatusEnum.VIEWED.getCode());
        }
        inspectionDataMapper.updateById(inspectionData);
    }

    /**
     * 解析 marked_regions 并只保留算法来源(source=1)的标记，用于判断图中是否包含算法识别出的缺陷
     */
    private List<DefectMarkBO> parseMarkedRegionsForAI(String markedRegions) {
        return parseMarkedRegions(markedRegions).stream()
                .filter(dm -> null != dm && DefectSourceTypeEnum.AI.getCode().equals(dm.getSource()))
                .collect(Collectors.toList());
    }

    /**
     * 解析标记区域字段：空串或非法 JSON 返回空列表
     */
    private List<DefectMarkBO> parseMarkedRegions(String markedRegionsJson) {
        if (StrUtil.isBlank(markedRegionsJson)) {
            return new ArrayList<>();
        }
        return JSONUtil.toList(markedRegionsJson, DefectMarkBO.class);
    }

    /**
     * 判断巡检数据下是否存在已确认(2)状态的记录，excludeResultId 用于排除当前正在误报提交的记录
     * （该记录在误报流程中已先被置为已确认，不能计入）
     */
    private boolean existsConfirmedRecord(Long inspectionId, Long excludeResultId) {
        LambdaQueryWrapper<InspectionReviewResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionReviewResult::getInspectionId, inspectionId)
                .eq(InspectionReviewResult::getConfirmStatus, ConfirmStatusEnum.CONFIRMED.getCode())
                .ne(excludeResultId != null, InspectionReviewResult::getId, excludeResultId);
        return super.count(wrapper) > 0;
    }

    /**
     * 判断巡检数据下非算法来源(其它)记录的 confirm_status 是否均为已查看(1)
     */
    private boolean isAllOthersViewed(Long inspectionId) {
        LambdaQueryWrapper<InspectionReviewResult> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionReviewResult::getInspectionId, inspectionId);
        List<InspectionReviewResult> reviewResults = super.list(wrapper);
        if (CollUtil.isEmpty(reviewResults)) {
            return false;
        }
        boolean existsOther = false;
        for (InspectionReviewResult result : reviewResults) {
            List<DefectMarkBO> markedRegions = parseMarkedRegions(result.getMarkedRegions());
            if (markedRegions.stream().anyMatch(dm -> DefectSourceTypeEnum.AI.getCode().equals(dm.getSource()))) {
                continue;
            }
            existsOther = true;
            if (!ConfirmStatusEnum.VIEWED.getCode().equals(result.getConfirmStatus())) {
                return false;
            }
        }
        return existsOther;
    }
}
