package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.UserRole;
import com.nbk.smart5c.pojo.bo.UserRoleSaveBO;
import com.nbk.smart5c.pojo.bo.UserRoleUpdateBO;
import com.nbk.smart5c.pojo.qo.UserRoleQO;
import com.nbk.smart5c.pojo.vo.UserRoleVO;
import com.nbk.smart5c.service.UserRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserRoleController 单元测试（Mock Service 层）
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class UserRoleControllerTest {

    @Mock
    private UserRoleService userRoleService;

    @InjectMocks
    private UserRoleController userRoleController;

    private UserRole userRole;

    @BeforeEach
    void setUp() {
        userRole = new UserRole();
    }

    @Test
    void testQueryById() {
        when(userRoleService.queryById("1")).thenReturn(userRole);

        ResultUtil<UserRole> result = userRoleController.queryById("1");

        assertEquals(200, result.getCode());
        assertEquals(userRole, result.getResult());
        verify(userRoleService, times(1)).queryById("1");
    }

    @Test
    void testQueryAndPaging() {
        Page<UserRoleVO> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new UserRoleVO()));
        page.setTotal(1);
        when(userRoleService.queryAndPaging(any(UserRoleQO.class))).thenReturn(page);

        ResultUtil<IPage<UserRoleVO>> result = userRoleController.queryAndPaging(new UserRoleQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().getTotal());
        verify(userRoleService, times(1)).queryAndPaging(any(UserRoleQO.class));
    }

    @Test
    void testQueryAll() {
        List<UserRoleVO> list = Collections.singletonList(new UserRoleVO());
        when(userRoleService.queryAll(any(UserRoleQO.class))).thenReturn(list);

        ResultUtil<List<UserRoleVO>> result = userRoleController.queryAll(new UserRoleQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(userRoleService, times(1)).queryAll(any(UserRoleQO.class));
    }

    @Test
    void testDeleteById() {
        when(userRoleService.removeById(anyInt())).thenReturn(true);

        ResultUtil<Boolean> result = userRoleController.deleteById(1);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(userRoleService, times(1)).removeById(1);
    }

    @Test
    void testDeleteByIds() {
        List<Integer> ids = Collections.singletonList(1);
        when(userRoleService.removeByIds(ids)).thenReturn(true);

        ResultUtil<Boolean> result = userRoleController.deleteByIds(ids);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(userRoleService, times(1)).removeByIds(ids);
    }

    @Test
    void testSave() {
        when(userRoleService.save(any(UserRoleSaveBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = userRoleController.save(new UserRoleSaveBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(userRoleService, times(1)).save(any(UserRoleSaveBO.class));
    }

    @Test
    void testUpdateById() {
        when(userRoleService.updateById(any(UserRoleUpdateBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = userRoleController.updateById(new UserRoleUpdateBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(userRoleService, times(1)).updateById(any(UserRoleUpdateBO.class));
    }

    @Test
    void testAnalysis() {
        MultipartFile file = mock(MultipartFile.class);
        doNothing().when(userRoleService).analysis(any(MultipartFile.class));

        ResultUtil<Void> result = userRoleController.analysis(file);

        assertEquals(200, result.getCode());
        verify(userRoleService, times(1)).analysis(file);
    }

    @Test
    void testExport() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(userRoleService).export(any(UserRoleQO.class), any());

        ResultUtil<Void> result = userRoleController.export(response, new UserRoleQO());

        assertEquals(200, result.getCode());
        verify(userRoleService, times(1)).export(any(UserRoleQO.class), eq(response));
    }

    @Test
    void testExportTemplate() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(userRoleService).exportTemplate(any());

        ResultUtil<Void> result = userRoleController.exportTemplate(response);

        assertEquals(200, result.getCode());
        verify(userRoleService, times(1)).exportTemplate(eq(response));
    }
}
