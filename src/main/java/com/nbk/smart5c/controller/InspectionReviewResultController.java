package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.InspectionReviewResult;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.mapper.InspectionReviewResultMapper;
import com.nbk.smart5c.pojo.bo.InspectionReviewResultSaveBO;
import com.nbk.smart5c.pojo.bo.InspectionReviewResultUpdateBO;
import com.nbk.smart5c.pojo.bo.MisreportReqBO;
import com.nbk.smart5c.pojo.qo.InspectionReviewResultQO;
import com.nbk.smart5c.pojo.vo.InspectionReviewResultVO;
import com.nbk.smart5c.service.InspectionReviewResultService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 巡查数据审核结果表控制层
 * @since 2026-01-29 10:35:00
 */
@Api(tags = "巡查数据审核结果表相关接口")
@RestController
@RequestMapping("/smart5cApi/inspectionReviewResult")
public class InspectionReviewResultController {

    @Autowired
    private InspectionReviewResultService<InspectionReviewResultMapper, InspectionReviewResult> inspectionReviewResultService;

    @ApiOperation("根据id查询巡查数据审核结果表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryById")
    public ResultUtil<InspectionReviewResult> queryById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") String id) {
        return ResultUtil.ok(inspectionReviewResultService.queryById(id));
    }

    @ApiOperation("根据条件分页查询巡查数据审核结果表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAndPaging")
    public ResultUtil<IPage<InspectionReviewResultVO>> queryAndPaging(InspectionReviewResultQO query) {
        return ResultUtil.ok(inspectionReviewResultService.queryAndPaging(query));
    }

    @ApiOperation("根据条件查询巡查数据审核结果表全部数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAll")
    public ResultUtil<List<InspectionReviewResultVO>> queryAll(InspectionReviewResultQO query) {
        return ResultUtil.ok(inspectionReviewResultService.queryAll(query));
    }

    @ApiOperation("根据id删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @DeleteMapping(value = "/deleteById")
    public ResultUtil<Boolean> deleteById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") Integer id) {
        return ResultUtil.OK("删除成功", inspectionReviewResultService.removeById(id));
    }

    @ApiOperation("根据id集合删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/deleteByIds")
    public ResultUtil<Boolean> deleteByIds(@RequestBody List<Integer> ids) {
        return ResultUtil.OK("删除成功", inspectionReviewResultService.removeByIds(ids));
    }

    @ApiOperation("新增数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/save")
    public ResultUtil<Boolean> save(@RequestBody InspectionReviewResultSaveBO info) {
        return ResultUtil.OK("新增成功", inspectionReviewResultService.save(info));
    }

    @ApiOperation("根据id修改数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/updateById")
    public ResultUtil<Boolean> updateById(@RequestBody InspectionReviewResultUpdateBO info) {
        return ResultUtil.OK("修改成功", inspectionReviewResultService.updateById(info));
    }

    @ApiOperation("导入数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/analysis")
    public ResultUtil<Void> analysis(@RequestPart("file") MultipartFile file) {
        inspectionReviewResultService.analysis(file);
        return ResultUtil.ok("导入成功");
    }

    @ApiOperation("导出数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/export")
    public ResultUtil<Void> export(HttpServletResponse response, InspectionReviewResultQO query) {
        inspectionReviewResultService.export(query, response);
        return ResultUtil.ok("导出成功");
    }

    @ApiOperation("误报提交")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/misreport")
    public ResultUtil<Boolean> misreport(@RequestBody MisreportReqBO misreportReqBO) throws ServiceException {
        return ResultUtil.OK("提交成功", inspectionReviewResultService.misreport(misreportReqBO));
    }
}
