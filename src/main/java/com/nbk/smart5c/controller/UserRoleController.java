package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.UserRole;
import com.nbk.smart5c.mapper.UserRoleMapper;
import com.nbk.smart5c.pojo.bo.UserRoleSaveBO;
import com.nbk.smart5c.pojo.bo.UserRoleUpdateBO;
import com.nbk.smart5c.pojo.qo.UserRoleQO;
import com.nbk.smart5c.pojo.vo.UserRoleVO;
import com.nbk.smart5c.service.UserRoleService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 用户角色信息表控制层
 * @since 2026-01-27 15:25:38
 */
@Api(tags = "用户角色信息表相关接口")
@RestController
@RequestMapping("/smart5cApi/userRole")
public class UserRoleController {

    @Autowired
    private UserRoleService<UserRoleMapper, UserRole> userRoleService;

    @ApiOperation("根据id查询用户角色信息表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryById")
    public ResultUtil<UserRole> queryById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") String id) {
        return ResultUtil.ok(userRoleService.queryById(id));
    }

    @ApiOperation("根据条件分页查询用户角色信息表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAndPaging")
    public ResultUtil<IPage<UserRoleVO>> queryAndPaging(UserRoleQO query) {
        return ResultUtil.ok(userRoleService.queryAndPaging(query));
    }

    @ApiOperation("根据条件查询用户角色信息表全部数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAll")
    public ResultUtil<List<UserRoleVO>> queryAll(UserRoleQO query) {
        return ResultUtil.ok(userRoleService.queryAll(query));
    }

    @ApiOperation("根据id删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @DeleteMapping(value = "/deleteById")
    public ResultUtil<Boolean> deleteById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") Integer id) {
        return ResultUtil.OK("删除成功", userRoleService.removeById(id));
    }

    @ApiOperation("根据id集合删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/deleteByIds")
    public ResultUtil<Boolean> deleteByIds(@RequestBody List<Integer> ids) {
        return ResultUtil.OK("删除成功", userRoleService.removeByIds(ids));
    }

    @ApiOperation("新增数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/save")
    public ResultUtil<Boolean> save(@RequestBody UserRoleSaveBO info) {
        return ResultUtil.OK("新增成功", userRoleService.save(info));
    }

    @ApiOperation("根据id修改数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/updateById")
    public ResultUtil<Boolean> updateById(@RequestBody UserRoleUpdateBO info) {
        return ResultUtil.OK("修改成功", userRoleService.updateById(info));
    }

    @ApiOperation("导入数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/analysis")
    public ResultUtil<Void> analysis(@RequestPart("file") MultipartFile file) {
        userRoleService.analysis(file);
        return ResultUtil.ok("导入成功");
    }

    @ApiOperation("导出数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/export")
    public ResultUtil<Void> export(HttpServletResponse response, UserRoleQO query) {
        userRoleService.export(query, response);
        return ResultUtil.ok("导出成功");
    }

    @ApiOperation("导出模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/exportTemplate")
    public ResultUtil<Void> exportTemplate(HttpServletResponse response) {
        userRoleService.exportTemplate(response);
        return ResultUtil.ok("导出成功");
    }
}
