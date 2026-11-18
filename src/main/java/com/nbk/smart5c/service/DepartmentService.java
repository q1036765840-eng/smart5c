package com.nbk.smart5c.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nbk.smart5c.entity.Department;
import com.nbk.smart5c.pojo.bo.DepartmentSaveBO;
import com.nbk.smart5c.pojo.bo.DepartmentUpdateBO;
import com.nbk.smart5c.pojo.qo.DepartmentQO;
import com.nbk.smart5c.pojo.vo.DepartTreeVO;
import com.nbk.smart5c.pojo.vo.DepartmentVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 部门信息表业务逻辑层
 * @since 2026-05-16 17:20:28
 */
@Service
public interface DepartmentService<M extends BaseMapper<T>, T> extends IService<T> {
    /**
     * 新增数据
     *
     * @param info 新增数据信息
     * @return 是否操作成功
     */
    Boolean save(DepartmentSaveBO info);

    /**
     * 根据查询删除数据
     *
     * @param id 数据id
     * @return 是否操作成功
     */
    Department queryById(String id);

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     * @return 满足条件的分页数据
     */
    IPage<DepartmentVO> queryAndPaging(DepartmentQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    List<DepartmentVO> queryAll(DepartmentQO query);

    Boolean updateById(DepartmentUpdateBO info);

    List<DepartTreeVO> getDepartmentTree(DepartmentQO query);
}
