package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nbk.smart5c.common.enums.DefectTypeEnum;
import com.nbk.smart5c.common.enums.SdgStatusEnum;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.InspectionData;
import com.nbk.smart5c.entity.InspectionReviewResult;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.mapper.InspectionDataMapper;
import com.nbk.smart5c.mapper.InspectionReviewResultMapper;
import com.nbk.smart5c.pojo.bo.*;
import com.nbk.smart5c.pojo.qo.InspectionDataQO;
import com.nbk.smart5c.pojo.vo.InspectionDataVO;
import com.nbk.smart5c.pojo.vo.InspectionReviewDataVO;
import com.nbk.smart5c.service.InspectionDataService;
import com.nbk.smart5c.service.InspectionReviewResultService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 巡查数据表控制层
 * @since 2026-01-27 15:25:38
 */
@Api(tags = "巡查数据表相关接口")
@RestController
@RequestMapping("/smart5cApi/inspectionData")
public class InspectionDataController {

    @Autowired
    private InspectionDataService<InspectionDataMapper, InspectionData> inspectionDataService;

    @Autowired
    private InspectionReviewResultService<InspectionReviewResultMapper, InspectionReviewResult> inspectionReviewResultService;

    @ApiOperation("根据id查询巡查数据表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryById")
    public ResultUtil<InspectionData> queryById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") String id) {
        return ResultUtil.ok(inspectionDataService.queryById(id));
    }

    @ApiOperation("获取巡查数据状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/getConfirmStatusById")
    public ResultUtil<Integer> getConfirmStatusById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") Long id) {
        return ResultUtil.ok(inspectionDataService.getConfirmStatusById(id));
    }

    @ApiOperation("根据条件分页查询巡查数据表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAndPaging")
    public ResultUtil<IPage<InspectionData>> queryAndPaging(InspectionDataQO query) {
        return ResultUtil.ok(inspectionDataService.queryAndPaging(query));
    }

    @ApiOperation("根据条件查询巡查数据表全部数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAll")
    public ResultUtil<List<InspectionDataVO>> queryAll(InspectionDataQO query) {
        return ResultUtil.ok(inspectionDataService.queryAll(query));
    }

    @ApiOperation("根据id删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @DeleteMapping(value = "/deleteById")
    public ResultUtil<Boolean> deleteById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") Long id) {
        return ResultUtil.OK("删除成功", inspectionDataService.deleteById(id));
    }

    @ApiOperation("根据id集合删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/deleteByIds")
    public ResultUtil<Boolean> deleteByIds(@RequestBody List<Long> ids) throws ServiceException {
        return ResultUtil.OK("删除成功", inspectionDataService.deleteByIds(ids));
    }

    @ApiOperation("新增数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/save")
    public ResultUtil<Boolean> save(@RequestBody InspectionDataSaveBO info) {
        return ResultUtil.OK("新增成功", inspectionDataService.save(info));
    }

    @ApiOperation("根据id修改数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/updateById")
    public ResultUtil<Boolean> updateById(@RequestBody InspectionDataUpdateBO info) {
        return ResultUtil.OK("修改成功", inspectionDataService.updateById(info));
    }

    @ApiOperation("导入数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/analysis")
    public ResultUtil<Void> analysis(@RequestPart("file") MultipartFile file) {
        inspectionDataService.analysis(file);
        return ResultUtil.ok("导入成功");
    }

    @ApiOperation("导出数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/export")
    public ResultUtil<Void> export(HttpServletResponse response, InspectionDataQO query) {
        inspectionDataService.export(query, response);
        return ResultUtil.ok("导出成功");
    }

    @ApiOperation("下载模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/exportTemplate")
    public ResultUtil<Void> exportTemplate(HttpServletResponse response) {
        inspectionDataService.exportTemplate(response);
        return ResultUtil.ok("导出成功");
    }

    @ApiOperation("获取要导出的巡查数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header"),
    })
    @GetMapping(value = "/getExportInspectionData")
    public ResultUtil<List<List<Object>>> getExportInspectionData(@RequestParam("ids") List<Long> ids, HttpServletResponse response) throws ServiceException {
        return ResultUtil.ok(inspectionDataService.getExportInspectionData(ids, response));
    }

    @ApiOperation("导出巡查数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/exportInspectionData", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportInspectionData(@RequestParam("ids") List<Long> ids, HttpServletResponse response) throws ServiceException {
        inspectionDataService.exportInspectionData(ids, response);
    }

    @ApiOperation("设备巡查信息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "deviceId", value = "设备id", required = false, dataType = "java.lang.Long", paramType = "query")
    })
    @GetMapping(value = "/reviewInfoList")
    public ResultUtil<List<InspectionReviewDataVO>> reviewInfoList(@RequestParam("deviceId") Long deviceId) {
        List<InspectionReviewDataVO> inspectDataList = inspectionDataService.inspectionDataList(deviceId);
        return ResultUtil.ok(inspectDataList);
    }

    @ApiOperation("设备巡查数据审核保存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/reviewInfoSave")
    public ResultUtil<Boolean> reviewInfoSave(@RequestBody InspectionDataReviewBO inspectionDataReviewBO) throws ServiceException {
        Boolean result = inspectionReviewResultService.saveReviewData(inspectionDataReviewBO);
        return ResultUtil.ok(result);
    }

    @ApiOperation("解析数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/parseData")
    public ResultUtil<Boolean> parseData(String filePath) throws ServiceException {
        Boolean result = inspectionDataService.parseData(null, filePath);
        return ResultUtil.ok(result);
    }

    @ApiOperation("巡查数据审核详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "inspectionId", value = "巡查数据Id", required = false, dataType = "java.lang.Long", paramType = "query")
    })
    @GetMapping(value = "/getReviewData")
    public ResultUtil<InspectionData> getReviewData(Long inspectionId) throws ServiceException {
        InspectionData inspectionData = inspectionDataService.getReviewData(inspectionId);
        return ResultUtil.ok(inspectionData);
    }

    @ApiOperation("验证图片有效性")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header"),
    })
    @GetMapping(value = "/validImage")
    public ResultUtil<Boolean> validImage(String imagePath) {
        Boolean result = inspectionDataService.validImage(imagePath);
        return ResultUtil.ok(result);
    }

    @ApiOperation("分析图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header"),
    })
    @PostMapping(value = "/startAnalysis")
    public ResultUtil<Boolean> startAnalysis(@RequestBody ManualReviewParamBO analysisParam) {
        Boolean result = inspectionDataService.startAnalysis(analysisParam.getIdList());
        return ResultUtil.ok(result);
    }

    @ApiOperation("获取解析进度")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header"),
    })
    @GetMapping(value = "/getParseProcess")
    public ResultUtil<String> getParseProcess() {
        String result = inspectionDataService.getParseProcess();
        return ResultUtil.ok(result);
    }

    @ApiOperation("获取算法分析进度")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header"),
    })
    @GetMapping(value = "/getAnalysisProcess")
    public ResultUtil<String> getAnalysisProcess(Long inspectionId) {
        String result = inspectionDataService.getAnalysisProcess(inspectionId);
        return ResultUtil.ok(result);
    }

    @ApiOperation("全部分析")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header"),
    })
    @GetMapping(value = "/analysisAll")
    public ResultUtil<Boolean> analysisAll() {
        Boolean result = inspectionDataService.analysisAll();
        return ResultUtil.ok(result);
    }

    @ApiOperation("异物测试")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header"),
    })
    @PostMapping(value = "/foreignObjectTest")
    public ResultUtil<Boolean> foreignObjectTest(@RequestBody AiAnalysisRespDataBO aiAnalysisRespBO) {
        InspectionData inspectionData = new InspectionData();
        inspectionData.setId(2029L);
        inspectionDataService.foreignObjectProcess(aiAnalysisRespBO, inspectionData, "/nbk/app/smart5c/data/上海高铁段/5C/5C数据2026.01.08/杭黄线/2025-9/20250921150504_杭黄线_三阳站_2291_K1_000000/20250921150504_000000_1_1.jpg");
        return ResultUtil.ok();
    }

    @ApiOperation("缺陷类型枚举列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header"),
    })
    @GetMapping(value = "/defectTypeList")
    public ResultUtil<List<Map<String, Object>>> defectTypeList() {
        return ResultUtil.ok(DefectTypeEnum.getDefectTypeList());
    }

    @ApiOperation("受电工状态列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/sdgStatusList")
    public ResultUtil<List<Map<String, Object>>> sdgStatusList() {
        return ResultUtil.ok(SdgStatusEnum.getSdgStatusList());
    }
}
