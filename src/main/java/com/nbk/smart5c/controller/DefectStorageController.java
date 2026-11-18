package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nbk.smart5c.common.enums.DefectReviewStatusEnum;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.DefectStorage;
import com.nbk.smart5c.mapper.DefectStorageMapper;
import com.nbk.smart5c.pojo.bo.DefectStorageReviewBO;
import com.nbk.smart5c.pojo.bo.DefectStorageSaveBO;
import com.nbk.smart5c.pojo.bo.DefectStorageUpdateBO;
import com.nbk.smart5c.pojo.qo.DefectStorageDetailQO;
import com.nbk.smart5c.pojo.qo.DefectStorageQO;
import com.nbk.smart5c.pojo.vo.DefectStorageDetailShowVO;
import com.nbk.smart5c.pojo.vo.DefectStorageVO;
import com.nbk.smart5c.service.DefectStorageService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 缺陷库表控制层
 * @since 2026-04-07 16:41:49
 */
@Api(tags = "缺陷库表相关接口")
@RestController
@RequestMapping("/smart5cApi/defectStorage")
public class DefectStorageController {

    @Autowired
    private DefectStorageService<DefectStorageMapper, DefectStorage> defectStorageService;

    @ApiOperation("根据id查询缺陷库表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryById")
    public ResultUtil<DefectStorage> queryById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") String id) {
        return ResultUtil.ok(defectStorageService.queryById(id));
    }

    @ApiOperation("根据条件分页查询缺陷库表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAndPaging")
    public ResultUtil<IPage<DefectStorageVO>> queryAndPaging(DefectStorageQO query) {
        return ResultUtil.ok(defectStorageService.queryAndPaging(query));
    }

    @ApiOperation("根据条件查询缺陷库表全部数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAll")
    public ResultUtil<List<DefectStorageVO>> queryAll(DefectStorageQO query) {
        return ResultUtil.ok(defectStorageService.queryAll(query));
    }

    @ApiOperation("根据id删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @DeleteMapping(value = "/deleteById")
    public ResultUtil<Boolean> deleteById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") Long id) {
        return ResultUtil.OK("删除成功", defectStorageService.deleteById(id));
    }

    @ApiOperation("根据id集合删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/deleteByIds")
    public ResultUtil<Boolean> deleteByIds(@RequestBody List<Long> ids) {
        return ResultUtil.OK("删除成功", defectStorageService.deleteByIds(ids));
    }

    @ApiOperation("新增数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/save")
    public ResultUtil<Boolean> save(@RequestBody DefectStorageSaveBO info) {
        return ResultUtil.OK("新增成功", defectStorageService.save(info));
    }

    @ApiOperation("根据id修改数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/updateById")
    public ResultUtil<Boolean> updateById(@RequestBody DefectStorageUpdateBO info) {
        return ResultUtil.OK("修改成功", defectStorageService.updateById(info));
    }

    @ApiOperation("导出第一页数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/exportQuery")
    public void exportQuery(HttpServletResponse response, DefectStorageQO query) {
        defectStorageService.export(query, response);
    }

    @ApiOperation("获取勾选导出的数据列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "idLIst", value = "主键集合", required = true, dataType = "java.lang.Long", allowMultiple = true, paramType = "query"),
            @ApiImplicitParam(name = "exportType", value = "导出类型(0:缺陷管理,1:缺陷库)", required = true, dataType = "java.lang.String", paramType = "query"),
    })
    @GetMapping(value = "/getExportChecked")
    public ResultUtil<List<List<Object>>> getExportChecked(@RequestParam List<Long> idLIst, @RequestParam String exportType) {
        return ResultUtil.ok(defectStorageService.getExportChecked(idLIst, exportType));
    }

    @ApiOperation("勾选导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "idLIst", value = "主键集合", required = true, dataType = "java.lang.Long", allowMultiple = true, paramType = "query"),
            @ApiImplicitParam(name = "exportType", value = "导出类型(0:缺陷管理,1:缺陷库)", required = true, dataType = "java.lang.String", paramType = "query"),
    })
    @GetMapping(value = "/exportChecked")
    public void exportChecked(@RequestParam List<Long> idLIst, @RequestParam String exportType) {
        defectStorageService.exportChecked(idLIst, exportType);
    }

    @ApiOperation("缺陷审核状态列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/getReviewStatusList")
    public ResultUtil<List<Map<String, Object>>> getReviewStatusList() {
        return ResultUtil.ok(DefectReviewStatusEnum.getReviewStatusList());
    }

    @ApiOperation("缺陷入库审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/defectReview")
    public ResultUtil<Boolean> defectReview(@RequestBody DefectStorageReviewBO defectStorageReviewBO) {
        Boolean result = defectStorageService.defectReview(defectStorageReviewBO);
        return ResultUtil.ok(result);
    }

    @ApiOperation("缺陷类型下拉列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/defectTypeList")
    public ResultUtil<List<String>> defectTypeList(@RequestParam(value = "taskId", required = false) Long taskId, @RequestParam(value = "reviewStatusList", required = false) List<Integer> reviewStatusList) {
        List<String> result = defectStorageService.defectTypeList(taskId, reviewStatusList);
        return ResultUtil.ok(result);
    }

    @ApiOperation("缺陷库详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/defectStorageDetail")
    public ResultUtil<DefectStorageDetailShowVO> defectStorageDetail(DefectStorageDetailQO defectStorageDetailQO) {
        DefectStorageDetailShowVO result = defectStorageService.defectStorageDetail(defectStorageDetailQO);
        return ResultUtil.ok(result);
    }

    @ApiOperation("下载缺陷图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/downloadDefectImage")
    public void downloadDefectImage(Long id, Integer hasDefect, HttpServletResponse response) {
        defectStorageService.downloadDefectImage(id, hasDefect, response);
    }
}
