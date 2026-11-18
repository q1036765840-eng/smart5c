package com.nbk.smart5c.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nbk.smart5c.entity.InspectionData;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.pojo.bo.AiAnalysisRespDataBO;
import com.nbk.smart5c.pojo.bo.InspectionDataSaveBO;
import com.nbk.smart5c.pojo.bo.InspectionDataUpdateBO;
import com.nbk.smart5c.pojo.qo.InspectionDataQO;
import com.nbk.smart5c.pojo.vo.InspectionDataVO;
import com.nbk.smart5c.pojo.vo.InspectionReviewDataVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 巡查数据表业务逻辑层
 * @since 2026-01-27 15:25:38
 */
@Service
public interface InspectionDataService<M extends BaseMapper<T>, T> extends IService<T> {
    /**
     * 新增数据
     *
     * @param info 新增数据信息
     * @return 是否操作成功
     */
    Boolean save(InspectionDataSaveBO info);

    /**
     * 根据查询删除数据
     *
     * @param id 数据id
     * @return 是否操作成功
     */
    InspectionData queryById(String id);

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     * @return 满足条件的分页数据
     */
    IPage<InspectionData> queryAndPaging(InspectionDataQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    List<InspectionDataVO> queryAll(InspectionDataQO query);

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     * @return 是否操作成功
     */
    Boolean updateById(InspectionDataUpdateBO info);

    /**
     * 数据导出
     *
     * @param response 响应体
     * @param query    查询条件
     * @return 是否操作成功
     */
    void export(InspectionDataQO query, HttpServletResponse response);

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

    List<InspectionReviewDataVO> inspectionDataList(Long deviceId);

    Boolean parseData(Long taskId, String filePath) throws ServiceException;

    Boolean parseDataNoTask(String filePath) throws ServiceException;

    void processImageValidation(File file, InspectionData inspectionData);

    void processFolderValidation(File file, InspectionData inspectionData);

    Boolean deleteById(Long id);

    InspectionData getReviewData(Long inspectionId);

    Boolean validImage(String imagePath);

    Boolean startAnalysis(List<Long> idList);

    void foreignObjectProcess(AiAnalysisRespDataBO aiAnalysisRespData, InspectionData inspectionData, String imagePath);

    String getParseProcess();

    String getAnalysisProcess(Long inspectionId);

    Boolean analysisAll();

    Boolean deleteByIds(List<Long> ids) throws ServiceException;

    List<List<Object>> getExportInspectionData(List<Long> ids, HttpServletResponse response) throws ServiceException;

    void exportInspectionData(List<Long> ids, HttpServletResponse response) throws ServiceException;

    Integer getConfirmStatusById(Long id);
}
