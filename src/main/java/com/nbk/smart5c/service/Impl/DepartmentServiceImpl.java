package com.nbk.smart5c.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.nbk.smart5c.entity.Department;
import com.nbk.smart5c.mapper.DepartmentMapper;
import com.nbk.smart5c.pojo.bo.DepartmentSaveBO;
import com.nbk.smart5c.pojo.bo.DepartmentUpdateBO;
import com.nbk.smart5c.pojo.qo.DepartmentQO;
import com.nbk.smart5c.pojo.vo.DepartTreeVO;
import com.nbk.smart5c.pojo.vo.DepartmentVO;
import com.nbk.smart5c.service.DepartmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 部门信息表业务逻辑层
 * @since 2026-05-16 17:20:28
 */
@Service
@Primary
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService<DepartmentMapper, Department> {

    private final static String FILE_NAME = "nbk_department(部门信息表)";

    private final static List<String> TITLE = Arrays.asList(
            "id(主键)",
            "department_code(部门编码)",
            "department_name(部门名称)",
            "parent_id(父机构id)",
            "remark(备注)",
            "created_user(创建人)",
            "created_time(创建时间)",
            "updated_user(修改人)",
            "updated_time(修改时间)",
            "ancestors(祖先)",
            "path(机构路径)"
    );

    /**
     * 新增数据
     *
     * @param info 新增数据信息
     */
    @Override
    public Boolean save(DepartmentSaveBO info) {
        Department entity = new Department();
        BeanUtils.copyProperties(info, entity);
        return super.save(entity);
    }

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     */
    @Override
    public Department queryById(String id) {
        return super.getById(id);
    }

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     */
    @Override
    public IPage<DepartmentVO> queryAndPaging(DepartmentQO query) {
        return null;
    }

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    @Override
    public List<DepartmentVO> queryAll(DepartmentQO query) {
        MPJLambdaWrapper<Department> departmentMapper = new MPJLambdaWrapper<>(Department.class);
        return departmentMapper.list(DepartmentVO.class);
    }

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     */
    @Override
    public Boolean updateById(DepartmentUpdateBO info) {
        Department entity = new Department();
        BeanUtils.copyProperties(info, entity);
        return super.updateById(entity);
    }

    @Override
    public List<DepartTreeVO> getDepartmentTree(DepartmentQO query) {
        List<DepartTreeVO> topList = new ArrayList<>();
        if (CharSequenceUtil.isBlank(query.getDepartmentName())) {
            topList = super.baseMapper.getTopDepartList();
            if (CollUtil.isEmpty(topList)) {
                return topList;
            }
            for (DepartTreeVO top : topList) {
                top.setChildList(getLowerDept(top.getId()));
            }
        }
        return topList;
    }

    private List<DepartTreeVO> getLowerDept(Long parentId) {
        List<DepartTreeVO> nodeList = super.baseMapper.getDeptByParentId(parentId);
        if (CollUtil.isEmpty(nodeList)) {
            return nodeList;
        }
        for (DepartTreeVO lower : nodeList) {
            List<DepartTreeVO> childList = getLowerDept(lower.getId());
            lower.setChildList(childList);
        }
        return nodeList;
    }

}
