package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.Bureau;
import com.nbk.smart5c.mapper.BureauMapper;
import com.nbk.smart5c.pojo.bo.BureauSaveBO;
import com.nbk.smart5c.pojo.bo.BureauUpdateBO;
import com.nbk.smart5c.pojo.qo.BureauQO;
import com.nbk.smart5c.pojo.vo.BureauVO;
import com.nbk.smart5c.service.BureauService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 供电部信息表控制层
 * @since 2026-01-30 17:29:19
 */
@Api(tags = "供电部信息表相关接口")
@RestController
@RequestMapping("/smart5cApi/bureau")
public class BureauController {

    @Autowired
    private BureauService<BureauMapper, Bureau> bureauService;

    @ApiOperation("根据id查询供电部信息表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryById")
    public ResultUtil<Bureau> queryById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") String id) {
        return ResultUtil.ok(bureauService.queryById(id));
    }

    @ApiOperation("根据条件分页查询供电部信息表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAndPaging")
    public ResultUtil<IPage<BureauVO>> queryAndPaging(BureauQO query) {
        return ResultUtil.ok(bureauService.queryAndPaging(query));
    }

    @ApiOperation("根据条件查询供电部信息表全部数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAll")
    public ResultUtil<List<BureauVO>> queryAll(BureauQO query) {
        return ResultUtil.ok(bureauService.queryAll(query));
    }

    @ApiOperation("根据id删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @DeleteMapping(value = "/deleteById")
    public ResultUtil<Boolean> deleteById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") Integer id) {
        return ResultUtil.OK("删除成功", bureauService.removeById(id));
    }

    @ApiOperation("根据id集合删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/deleteByIds")
    public ResultUtil<Boolean> deleteByIds(@RequestBody List<Integer> ids) {
        return ResultUtil.OK("删除成功", bureauService.removeByIds(ids));
    }

    @ApiOperation("新增数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/save")
    public ResultUtil<Boolean> save(@RequestBody BureauSaveBO info) {
        return ResultUtil.OK("新增成功", bureauService.save(info));
    }

    @ApiOperation("根据id修改数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/updateById")
    public ResultUtil<Boolean> updateById(@RequestBody BureauUpdateBO info) {
        return ResultUtil.OK("修改成功", bureauService.updateById(info));
    }
}
