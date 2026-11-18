package com.nbk.smart5c.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nbk.smart5c.entity.DefectStorage;
import com.nbk.smart5c.pojo.bo.DefectStorageReviewBO;
import com.nbk.smart5c.pojo.bo.DefectStorageSaveBO;
import com.nbk.smart5c.pojo.bo.DefectStorageUpdateBO;
import com.nbk.smart5c.pojo.qo.DefectStorageDetailQO;
import com.nbk.smart5c.pojo.qo.DefectStorageQO;
import com.nbk.smart5c.pojo.vo.DefectStorageDetailShowVO;
import com.nbk.smart5c.pojo.vo.DefectStorageVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 缺陷库表业务逻辑层
 * @since 2026-04-07 16:41:49
 */
@Service
public interface DefectStorageService<M extends BaseMapper<T>, T> extends IService<T> {
    /**
     * 新增数据
     *
     * @param info 新增数据信息
     * @return 是否操作成功
     */
    Boolean save(DefectStorageSaveBO info);

    /**
     * 根据查询删除数据
     *
     * @param id 数据id
     * @return 是否操作成功
     */
    DefectStorage queryById(String id);

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     * @return 满足条件的分页数据
     */
    IPage<DefectStorageVO> queryAndPaging(DefectStorageQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    List<DefectStorageVO> queryAll(DefectStorageQO query);

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     * @return 是否操作成功
     */
    Boolean updateById(DefectStorageUpdateBO info);

    /**
     * 数据导出
     *
     * @param response 响应体
     * @param query    查询条件
     * @return 是否操作成功
     */
    void export(DefectStorageQO query, HttpServletResponse response);

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
     * 担风险库审核
     *
     * @param defectStorageReviewBO
     * @return
     */
    Boolean defectReview(DefectStorageReviewBO defectStorageReviewBO);

    void exportChecked(List<Long> idLIst, String exportType);

    List<List<Object>> getExportChecked(List<Long> idLIst, String exportType);

    List<String> defectTypeList(Long taskId, List<Integer> reviewStatusList);

    DefectStorageDetailShowVO defectStorageDetail(DefectStorageDetailQO defectStorageDetailQO);

    void downloadDefectImage(Long id, Integer hasDefect, HttpServletResponse response);

    Boolean deleteById(Long id);

    Boolean deleteByIds(List<Long> ids);
}
