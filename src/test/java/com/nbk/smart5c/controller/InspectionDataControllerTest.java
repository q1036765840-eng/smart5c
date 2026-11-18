package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.InspectionData;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.pojo.bo.AiAnalysisRespDataBO;
import com.nbk.smart5c.pojo.bo.ConfirmDefectBO;
import com.nbk.smart5c.pojo.bo.InspectionDataReviewBO;
import com.nbk.smart5c.pojo.bo.InspectionDataSaveBO;
import com.nbk.smart5c.pojo.bo.InspectionDataUpdateBO;
import com.nbk.smart5c.pojo.bo.ManualReviewParamBO;
import com.nbk.smart5c.pojo.qo.InspectionDataQO;
import com.nbk.smart5c.pojo.vo.InspectionDataVO;
import com.nbk.smart5c.pojo.vo.InspectionReviewDataVO;
import com.nbk.smart5c.service.InspectionDataService;
import com.nbk.smart5c.service.InspectionReviewResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * InspectionDataController 单元测试（Mock Service 层）
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class InspectionDataControllerTest {

    @Mock
    private InspectionDataService inspectionDataService;

    @Mock
    private InspectionReviewResultService inspectionReviewResultService;

    @InjectMocks
    private InspectionDataController inspectionDataController;

    private InspectionData inspectionData;

    @BeforeEach
    void setUp() {
        inspectionData = new InspectionData();
        inspectionData.setLocomotiveNumber("TEST-001");
    }

    @Test
    void testQueryById() {
        when(inspectionDataService.queryById("1")).thenReturn(inspectionData);

        ResultUtil<InspectionData> result = inspectionDataController.queryById("1");

        assertEquals(200, result.getCode());
        assertEquals(inspectionData, result.getResult());
        verify(inspectionDataService, times(1)).queryById("1");
    }

    @Test
    void testQueryAndPaging() {
        Page<InspectionData> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(inspectionData));
        page.setTotal(1);
        when(inspectionDataService.queryAndPaging(any(InspectionDataQO.class))).thenReturn(page);

        ResultUtil<IPage<InspectionData>> result = inspectionDataController.queryAndPaging(new InspectionDataQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().getTotal());
        verify(inspectionDataService, times(1)).queryAndPaging(any(InspectionDataQO.class));
    }

    @Test
    void testQueryAll() {
        List<InspectionDataVO> list = Collections.singletonList(new InspectionDataVO());
        when(inspectionDataService.queryAll(any(InspectionDataQO.class))).thenReturn(list);

        ResultUtil<List<InspectionDataVO>> result = inspectionDataController.queryAll(new InspectionDataQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(inspectionDataService, times(1)).queryAll(any(InspectionDataQO.class));
    }

    @Test
    void testDeleteById() {
        when(inspectionDataService.deleteById(1L)).thenReturn(true);

        ResultUtil<Boolean> result = inspectionDataController.deleteById(1L);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(inspectionDataService, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteByIds() throws ServiceException {
        List<Long> ids = Collections.singletonList(1L);
        when(inspectionDataService.deleteByIds(ids)).thenReturn(true);

        ResultUtil<Boolean> result = inspectionDataController.deleteByIds(ids);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(inspectionDataService, times(1)).deleteByIds(ids);
    }

    @Test
    void testSave() {
        when(inspectionDataService.save(any(InspectionDataSaveBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = inspectionDataController.save(new InspectionDataSaveBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(inspectionDataService, times(1)).save(any(InspectionDataSaveBO.class));
    }

    @Test
    void testUpdateById() {
        when(inspectionDataService.updateById(any(InspectionDataUpdateBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = inspectionDataController.updateById(new InspectionDataUpdateBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(inspectionDataService, times(1)).updateById(any(InspectionDataUpdateBO.class));
    }

    @Test
    void testAnalysis() {
        MultipartFile file = mock(MultipartFile.class);
        doNothing().when(inspectionDataService).analysis(any(MultipartFile.class));

        ResultUtil<Void> result = inspectionDataController.analysis(file);

        assertEquals(200, result.getCode());
        verify(inspectionDataService, times(1)).analysis(file);
    }

    @Test
    void testExport() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(inspectionDataService).export(any(InspectionDataQO.class), any());

        ResultUtil<Void> result = inspectionDataController.export(response, new InspectionDataQO());

        assertEquals(200, result.getCode());
        verify(inspectionDataService, times(1)).export(any(InspectionDataQO.class), eq(response));
    }

    @Test
    void testExportTemplate() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(inspectionDataService).exportTemplate(any());

        ResultUtil<Void> result = inspectionDataController.exportTemplate(response);

        assertEquals(200, result.getCode());
        verify(inspectionDataService, times(1)).exportTemplate(eq(response));
    }

    @Test
    void testExportInspectionData() throws ServiceException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        List<Long> ids = Arrays.asList(1L, 2L);
        doNothing().when(inspectionDataService).exportInspectionData(anyList(), any());

        inspectionDataController.exportInspectionData(ids, response);

        verify(inspectionDataService, times(1)).exportInspectionData(eq(ids), eq(response));
    }

    @Test
    void testReviewInfoList() {
        List<InspectionReviewDataVO> list = Collections.singletonList(new InspectionReviewDataVO());
        when(inspectionDataService.inspectionDataList(1L)).thenReturn(list);

        ResultUtil<List<InspectionReviewDataVO>> result = inspectionDataController.reviewInfoList(1L);

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(inspectionDataService, times(1)).inspectionDataList(1L);
    }

    @Test
    void testReviewInfoSave() throws ServiceException {
        when(inspectionReviewResultService.saveReviewData(any(InspectionDataReviewBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = inspectionDataController.reviewInfoSave(new InspectionDataReviewBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(inspectionReviewResultService, times(1)).saveReviewData(any(InspectionDataReviewBO.class));
    }

    @Test
    void testParseData() throws ServiceException {
        when(inspectionDataService.parseData(null, "/test/path")).thenReturn(true);

        ResultUtil<Boolean> result = inspectionDataController.parseData("/test/path");

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(inspectionDataService, times(1)).parseData(null, "/test/path");
    }

    @Test
    void testGetReviewData() throws ServiceException {
        when(inspectionDataService.getReviewData(1L)).thenReturn(inspectionData);

        ResultUtil<InspectionData> result = inspectionDataController.getReviewData(1L);

        assertEquals(200, result.getCode());
        assertEquals(inspectionData, result.getResult());
        verify(inspectionDataService, times(1)).getReviewData(1L);
    }

    @Test
    void testValidImage() {
        when(inspectionDataService.validImage("/test/image.jpg")).thenReturn(true);

        ResultUtil<Boolean> result = inspectionDataController.validImage("/test/image.jpg");

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(inspectionDataService, times(1)).validImage("/test/image.jpg");
    }

    @Test
    void testStartAnalysis() {
        ManualReviewParamBO param = new ManualReviewParamBO();
        param.setIdList(Arrays.asList(1L, 2L));
        when(inspectionDataService.startAnalysis(param.getIdList())).thenReturn(true);

        ResultUtil<Boolean> result = inspectionDataController.startAnalysis(param);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(inspectionDataService, times(1)).startAnalysis(param.getIdList());
    }

    @Test
    void testGetParseProcess() {
        when(inspectionDataService.getParseProcess()).thenReturn("50%");

        ResultUtil<String> result = inspectionDataController.getParseProcess();

        assertEquals(200, result.getCode());
        assertEquals("50%", result.getResult());
        verify(inspectionDataService, times(1)).getParseProcess();
    }

    @Test
    void testGetAnalysisProcess() {
        when(inspectionDataService.getAnalysisProcess(1L)).thenReturn("80%");

        ResultUtil<String> result = inspectionDataController.getAnalysisProcess(1L);

        assertEquals(200, result.getCode());
        assertEquals("80%", result.getResult());
        verify(inspectionDataService, times(1)).getAnalysisProcess(1L);
    }

    @Test
    void testAnalysisAll() {
        when(inspectionDataService.analysisAll()).thenReturn(true);

        ResultUtil<Boolean> result = inspectionDataController.analysisAll();

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(inspectionDataService, times(1)).analysisAll();
    }

    @Test
    void testForeignObjectTest() {
        doNothing().when(inspectionDataService).foreignObjectProcess(any(AiAnalysisRespDataBO.class),
                any(InspectionData.class), anyString());

        ResultUtil<Boolean> result = inspectionDataController.foreignObjectTest(new AiAnalysisRespDataBO());

        assertEquals(200, result.getCode());
        verify(inspectionDataService, times(1)).foreignObjectProcess(any(AiAnalysisRespDataBO.class),
                any(InspectionData.class), anyString());
    }

    @Test
    void testDefectTypeList() {
        ResultUtil<List<java.util.Map<String, Object>>> result = inspectionDataController.defectTypeList();

        assertEquals(200, result.getCode());
        assertNotNull(result.getResult());
        assertFalse(result.getResult().isEmpty());
    }
}
