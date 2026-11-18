
package com.nbk.smart5c.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.nbk.smart5c.common.enums.AnalyzeStatusEnum;
import com.nbk.smart5c.common.enums.ExportStatusEnum;
import com.nbk.smart5c.common.utils.ApiUtils;
import com.nbk.smart5c.common.utils.ByteCountingServletResponse;
import com.nbk.smart5c.common.utils.ClientAbortUtil;
import com.nbk.smart5c.common.utils.DateUtil;
import com.nbk.smart5c.common.utils.ExcelProcessUtil;
import com.nbk.smart5c.common.utils.ExportProgressUtil;
import com.nbk.smart5c.common.utils.FilteredFileLister;
import com.nbk.smart5c.entity.ExportInfo;
import com.nbk.smart5c.entity.InspectionData;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.mapper.ExportInfoMapper;
import com.nbk.smart5c.mapper.InspectionDataMapper;
import com.nbk.smart5c.pojo.qo.InspectionDataQO;
import com.nbk.smart5c.pojo.vo.LabelValueVO;
import com.nbk.smart5c.pojo.vo.LatestAlarmListVO;
import com.nbk.smart5c.pojo.vo.TodayCheckDataVO;
import com.nbk.smart5c.service.HomePageService;
import com.nbk.smart5c.service.InspectionDataService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 角色信息表业务逻辑层
 * @since 2026-01-27 15:25:38
 */
@Service
public class HomePageServiceImpl implements HomePageService {
    private static final int REPORT_PAGE_SIZE = 100;

    @Value("${inspect.basepath}")
    private String inspectDataPath;
    @Value("${inspect.host}")
    private String inspectHost;

    @Autowired
    private InspectionDataMapper inspectionDataMapper;

    @Autowired
    private InspectionDataService<InspectionDataMapper, InspectionData> inspectionDataService;

    @Autowired
    private ExcelProcessUtil excelProcessUtil;

    @Autowired
    private ApiUtils apiUtils;

    @Autowired
    private ExportInfoMapper exportInfoMapper;

    @Override
    public Long countAlarmsToday() {
        return inspectionDataMapper.countAlarmsToday();
    }

    @Override
    public List<LabelValueVO> lastWeekAlarmsTrend() {
        List<LabelValueVO> weekData = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {
            LabelValueVO labelValueVO = new LabelValueVO();
            labelValueVO.setLabel(DateUtil.localdate_To_yyyy_MM_dd(LocalDate.now().minusDays(i))).setValue(0L);
            weekData.add(labelValueVO);
        }
        List<LabelValueVO> happenData = inspectionDataMapper.lastWeekAlarms();
        if (CollUtil.isNotEmpty(happenData)) {
//            for(LabelValueVO happen:happenData){
//                weekData.remove(happen.getLabel());
//            }
            weekData.addAll(happenData);
        }
        return removeDuplicatesByLabelKeepLast(weekData);
    }

    public static List<LabelValueVO> removeDuplicatesByLabelKeepLast(List<LabelValueVO> list) {
        // 使用LinkedHashMap保持插入顺序，保留最后一个元素
        return new ArrayList<>(list.stream()
                .collect(Collectors.toMap(
                        LabelValueVO::getLabel,
                        Function.identity(),
                        (existing, replacement) -> replacement,  // 保留最后一个
                        LinkedHashMap::new
                ))
                .values());
    }

    @Override
    public TodayCheckDataVO todayCheckDataCount() {
        String today = DateUtil.localdate_To_yyyy_MM_dd(LocalDate.now());
        Long locomotiveNum = inspectionDataMapper.defectLocomotiveNum(today);
        Long totalPictures = inspectionDataMapper.processTotalPictures();
        totalPictures = null == totalPictures ? 0L : totalPictures;
        Long defectFolders = inspectionDataMapper.defectCount();
        defectFolders = defectFolders == null ? 0L : defectFolders;
        TodayCheckDataVO todayCheckDataVO = new TodayCheckDataVO();
        todayCheckDataVO.setLocomotiveNum(locomotiveNum).setDetectRate(defectFolders).setPictures(totalPictures);
        return todayCheckDataVO;
    }

