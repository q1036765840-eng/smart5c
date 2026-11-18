package com.nbk.smart5c.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.google.common.collect.Lists;
import com.nbk.smart5c.common.enums.ConfirmStatusEnum;
import com.nbk.smart5c.common.enums.DefectReviewStatusEnum;
import com.nbk.smart5c.common.enums.ExportStatusEnum;
import com.nbk.smart5c.common.enums.LogicStatusEnum;
import com.nbk.smart5c.common.utils.*;
import com.nbk.smart5c.common.utils.holder.CurrentUserHolder;
import com.nbk.smart5c.entity.DefectStorage;
import com.nbk.smart5c.entity.ExportInfo;
import com.nbk.smart5c.entity.InspectionData;
import com.nbk.smart5c.entity.InspectionReviewResult;
import com.nbk.smart5c.entity.UserInfo;
import com.nbk.smart5c.mapper.DefectStorageMapper;
import com.nbk.smart5c.mapper.ExportInfoMapper;
import com.nbk.smart5c.mapper.InspectionDataMapper;
import com.nbk.smart5c.mapper.InspectionReviewResultMapper;
import com.nbk.smart5c.mapper.UserInfoMapper;
import com.nbk.smart5c.pojo.bo.DefectMarkBO;
import com.nbk.smart5c.pojo.bo.DefectStorageReviewBO;
import com.nbk.smart5c.pojo.bo.DefectStorageSaveBO;
import com.nbk.smart5c.pojo.bo.DefectStorageUpdateBO;
import com.nbk.smart5c.pojo.qo.DefectStorageDetailQO;
import com.nbk.smart5c.pojo.qo.DefectStorageQO;
import com.nbk.smart5c.pojo.vo.DefectStorageDetailShowVO;
import com.nbk.smart5c.pojo.vo.DefectStorageVO;
import com.nbk.smart5c.service.DefectStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 缺陷库表业务逻辑层
 * @since 2026-04-07 16:41:49
 */
@Log4j2
@Service
@Primary
@RequiredArgsConstructor
public class DefectStorageServiceImpl extends ServiceImpl<DefectStorageMapper, DefectStorage> implements DefectStorageService<DefectStorageMapper, DefectStorage> {
    private final HttpServletResponse response;
    @Value("${inspect.defectpath}")
    private String defectpath;
    private final DefectStorageMapper defectStorageMapper;
    private final UserInfoMapper userInfoMapper;
    private final ExportInfoMapper exportInfoMapper;
    private final InspectionDataMapper inspectionDataMapper;
    private final InspectionReviewResultMapper inspectionReviewResultMapper;

    /**
     * 新增数据
     *
     * @param info 新增数据信息
     */
    @Override
    public Boolean save(DefectStorageSaveBO info) {
        DefectStorage entity = new DefectStorage();
        BeanUtils.copyProperties(info, entity);
        return super.save(entity);
    }

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     */
    @Override
    public DefectStorage queryById(String id) {
        return super.getById(id);
    }

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     */
    @Override
    public IPage<DefectStorageVO> queryAndPaging(DefectStorageQO query) {
        MPJLambdaWrapper<DefectStorage> defectStorageWrapper = new MPJLambdaWrapper<>(DefectStorage.class);
        defectStorageWrapper.like(CharSequenceUtil.isNotBlank(query.getTaskName()), DefectStorage::getTaskName, query.getTaskName());
        defectStorageWrapper.like(CharSequenceUtil.isNotBlank(query.getStationName()), DefectStorage::getStationName, query.getStationName());
        defectStorageWrapper.like(CharSequenceUtil.isNotBlank(query.getPoleNo()), DefectStorage::getPoleNo, query.getPoleNo());
        defectStorageWrapper.like(CharSequenceUtil.isNotBlank(query.getKmSign()), DefectStorage::getKmSign, query.getKmSign());
        defectStorageWrapper.like(CharSequenceUtil.isNotBlank(query.getLocomotiveNumber()), DefectStorage::getLocomotiveNumber, query.getLocomotiveNumber());
        defectStorageWrapper.like(CharSequenceUtil.isNotBlank(query.getDefectType()), DefectStorage::getDefectType, query.getDefectType());
        defectStorageWrapper.like(CharSequenceUtil.isNotBlank(query.getReviewer()), DefectStorage::getReviewer, query.getReviewer());
        defectStorageWrapper.in(CollUtil.isNotEmpty(query.getRevieweStatusList()), DefectStorage::getReviewStatus, query.getRevieweStatusList());
        defectStorageWrapper.gt(Objects.nonNull(query.getStartTime()), DefectStorage::getInspectionTime, query.getStartTime());
        defectStorageWrapper.lt(Objects.nonNull(query.getEndTime()), DefectStorage::getInspectionTime, query.getEndTime());
        defectStorageWrapper.orderByDesc(DefectStorage::getId);
        Page<DefectStorageVO> page = defectStorageWrapper.page(new Page<>(query.getPageNo(), query.getPageSize()), DefectStorageVO.class);
        if (CollUtil.isNotEmpty(page.getRecords())) {
            page.getRecords().forEach(r -> {
                String reviewer = r.getReviewer();
                String account = reviewer.substring(reviewer.indexOf("(") + 1, reviewer.indexOf(")"));
                r.setReviewer(account);
                String[] pathArr = r.getImagePath().split("/");
                r.setImageName(pathArr[pathArr.length - 2] + "/" + pathArr[pathArr.length - 1]);
            });
        }
        return page;
    }

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    @Override
    public List<DefectStorageVO> queryAll(DefectStorageQO query) {
        return null;
    }

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     */
    @Override
    public Boolean updateById(DefectStorageUpdateBO info) {
        DefectStorage entity = new DefectStorage();
        ObjectCopyTool.copyPropertiesNotNullandEmpty(info, entity);
        return super.updateById(entity);
    }

