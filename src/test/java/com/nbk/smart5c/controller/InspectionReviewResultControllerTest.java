package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.InspectionReviewResult;
import com.nbk.smart5c.pojo.bo.InspectionReviewResultSaveBO;
import com.nbk.smart5c.pojo.bo.InspectionReviewResultUpdateBO;
import com.nbk.smart5c.pojo.qo.InspectionReviewResultQO;
import com.nbk.smart5c.pojo.vo.InspectionReviewResultVO;
import com.nbk.smart5c.service.InspectionReviewResultService;
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
 * InspectionReviewResultController 单元测试（Mock Service 层）
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class InspectionReviewResultControllerTest {

    @Mock
    private InspectionReviewResultService inspectionReviewResultService;

    @InjectMocks
    private InspectionReviewResultController inspectionReviewResultController;

    private InspectionReviewResult inspectionReviewResult;

    @BeforeEach
    void setUp() {
        inspectionReviewResult = new InspectionReviewResult();
    }

    @Test
    void testQueryById() {
        when(inspectionReviewResultService.queryById("1")).thenReturn(inspectionReviewResult);

        ResultUtil<InspectionReviewResult> result = inspectionReviewResultController.queryById("1");

        assertEquals(200, result.getCode());
        assertEquals(inspectionReviewResult, result.getResult());
        verify(inspectionReviewResultService, times(1)).queryById("1");
    }

    @Test
    void testQueryAndPaging() {
        Page<InspectionReviewResultVO> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new InspectionReviewResultVO()));
        page.setTotal(1);
        when(inspectionReviewResultService.queryAndPaging(any(InspectionReviewResultQO.class))).thenReturn(page);

        ResultUtil<IPage<InspectionReviewResultVO>> result =
                inspectionReviewResultController.queryAndPaging(new InspectionReviewResultQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().getTotal());
        verify(inspectionReviewResultService, times(1)).queryAndPaging(any(InspectionReviewResultQO.class));
    }

    @Test
    void testQueryAll() {
        List<InspectionReviewResultVO> list = Collections.singletonList(new InspectionReviewResultVO());
        when(inspectionReviewResultService.queryAll(any(InspectionReviewResultQO.class))).thenReturn(list);

        ResultUtil<List<InspectionReviewResultVO>> result =
                inspectionReviewResultController.queryAll(new InspectionReviewResultQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(inspectionReviewResultService, times(1)).queryAll(any(InspectionReviewResultQO.class));
    }

    @Test
    void testDeleteById() {
        when(inspectionReviewResultService.removeById(anyInt())).thenReturn(true);

        ResultUtil<Boolean> result = inspectionReviewResultController.deleteById(1);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(inspectionReviewResultService, times(1)).removeById(1);
    }

    @Test
    void testDeleteByIds() {
        List<Integer> ids = Collections.singletonList(1);
        when(inspectionReviewResultService.removeByIds(ids)).thenReturn(true);

        ResultUtil<Boolean> result = inspectionReviewResultController.deleteByIds(ids);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(inspectionReviewResultService, times(1)).removeByIds(ids);
    }

    @Test
    void testSave() {
        when(inspectionReviewResultService.save(any(InspectionReviewResultSaveBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = inspectionReviewResultController.save(new InspectionReviewResultSaveBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(inspectionReviewResultService, times(1)).save(any(InspectionReviewResultSaveBO.class));
    }

    @Test
    void testUpdateById() {
        when(inspectionReviewResultService.updateById(any(InspectionReviewResultUpdateBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = inspectionReviewResultController.updateById(new InspectionReviewResultUpdateBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(inspectionReviewResultService, times(1)).updateById(any(InspectionReviewResultUpdateBO.class));
    }

    @Test
    void testAnalysis() {
        MultipartFile file = mock(MultipartFile.class);
        doNothing().when(inspectionReviewResultService).analysis(any(MultipartFile.class));

        ResultUtil<Void> result = inspectionReviewResultController.analysis(file);

        assertEquals(200, result.getCode());
        verify(inspectionReviewResultService, times(1)).analysis(file);
    }

    @Test
    void testExport() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(inspectionReviewResultService).export(any(InspectionReviewResultQO.class), any());

        ResultUtil<Void> result = inspectionReviewResultController.export(response, new InspectionReviewResultQO());

        assertEquals(200, result.getCode());
        verify(inspectionReviewResultService, times(1)).export(any(InspectionReviewResultQO.class), eq(response));
    }
}
