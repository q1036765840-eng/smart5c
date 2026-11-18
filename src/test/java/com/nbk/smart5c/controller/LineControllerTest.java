package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.Line;
import com.nbk.smart5c.pojo.bo.LineSaveBO;
import com.nbk.smart5c.pojo.bo.LineUpdateBO;
import com.nbk.smart5c.pojo.qo.LineQO;
import com.nbk.smart5c.pojo.vo.DropDownListVO;
import com.nbk.smart5c.pojo.vo.LineVO;
import com.nbk.smart5c.service.LineService;
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
 * LineController 单元测试（Mock Service 层）
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class LineControllerTest {

    @Mock
    private LineService lineService;

    @InjectMocks
    private LineController lineController;

    private Line line;

    @BeforeEach
    void setUp() {
        line = new Line();
    }

    @Test
    void testQueryById() {
        when(lineService.queryById("1")).thenReturn(line);

        ResultUtil<Line> result = lineController.queryById("1");

        assertEquals(200, result.getCode());
        assertEquals(line, result.getResult());
        verify(lineService, times(1)).queryById("1");
    }

    @Test
    void testQueryAndPaging() {
        Page<LineVO> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new LineVO()));
        page.setTotal(1);
        when(lineService.queryAndPaging(any(LineQO.class))).thenReturn(page);

        ResultUtil<IPage<LineVO>> result = lineController.queryAndPaging(new LineQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().getTotal());
        verify(lineService, times(1)).queryAndPaging(any(LineQO.class));
    }

    @Test
    void testQueryAll() {
        List<LineVO> list = Collections.singletonList(new LineVO());
        when(lineService.queryAll(any(LineQO.class))).thenReturn(list);

        ResultUtil<List<LineVO>> result = lineController.queryAll(new LineQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(lineService, times(1)).queryAll(any(LineQO.class));
    }

    @Test
    void testDeleteById() {
        when(lineService.removeById(anyInt())).thenReturn(true);

        ResultUtil<Boolean> result = lineController.deleteById(1);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(lineService, times(1)).removeById(1);
    }

    @Test
    void testDeleteByIds() {
        List<Integer> ids = Collections.singletonList(1);
        when(lineService.removeByIds(ids)).thenReturn(true);

        ResultUtil<Boolean> result = lineController.deleteByIds(ids);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(lineService, times(1)).removeByIds(ids);
    }

    @Test
    void testSave() {
        when(lineService.save(any(LineSaveBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = lineController.save(new LineSaveBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(lineService, times(1)).save(any(LineSaveBO.class));
    }

    @Test
    void testUpdateById() {
        when(lineService.updateById(any(LineUpdateBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = lineController.updateById(new LineUpdateBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(lineService, times(1)).updateById(any(LineUpdateBO.class));
    }

    @Test
    void testAnalysis() {
        MultipartFile file = mock(MultipartFile.class);
        doNothing().when(lineService).analysis(any(MultipartFile.class));

        ResultUtil<Void> result = lineController.analysis(file);

        assertEquals(200, result.getCode());
        verify(lineService, times(1)).analysis(file);
    }

    @Test
    void testExport() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(lineService).export(any(LineQO.class), any());

        ResultUtil<Void> result = lineController.export(response, new LineQO());

        assertEquals(200, result.getCode());
        verify(lineService, times(1)).export(any(LineQO.class), eq(response));
    }

    @Test
    void testExportTemplate() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(lineService).exportTemplate(any());

        ResultUtil<Void> result = lineController.exportTemplate(response);

        assertEquals(200, result.getCode());
        verify(lineService, times(1)).exportTemplate(eq(response));
    }

    @Test
    void testGetLineDownList() {
        List<DropDownListVO> list = Collections.singletonList(new DropDownListVO());
        when(lineService.getLineDownList(1L)).thenReturn(list);

        ResultUtil<List<DropDownListVO>> result = lineController.getLineDownList(1L);

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(lineService, times(1)).getLineDownList(1L);
    }
}