    /**
     * 导出第一页数据
     *
     * @param query 查询条件
     */
    @Override
    public void export(DefectStorageQO query, HttpServletResponse response) {
        List<Long> result = baseMapper.queryCurrentPage(query);
        doExport(result, query.getBusiType(), "/smart5cApi/defectStorage/exportQuery");
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

    /**
     * 缺陷库复核
     * @param defectStorageReviewBO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean defectReview(DefectStorageReviewBO defectStorageReviewBO) {
        List<DefectStorage> defectStorages = super.listByIds(defectStorageReviewBO.getIds());
        if (CollUtil.isNotEmpty(defectStorages)) {
            for (DefectStorage defectStorage : defectStorages) {
                defectStorage.setReviewText(defectStorageReviewBO.getReviewText()).
                        setReviewStatus(defectStorageReviewBO.getReviewStatus())
                        .setReviewerId(CurrentUserHolder.get().getId())
                        .setReviewer(CurrentUserHolder.get().getUserName() + "(" + CurrentUserHolder.get().getAccountNo() + ")")
                ;
            }
            super.updateBatchById(defectStorages, 100);
        }
        return true;
    }

    /**
     * 巡检数据下复核结果受电弓状态全部正常、且有过缺陷的记录确认状态全部为误报时，
     * 将巡检数据的确认状态、受电弓状态、误报标记一并置为误报。
     */
    private void refreshInspectionMisreport(Long inspectionId) {
        List<InspectionReviewResult> reviewResults = inspectionReviewResultMapper.selectList(
                new LambdaQueryWrapper<InspectionReviewResult>()
                        .eq(InspectionReviewResult::getInspectionId, inspectionId));
        List<InspectionReviewResult> defectResults = reviewResults.stream()
                .filter(r -> CharSequenceUtil.isNotBlank(r.getMarkedRegions()) && !"[]".equals(r.getMarkedRegions()))
                .collect(Collectors.toList());
        boolean allNormal = reviewResults.stream()
                .allMatch(r -> InspectionData.PANTOGRAPH_STATUS_RIGHT.equals(r.getPantographStatus()));
        boolean allDefectMisreport = CollUtil.isNotEmpty(defectResults) && defectResults.stream()
                .allMatch(r -> ConfirmStatusEnum.MISREPORT.getCode().equals(r.getConfirmStatus()));
        if (!allNormal || !allDefectMisreport) {
            return;
        }
        InspectionData inspectionData = inspectionDataMapper.selectById(inspectionId);
        if (null == inspectionData) {
            return;
        }
        inspectionData.setConfirmStatus(ConfirmStatusEnum.MISREPORT.getCode())
                .setPantographStatus(InspectionData.PANTOGRAPH_STATUS_RIGHT)
                .setMisreport(LogicStatusEnum.YES.getCode());
        inspectionDataMapper.updateById(inspectionData);
    }

    @Autowired
    private ExcelProcessUtil excelProcessUtil;

    @Override
    public void exportChecked(List<Long> idLIst, String exportType) {
        doExport(idLIst, exportType, "/smart5cApi/defectStorage/exportChecked");
    }

    @Override
    public List<List<Object>> getExportChecked(List<Long> idLIst, String exportType) {
        List<DefectStorage> storageList = super.listByIds(idLIst);
        List<List<Object>> baseDataList = new ArrayList<>(storageList.size());
        List<Object> titles = Lists.newArrayList("缺陷图片","任务名", "区站", "复核人", "缺陷类型", "杆号", "机车号", "检测时间");
        baseDataList.add(titles);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int total = storageList.size();
        for (int i = 0; i < total; i++) {
            DefectStorage item = storageList.get(i);
            String imageInfo = null;
            Map<String,Object> param = new HashMap<>();
            if (CharSequenceUtil.isNotBlank(item.getImagePath())) {
                imageInfo = item.getNetPath();
                param.put("imageUrl", imageInfo);
                if (CharSequenceUtil.isNotBlank(item.getMarkedRegions()) && !"[]".equals(item.getMarkedRegions())) {
                    // 缺陷库的 marked_regions 是单个coord字符串，包装成DefectMarkBO数组JSON，
                    // 与 ExcelProcessUtil 解析格式（路径;[{"coord":..,"defectType":..}]）对齐
                    DefectMarkBO markBO = new DefectMarkBO()
                            .setCoord(item.getMarkedRegions())
                            .setDefectType(item.getDefectType() == null ? "" : item.getDefectType());
                    param.put("markedRegions", JSONUtil.toJsonStr(Collections.singletonList(markBO)));
                }
            }
            baseDataList.add(Lists.newArrayList(
                    param,
                    item.getTaskName() == null ? "" : item.getTaskName(),
                    item.getStationName() == null ? "" : item.getStationName(),
                    item.getReviewer() == null ? "" : item.getReviewer(),
                    item.getDefectType() == null ? "" : item.getDefectType(),
                    item.getPoleNo() == null ? "" : item.getPoleNo(),
                    item.getLocomotiveNumber() == null ? "" : item.getLocomotiveNumber(),
                    item.getInspectionTime() == null ? "" : item.getInspectionTime().format(formatter)
            ));
        }
        return baseDataList;
    }

    private void doExport(List<Long> idLIst, String exportType, String exportApi) {
        Long userId = currentUserId();
        AtomicInteger lastProgress = new AtomicInteger(-1);
        String exportTypeStr = "0".equals(exportType) ? ExportProgressUtil.EXPORT_TYPE_DEFECT_MANAGE : ExportProgressUtil.EXPORT_TYPE_DEFECT_STORAGE;
        IntConsumer progressConsumer = progress -> ExportProgressUtil.send(userId,
                exportTypeStr, progress, lastProgress);
        //添加导出记录
        Map<String,Object> param = new HashMap<>();
        param.put("idLIst", idLIst);
        param.put("exportType", exportType);
        ExportInfo exportInfo = new ExportInfo();
        exportInfo.setSourcePage("0".equals(exportType) ? "缺陷管理" : "缺陷库")
                .setExportType("0".equals(exportType) ? "缺陷管理" : "缺陷库")
                .setQueryParam(JSONUtil.toJsonStr(param))
                .setFileName(("0".equals(exportType) ? "缺陷管理" : "缺陷库") + System.currentTimeMillis() + ".xlsx")
                .setQueryApi("/smart5cApi/defectStorage/getExportChecked")
                .setExportApi(exportApi);
        List<DefectStorage> storageList = super.listByIds(idLIst);
        List<List<Object>> baseDataList = new ArrayList<>(storageList.size());
        progressConsumer.accept(0);
        try {
            List<String> titles = Lists.newArrayList("任务名", "区站", "复核人", "缺陷类型", "杆号", "机车号", "检测时间", "缺陷图片");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int total = storageList.size();
            for (int i = 0; i < total; i++) {
                DefectStorage item = storageList.get(i);
                String imageInfo = null;
                if (CharSequenceUtil.isNotBlank(item.getImagePath())) {
                    imageInfo = defectpath + item.getImagePath();
                    if (CharSequenceUtil.isNotBlank(item.getMarkedRegions()) && !"[]".equals(item.getMarkedRegions())) {
                        // 缺陷库的 marked_regions 是单个coord字符串，包装成DefectMarkBO数组JSON，
                        // 与 ExcelProcessUtil 解析格式（路径;[{"coord":..,"defectType":..}]）对齐
                        DefectMarkBO markBO = new DefectMarkBO()
                                .setCoord(item.getMarkedRegions())
                                .setDefectType(item.getDefectType() == null ? "" : item.getDefectType());
                        imageInfo += ";" + JSONUtil.toJsonStr(Collections.singletonList(markBO));
                    }
                }
                baseDataList.add(Lists.newArrayList(
                        item.getTaskName() == null ? "" : item.getTaskName(),
                        item.getStationName() == null ? "" : item.getStationName(),
                        item.getReviewer() == null ? "" : item.getReviewer(),
                        item.getDefectType() == null ? "" : item.getDefectType(),
                        item.getPoleNo() == null ? "" : item.getPoleNo(),
                        item.getLocomotiveNumber() == null ? "" : item.getLocomotiveNumber(),
                        item.getInspectionTime() == null ? "" : item.getInspectionTime().format(formatter),
                        imageInfo
                ));
                // 组装数据阶段占 0~10%（导出方法从 10% 开始上报图片处理进度）
                progressConsumer.accept((int) ((long) (i + 1) * 10 / total));
            }
            ByteCountingServletResponse countingResponse = new ByteCountingServletResponse(response);
            excelProcessUtil.exportContainPicture(titles, baseDataList, true, "缺陷.xlsx", countingResponse, progressConsumer);
            exportInfo.setQuantity(String.valueOf(baseDataList.size()))
                    .setFileSize(countingResponse.getReadableSize())
                    .setExportStatus(ExportStatusEnum.SUCCESS.getCode());
            exportInfoMapper.insert(exportInfo);
            exportInfo.setTaskNo(ExportProgressUtil.EXPORT_TASK_PREFIX + DateUtil.localDateToYyyymmdd(LocalDate.now()) + "-" + exportInfo.getId());
            exportInfoMapper.updateById(exportInfo);
        } catch (Exception e) {
            // 用户主动取消下载不记为失败，避免误报
            if (ClientAbortUtil.isClientAbort(e)) {
                throw new RuntimeException(e);
            }
            progressConsumer.accept(ExportProgressUtil.FAILED);
            exportInfo.setQuantity(String.valueOf(baseDataList.size())).setExportStatus(ExportStatusEnum.FAILED.getCode());
            exportInfoMapper.insert(exportInfo);
            exportInfo.setTaskNo(ExportProgressUtil.EXPORT_TASK_PREFIX + DateUtil.localDateToYyyymmdd(LocalDate.now()) + "-" + exportInfo.getId());
            exportInfoMapper.updateById(exportInfo);
            throw new RuntimeException(e);
        }
    }

    private Long currentUserId() {
        UserInfo user = CurrentUserHolder.get();
        return null == user ? null : user.getId();
    }

    @Override
    public List<String> defectTypeList(Long taskId, List<Integer> reviewStatusList) {
        return super.baseMapper.getTaskDefectType(taskId, reviewStatusList);
    }

    @Override
    public DefectStorageDetailShowVO defectStorageDetail(DefectStorageDetailQO defectStorageDetailQO) {
        Long id = super.baseMapper.getDefectIdByCont(defectStorageDetailQO);
        if (null == id) {
            return new DefectStorageDetailShowVO();
        }
        DefectStorage defectStorage = super.getById(id);
        DefectStorageDetailShowVO detailShowVO = new DefectStorageDetailShowVO();
        Long minId = super.baseMapper.getMinId(defectStorageDetailQO);
        if (null == minId || 0 == minId) {
            minId = -1L;
        }
        Long maxId = super.baseMapper.getMaxId(defectStorageDetailQO);
        if (null == maxId || 0 == maxId) {
            maxId = -1L;
        }
        List<Long> idList = new ArrayList<>();
        idList.add(maxId);
        idList.add(minId);
        BeanUtils.copyProperties(defectStorage, detailShowVO);
        String[] pathArr = detailShowVO.getImagePath().split("/");
        detailShowVO.setImageName(pathArr[pathArr.length - 2] + "/" + pathArr[pathArr.length - 1]);
        detailShowVO.setReviewStatusText(DefectReviewStatusEnum.getNameByCode(defectStorage.getReviewStatus()));
        if (CharSequenceUtil.isNotBlank(defectStorage.getMarkedRegions())) {
            detailShowVO.setMarkedRegions(defectStorage.getMarkedRegions());
        }
        detailShowVO.setIdList(idList);
        return detailShowVO;
    }

    @Override
    public void downloadDefectImage(Long id, Integer hasDefect, HttpServletResponse response) {
        DefectStorage byId = super.getById(id);
        if (byId == null || CharSequenceUtil.isBlank(byId.getImagePath())) {
            throw new RuntimeException("缺陷图片不存在");
        }
        File image = new File(defectpath + byId.getImagePath());
        if (!image.exists()) {
            throw new RuntimeException("缺陷图片文件不存在");
        }
        String fileName = image.getName();
        try {
            BufferedImage bufferedImage = null;
            if (LogicStatusEnum.YES.getCode().equals(hasDefect)) {
                DefectMarkBO defectMarkBO = new DefectMarkBO();
                defectMarkBO.setCoord(byId.getMarkedRegions()).setDefectType(byId.getDefectType() == null ? "" : byId.getDefectType());
                log.info("下载图片参数:{}", JSONUtil.toJsonStr(defectMarkBO));
                bufferedImage = WaterMarkUtils.drawRedRectangle(image, Lists.newArrayList(defectMarkBO));
            } else {
                bufferedImage = ImageIO.read(image);
            }
            response.setContentType("image/jpeg");
            response.setHeader("Content-Disposition", "inline;filename=" + fileName);
            ImageIO.write(bufferedImage, "jpg", response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            throw new RuntimeException("下载缺陷图片失败", e);
        }
    }

    @Override
    public Boolean deleteById(Long id) {
        DefectStorage byId = super.getById(id);
        if (byId == null) {
            return false;
        }
        String imagePath = byId.getImagePath();
        //删除该条缺陷
        boolean removed = super.removeById(id);
        //该图片上的缺陷是否已全部删除，是则连带删除图片文件
        if (CharSequenceUtil.isNotBlank(imagePath)) {
            long remaining = super.count(new LambdaQueryWrapper<DefectStorage>()
                    .eq(DefectStorage::getImagePath, imagePath));
            if (remaining == 0) {
                File image = new File(defectpath + imagePath);
                if (image.exists() && image.delete()) {
                    log.info("图片缺陷已全部删除，清理图片文件: {}", image.getAbsolutePath());
                    //向上清理空目录
                    deleteEmptyParentDirs(image.getParentFile(), defectpath);
                } else {
                    log.warn("图片缺陷已全部删除，但图片文件不存在或删除失败: {}", image.getAbsolutePath());
                }
            }
        }
        return removed;
    }

    /**
     * 从指定目录起向上递归：若目录为空则删除，直到非空或到达根目录为止
     *
     * @param dir  起始目录
     * @param root 根目录（不删除，作为终止条件）
     */
    private void deleteEmptyParentDirs(File dir, String root) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return;
        }
        File rootFile = new File(root).getAbsoluteFile();
        File current = dir.getAbsoluteFile();
        while (current != null && !current.equals(rootFile) && current.getAbsolutePath().startsWith(rootFile.getAbsolutePath())) {
            String[] children = current.list();
            if (children != null && children.length > 0) {
                break;
            }
            if (!current.delete()) {
                log.warn("空目录删除失败: {}", current.getAbsolutePath());
                break;
            }
            log.info("清理空目录: {}", current.getAbsolutePath());
            current = current.getParentFile();
        }
    }

    @Override
    public Boolean deleteByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        List<DefectStorage> defectStorages = super.listByIds(ids);
        if (CollUtil.isEmpty(defectStorages)) {
            return false;
        }
        //删除选中的缺陷
        boolean removed = super.removeByIds(ids);
        //按图片路径分组统计：被删缺陷涉及的每张图片，若无其他缺陷引用则清理图片文件
        defectStorages.stream()
                .map(DefectStorage::getImagePath)
                .filter(CharSequenceUtil::isNotBlank)
                .distinct()
                .forEach(imagePath -> {
                    long remaining = super.count(new LambdaQueryWrapper<DefectStorage>()
                            .eq(DefectStorage::getImagePath, imagePath));
                    if (remaining == 0) {
                        File image = new File(defectpath + imagePath);
                        if (image.exists() && image.delete()) {
                            log.info("图片缺陷已全部删除，清理图片文件: {}", image.getAbsolutePath());
                            //向上清理空目录
                            deleteEmptyParentDirs(image.getParentFile(), defectpath);
                        } else {
                            log.warn("图片缺陷已全部删除，但图片文件不存在或删除失败: {}", image.getAbsolutePath());
                        }
                    }
                });
        return removed;
    }
}
