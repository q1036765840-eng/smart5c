package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.RoleMenu;
import com.nbk.smart5c.pojo.bo.RoleMenuSaveBO;
import com.nbk.smart5c.pojo.bo.RoleMenuUpdateBO;
import com.nbk.smart5c.pojo.qo.RoleMenuQO;
import com.nbk.smart5c.pojo.vo.RoleMenuVO;
import com.nbk.smart5c.service.RoleMenuService;
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
 * RoleMenuController 单元测试（Mock Service 层）
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class RoleMenuControllerTest {

    @Mock
    private RoleMenuService roleMenuService;

    @InjectMocks
    private RoleMenuController roleMenuController;

    private RoleMenu roleMenu;

    @BeforeEach
    void setUp() {
        roleMenu = new RoleMenu();
    }

    @Test
    void testQueryById() {
        when(roleMenuService.queryById("1")).thenReturn(roleMenu);

        ResultUtil<RoleMenu> result = roleMenuController.queryById("1");

        assertEquals(200, result.getCode());
        assertEquals(roleMenu, result.getResult());
        verify(roleMenuService, times(1)).queryById("1");
    }

    @Test
    void testQueryAndPaging() {
        Page<RoleMenuVO> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new RoleMenuVO()));
        page.setTotal(1);
        when(roleMenuService.queryAndPaging(any(RoleMenuQO.class))).thenReturn(page);

        ResultUtil<IPage<RoleMenuVO>> result = roleMenuController.queryAndPaging(new RoleMenuQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().getTotal());
        verify(roleMenuService, times(1)).queryAndPaging(any(RoleMenuQO.class));
    }

    @Test
    void testQueryAll() {
        List<RoleMenuVO> list = Collections.singletonList(new RoleMenuVO());
        when(roleMenuService.queryAll(any(RoleMenuQO.class))).thenReturn(list);

        ResultUtil<List<RoleMenuVO>> result = roleMenuController.queryAll(new RoleMenuQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(roleMenuService, times(1)).queryAll(any(RoleMenuQO.class));
    }

    @Test
    void testDeleteById() {
        when(roleMenuService.removeById(anyInt())).thenReturn(true);

        ResultUtil<Boolean> result = roleMenuController.deleteById(1);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(roleMenuService, times(1)).removeById(1);
    }

    @Test
    void testDeleteByIds() {
        List<Integer> ids = Collections.singletonList(1);
        when(roleMenuService.removeByIds(ids)).thenReturn(true);

        ResultUtil<Boolean> result = roleMenuController.deleteByIds(ids);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(roleMenuService, times(1)).removeByIds(ids);
    }

    @Test
    void testSave() {
        when(roleMenuService.save(any(RoleMenuSaveBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = roleMenuController.save(new RoleMenuSaveBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(roleMenuService, times(1)).save(any(RoleMenuSaveBO.class));
    }

    @Test
    void testUpdateById() {
        when(roleMenuService.updateById(any(RoleMenuUpdateBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = roleMenuController.updateById(new RoleMenuUpdateBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(roleMenuService, times(1)).updateById(any(RoleMenuUpdateBO.class));
    }

    @Test
    void testAnalysis() {
        MultipartFile file = mock(MultipartFile.class);
        doNothing().when(roleMenuService).analysis(any(MultipartFile.class));

        ResultUtil<Void> result = roleMenuController.analysis(file);

        assertEquals(200, result.getCode());
        verify(roleMenuService, times(1)).analysis(file);
    }

    @Test
    void testExport() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(roleMenuService).export(any(RoleMenuQO.class), any());

        ResultUtil<Void> result = roleMenuController.export(response, new RoleMenuQO());

        assertEquals(200, result.getCode());
        verify(roleMenuService, times(1)).export(any(RoleMenuQO.class), eq(response));
    }

    @Test
    void testExportTemplate() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(roleMenuService).exportTemplate(any());

        ResultUtil<Void> result = roleMenuController.exportTemplate(response);

        assertEquals(200, result.getCode());
        verify(roleMenuService, times(1)).exportTemplate(eq(response));
    }

    @Test
    void testGetRoleMenuTree() {
        // getRoleMenuTree 目前直接返回成功，不调用 service
        ResultUtil<Void> result = roleMenuController.getRoleMenuTree(1L);

        assertEquals(200, result.getCode());
        verifyNoInteractions(roleMenuService);
    }
}
