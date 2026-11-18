package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.ExportInfo;
import com.nbk.smart5c.mapper.ExportInfoMapper;
import com.nbk.smart5c.pojo.bo.ExportInfoSaveBO;
import com.nbk.smart5c.pojo.bo.ExportInfoUpdateBO;
import com.nbk.smart5c.pojo.qo.ExportInfoQO;
import com.nbk.smart5c.pojo.vo.ExportInfoVO;
import com.nbk.smart5c.service.ExportInfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 系统导出信息表控制层
 * @since 2026-10-27
 */
@Api(tags = "系统导出信息表相关接口")
@RestController
@RequestMapping("/smart5cApi/exportInfo")
public class ExportInfoController {

    @Autowired
    private ExportInfoService<ExportInfoMapper, ExportInfo> exportInfoService;

    @ApiOperation("根据id查询系统导出信息表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryById")
    public ResultUtil<ExportInfo> queryById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") String id) {
        return ResultUtil.ok(exportInfoService.queryById(id));
    }

    @ApiOperation("根据条件分页查询系统导出信息表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAndPaging")
    public ResultUtil<IPage<ExportInfoVO>> queryAndPaging(ExportInfoQO query) {
        return ResultUtil.ok(exportInfoService.queryAndPaging(query));
    }

    @ApiOperation("根据条件查询系统导出信息表全部数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAll")
    public ResultUtil<List<ExportInfoVO>> queryAll(ExportInfoQO query) {
        return ResultUtil.ok(exportInfoService.queryAll(query));
    }

    @ApiOperation("根据id删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @DeleteMapping(value = "/deleteById")
    public ResultUtil<Boolean> deleteById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") Long id) {
        return ResultUtil.OK("删除成功", exportInfoService.removeById(id));
    }

    @ApiOperation("根据id集合删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/deleteByIds")
    public ResultUtil<Boolean> deleteByIds(@RequestBody List<Long> ids) {
        return ResultUtil.OK("删除成功", exportInfoService.removeByIds(ids));
    }

    @ApiOperation("新增数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/save")
    public ResultUtil<Boolean> save(@RequestBody ExportInfoSaveBO info) {
        return ResultUtil.OK("新增成功", exportInfoService.save(info));
    }

    @ApiOperation("根据id修改数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/updateById")
    public ResultUtil<Boolean> updateById(@RequestBody ExportInfoUpdateBO info) {
        return ResultUtil.OK("修改成功", exportInfoService.updateById(info));
    }
}
