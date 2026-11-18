package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nbk.smart5c.common.enums.PositionEnum;
import com.nbk.smart5c.common.utils.HttpClientUtils;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.UserInfo;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.mapper.UserInfoMapper;
import com.nbk.smart5c.pojo.bo.UserInfoSaveBO;
import com.nbk.smart5c.pojo.bo.UserInfoUpdateBO;
import com.nbk.smart5c.pojo.bo.UserUpdatePwdBO;
import com.nbk.smart5c.pojo.qo.UserInfoQO;
import com.nbk.smart5c.pojo.qo.UserLoginParam;
import com.nbk.smart5c.pojo.vo.UserInfoVO;
import com.nbk.smart5c.service.UserInfoService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 用户信息表控制层
 * @since 2026-01-27 15:25:38
 */
@Api(tags = "用户信息表相关接口")
@RestController
@Slf4j
@RequestMapping("/smart5cApi/userInfo")
public class UserInfoController {

    @Autowired
    private UserInfoService<UserInfoMapper, UserInfo> userInfoService;

    @ApiOperation("根据id查询用户信息表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryById")
    public ResultUtil<UserInfo> queryById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") String id) {
        return ResultUtil.ok(userInfoService.queryById(id));
    }

    @ApiOperation("用户登录")
    @PostMapping(value = "/login")
    public ResultUtil login(@RequestBody @Valid UserLoginParam loginParam) {
        return userInfoService.login(loginParam);
    }

    @ApiOperation("根据条件分页查询用户信息表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAndPaging")
    public ResultUtil<IPage<UserInfo>> queryAndPaging(UserInfoQO query) {
        return ResultUtil.ok(userInfoService.queryAndPaging(query));
    }

    @ApiOperation("根据条件查询用户信息表全部数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAll")
    public ResultUtil<List<UserInfoVO>> queryAll(UserInfoQO query) {
        return ResultUtil.ok(userInfoService.queryAll(query));
    }

    @ApiOperation("根据id删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @DeleteMapping(value = "/deleteById")
    public ResultUtil<Boolean> deleteById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") Integer id) {
        return ResultUtil.OK("删除成功", userInfoService.removeById(id));
    }

    @ApiOperation("根据id集合删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/deleteByIds")
    public ResultUtil<Boolean> deleteByIds(@RequestBody List<Integer> ids) {
        return ResultUtil.OK("删除成功", userInfoService.removeByIds(ids));
    }

    @ApiOperation("新增数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/save")
    public ResultUtil<Boolean> save(@RequestBody UserInfoSaveBO info) {
        return ResultUtil.OK("新增成功", userInfoService.save(info));
    }

    @ApiOperation("根据id修改数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/updateById")
    public ResultUtil<Boolean> updateById(@RequestBody UserInfoUpdateBO info) {
        return ResultUtil.OK("修改成功", userInfoService.updateById(info));
    }

    @ApiOperation("导入数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/analysis")
    public ResultUtil<Void> analysis(@RequestPart("file") MultipartFile file) {
        userInfoService.analysis(file);
        return ResultUtil.ok("导入成功");
    }

    @ApiOperation("导出数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/export")
    public ResultUtil<Void> export(HttpServletResponse response, UserInfoQO query) {
        userInfoService.export(query, response);
        return ResultUtil.ok("导出成功");
    }

    @ApiOperation("导出模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/exportTemplate")
    public ResultUtil<Void> exportTemplate(HttpServletResponse response) {
        userInfoService.exportTemplate(response);
        return ResultUtil.ok("导出成功");
    }

    @ApiOperation("职位下位列表")
    @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header")
    @GetMapping(value = "/getPositionList")
    public ResultUtil<List<Map<String, Object>>> getPositionList() {
        return ResultUtil.OK(PositionEnum.getPositionList());
    }

    @ApiOperation("获取用户请求IP")
    @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header")
    @GetMapping(value = "/getUserReqIp")
    public ResultUtil<String> getPositionList(HttpServletRequest request) {
        String clientIP = HttpClientUtils.getClientIP(request);
        log.info("前端用户请求IP: {}", clientIP);
        return ResultUtil.OK(clientIP);
    }

    @ApiOperation("用户修改密码")
    @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header")
    @PostMapping(value = "/updatePassword")
    public ResultUtil<Boolean> updatePassword(@RequestBody UserUpdatePwdBO userUpdatePwdBO) {
        try {
            return ResultUtil.OK("修改密码成功", userInfoService.updatePassword(userUpdatePwdBO));
        } catch (ServiceException e) {
            return ResultUtil.error("修改密码失败:" + e.getLocalizedMessage());
        }
    }
}
