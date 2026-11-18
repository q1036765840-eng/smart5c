package com.nbk.smart5c.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nbk.smart5c.entity.InspectionData;
import com.nbk.smart5c.entity.InspectionReviewResult;
import com.nbk.smart5c.entity.Task;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.pojo.bo.*;
import com.nbk.smart5c.pojo.qo.TaskQO;
import com.nbk.smart5c.pojo.qo.TaskReviewQO;
import com.nbk.smart5c.pojo.vo.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 任务表业务逻辑层
 * @since 2026-04-07 16:41:49
 */
@Service
public interface TaskService<M extends BaseMapper<T>, T> extends IService<T> {
    /**
     * 新增数据
     *
     * @param info 新增数据信息
     * @return 是否操作成功
     */
    Boolean save(TaskSaveBO info) throws ServiceException;

    /**
     * 根据查询删除数据
     *
     * @param id 数据id
     * @return 是否操作成功
     */
    TaskEditVO queryById(Long id);

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     * @return 满足条件的分页数据
     */
    IPage<TaskVO> queryAndPaging(TaskQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    List<TaskVO> queryAll(TaskQO query);

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     * @return 是否操作成功
     */
    Boolean updateById(TaskUpdateBO info);

    /**
     * 数据导出
     *
     * @param response 响应体
     * @param query    查询条件
     * @return 是否操作成功
     */
    void export(TaskQO query, HttpServletResponse response);

    /**
     * 模板导出
     *
     * @param response 响应体
     * @return 是否操作成功
     */
    void exportTemplate(HttpServletResponse response);

    /**
     * 数据导入
     *
     * @param file 导入文件
     */
    void analysis(MultipartFile file);

    /**
     * @param taskId
     * @return
     * @desc 任务开始分析
     */
    Boolean startAnalysis(Long taskId) throws ServiceException;

    /**
     * @return 是否触发了队列中的任务
     * @desc 触发等待队列：当系统中无分析中任务时，从等待队列取出下一个任务开始分析
     */
    boolean triggerWaitQueue();

    Page<TaskFileVO> listChildFile(String filePath, Integer pageNo, Integer pageSize);

    /**
     * 任务复核
     *
     * @param taskId
     * @return
     */
    TaskReviewDetailVO gotoReview(Long taskId);

    TaskReviewNextFolderVO reviewNextFolder(Long inspectionId);

    List<TaskPullDownVO> taskPulldownList();

    Boolean pauseAnalysis(Long taskId);

    Boolean confirmDefect(ConfirmDefectBO confirmDefectBO) throws ServiceException;


    void saveDefectStorage(InspectionData inspectionData, Task task,
                           InspectionReviewResult reviewResult, List<DefectMarkBO> markList, Long inspectionResultId, Integer misreport) throws ServiceException;

    TaskReviewDetailVO checkFolderSearch(TaskReviewQO taskReviewQO);

    List<String> taskDefectTypeList(Long taskId);

    Boolean deleteById(Long id) throws ServiceException;

    Boolean deleteByIds(List<Long> ids) throws ServiceException;

    Page<InspectionDataVO> taskFolderPaging(TaskReviewQO taskReviewQO);

    Boolean deleteDefect(DefectDeleteBO defectDefectBO) throws ServiceException;

    Boolean addDefect(ConfirmDefectBO confirmDefectBO);

    Boolean viewDefect(ViewDefectBO viewDefectBO);

    boolean isAllAiConfirmedAndOthersViewed(Long inspectionId);

    boolean isAllOthersViewed(Long inspectionId);

    Boolean autoCreate(TaskPushSaveBO info) throws ServiceException;

    /**
     * @desc 消费外部推送数据分析队列：从队列取文件夹路径，交线程池解析并触发 AI 分析
     */
    void consumePushParseQueue();

    /**
     * @desc 将待解析文件夹按顺序放入解析队列，已在队列或解析中的目录不重复入队
     * @return 是否成功入队
     */
    boolean enqueuePushParseFolder(String folderPath);

    Boolean batchDeleteDefect(Long inspectionResultId) throws ServiceException;
}
