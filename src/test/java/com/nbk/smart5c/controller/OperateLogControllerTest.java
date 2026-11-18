package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.OperateLog;
import com.nbk.smart5c.pojo.bo.OperateLogSaveBO;
import com.nbk.smart5c.pojo.bo.OperateLogUpdateBO;
import com.nbk.smart5c.pojo.qo.OperateLogQO;
import com.nbk.smart5c.pojo.vo.OperateLogVO;
import com.nbk.smart5c.service.OperateLogService;
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
 * OperateLogController 单元测试（Mock Service 层）
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class OperateLogControllerTest {

    @Mock
    private OperateLogService operateLogService;

    @InjectMocks
    private OperateLogController operateLogController;

    private OperateLog operateLog;

    @BeforeEach
    void setUp() {
        operateLog = new OperateLog();
    }

    @Test
    void testQueryById() {
        when(operateLogService.queryById("1")).thenReturn(operateLog);

        ResultUtil<OperateLog> result = operateLogController.queryById("1");

        assertEquals(200, result.getCode());
        assertEquals(operateLog, result.getResult());
        verify(operateLogService, times(1)).queryById("1");
    }

    @Test
    void testQueryAndPaging() {
        Page<OperateLogVO> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new OperateLogVO()));
        page.setTotal(1);
        when(operateLogService.queryAndPaging(any(OperateLogQO.class))).thenReturn(page);

        ResultUtil<IPage<OperateLogVO>> result = operateLogController.queryAndPaging(new OperateLogQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().getTotal());
        verify(operateLogService, times(1)).queryAndPaging(any(OperateLogQO.class));
    }

    @Test
    void testQueryAll() {
        List<OperateLogVO> list = Collections.singletonList(new OperateLogVO());
        when(operateLogService.queryAll(any(OperateLogQO.class))).thenReturn(list);

        ResultUtil<List<OperateLogVO>> result = operateLogController.queryAll(new OperateLogQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(operateLogService, times(1)).queryAll(any(OperateLogQO.class));
    }

    @Test
    void testDeleteById() {
        when(operateLogService.removeById(anyInt())).thenReturn(true);

        ResultUtil<Boolean> result = operateLogController.deleteById(1);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(operateLogService, times(1)).removeById(1);
    }

    @Test
    void testDeleteByIds() {
        List<Integer> ids = Collections.singletonList(1);
        when(operateLogService.removeByIds(ids)).thenReturn(true);

        ResultUtil<Boolean> result = operateLogController.deleteByIds(ids);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(operateLogService, times(1)).removeByIds(ids);
    }

    @Test
    void testSave() {
        when(operateLogService.save(any(OperateLogSaveBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = operateLogController.save(new OperateLogSaveBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(operateLogService, times(1)).save(any(OperateLogSaveBO.class));
    }

    @Test
    void testUpdateById() {
        when(operateLogService.updateById(any(OperateLogUpdateBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = operateLogController.updateById(new OperateLogUpdateBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(operateLogService, times(1)).updateById(any(OperateLogUpdateBO.class));
    }

    @Test
    void testAnalysis() {
        MultipartFile file = mock(MultipartFile.class);
        doNothing().when(operateLogService).analysis(any(MultipartFile.class));

        ResultUtil<Void> result = operateLogController.analysis(file);

        assertEquals(200, result.getCode());
        verify(operateLogService, times(1)).analysis(file);
    }

    @Test
    void testExport() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(operateLogService).export(any(OperateLogQO.class), any());

        ResultUtil<Void> result = operateLogController.export(response, new OperateLogQO());

        assertEquals(200, result.getCode());
        verify(operateLogService, times(1)).export(any(OperateLogQO.class), eq(response));
    }

    @Test
    void testExportTemplate() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(operateLogService).exportTemplate(any());

        ResultUtil<Void> result = operateLogController.exportTemplate(response);

        assertEquals(200, result.getCode());
        verify(operateLogService, times(1)).exportTemplate(eq(response));
    }
}
