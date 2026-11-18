package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.pojo.bo.DirectoryUpdateBO;
import com.nbk.smart5c.pojo.bo.FileUploadRecordsSaveBO;
import com.nbk.smart5c.pojo.qo.FileUploadLogQO;
import com.nbk.smart5c.pojo.qo.FileUploadRecordsQO;
import com.nbk.smart5c.pojo.vo.FileCatalogVO;
import com.nbk.smart5c.pojo.vo.FileUploadLogVO;
import com.nbk.smart5c.pojo.vo.FileUploadRecordsVO;
import com.nbk.smart5c.service.FileUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName: FileUploadController
 * @Author: Alcedo
 * @CreateTime: 2023-05-16
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/web/dataUpload")
@Api(tags = "数据上传")
public class DataUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 上传数据分页查询
     *
     */
    @ApiOperation("上传数据分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
    })
    @GetMapping("/uploadDataPaging")
    public ResultUtil<Page<FileUploadRecordsVO>> uploadDataPaging(FileUploadRecordsQO query) throws ServiceException {
        return ResultUtil.ok(fileUploadService.uploadDataPaging(query));
    }

    /**
     * 上传日志分页查询
     *
     */
    @ApiOperation("上传日志分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
    })
//    @PerAnnotation(module = "基础数据",function = "分页查询上传数据",url = "/web/dataUpload/uploadDataPaging",sort = 5,secondModule = "数据上传",code = "dataUpload")
    @GetMapping("/uploadLogPaging")
    public ResultUtil<Page<FileUploadLogVO>> uploadLogPaging(FileUploadLogQO query) throws ServiceException {
        return ResultUtil.ok(fileUploadService.uploadLogPaging(query));
    }

    /**
     * 获取基本文件目录树
     */
    @ApiOperation("获取基本文件目录树")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
    })
    @GetMapping("/getFileCatalogTree")
    public ResultUtil<List<FileCatalogVO>> getFileCatalogTree(@RequestParam Integer fileType, @RequestParam(required = false) String catalogPath, @RequestParam(required = false) Boolean taskCreateFlag) throws ServiceException {
        List<FileCatalogVO> fileCatalogSet = fileUploadService.getFileCatalogTree(fileType, catalogPath, taskCreateFlag);
        return ResultUtil.ok(fileCatalogSet);
    }

    /**
     * 获取任务文件目录树
     */
    @ApiOperation("获取任务文件目录树")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
    })
    @GetMapping("/getTaskFileCatalogTree")
    public ResultUtil<List<FileCatalogVO>> getTaskFileCatalogTree(@RequestParam Integer fileType, @RequestParam(required = false) String catalogPath) throws ServiceException {
        List<FileCatalogVO> fileCatalogSet = fileUploadService.getTaskFileCatalogTree(fileType, catalogPath);
        return ResultUtil.ok(fileCatalogSet);
    }

    /**
     * SFTP主机信息获取
     */
    @ApiOperation("SFTP主机信息获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
    })
    @GetMapping("/getSftpHostInfo")
    public ResultUtil<String> getSftpHostInfo() throws ServiceException {
        String sftpHostInfo = fileUploadService.getSftpHostInfo();
        return ResultUtil.ok(sftpHostInfo);
    }

    @ApiOperation("文件上传成功回调接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/fileUploadCallback")
    public ResultUtil<Boolean> fileUploadCallback(@RequestBody FileUploadRecordsSaveBO info) {
        return ResultUtil.OK("新增成功", fileUploadService.fileUploadCallback(info));
    }

    @ApiOperation("文件夹或文件删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/fileDelete")
    public ResultUtil<Boolean> fileDelete(String filePath) throws ServiceException {
        return ResultUtil.OK("删除成功", fileUploadService.fileDelete(filePath));
    }

    @ApiOperation("判断文件能不能删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @GetMapping(value = "/fileCanDelete")
    public ResultUtil<Boolean> fileCanDelete(String filePath) throws ServiceException {
        return ResultUtil.OK("删除成功", fileUploadService.fileCanDelete(filePath));
    }

    @ApiOperation("新增目录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/addDirectory")
    public ResultUtil<Boolean> addDirectory(@RequestBody DirectoryUpdateBO directoryUpdate) throws ServiceException {
        return ResultUtil.OK("新增目录成功", fileUploadService.addDirectory(directoryUpdate));
    }

    @ApiOperation("修改目录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization", required = true, dataType = "java.lang.String", paramType = "header"),
            @ApiImplicitParam(name = "Accept-Language", value = "接受语言", required = false, dataType = "java.lang.String", paramType = "header")
    })
    @PostMapping(value = "/updateDirectory")
    public ResultUtil<Boolean> updateDirectory(@RequestBody DirectoryUpdateBO directoryUpdate) throws ServiceException {
        return ResultUtil.OK("修改目录成功", fileUploadService.updateDirectory(directoryUpdate));
    }
}
