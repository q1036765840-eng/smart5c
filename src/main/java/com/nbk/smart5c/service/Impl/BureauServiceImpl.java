package com.nbk.smart5c.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbk.smart5c.entity.Bureau;
import com.nbk.smart5c.mapper.BureauMapper;
import com.nbk.smart5c.mapper.SectionMapper;
import com.nbk.smart5c.pojo.bo.BureauSaveBO;
import com.nbk.smart5c.pojo.bo.BureauUpdateBO;
import com.nbk.smart5c.pojo.qo.BureauQO;
import com.nbk.smart5c.pojo.vo.BureauVO;
import com.nbk.smart5c.service.BureauService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 供电部信息表业务逻辑层
 * @since 2026-01-30 17:29:19
 */
@Service
@Primary
public class BureauServiceImpl extends ServiceImpl<BureauMapper, Bureau> implements BureauService<BureauMapper, Bureau> {
    @Autowired
    private SectionMapper sectionMapper;

    /**
     * 新增数据
     *
     * @param info 新增数据信息
     */
    @Override
    public Boolean save(BureauSaveBO info) {
        Bureau entity = new Bureau();
        BeanUtils.copyProperties(info, entity);
        return super.save(entity);
    }

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     */
    @Override
    public Bureau queryById(String id) {
        return super.getById(id);
    }

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     */
    @Override
    public IPage<BureauVO> queryAndPaging(BureauQO query) {
        return null;
    }

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    @Override
    public List<BureauVO> queryAll(BureauQO query) {
        return super.baseMapper.queryAll(query);
    }

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     */
    @Override
    public Boolean updateById(BureauUpdateBO info) {
        Bureau entity = new Bureau();
        BeanUtils.copyProperties(info, entity);
        return super.updateById(entity);
    }

}
