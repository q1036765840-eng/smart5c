package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.Department;
import com.nbk.smart5c.mapper.DepartmentMapper;
import com.nbk.smart5c.pojo.bo.DepartmentSaveBO;
import com.nbk.smart5c.pojo.bo.DepartmentUpdateBO;
import com.nbk.smart5c.pojo.qo.DepartmentQO;
import com.nbk.smart5c.pojo.vo.DepartTreeVO;
import com.nbk.smart5c.pojo.vo.DepartmentVO;
import com.nbk.smart5c.service.DepartmentService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 部门信息表控制层
 * @since 2026-05-16 17:20:28
 */
@Api(tags = "部门信息表相关接口")
@RestController
@RequestMapping("/smart5cApi/department")
public class DepartmentController {

    @Autowired
    private DepartmentService<DepartmentMapper, Department> departmentService;

    @ApiOperation("根据id查询部门信息表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryById")
    public ResultUtil<Department> queryById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") String id) {
        return ResultUtil.ok(departmentService.queryById(id));
    }

    @ApiOperation("根据条件分页查询部门信息表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAndPaging")
    public ResultUtil<IPage<DepartmentVO>> queryAndPaging(DepartmentQO query) {
        return ResultUtil.ok(departmentService.queryAndPaging(query));
    }

    @ApiOperation("根据条件查询部门信息表全部数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAll")
    public ResultUtil<List<DepartmentVO>> queryAll(DepartmentQO query) {
        return ResultUtil.ok(departmentService.queryAll(query));
    }

    @ApiOperation("根据id删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @DeleteMapping(value = "/deleteById")
    public ResultUtil<Boolean> deleteById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") Integer id) {
        return ResultUtil.OK("删除成功", departmentService.removeById(id));
    }

    @ApiOperation("根据id集合删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/deleteByIds")
    public ResultUtil<Boolean> deleteByIds(@RequestBody List<Integer> ids) {
        return ResultUtil.OK("删除成功", departmentService.removeByIds(ids));
    }

    @ApiOperation("新增数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/save")
    public ResultUtil<Boolean> save(@RequestBody DepartmentSaveBO info) {
        return ResultUtil.OK("新增成功", departmentService.save(info));
    }

    @ApiOperation("根据id修改数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/updateById")
    public ResultUtil<Boolean> updateById(@RequestBody DepartmentUpdateBO info) {
        return ResultUtil.OK("修改成功", departmentService.updateById(info));
    }

    @ApiOperation("获取部门层级树")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/getDepartmentTree")
    public ResultUtil<List<DepartTreeVO>> getDepartmentTree(DepartmentQO query) {
        return ResultUtil.ok(departmentService.getDepartmentTree(query));
    }
}
