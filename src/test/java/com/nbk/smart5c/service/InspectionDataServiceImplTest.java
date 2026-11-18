//package com.nbk.smart5c.service;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.nbk.smart5c.client.AiClient;
//import com.nbk.smart5c.common.utils.ApiUtils;
//import com.nbk.smart5c.entity.*;
//import com.nbk.smart5c.exception.ServiceException;
//import com.nbk.smart5c.mapper.*;
//import com.nbk.smart5c.pojo.bo.*;
//import com.nbk.smart5c.pojo.qo.InspectionDataQO;
//import com.nbk.smart5c.pojo.vo.InspectionReviewResultVO;
//import com.nbk.smart5c.service.Impl.InspectionDataServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.servlet.http.HttpServletResponse;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.locks.ReentrantLock;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class InspectionDataServiceImplTest {
//
//    @Mock
//    private InspectionDataMapper inspectionDataMapper;
//
//    @Mock
//    private InspectionReviewResultMapper inspectionReviewResultMapper;
//
//    @Mock
//    private AiClient aiClient;
//
//    @Mock
//    private SectionMapper sectionMapper;
//
//    @Mock
//    private LineMapper lineMapper;
//
//    @Mock
//    private StationMapper stationMapper;
//
//    @Mock
//    private DeviceMapper deviceMapper;
//
//    @Mock
//    private DeviceService<DeviceMapper, Device> deviceService;
//
//    @Mock
//    private ApiUtils apiUtils;
//
//    @Mock
//    private HttpServletResponse response;
//
//    @Mock
//    private MultipartFile multipartFile;
//
//    @Spy
//    private InspectionDataServiceImpl inspectionDataService;
//
//    private InspectionData testInspectionData;
//    private InspectionDataSaveBO testSaveBO;
//    private InspectionDataUpdateBO testUpdateBO;
//    private InspectionDataQO testQuery;
//
//    @BeforeEach
//    void setUp() {
//        // 初始化测试数据
//        testInspectionData = new InspectionData();
//        testInspectionData.setDeviceId(1L);
//        testInspectionData.setSectionId(1L);
//        testInspectionData.setLineId(1L);
//        testInspectionData.setStationId(1L);
//        testInspectionData.setLocomotiveNumber("TEST-001");
//        testInspectionData.setAnalysisStatus(0);
//        testInspectionData.setPantographStatus(1);
//        testInspectionData.setLocomotiveImage("/上海高铁段/5C/5C数据2026.01.08/杭黄线/2025-10/20251007120156_杭黄线_三阳站_2292_K1_CRH2L");
//
//        testSaveBO = new InspectionDataSaveBO();
//        testSaveBO.setDeviceId(1L);
//        testSaveBO.setLocomotiveNumber("TEST-SAVE-001");
//
//        testUpdateBO = new InspectionDataUpdateBO();
//        testUpdateBO.setId(1L);
//        testUpdateBO.setLocomotiveNumber("TEST-UPDATE-001");
//
//        testQuery = new InspectionDataQO();
//        testQuery.setPageNo(1);
//        testQuery.setPageSize(10);
//        testQuery.setLocomotiveNumber("TEST");
//
//        ReflectionTestUtils.setField(inspectionDataService, "baseMapper", inspectionDataMapper);
//
//        // 设置其他私有字段
//        ReflectionTestUtils.setField(inspectionDataService, "inspectDataPath", "/test/path");
//        ReflectionTestUtils.setField(inspectionDataService, "inspectHost", "http://testhost");
//        ReflectionTestUtils.setField(inspectionDataService, "reconnectLock", new ReentrantLock());
//        ReflectionTestUtils.setField(inspectionDataService, "processMap", new ConcurrentHashMap<>());
//
//    }
/// /        // 手动设置baseMapper
/// /        Field baseMapperField = null;
/// /        try {
/// /            baseMapperField = ServiceImpl.class.getDeclaredField("baseMapper");
/// /        } catch (NoSuchFieldException e) {
/// /            e.printStackTrace();
/// /        }
/// /        baseMapperField.setAccessible(true);
/// /        try {
/// /            baseMapperField.set(inspectionDataService, inspectionDataMapper);
/// /        } catch (IllegalAccessException e) {
/// /            e.printStackTrace();
/// /        }
//    @Test
//    void testSave_Success() {
//        // 准备
//        when(inspectionDataMapper.insert(any(InspectionData.class))).thenReturn(1);
//
//        // 执行
//        Boolean result = inspectionDataService.save(testSaveBO);
//
//        // 验证
//        assertTrue(result);
//        verify(inspectionDataMapper, times(1)).insert(any(InspectionData.class));
//    }
//
//    @Test
//    void testSave_Failure() {
//        // 准备
//        when(inspectionDataMapper.insert(any(InspectionData.class))).thenReturn(0);
//
//        // 执行
//        Boolean result = inspectionDataService.save(testSaveBO);
//
//        // 验证
//        assertFalse(result);
//        verify(inspectionDataMapper, times(1)).insert(any(InspectionData.class));
//    }
//
//    @Test
//    void testQueryById_Found() {
//        // 准备
//        String testId = "123";
//        when(inspectionDataMapper.selectById(testId)).thenReturn(testInspectionData);
//
//        // 执行
//        InspectionData result = inspectionDataService.queryById(testId);
//
//        // 验证
//        assertNotNull(result);
////        assertEquals(testId, result.getId());
//        verify(inspectionDataMapper, times(1)).selectById(testId);
//    }
//
////    @Test
////    void testQueryById_NotFound() {
////        // 准备
////        String testId = "non-existent-id";
////        when(inspectionDataMapper.selectById(testId)).thenReturn(null);
////
////        // 执行
////        InspectionData result = inspectionDataService.queryById(testId);
////
////        // 验证
////        assertNull(result);
////        verify(inspectionDataMapper, times(1)).selectById(testId);
////    }
//
////    @Test
////    void testQueryAndPaging_WithConditions() {
////        // 准备
////        List<InspectionData> mockDataList = Collections.singletonList(testInspectionData);
////        Page<InspectionData> mockPage = new Page<>(1, 10);
////        mockPage.setRecords(mockDataList);
////        mockPage.setTotal(1);
////
////        // 模拟 sectionMapper
////        Section mockSection = new Section();
////        mockSection.setId(1L);
////        mockSection.setSectionName("测试段");
////        when(sectionMapper.selectById(anyLong())).thenReturn(mockSection);
////
////        // 模拟 lineMapper
////        Line mockLine = new Line();
////        mockLine.setId(1L);
////        mockLine.setLineName("测试线路");
////        when(lineMapper.selectById(anyLong())).thenReturn(mockLine);
////
////        // 模拟 stationMapper
////        Station mockStation = new Station();
////        mockStation.setId(1L);
////        mockStation.setStationName("测试区站");
////        when(stationMapper.selectById(anyLong())).thenReturn(mockStation);
////
////        // 模拟 deviceService
////        Device mockDevice = new Device();
////        mockDevice.setId(1L);
////        mockDevice.setDeviceCode("DEV-001");
////        mockDevice.setPillarNo("PILLAR-001");
////        when(deviceService.getById(anyLong())).thenReturn(mockDevice);
////
////        // 模拟基础分页查询
////        when(inspectionDataMapper.selectPage(any(Page.class), any())).thenReturn(mockPage);
////
////        // 执行
////        IPage<InspectionData> result = inspectionDataService.queryAndPaging(testQuery);
////
////        // 验证
////        assertNotNull(result);
////        assertEquals(1, result.getTotal());
////        assertEquals(1, result.getRecords().size());
////        assertEquals("测试段", result.getRecords().get(0).getSectionName());
////        verify(inspectionDataMapper, times(1)).selectPage(any(Page.class), any());
////    }
//
//    @Test
//    void testUpdateById_Success() {
//        // 准备
//        when(inspectionDataMapper.updateById(any(InspectionData.class))).thenReturn(1);
//
//        // 执行
//        Boolean result = inspectionDataService.updateById(testUpdateBO);
//
//        // 验证
//        assertTrue(result);
//        verify(inspectionDataMapper, times(1)).updateById(any(InspectionData.class));
//    }
//
////    @Test
////    void testDeleteById_Success() {
////        Long id = 100L;
////        InspectionData mockData = new InspectionData();
////        mockData.setId(id);
////        // 设置其他必要字段以防空指针（如果父类 removeById 内部有校验）
////        // 1. 模拟父类 getById 返回存在的数据
////        doReturn(mockData).when(inspectionDataService).getById(id);
////        // 2. 模拟父类 removeById 返回成功 (true)
////        doReturn(true).when(inspectionDataService).removeById(any(InspectionData.class));
////        // 执行测试
////        Boolean result = inspectionDataService.deleteById(id);
////        // 验证结果
//////        assertTrue(result, "删除操作应返回 true");
////        // 验证交互逻辑
////        // 验证是否调用了父类的 getById
////        verify(inspectionDataService, times(1)).getById(id);
////        // 验证是否调用了关联表的删除方法，且参数正确
////        verify(inspectionReviewResultMapper, times(1)).deleteByInspectionId(id);
////        // 验证是否调用了父类的 removeById，且传入了正确的对象
////        verify(inspectionDataService, times(1)).removeById(mockData);
////    }
//
////    @Test
////    void testStartAnalysis_Success() {
////        // 准备
////        List<Long> idList = Arrays.asList(1L, 2L, 3L);
////        List<InspectionData> mockDataList = new ArrayList<>();
////
////        for (Long id : idList) {
////            InspectionData data = new InspectionData();
////            data.setId(1L);
////            data.setAnalysisStatus(0); // WAIT_ANALYZE
////            data.setLocomotiveImage("/test/path/" + id);
////            mockDataList.add(data);
////        }
////
////        when(inspectionDataMapper.selectBatchIds(idList)).thenReturn(mockDataList);
////        when(inspectionDataMapper.updateById(any(InspectionData.class))).thenReturn(1);
////
////        // 模拟 FilteredFileLister 返回的文件列表
////        // 注意：这里使用了PowerMock/Mockito的静态方法模拟
////        // 如果使用Mockito 3.4.0+，可以用Mockito.mockStatic
////        // 这里先假设我们使用依赖注入的方式处理FilteredFileLister
////
////        // 模拟AI客户端响应
////        AiAnalysisRespBO mockResp = new AiAnalysisRespBO();
////        mockResp.setSuccess(true);
////        AiAnalysisRespDataBO data = new AiAnalysisRespDataBO();
////        data.setLicense_plate("AI-DETECTED-001");
////        data.setHas_sdg(true);
////        data.setStatus("正常");
////        mockResp.setData(data);
////
////        when(aiClient.analysis(any(AiRequestBO.class))).thenReturn(mockResp);
////
////        // 执行
////        Boolean result = inspectionDataService.startAnalysis(idList);
////
////        // 验证
////        assertTrue(result);
////        verify(inspectionDataMapper, times(1)).selectBatchIds(idList);
////        // 验证updateById至少被调用一次
////        verify(inspectionDataMapper, atLeastOnce()).updateById(any(InspectionData.class));
////    }
//
//    @Test
//    void testStartAnalysis_EmptyList() {
//        // 准备
//        List<Long> idList = Collections.emptyList();
//        when(inspectionDataMapper.selectBatchIds(idList)).thenReturn(Collections.emptyList());
//
//        // 执行
//        Boolean result = inspectionDataService.startAnalysis(idList);
//
//        // 验证
//        assertFalse(result);
//        verify(inspectionDataMapper, times(1)).selectBatchIds(idList);
//        verify(inspectionDataMapper, never()).updateById(any(InspectionData.class));
//    }
//
//    @Test
//    void testValidImage_Success() {
//        // 准备
////        String testImagePath = "/test/path/image.jpg";
////        AiRequestBO capturedRequest = new AiRequestBO();
////
////        ImageValidRespBO mockResp = new ImageValidRespBO();
////        mockResp.setData(1); // 有效图片
////
////        when(aiClient.imageValid(any(AiRequestBO.class))).thenReturn(mockResp);
////
////        // 执行
////        Boolean result = inspectionDataService.validImage(testImagePath);
////
////        // 验证
////        assertTrue(result);
////        verify(aiClient, times(1)).imageValid(argThat(request -> {
////            assertEquals(testImagePath, request.getImage());
////            return true;
////        }));
//    }
//
////    @Test
////    void testGetReviewData_WithImages() {
////        // 准备
////        Long testId = 123L;
////
////        // 模拟主数据查询
////        when(inspectionDataMapper.selectById(testId)).thenReturn(testInspectionData);
////
////        // 模拟段、线路、区站查询
////        Section mockSection = new Section();
////        mockSection.setSectionName("测试段");
////        when(sectionMapper.selectById(anyLong())).thenReturn(mockSection);
////
////        Line mockLine = new Line();
////        mockLine.setLineName("测试线路");
////        when(lineMapper.selectById(anyLong())).thenReturn(mockLine);
////
////        Station mockStation = new Station();
////        mockStation.setStationName("测试区站");
////        when(stationMapper.selectById(anyLong())).thenReturn(mockStation);
////
////        // 模拟设备查询
////        Device mockDevice = new Device();
////        mockDevice.setDeviceCode("DEV-001");
////        mockDevice.setPillarNo("PILLAR-001");
////        when(deviceService.getById(anyLong())).thenReturn(mockDevice);
////
////        // 模拟评审结果查询
////        InspectionReviewResultVO mockReviewVO = new InspectionReviewResultVO();
////        mockReviewVO.setMarkedRegions("[[100,200,300,400]]");
////        mockReviewVO.setCreatedUser("审核员");
////        when(inspectionReviewResultMapper.getByInspectionIdAndImagePath(anyLong(), anyString()))
////            .thenReturn(mockReviewVO);
////
////        // 执行
////        InspectionData result = inspectionDataService.getReviewData(testId);
////
////        // 验证
////        assertNotNull(result);
////        assertEquals("测试段", result.getSectionName());
////        assertEquals("测试线路", result.getLineName());
////        assertEquals("测试区站", result.getStationName());
////        assertEquals("DEV-001", result.getDeviceCode());
////        verify(inspectionDataMapper, times(1)).selectById(testId);
////    }
//
//    // 异常情况测试
//    @Test
//    void testParseData_FileNotExists_ThrowsException() {
//        // 准备
//        String nonExistentPath = "/non/existent/path";
//
//        // 执行 & 验证
//        assertThrows(ServiceException.class, () -> {
//            inspectionDataService.parseData(nonExistentPath);
//        });
//    }
//
////    @Test
////    void testStartAnalysis_AIException_ContinuesProcessing() {
////        // 准备
////        List<Long> idList = Collections.singletonList(1L);
////        List<InspectionData> mockDataList = Collections.singletonList(testInspectionData);
////
////        when(inspectionDataMapper.selectBatchIds(idList)).thenReturn(mockDataList);
////        when(inspectionDataMapper.updateById(any(InspectionData.class))).thenReturn(1);
////
////        // 模拟AI客户端抛出异常
////        when(aiClient.analysis(any(AiRequestBO.class))).thenThrow(new RuntimeException("AI服务异常"));
////
////        // 执行 - 不应抛出异常，应该继续处理
////        Boolean result = inspectionDataService.startAnalysis(idList);
////
////        // 验证
////        assertTrue(result);
////        verify(aiClient, atLeastOnce()).analysis(any(AiRequestBO.class));
////        // 即使AI异常，数据状态仍应更新
////        verify(inspectionDataMapper, atLeastOnce()).updateById(any(InspectionData.class));
////    }
//}