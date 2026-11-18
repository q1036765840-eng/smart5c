package com.nbk.smart5c.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.nbk.smart5c.common.constants.RSASecret;
import com.nbk.smart5c.common.constants.SystemConstants;
import com.nbk.smart5c.common.utils.*;
import com.nbk.smart5c.entity.Department;
import com.nbk.smart5c.entity.FileUploadRecords;
import com.nbk.smart5c.entity.FolderCreate;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.mapper.*;
import com.nbk.smart5c.pojo.bo.DirectoryUpdateBO;
import com.nbk.smart5c.pojo.bo.FileUploadRecordsSaveBO;
import com.nbk.smart5c.pojo.qo.FileUploadLogQO;
import com.nbk.smart5c.pojo.qo.FileUploadRecordsQO;
import com.nbk.smart5c.pojo.vo.*;
import com.nbk.smart5c.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName: FileUploadServiceImpl
 * @Author: Alcedo
 * @CreateTime: 2023-05-16
 * @Description:
 */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

//    @Value("${upload.video}")
//    private String videoPath;

    @Value("${inspect.basepath}")
    private String picturePath;

    @Value("${sftp.host}")
    private String sftpHost;

    @Value("${sftp.port}")
    private Integer sftpPort;

    @Value("${sftp.username}")
    private String sftpUsername;

    @Value("${sftp.password}")
    private String sftpPassword;

    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private FileUploadRecordsMapper fileUploadRecordsMapper;
    @Autowired
    private InspectionDataMapper inspectionDataMapper;
    @Autowired
    private DefectStorageMapper defectStorageMapper;
    @Autowired
    private FolderCreateMapper folderCreateMapper;
    @Autowired
    private ApiUtils apiUtils;

    private static final String videoPath = "";

    @Override
    public Page<FileUploadRecordsVO> uploadDataPaging(FileUploadRecordsQO query) throws ServiceException {
        Page<FileUploadRecordsVO> page = new Page<>();
        List<FileUploadRecordsVO> records = new ArrayList<>();

        try {
            if (CharSequenceUtil.isBlank(query.getFileName())) {
                processFolderListing(query, records, page);
            } else {
                processFileSearch(query, records, page);
            }
        } catch (Exception e) {
            log.error("文件操作失败: {}", e.getLocalizedMessage(), e);
            throw new ServiceException(520, "文件操作失败");
        }

        page.setRecords(records).setTotal(page.getTotal());
        return page;
    }

    private void processFolderListing(FileUploadRecordsQO query, List<FileUploadRecordsVO> records, Page<FileUploadRecordsVO> page) throws ServiceException {
        PathCheckUtil.requireUnderBase(picturePath, query.getFolderName());
        if (StrUtil.isBlank(query.getFolderName())) {
            query.setFolderName(SystemConstants.FILE_VIDEO.equals(query.getFileType()) ? videoPath : picturePath);
        }
        File folder = new File(query.getFolderName());
        File[] files = folder.listFiles();

        if (ArrayUtil.isEmpty(files)) {
            page.setTotal(0L);
            return;
        }
        Arrays.sort(files, (f1, f2) -> {
            String f1Name = f1 == null ? "" : f1.getName();
            String f2Name = f2 == null ? "" : f2.getName();
            if (Character.isDigit(f1Name.charAt(0)) && Character.isDigit(f2Name.charAt(0))) {
                return CompareUtil.compareTo(f1Name, f2Name);
            } else {
                return f1Name.compareTo(f2Name);
            }
        });
        page.setTotal(files.length);
        int imageCount = 0;
        final int MAX_IMAGES = 100;

        for (File file : files) {
            if (imageCount >= MAX_IMAGES && FileUtil.judgeImageFile(file)) {
                continue; // Skip additional images beyond limit
            }

            FileUploadRecordsVO record = buildRecordFromFile(file, query.getFileType());
            records.add(record);

            if (FileUtil.judgeImageFile(file)) {
                imageCount++;
            }
        }
    }

    private void processFileSearch(FileUploadRecordsQO query, List<FileUploadRecordsVO> records, Page<FileUploadRecordsVO> page) throws IOException, ServiceException {
        page.setTotal(0L);
        PathCheckUtil.requireUnderBase(picturePath, query.getFolderName());
        if (CharSequenceUtil.isBlank(query.getFileName())) {
            return;
        }
        if (CharSequenceUtil.isBlank(query.getFolderName())) {
            String basePath = SystemConstants.FILE_VIDEO.equals(query.getFileType()) ? videoPath : picturePath;
            query.setFolderName(basePath);
        }
        File folder = new File(query.getFolderName());
        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }

        // 同时搜索文件和目录：query.fileName 既能匹配文件名也能匹配目录名
        List<Path> paths = FileSearcherUtil.searchFilesAndDirs(folder.toPath(), query.getFileName());
        paths.sort((f1, f2) -> {
            String f1Name = (null == f1 || null == f1.getFileName()) ? "" : f1.getFileName().toString();
            String f2Name = (null == f2 || null == f2.getFileName()) ? "" : f2.getFileName().toString();
            // 文件名可能为空串，先判空避免 charAt(0) 抛 StringIndexOutOfBoundsException 导致整个排序失败
            boolean f1Digit = !f1Name.isEmpty() && Character.isDigit(f1Name.charAt(0));
            boolean f2Digit = !f2Name.isEmpty() && Character.isDigit(f2Name.charAt(0));
            if (f1Digit && f2Digit) {
                return CompareUtil.compareTo(f1Name, f2Name);
            } else {
                return f1Name.compareTo(f2Name);
            }
        });
        page.setTotal(paths.size());

        for (Path path : paths) {
            File file = path.toFile();
            FileUploadRecordsVO record = buildRecordFromFile(file, query.getFileType());
            records.add(record);
        }
    }

    private FileUploadRecordsVO buildRecordFromFile(File file, Integer fileType) {
        FileUploadRecordsVO record = new FileUploadRecordsVO();

        // Set basic properties
        record.setFileName(file.getName())
                .setFileSize(file.length())
                .setCreateTime(DateUtil.date(file.lastModified()))
                .setFolderName(file.getParentFile() != null ? file.getParentFile().getName() : "")
                .setUploadCatalog(file.getAbsolutePath());

        // Determine file type
        if (file.isDirectory()) {
            record.setFileType(SystemConstants.FILE_FOLDER);
            record.setFileCount(file.listFiles() != null ? file.listFiles().length : 0);
        } else if (FileUtil.judgeVideoFile(file)) {
            record.setFileType(SystemConstants.FILE_VIDEO);
            record.setVideoDuration(FileUtil.getVideoDuration(file));
        } else if (FileUtil.judgeImageFile(file)) {
            record.setFileType(SystemConstants.FILE_IMAGE);
        } else {
            record.setFileType(3); // Unknown type
        }

        // Set paths based on file type
        setPathsForRecord(record, file, fileType);

        return record;
    }

    private void setPathsForRecord(FileUploadRecordsVO record, File file, Integer fileType) {
        String absolutePath = file.getAbsolutePath();
        String basePath, replacement;

        if (SystemConstants.FILE_VIDEO.equals(fileType)) {
            basePath = videoPath;
            replacement = "/video";
        } else {
            basePath = picturePath;
            replacement = "/image";
        }

        record.setNetPath(absolutePath.replace(basePath, replacement));
        record.setExpandPath(absolutePath.replace(basePath, ""));
    }

    /**
     *
     * @param fileType       文件类型0:视频,1:图片
     * @param catalogPath    目录路径
     * @param taskCreateFlag 创建任务时用，创建时图片的上级目录不能选
     * @return
     * @throws ServiceException
     */
    @Override
    public List<FileCatalogVO> getFileCatalogTree(Integer fileType, String catalogPath, Boolean taskCreateFlag) throws ServiceException {
        List<FileCatalogVO> catalogVOList = new ArrayList<>();
        File currentFile = resolveCatalogFile(fileType, catalogPath);
        File[] files = currentFile == null ? null : currentFile.listFiles();
        if (ArrayUtil.isEmpty(files)) {
            return catalogVOList;
        }
//        if (StrUtil.isBlank(catalogPath)) { //因为缺陷库也是放这下面的，需要过滤，不能在此放出来
//            files = Arrays.stream(files).filter(file -> !file.getName().equals(SystemConstants.DEFECT_STORAGE)).toArray(File[]::new);
//        }

        if (Boolean.TRUE.equals(taskCreateFlag) && hasImageInCurrentOrChildren(currentFile, files)) {
            return catalogVOList;
        }
        if (ArrayUtil.isNotEmpty(files)) {
            sortFilesByName(files);
            for (File file : files) {
                catalogVOList.add(buildCatalogVO(file));
            }
        }
        return catalogVOList;
    }

    private File resolveCatalogFile(Integer fileType, String catalogPath) throws ServiceException {
        if (!SystemConstants.FILE_IMAGE.equals(fileType)) {
            return null;
        }
        PathCheckUtil.requireUnderBase(picturePath, catalogPath);
        return CharSequenceUtil.isNotBlank(catalogPath) ? new File(catalogPath) : new File(picturePath);
    }

    private boolean hasImageInCurrentOrChildren(File currentFile, File[] files) {
        if (CollUtil.isNotEmpty(FilteredFileLister.listFilesByExtension(currentFile.getAbsolutePath()))) {
            return true;
        }
        if (ArrayUtil.isNotEmpty(files)) {
            for (File childFile : files) {
                if (CollUtil.isNotEmpty(FilteredFileLister.listFilesByExtension(childFile.getAbsolutePath()))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void sortFilesByName(File[] files) {
        Arrays.sort(files, (f1, f2) -> {
            String f1Name = f1 == null ? "" : f1.getName();
            String f2Name = f2 == null ? "" : f2.getName();
            if (Character.isDigit(f1Name.charAt(0)) && Character.isDigit(f2Name.charAt(0))) {
                return CompareUtil.compareTo(f1Name, f2Name);
            }
            return f1Name.compareTo(f2Name);
        });
    }

    private FileCatalogVO buildCatalogVO(File file) throws ServiceException {
        FileCatalogVO vo = new FileCatalogVO();
        vo.setCatalogName(file.getName())
                .setCatalogPath(file.getAbsolutePath())
                .setFileAttribute(file.isDirectory() ? 0 : 1)
                .setEnabled(true)
                .setHasChildCatalog(file.isDirectory() && ArrayUtil.isNotEmpty(file.listFiles(File::isDirectory)));
        return vo;
    }

//    private void ensureDeptDirectories() throws ServiceException {
//        Long deptId = apiUtils.currentDeptId();
//        Department department = departmentMapper.selectById(deptId);
//        if (department == null) {
//            return;
//        }
//        File deptFilePath = new File(picturePath + department.getPath());
//        if (!deptFilePath.exists()) {
//            deptFilePath.mkdirs();
//            LinuxUtil.chmodFile(deptFilePath.getAbsolutePath());
//        }
//        List<String> childDeptPathList = departmentMapper.getChildDeptPathList(department.getPath());
//        if (CollUtil.isEmpty(childDeptPathList)) {
//            return;
//        }
//        for (String childPath : childDeptPathList) {
//            File childFile = new File(picturePath + childPath);
//            if (!childFile.exists()) {
//                childFile.mkdirs();
//            }
//            LinuxUtil.chmodFile(childFile.getAbsolutePath());
//        }
//    }


    @Override
    public Page<FileUploadLogVO> uploadLogPaging(FileUploadLogQO query) {
        MPJLambdaWrapper<FileUploadRecords> queryWrapper = new MPJLambdaWrapper<>(FileUploadRecords.class);
        queryWrapper.like(CharSequenceUtil.isNotBlank(query.getFolderName()), FileUploadRecords::getFolderName, query.getFolderName());
        queryWrapper.ge(CharSequenceUtil.isNotBlank(query.getStartDate()), FileUploadRecords::getCreatedTime, query.getStartDate() + " 00:00:00");
        queryWrapper.le(CharSequenceUtil.isNotBlank(query.getEndDate()), FileUploadRecords::getCreatedTime, query.getStartDate() + " 23:59:59");
        Page<FileUploadLogVO> pagingVo = queryWrapper.page(new Page<>(query.getPageNo(), query.getPageSize()), FileUploadLogVO.class);
        return pagingVo;
    }

    @Override
    public String getSftpHostInfo() throws ServiceException {
        if (!Boolean.TRUE.equals(apiUtils.isAdmin())) {
            throw new ServiceException(520, "无权限访问SFTP主机信息");
        }
        String result = "";
        try {
            SftpHostVO sftpHostVO = new SftpHostVO();
            sftpHostVO.setHost(sftpHost).setPort(null == sftpPort ? 22 : sftpPort).setUsername(sftpUsername).setPassword(sftpPassword);
            result = RsaUtil.encryptByPublicKey(JSONUtil.toJsonStr(sftpHostVO), RSASecret.PUBLIC_KEY);
        } catch (Exception e) {
            throw new ServiceException(520, "加密失败");
        }
        return result;
    }

    @Override
    public Boolean fileUploadCallback(FileUploadRecordsSaveBO info) {
        log.info("call_back_token:{}", apiUtils.currentToken());
        log.info("文件上传回调传入参数:{}", info);
        FileUploadRecords entity = new FileUploadRecords();
        entity.setUploadPath(info.getUploadPath());
        entity.setFolderName(info.getFolderName());
        entity.setCreatedUser(apiUtils.currentAccount());
        fileUploadRecordsMapper.insert(entity);
        return true;
    }

    @Override
    public Boolean fileDelete(String filePath) throws ServiceException {
        PathCheckUtil.requireUnderBase(picturePath, filePath);
        String relativeFilePath = filePath.replace(picturePath, "");
        Long existTask = inspectionDataMapper.countLikeLocomotiveImage(relativeFilePath);
        if (existTask > 0L) {
            throw new ServiceException(520, "该文件有分析任务绑定，不能删除！如果确实需要删除，请删除任务后再删除文件！");
        }
        String account = apiUtils.currentAccount();
//        if(!apiUtils.isAdmin()){
//            File[] files = new File(filePath).listFiles();
//            if(ArrayUtil.isNotEmpty(files)){
//                throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(),"只允许删除空目录");
//            }
//            log.info("current_account:{}",account);
//            //判断目录是不是本人创建的
//            long existCount = folderCreateMapper.countByFilePath(filePath);
//            long count = folderCreateMapper.countByFilePathAndCreateUser(filePath,account);
//            if(existCount>0 && 0==count){
//                throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(),"该目录不是你本人创建的");
//            }
////            //判断目录下有没有别人创建的
////            count = folderCreateMapper.countByFilePathNotCreateUser(filePath,account);
////            if(count>0){
////                throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(),"该目录下有别人创建的目录");
////            }
//        }
        folderCreateMapper.deleteCreateRecord(filePath, account);
        fileUploadRecordsMapper.deleteUploadRecord(filePath, account);
        LinuxUtil.chmodFile(filePath);
        LinuxUtil.rmFile(filePath);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addDirectory(DirectoryUpdateBO directoryUpdate) throws ServiceException {
        PathCheckUtil.requireUnderBase(picturePath, directoryUpdate.getDirPath());
        File folder;
        if (CharSequenceUtil.isBlank(directoryUpdate.getDirPath())) {
            folder = new File(picturePath + File.separator + directoryUpdate.getDirName());
        } else {
            folder = new File(directoryUpdate.getDirPath() + File.separator + directoryUpdate.getDirName());
            if (folder.exists()) {
                throw new ServiceException(520, "该目录已存在");
            }
        }
        FolderCreate folderCreate = new FolderCreate();
        folderCreate.setFolderName(folder.getName()).setFolderPath(folder.getAbsolutePath());
        if(folderCreateMapper.insert(folderCreate) > 0){
            folder.mkdirs();
            LinuxUtil.chmodFile(folder.getAbsolutePath());
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDirectory(DirectoryUpdateBO directoryUpdate) throws ServiceException {
        PathCheckUtil.requireUnderBase(picturePath, directoryUpdate.getDirPath());
        File folder = new File(directoryUpdate.getDirPath());
        if (!folder.exists()) {
            throw new ServiceException(520, "旧目录不存在");
        }
        File updateFolder = new File(folder.getParent() + File.separator + directoryUpdate.getDirName());
        if (updateFolder.exists()) {
            throw new ServiceException(520, "更新的目录与当前文件夹下的文件重名");
        }
        String relativeFilePath = directoryUpdate.getDirPath().replace(picturePath, "");
        Long existTask = inspectionDataMapper.countLikeLocomotiveImage(relativeFilePath);
        if (existTask > 0L) {
            throw new ServiceException(520, "该文件有分析任务绑定，不能改名！如果确实需要改名，请删除任务后再更新名称！");
        }
//        if(!apiUtils.isAdmin()){
//            File[] files = new File(folder.getAbsolutePath()).listFiles();
//            if(ArrayUtil.isNotEmpty(files)){
//                throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(),"只允许删除空目录");
//            }
//            //判断目录是不是本人创建的
//            String currentAccount = apiUtils.currentAccount();
//            long existCount = folderCreateMapper.countByFilePath(folder.getAbsolutePath());
//            long count = folderCreateMapper.countByFilePathAndCreateUser(folder.getAbsolutePath(),currentAccount);
//            if(existCount>0 && 0==count){
//                throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(),"该目录不是你本人创建的");
//            }
////            //判断目录下有没有别人创建的
////            count = folderCreateMapper.countByFilePathNotCreateUser(folder.getAbsolutePath(),currentAccount);
////            if(count>0){
////                throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(),"该目录下有别人创建的目录");
////            }
//        }
        folder.renameTo(updateFolder);
        FolderCreate folderCreate = folderCreateMapper.queryByFilePath(folder.getAbsolutePath());
        if (null == folderCreate) {
            return true;
        }
        folderCreate.setFolderPath(updateFolder.getAbsolutePath()).setFolderName(updateFolder.getName());
        return folderCreateMapper.updateById(folderCreate)>0;
    }

    @Override
    public List<FileCatalogVO> getTaskFileCatalogTree(Integer fileType, String catalogPath) throws ServiceException {
        File[] files = null;
        if (SystemConstants.FILE_IMAGE.equals(fileType)) {
            PathCheckUtil.requireUnderBase(picturePath, catalogPath);
            if (CharSequenceUtil.isNotBlank(catalogPath)) {
                files = new File(catalogPath).listFiles();
            } else {
                files = new File(picturePath).listFiles();
            }
        }
        List<FileCatalogVO> catalogVOList = new ArrayList<>();
        if (ArrayUtil.isNotEmpty(files)) {
            for (File file : files) {
                if (file.isDirectory() && file.listFiles()[0].isFile()) {
                    continue;
                }
                FileCatalogVO fileCatalogVO = new FileCatalogVO();
                fileCatalogVO.setCatalogName(file.getName()).setCatalogPath(file.getAbsolutePath()).setFileAttribute(file.isDirectory() ? 0 : 1).setEnabled(true);
                catalogVOList.add(fileCatalogVO);
            }
        }
        Long deptId = apiUtils.currentDeptId();
        String basePath = picturePath;
        Department department = departmentMapper.selectById(deptId);
        if (null == department) {
            return catalogVOList;
        }
        File deptFilePath = new File(basePath + department.getPath());
        if (!deptFilePath.exists()) {
            deptFilePath.mkdirs();
        }
        List<String> childDeptPathList = departmentMapper.getChildDeptPathList(department.getPath());
        if (CollUtil.isNotEmpty(childDeptPathList)) {
            for (String childPath : childDeptPathList) {
                deptFilePath = new File(basePath + childPath);
                if (!deptFilePath.exists()) {
                    deptFilePath.mkdirs();
                }
            }
        }
        return catalogVOList;
    }

    @Override
    public DiskStorageVO getDiskSpaceStatus() {
        FileSystem fileSystem = FileSystems.getDefault();
        Iterable<FileStore> fileStores = fileSystem.getFileStores();
        Iterator<FileStore> iterator = fileStores.iterator();
        long diskTotalSpace = 0;
//        long diskUsableSpace = 0;
        while (iterator.hasNext()) {
            FileStore fileStore = iterator.next();
            try {
                long totalSpace = fileStore.getTotalSpace();
                diskTotalSpace += totalSpace;
//                long usableSpace = fileStore.getUsableSpace();
//                diskUsableSpace += usableSpace;
            } catch (IOException e) {
                log.error("getDiskSpaceStatus_error:{}", e.getLocalizedMessage());
            }
        }
        File videoFolder = new File(picturePath);
        DiskStorageVO diskStorageVO = new DiskStorageVO();
        diskStorageVO.setTotalSize(diskTotalSpace).setOccupiedSize(FileUtil.calculateDirectorySize(videoFolder.getParentFile()));
        return diskStorageVO;
    }

    @Override
    public Boolean fileCanDelete(String filePath) throws ServiceException {
        PathCheckUtil.requireUnderBase(picturePath, filePath);
        String relativeFilePath = filePath.replace(picturePath, "");
        Long existTask = inspectionDataMapper.countLikeLocomotiveImage(relativeFilePath);
        if (existTask > 0L) {
            throw new ServiceException(520, "该文件有分析任务绑定，不能删除！如果确实需要删除，请删除任务后再删除文件！");
        }
//        if(!apiUtils.isAdmin()){
//            File[] files = new File(filePath).listFiles();
//            if(ArrayUtil.isNotEmpty(files)){
//                throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(),"只允许删除空目录");
//            }
//            //判断目录是不是本人创建的
//            String currentAccount = apiUtils.currentAccount();
//            long existCount = folderCreateMapper.countByFilePath(filePath);
//            long count = folderCreateMapper.countByFilePathAndCreateUser(filePath,currentAccount);
//            if(existCount>0 && 0==count){
//                throw new ServiceException(ResultCode.BUSI_FORBIDDEN.getCode(),"该目录不是你本人创建的");
//            }
//        }
        return true;
    }
}