    public static String getPercentFormat(Double data, Integer FractionDigits) {
        if (Objects.isNull(data)) {
            return "0%";
        }
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(FractionDigits);// 小数点后保留几位
        return nf.format(data);
    }

    public static String getTwoNumberDivideToPercent(Integer number1, Integer number2) {
        if (Objects.isNull(number1) || Objects.isNull(number2) || number2 == 0 || number1 == 0) {
            return getPercentFormat(Double.parseDouble("0"), 2);
        }
        BigDecimal bigDecimal1 = new BigDecimal(number1);
        BigDecimal bigDecimal2 = new BigDecimal(number2);
        BigDecimal divide = bigDecimal1.divide(bigDecimal2, 6, RoundingMode.HALF_UP);
        return getPercentFormat(divide.doubleValue(), 2);
    }

    @Override
    public Page<LatestAlarmListVO> lastAlarmPaging(Integer pageNo, Integer pageSize) {
        Integer offset = (pageNo - 1) * pageSize;
        List<LatestAlarmListVO> latestAlarmList = inspectionDataMapper.latestAlarmPage(offset, pageSize);
        if (CollUtil.isNotEmpty(latestAlarmList)) {
            for (LatestAlarmListVO latestAlarmListVO : latestAlarmList) {
                String imageName = FilteredFileLister.getFirstFileByExtension(inspectDataPath + latestAlarmListVO.getLocomotiveImage());
                latestAlarmListVO.setLocomotiveImage(inspectHost + latestAlarmListVO.getLocomotiveImage() + imageName);
            }
        }
        Integer total = inspectionDataMapper.countLatestAlarm();
        Page<LatestAlarmListVO> page = new Page<>();
        int pages = total / pageSize + (total % pageSize == 0 ? 0 : 1);
        page.setRecords(latestAlarmList).setTotal(total).setSize(pageSize).setPages(pages).setCurrent(pageNo);
        return page;
    }

    @Override
    public List<List<Object>> getGenerateReportData() throws ServiceException {
        List<InspectionData> inspectionDataList = queryReportData();
        List<List<Object>> baseDataList = new ArrayList<>();
        List<Object> title = Arrays.asList(
                "机车图",
                "机车号",
                "检测时间",
                "支柱号",
                "分析状态"
        );
        baseDataList.add(title);
        if (CollUtil.isNotEmpty(inspectionDataList)) {
            for (InspectionData inspectionData : inspectionDataList) {
                Map<String, Object> param = new HashMap<>();
                if (CharSequenceUtil.isNotBlank(inspectionData.getLocomotiveImage())) {
                    param.put("imageUrl", inspectionData.getLocomotiveImage());
                    if (CollUtil.isNotEmpty(inspectionData.getMarkedRegions())) {
                        param.put("markedRegions", JSONUtil.toJsonStr(inspectionData.getMarkedRegions()));
                    }
                }
                List<Object> data = Lists.newArrayList(param,inspectionData.getLocomotiveNumber(), inspectionData.getInspectionTime(),
                        inspectionData.getPillarNo(), AnalyzeStatusEnum.getNameByCode(inspectionData.getAnalysisStatus()));
                baseDataList.add(data);
            }
        }
        return baseDataList;
    }

    /**
     * 报表数据统一取自巡查数据分页查询，固定取前 {@link #REPORT_PAGE_SIZE} 条
     */
    private List<InspectionData> queryReportData() {
        InspectionDataQO query = new InspectionDataQO()
                .setPageNo(1)
                .setPageSize(REPORT_PAGE_SIZE);
        return inspectionDataService.queryAndPaging(query).getRecords();
    }

    /**
     * 把审核结果中的网络路径还原为服务器本地路径，导出画框时需要读取原图
     */
    private String toLocalImagePath(String netPath) {
        if (CharSequenceUtil.isBlank(netPath)) {
            return "";
        }
        return CharSequenceUtil.startWith(netPath, inspectHost)
                ? inspectDataPath + CharSequenceUtil.removePrefix(netPath, inspectHost)
                : netPath;
    }

