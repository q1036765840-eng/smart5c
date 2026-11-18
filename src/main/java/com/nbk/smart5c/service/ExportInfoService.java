package com.nbk.smart5c.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nbk.smart5c.entity.ExportInfo;
import com.nbk.smart5c.pojo.bo.ExportInfoSaveBO;
import com.nbk.smart5c.pojo.bo.ExportInfoUpdateBO;
import com.nbk.smart5c.pojo.qo.ExportInfoQO;
import com.nbk.smart5c.pojo.vo.ExportInfoVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 系统导出信息表业务逻辑层
 * @since 2026-10-27
 */
@Service
public interface ExportInfoService<M extends BaseMapper<T>, T> extends IService<T> {
    /**
     * 新增数据
     *
     * @param info 新增数据信息
     * @return 是否操作成功
     */
    Boolean save(ExportInfoSaveBO info);

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     * @return 查询结果
     */
    ExportInfo queryById(String id);

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     * @return 满足条件的分页数据
     */
    IPage<ExportInfoVO> queryAndPaging(ExportInfoQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    List<ExportInfoVO> queryAll(ExportInfoQO query);

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     * @return 是否操作成功
     */
    Boolean updateById(ExportInfoUpdateBO info);
}
