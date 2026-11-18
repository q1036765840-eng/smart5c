package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.Menu;
import com.nbk.smart5c.mapper.MenuMapper;
import com.nbk.smart5c.pojo.bo.MenuSaveBO;
import com.nbk.smart5c.pojo.bo.MenuUpdateBO;
import com.nbk.smart5c.pojo.qo.MenuQO;
import com.nbk.smart5c.pojo.vo.MenuVO;
import com.nbk.smart5c.service.MenuService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 菜单权限表控制层
 * @since 2026-01-27 20:44:51
 */
@Api(tags = "菜单权限表相关接口")
@RestController
@RequestMapping("/smart5cApi/menu")
public class MenuController {

    @Autowired
    private MenuService<MenuMapper, Menu> menuService;

    @ApiOperation("根据id查询菜单权限表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryById")
    public ResultUtil<Menu> queryById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") String id) {
        return ResultUtil.ok(menuService.queryById(id));
    }

    @ApiOperation("根据条件分页查询菜单权限表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAndPaging")
    public ResultUtil<IPage<MenuVO>> queryAndPaging(MenuQO query) {
        return ResultUtil.ok(menuService.queryAndPaging(query));
    }

    @ApiOperation("根据条件查询菜单权限表全部数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAll")
    public ResultUtil<List<MenuVO>> queryAll(MenuQO query) {
        return ResultUtil.ok(menuService.queryAll(query));
    }

    @ApiOperation("根据id删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @DeleteMapping(value = "/deleteById")
    public ResultUtil<Boolean> deleteById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") Integer id) {
        return ResultUtil.OK("删除成功", menuService.removeById(id));
    }

    @ApiOperation("根据id集合删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/deleteByIds")
    public ResultUtil<Boolean> deleteByIds(@RequestBody List<Integer> ids) {
        return ResultUtil.OK("删除成功", menuService.removeByIds(ids));
    }

    @ApiOperation("新增数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/save")
    public ResultUtil<Boolean> save(@RequestBody MenuSaveBO info) {
        return ResultUtil.OK("新增成功", menuService.save(info));
    }

    @ApiOperation("根据id修改数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/updateById")
    public ResultUtil<Boolean> updateById(@RequestBody MenuUpdateBO info) {
        return ResultUtil.OK("修改成功", menuService.updateById(info));
    }

    @ApiOperation("导入数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/analysis")
    public ResultUtil<Void> analysis(@RequestPart("file") MultipartFile file) {
        menuService.analysis(file);
        return ResultUtil.ok("导入成功");
    }

    @ApiOperation("导出数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/export")
    public ResultUtil<Void> export(HttpServletResponse response, MenuQO query) {
        menuService.export(query, response);
        return ResultUtil.ok("导出成功");
    }

    @ApiOperation("导出模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/exportTemplate")
    public ResultUtil<Void> exportTemplate(HttpServletResponse response) {
        menuService.exportTemplate(response);
        return ResultUtil.ok("导出成功");
    }
}