    @Override
    public void generateReport(HttpServletResponse response) throws ServiceException {
        Long userId = apiUtils.currentUserId();
        AtomicInteger lastProgress = new AtomicInteger(-1);
        IntConsumer progressConsumer = progress -> ExportProgressUtil.send(userId,
                ExportProgressUtil.EXPORT_TYPE_HOME_REPORT, progress, lastProgress);
        progressConsumer.accept(0);
        List<List<Object>> baseDataList = new ArrayList<>();
        //添加导出记录
        ExportInfo exportInfo = new ExportInfo();
        exportInfo.setSourcePage("首页")
                .setExportType("最新数据报表")
                .setFileName("数据报表.xlsx")
                .setQueryApi("/smart5cApi/homePage/getGenerateReportData")
                .setExportApi("/smart5cApi/homePage/generateReport");
        try {
            List<String> title = Arrays.asList(
                    "机车号",
                    "检测时间",
                    "支柱号",
                    "分析状态",
                    "机车图"
            );
            List<InspectionData> inspectionDataList = queryReportData();
            if (CollUtil.isNotEmpty(inspectionDataList)) {
                int total = inspectionDataList.size();
                for (int i = 0; i < total; i++) {
                    InspectionData inspectionData = inspectionDataList.get(i);
                    String imagePath = toLocalImagePath(inspectionData.getLocomotiveImage());
                    if (CharSequenceUtil.isNotBlank(imagePath) && CollUtil.isNotEmpty(inspectionData.getMarkedRegions())) {
                        if(new File(imagePath).isFile()){
                            imagePath = imagePath + ";" + JSONUtil.toJsonStr(inspectionData.getMarkedRegions());
                        }else{
                            imagePath="";
                        }
                    } else {
                        if(!new File(imagePath).isFile()){
                            imagePath="";
                        }
                    }
                    List<Object> data = Lists.newArrayList(inspectionData.getLocomotiveNumber(), inspectionData.getInspectionTime(),
                            inspectionData.getPillarNo(), AnalyzeStatusEnum.getNameByCode(inspectionData.getAnalysisStatus()), imagePath);
                    baseDataList.add(data);
                    // 组装数据阶段占 0~10%（导出方法从 10% 开始上报图片处理进度）
                    progressConsumer.accept((int) ((long) (i + 1) * 10 / total));
                }
            }
            ByteCountingServletResponse countingResponse = new ByteCountingServletResponse(response);
            excelProcessUtil.exportContainPicture(title, baseDataList, true, "数据报表.xlsx", countingResponse, progressConsumer);
            exportInfo.setQuantity(String.valueOf(baseDataList.size()))
                    .setFileSize(countingResponse.getReadableSize())
                    .setExportStatus(ExportStatusEnum.SUCCESS.getCode());
            exportInfoMapper.insert(exportInfo);
            exportInfo.setTaskNo(ExportProgressUtil.EXPORT_TASK_PREFIX+DateUtil.localDateToYyyymmdd(LocalDate.now())+"-"+exportInfo.getId());
            exportInfoMapper.updateById(exportInfo);
        } catch (ServiceException e) {
            // 用户主动取消下载不记为失败，避免误报
            if (ClientAbortUtil.isClientAbort(e)) {
                throw e;
            }
            progressConsumer.accept(ExportProgressUtil.FAILED);
            exportInfo.setExportStatus(ExportStatusEnum.FAILED.getCode()).setQuantity(String.valueOf(baseDataList.size()));
            exportInfoMapper.insert(exportInfo);
            exportInfo.setTaskNo(ExportProgressUtil.EXPORT_TASK_PREFIX+DateUtil.localDateToYyyymmdd(LocalDate.now())+"-"+exportInfo.getId());
            exportInfoMapper.updateById(exportInfo);
            throw e;
        } catch (Exception e) {
            progressConsumer.accept(ExportProgressUtil.FAILED);
            exportInfo.setExportStatus(ExportStatusEnum.FAILED.getCode()).setQuantity(String.valueOf(baseDataList.size()));
            exportInfoMapper.insert(exportInfo);
            exportInfo.setTaskNo(ExportProgressUtil.EXPORT_TASK_PREFIX+DateUtil.localDateToYyyymmdd(LocalDate.now())+"-"+exportInfo.getId());
            exportInfoMapper.updateById(exportInfo);
            throw new ServiceException("生成报告失败: " + e.getMessage(), e);
        }
    }
}
