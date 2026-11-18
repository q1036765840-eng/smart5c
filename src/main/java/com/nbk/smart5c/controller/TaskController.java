package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.common.enums.TaskLevelEnum;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.Task;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.mapper.TaskMapper;
import com.nbk.smart5c.pojo.bo.*;
import com.nbk.smart5c.pojo.qo.TaskQO;
import com.nbk.smart5c.pojo.qo.TaskReviewQO;
import com.nbk.smart5c.pojo.vo.*;
import com.nbk.smart5c.service.TaskService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 任务表控制层
 * @since 2026-04-07 16:41:49
 */
@Api(tags = "任务表相关接口")
@RestController
@RequestMapping("/smart5cApi/task")
public class TaskController {

    @Autowired
    private TaskService<TaskMapper, Task> taskService;

    @ApiOperation("获取任务详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryById")
    public ResultUtil<TaskEditVO> queryById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") Long id) {
        return ResultUtil.ok(taskService.queryById(id));
    }

    @ApiOperation("根据条件分页查询任务表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAndPaging")
    public ResultUtil<IPage<TaskVO>> queryAndPaging(TaskQO query) {
        return ResultUtil.ok(taskService.queryAndPaging(query));
    }

    @ApiOperation("根据条件查询任务表全部数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/queryAll")
    public ResultUtil<List<TaskVO>> queryAll(TaskQO query) {
        return ResultUtil.ok(taskService.queryAll(query));
    }

    @ApiOperation("根据id删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @DeleteMapping(value = "/deleteById")
    public ResultUtil<Boolean> deleteById(
            @ApiParam(name = "id", value = "主键") @RequestParam("id") Long id) throws ServiceException {
        return ResultUtil.OK("删除成功", taskService.deleteById(id));
    }

    @ApiOperation("根据id集合删除数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/deleteByIds")
    public ResultUtil<Boolean> deleteByIds(@RequestBody List<Long> ids) throws ServiceException {
        return ResultUtil.OK("删除成功", taskService.deleteByIds(ids));
    }

    @ApiOperation("任务创建")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/save")
    public ResultUtil<Boolean> save(@RequestBody TaskSaveBO info) throws ServiceException {
        return ResultUtil.OK("创建成功", taskService.save(info));
    }

    @ApiOperation("外部推数据：实时分析")
    @ApiImplicitParams({
    })
    @PostMapping(value = "/autoCreate")
    public ResultUtil<Boolean> autoCreate(@RequestBody TaskPushSaveBO info) throws ServiceException {
        return ResultUtil.OK("创建成功", taskService.autoCreate(info));
    }

    @ApiOperation("任务修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/updateById")
    public ResultUtil<Boolean> updateById(@RequestBody TaskUpdateBO info) {
        return ResultUtil.OK("修改成功", taskService.updateById(info));
    }

    @ApiOperation("导入数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/analysis")
    public ResultUtil<Void> analysis(@RequestPart("file") MultipartFile file) {
        taskService.analysis(file);
        return ResultUtil.ok("导入成功");
    }

    @ApiOperation("导出数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/export")
    public ResultUtil<Void> export(HttpServletResponse response, TaskQO query) {
        taskService.export(query, response);
        return ResultUtil.ok("导出成功");
    }

    @ApiOperation("导出模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/exportTemplate")
    public ResultUtil<Void> exportTemplate(HttpServletResponse response) {
        taskService.exportTemplate(response);
        return ResultUtil.ok("导出成功");
    }

    @ApiOperation("开始分析")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "taskId", value = "任务编号", required = false, dataType = "java.lang.Long", paramType = "query")
    })
    @GetMapping(value = "/startAnalysis")
    public ResultUtil<Boolean> startAnalysis(Long taskId) throws ServiceException {
        Boolean result = taskService.startAnalysis(taskId);
        return ResultUtil.ok(result);
    }

    @ApiOperation("暂停分析")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "taskId", value = "任务编号", required = false, dataType = "java.lang.Long", paramType = "query")
    })
    @GetMapping(value = "/pauseAnalysis")
    public ResultUtil<Boolean> pauseAnalysis(Long taskId) {
        Boolean result = taskService.pauseAnalysis(taskId);
        return ResultUtil.ok(result);
    }

    @ApiOperation("获取当前文件夹下的子文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "filePath", value = "文件路径", required = false, dataType = "java.lang.String", paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "页号", required = false, dataType = "java.lang.Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "java.lang.Integer", paramType = "query")
    })
    @GetMapping(value = "/listChildFile")
    public ResultUtil<Page<TaskFileVO>> listChildFile(String filePath, @RequestParam(defaultValue = "1") Integer pageNo, @RequestParam(defaultValue = "50") Integer pageSize) {
        Page<TaskFileVO> result = taskService.listChildFile(filePath,pageNo,pageSize);
        return ResultUtil.ok(result);
    }

    @ApiOperation("任务等级枚举")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "filePath", value = "文件路径", required = false, dataType = "java.lang.String", paramType = "query")
    })
    @GetMapping(value = "/getTaskLevelList")
    public ResultUtil<List<Map<String, Object>>> getTaskLevelList() {
        List<Map<String, Object>> levelList = TaskLevelEnum.getLevelList();
        return ResultUtil.ok(levelList);
    }

