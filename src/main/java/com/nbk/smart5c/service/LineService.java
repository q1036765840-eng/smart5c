package com.nbk.smart5c.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nbk.smart5c.entity.Line;
import com.nbk.smart5c.pojo.bo.LineSaveBO;
import com.nbk.smart5c.pojo.bo.LineUpdateBO;
import com.nbk.smart5c.pojo.qo.LineQO;
import com.nbk.smart5c.pojo.vo.DropDownListVO;
import com.nbk.smart5c.pojo.vo.LineVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 线路信息表业务逻辑层
 * @since 2026-01-27 15:25:38
 */
@Service
public interface LineService<M extends BaseMapper<T>, T> extends IService<T> {
    /**
     * 新增数据
     *
     * @param info 新增数据信息
     * @return 是否操作成功
     */
    Boolean save(LineSaveBO info);

    /**
     * 根据查询删除数据
     *
     * @param id 数据id
     * @return 是否操作成功
     */
    Line queryById(String id);

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     * @return 满足条件的分页数据
     */
    IPage<LineVO> queryAndPaging(LineQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    List<LineVO> queryAll(LineQO query);

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     * @return 是否操作成功
     */
    Boolean updateById(LineUpdateBO info);

    /**
     * 数据导出
     *
     * @param response 响应体
     * @param query    查询条件
     * @return 是否操作成功
     */
    void export(LineQO query, HttpServletResponse response);

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
     * 线路下拉列表
     *
     * @return
     */
    List<DropDownListVO> getLineDownList(Long sectionId);
}
