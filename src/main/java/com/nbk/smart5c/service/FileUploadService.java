package com.nbk.smart5c.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.pojo.bo.DirectoryUpdateBO;
import com.nbk.smart5c.pojo.bo.FileUploadRecordsSaveBO;
import com.nbk.smart5c.pojo.qo.FileUploadLogQO;
import com.nbk.smart5c.pojo.qo.FileUploadRecordsQO;
import com.nbk.smart5c.pojo.vo.DiskStorageVO;
import com.nbk.smart5c.pojo.vo.FileCatalogVO;
import com.nbk.smart5c.pojo.vo.FileUploadLogVO;
import com.nbk.smart5c.pojo.vo.FileUploadRecordsVO;

import java.util.List;

/**
 * @ClassName: FileUploadService
 * @Author: Alcedo
 * @CreateTime: 2023-05-16
 * @Description:
 */
public interface FileUploadService {


    Page<FileUploadRecordsVO> uploadDataPaging(FileUploadRecordsQO query) throws ServiceException;

    List<FileCatalogVO> getFileCatalogTree(Integer fileType, String catalogPath, Boolean taskCreateFlag) throws ServiceException;

    Page<FileUploadLogVO> uploadLogPaging(FileUploadLogQO query);

    String getSftpHostInfo() throws ServiceException;

    Boolean fileUploadCallback(FileUploadRecordsSaveBO info);

    Boolean fileDelete(String filePath) throws ServiceException;

    Boolean addDirectory(DirectoryUpdateBO directoryUpdate) throws ServiceException;

    Boolean updateDirectory(DirectoryUpdateBO directoryUpdate) throws ServiceException;

    List<FileCatalogVO> getTaskFileCatalogTree(Integer fileType, String catalogPath) throws ServiceException;

    DiskStorageVO getDiskSpaceStatus();

    Boolean fileCanDelete(String filePath) throws ServiceException;
}
