package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.Department;
import com.nbk.smart5c.pojo.bo.DepartmentSaveBO;
import com.nbk.smart5c.pojo.bo.DepartmentUpdateBO;
import com.nbk.smart5c.pojo.qo.DepartmentQO;
import com.nbk.smart5c.pojo.vo.DepartmentVO;
import com.nbk.smart5c.service.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DepartmentController 单元测试（Mock Service 层）
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class DepartmentControllerTest {

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private DepartmentController departmentController;

    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department();
    }

    @Test
    void testQueryById() {
        when(departmentService.queryById("1")).thenReturn(department);

        ResultUtil<Department> result = departmentController.queryById("1");

        assertEquals(200, result.getCode());
        assertEquals(department, result.getResult());
        verify(departmentService, times(1)).queryById("1");
    }

    @Test
    void testQueryAndPaging() {
        Page<DepartmentVO> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new DepartmentVO()));
        page.setTotal(1);
        when(departmentService.queryAndPaging(any(DepartmentQO.class))).thenReturn(page);

        ResultUtil<IPage<DepartmentVO>> result = departmentController.queryAndPaging(new DepartmentQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().getTotal());
        verify(departmentService, times(1)).queryAndPaging(any(DepartmentQO.class));
    }

    @Test
    void testQueryAll() {
        List<DepartmentVO> list = Collections.singletonList(new DepartmentVO());
        when(departmentService.queryAll(any(DepartmentQO.class))).thenReturn(list);

        ResultUtil<List<DepartmentVO>> result = departmentController.queryAll(new DepartmentQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(departmentService, times(1)).queryAll(any(DepartmentQO.class));
    }

    @Test
    void testDeleteById() {
        when(departmentService.removeById(anyInt())).thenReturn(true);

        ResultUtil<Boolean> result = departmentController.deleteById(1);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(departmentService, times(1)).removeById(1);
    }

    @Test
    void testDeleteByIds() {
        List<Integer> ids = Collections.singletonList(1);
        when(departmentService.removeByIds(ids)).thenReturn(true);

        ResultUtil<Boolean> result = departmentController.deleteByIds(ids);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(departmentService, times(1)).removeByIds(ids);
    }

    @Test
    void testSave() {
        when(departmentService.save(any(DepartmentSaveBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = departmentController.save(new DepartmentSaveBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(departmentService, times(1)).save(any(DepartmentSaveBO.class));
    }

    @Test
    void testUpdateById() {
        when(departmentService.updateById(any(DepartmentUpdateBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = departmentController.updateById(new DepartmentUpdateBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(departmentService, times(1)).updateById(any(DepartmentUpdateBO.class));
    }
}
