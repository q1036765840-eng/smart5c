package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.pojo.vo.LabelValueVO;
import com.nbk.smart5c.pojo.vo.LatestAlarmListVO;
import com.nbk.smart5c.pojo.vo.TodayCheckDataVO;
import com.nbk.smart5c.service.HomePageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 首页相关接口
 * @since 2026-01-27 15:25:38
 */
@Api(tags = "首页相关接口")
@RestController
@RequestMapping("/smart5cApi/homePage")
public class HomePageController {

    @Autowired
    private HomePageService homePageService;

    @ApiOperation("今日待处理报警数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/countAlarmsToday")
    public ResultUtil<Long> countAlarmsToday() {
        return ResultUtil.ok(homePageService.countAlarmsToday());
    }

    @ApiOperation("最近一周处理报警数变化趋势")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/lastWeekAlarmsTrend")
    public ResultUtil<List<LabelValueVO>> lastWeekAlarmsTrend() {
        return ResultUtil.ok(homePageService.lastWeekAlarmsTrend());
    }

    @ApiOperation("今日检测数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/todayCheckDataCount")
    public ResultUtil<TodayCheckDataVO> todayCheckDataCount() {
        return ResultUtil.ok(homePageService.todayCheckDataCount());
    }

    @ApiOperation("最新报警列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/lastAlarmPaging")
    public ResultUtil<Page<LatestAlarmListVO>> lastAlarmPaging(@RequestParam(required = false, defaultValue = "1") Integer pageNo, @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return ResultUtil.ok(homePageService.lastAlarmPaging(pageNo, pageSize));
    }

    @ApiOperation("生成报告")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/generateReport", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void generateReport(HttpServletResponse response) throws ServiceException {
        homePageService.generateReport(response);
    }

    @ApiOperation("生成报告数据获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/getGenerateReportData")
    public ResultUtil<List<List<Object>>> getGenerateReportData() throws ServiceException {
        return ResultUtil.ok(homePageService.getGenerateReportData());
    }
}
