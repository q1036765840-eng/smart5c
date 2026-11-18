package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.Menu;
import com.nbk.smart5c.pojo.bo.MenuSaveBO;
import com.nbk.smart5c.pojo.bo.MenuUpdateBO;
import com.nbk.smart5c.pojo.qo.MenuQO;
import com.nbk.smart5c.pojo.vo.MenuVO;
import com.nbk.smart5c.service.MenuService;
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
 * MenuController 单元测试（Mock Service 层）
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class MenuControllerTest {

    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuController menuController;

    private Menu menu;

    @BeforeEach
    void setUp() {
        menu = new Menu();
    }

    @Test
    void testQueryById() {
        when(menuService.queryById("1")).thenReturn(menu);

        ResultUtil<Menu> result = menuController.queryById("1");

        assertEquals(200, result.getCode());
        assertEquals(menu, result.getResult());
        verify(menuService, times(1)).queryById("1");
    }

    @Test
    void testQueryAndPaging() {
        Page<MenuVO> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new MenuVO()));
        page.setTotal(1);
        when(menuService.queryAndPaging(any(MenuQO.class))).thenReturn(page);

        ResultUtil<IPage<MenuVO>> result = menuController.queryAndPaging(new MenuQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().getTotal());
        verify(menuService, times(1)).queryAndPaging(any(MenuQO.class));
    }

    @Test
    void testQueryAll() {
        List<MenuVO> list = Collections.singletonList(new MenuVO());
        when(menuService.queryAll(any(MenuQO.class))).thenReturn(list);

        ResultUtil<List<MenuVO>> result = menuController.queryAll(new MenuQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(menuService, times(1)).queryAll(any(MenuQO.class));
    }

    @Test
    void testDeleteById() {
        when(menuService.removeById(anyInt())).thenReturn(true);

        ResultUtil<Boolean> result = menuController.deleteById(1);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(menuService, times(1)).removeById(1);
    }

    @Test
    void testDeleteByIds() {
        List<Integer> ids = Collections.singletonList(1);
        when(menuService.removeByIds(ids)).thenReturn(true);

        ResultUtil<Boolean> result = menuController.deleteByIds(ids);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(menuService, times(1)).removeByIds(ids);
    }

    @Test
    void testSave() {
        when(menuService.save(any(MenuSaveBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = menuController.save(new MenuSaveBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(menuService, times(1)).save(any(MenuSaveBO.class));
    }

    @Test
    void testUpdateById() {
        when(menuService.updateById(any(MenuUpdateBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = menuController.updateById(new MenuUpdateBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(menuService, times(1)).updateById(any(MenuUpdateBO.class));
    }

    @Test
    void testAnalysis() {
        MultipartFile file = mock(MultipartFile.class);
        doNothing().when(menuService).analysis(any(MultipartFile.class));

        ResultUtil<Void> result = menuController.analysis(file);

        assertEquals(200, result.getCode());
        verify(menuService, times(1)).analysis(file);
    }

    @Test
    void testExport() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(menuService).export(any(MenuQO.class), any());

        ResultUtil<Void> result = menuController.export(response, new MenuQO());

        assertEquals(200, result.getCode());
        verify(menuService, times(1)).export(any(MenuQO.class), eq(response));
    }

    @Test
    void testExportTemplate() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(menuService).exportTemplate(any());

        ResultUtil<Void> result = menuController.exportTemplate(response);

        assertEquals(200, result.getCode());
        verify(menuService, times(1)).exportTemplate(eq(response));
    }
}
