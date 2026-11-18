package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.RoleInfo;
import com.nbk.smart5c.pojo.bo.RoleInfoSaveBO;
import com.nbk.smart5c.pojo.bo.RoleInfoUpdateBO;
import com.nbk.smart5c.pojo.qo.RoleInfoQO;
import com.nbk.smart5c.pojo.vo.DropDownListVO;
import com.nbk.smart5c.pojo.vo.RoleInfoVO;
import com.nbk.smart5c.service.RoleInfoService;
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
 * RoleInfoController 单元测试（Mock Service 层）
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class RoleInfoControllerTest {

    @Mock
    private RoleInfoService roleInfoService;

    @InjectMocks
    private RoleInfoController roleInfoController;

    private RoleInfo roleInfo;

    @BeforeEach
    void setUp() {
        roleInfo = new RoleInfo();
    }

    @Test
    void testQueryById() {
        when(roleInfoService.queryById("1")).thenReturn(roleInfo);

        ResultUtil<RoleInfo> result = roleInfoController.queryById("1");

        assertEquals(200, result.getCode());
        assertEquals(roleInfo, result.getResult());
        verify(roleInfoService, times(1)).queryById("1");
    }

    @Test
    void testQueryAndPaging() {
        Page<RoleInfoVO> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new RoleInfoVO()));
        page.setTotal(1);
        when(roleInfoService.queryAndPaging(any(RoleInfoQO.class))).thenReturn(page);

        ResultUtil<IPage<RoleInfoVO>> result = roleInfoController.queryAndPaging(new RoleInfoQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().getTotal());
        verify(roleInfoService, times(1)).queryAndPaging(any(RoleInfoQO.class));
    }

    @Test
    void testQueryAll() {
        List<RoleInfoVO> list = Collections.singletonList(new RoleInfoVO());
        when(roleInfoService.queryAll(any(RoleInfoQO.class))).thenReturn(list);

        ResultUtil<List<RoleInfoVO>> result = roleInfoController.queryAll(new RoleInfoQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(roleInfoService, times(1)).queryAll(any(RoleInfoQO.class));
    }

    @Test
    void testDeleteById() {
        when(roleInfoService.removeById(anyInt())).thenReturn(true);

        ResultUtil<Boolean> result = roleInfoController.deleteById(1);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(roleInfoService, times(1)).removeById(1);
    }

    @Test
    void testDeleteByIds() {
        List<Integer> ids = Collections.singletonList(1);
        when(roleInfoService.removeByIds(ids)).thenReturn(true);

        ResultUtil<Boolean> result = roleInfoController.deleteByIds(ids);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(roleInfoService, times(1)).removeByIds(ids);
    }

    @Test
    void testSave() {
        when(roleInfoService.save(any(RoleInfoSaveBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = roleInfoController.save(new RoleInfoSaveBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(roleInfoService, times(1)).save(any(RoleInfoSaveBO.class));
    }

    @Test
    void testUpdateById() {
        when(roleInfoService.updateById(any(RoleInfoUpdateBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = roleInfoController.updateById(new RoleInfoUpdateBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(roleInfoService, times(1)).updateById(any(RoleInfoUpdateBO.class));
    }

    @Test
    void testAnalysis() {
        MultipartFile file = mock(MultipartFile.class);
        doNothing().when(roleInfoService).analysis(any(MultipartFile.class));

        ResultUtil<Void> result = roleInfoController.analysis(file);

        assertEquals(200, result.getCode());
        verify(roleInfoService, times(1)).analysis(file);
    }

    @Test
    void testExport() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(roleInfoService).export(any(RoleInfoQO.class), any());

        ResultUtil<Void> result = roleInfoController.export(response, new RoleInfoQO());

        assertEquals(200, result.getCode());
        verify(roleInfoService, times(1)).export(any(RoleInfoQO.class), eq(response));
    }

    @Test
    void testExportTemplate() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(roleInfoService).exportTemplate(any());

        ResultUtil<Void> result = roleInfoController.exportTemplate(response);

        assertEquals(200, result.getCode());
        verify(roleInfoService, times(1)).exportTemplate(eq(response));
    }

    @Test
    void testGetRoleDownList() {
        List<DropDownListVO> list = Collections.singletonList(new DropDownListVO());
        when(roleInfoService.getRoleDownList()).thenReturn(list);

        ResultUtil<List<DropDownListVO>> result = roleInfoController.getRoleDownList();

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(roleInfoService, times(1)).getRoleDownList();
    }
}