//    @ApiOperation("跳转复核")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
//            @ApiImplicitParam(name = "taskId", value = "任务id", required = false, dataType = "java.lang.Long", paramType = "query")
//    })
//    @GetMapping(value = "/gotoReview")
//    public ResultUtil<TaskReviewDetailVO> gotoReview(Long taskId) {
//        TaskReviewDetailVO taskReviewDetailVO = taskService.gotoReview(taskId);
//        return ResultUtil.ok(taskReviewDetailVO);
//    }

    @ApiOperation("复核下一个文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "inspectionId", value = "巡检id", required = false, dataType = "java.lang.Long", paramType = "query")
    })
    @GetMapping(value = "/reviewNextFolder")
    public ResultUtil<TaskReviewNextFolderVO> reviewNextFolder(Long inspectionId) {
        TaskReviewNextFolderVO reviewNextFolder = taskService.reviewNextFolder(inspectionId);
        return ResultUtil.ok(reviewNextFolder);
    }

    @ApiOperation("跳转复核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
    })
    @GetMapping(value = "/gotoReview")
    public ResultUtil<TaskReviewDetailVO> gotoReview(TaskReviewQO taskReviewQO) {
        TaskReviewDetailVO taskReviewDetailVO = taskService.checkFolderSearch(taskReviewQO);
        return ResultUtil.ok(taskReviewDetailVO);
    }

    @ApiOperation("文件夹复核分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
    })
    @GetMapping(value = "/taskFolderPaging")
    public ResultUtil<Page<InspectionDataVO>> taskFolderPaging(TaskReviewQO taskReviewQO) {
        Page<InspectionDataVO> paging = taskService.taskFolderPaging(taskReviewQO);
        return ResultUtil.ok(paging);
    }

    @ApiOperation("任务下拉列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
    })
    @GetMapping(value = "/taskPulldownList")
    public ResultUtil<List<TaskPullDownVO>> taskPulldownList() {
        List<TaskPullDownVO> pulldownList = taskService.taskPulldownList();
        return ResultUtil.ok(pulldownList);
    }

    @ApiOperation("确认缺陷")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "inspectionId", value = "巡检id", required = false, dataType = "java.lang.Long", paramType = "query")
    })
    @PostMapping(value = "/confirmDefect")
    public ResultUtil<Boolean> confirmDefect(@RequestBody ConfirmDefectBO confirmDefectBO) throws ServiceException {
        Boolean result = taskService.confirmDefect(confirmDefectBO);
        return ResultUtil.ok(result);
    }

    @ApiOperation("查看缺陷")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "inspectionId", value = "巡检id", required = false, dataType = "java.lang.Long", paramType = "query")
    })
    @PostMapping(value = "/viewDefect")
    public ResultUtil<Boolean> viewDefect(@RequestBody ViewDefectBO viewDefectBO) throws ServiceException {
        Boolean result = taskService.viewDefect(viewDefectBO);
        return ResultUtil.ok(result);
    }

    @ApiOperation("新增缺陷")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "inspectionId", value = "巡检id", required = false, dataType = "java.lang.Long", paramType = "query")
    })
    @PostMapping(value = "/addDefect")
    public ResultUtil<Boolean> addDefect(@RequestBody ConfirmDefectBO confirmDefectBO) throws ServiceException {
        Boolean result = taskService.addDefect(confirmDefectBO);
        return ResultUtil.ok(result);
    }

    @ApiOperation("删除某一缺陷")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
    })
    @PostMapping(value = "/deleteDefect")
    public ResultUtil<Boolean> deleteDefect(@RequestBody DefectDeleteBO defectDefectBO) throws ServiceException {
        Boolean result = taskService.deleteDefect(defectDefectBO);
        return ResultUtil.ok(result);
    }

    @ApiOperation("批量删除图片缺陷")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "inspectionResultId", value = "巡检结果id", required = true, dataType = "java.lang.Long", paramType = "query")
    })
    @GetMapping(value = "/batchDeleteDefect")
    public ResultUtil<Boolean> batchDeleteDefect(@RequestParam Long inspectionResultId) throws ServiceException {
        Boolean result = taskService.batchDeleteDefect(inspectionResultId);
        return ResultUtil.OK("已清除人工分析出的缺陷", result);
    }

    @ApiOperation("任务缺陷类型列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "taskId", value = "任务id", required = false, dataType = "java.lang.Long", paramType = "query")
    })
    @GetMapping(value = "/taskDefectTypeList")
    public ResultUtil<List<String>> taskDefectTypeList(Long taskId) {
        List<String> result = taskService.taskDefectTypeList(taskId);
        return ResultUtil.ok(result);
    }
}
