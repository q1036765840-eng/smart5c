package com.nbk.smart5c.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nbk.smart5c.entity.Bureau;
import com.nbk.smart5c.pojo.bo.BureauSaveBO;
import com.nbk.smart5c.pojo.bo.BureauUpdateBO;
import com.nbk.smart5c.pojo.qo.BureauQO;
import com.nbk.smart5c.pojo.vo.BureauVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 供电部信息表业务逻辑层
 * @since 2026-01-30 17:29:19
 */
@Service
public interface BureauService<M extends BaseMapper<T>, T> extends IService<T> {
    /**
     * 新增数据
     *
     * @param info 新增数据信息
     * @return 是否操作成功
     */
    Boolean save(BureauSaveBO info);

    /**
     * 根据查询删除数据
     *
     * @param id 数据id
     * @return 是否操作成功
     */
    Bureau queryById(String id);

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     * @return 满足条件的分页数据
     */
    IPage<BureauVO> queryAndPaging(BureauQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    List<BureauVO> queryAll(BureauQO query);

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     * @return 是否操作成功
     */
    Boolean updateById(BureauUpdateBO info);
}
