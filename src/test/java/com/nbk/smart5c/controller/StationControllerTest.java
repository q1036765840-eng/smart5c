package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.Station;
import com.nbk.smart5c.pojo.bo.StationSaveBO;
import com.nbk.smart5c.pojo.bo.StationUpdateBO;
import com.nbk.smart5c.pojo.qo.StationQO;
import com.nbk.smart5c.pojo.vo.DropDownListVO;
import com.nbk.smart5c.pojo.vo.StationVO;
import com.nbk.smart5c.service.StationService;
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
 * StationController 单元测试（Mock Service 层）
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class StationControllerTest {

    @Mock
    private StationService stationService;

    @InjectMocks
    private StationController stationController;

    private Station station;

    @BeforeEach
    void setUp() {
        station = new Station();
    }

    @Test
    void testQueryById() {
        when(stationService.queryById("1")).thenReturn(station);

        ResultUtil<Station> result = stationController.queryById("1");

        assertEquals(200, result.getCode());
        assertEquals(station, result.getResult());
        verify(stationService, times(1)).queryById("1");
    }

    @Test
    void testQueryAndPaging() {
        Page<StationVO> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new StationVO()));
        page.setTotal(1);
        when(stationService.queryAndPaging(any(StationQO.class))).thenReturn(page);

        ResultUtil<IPage<StationVO>> result = stationController.queryAndPaging(new StationQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().getTotal());
        verify(stationService, times(1)).queryAndPaging(any(StationQO.class));
    }

    @Test
    void testQueryAll() {
        List<StationVO> list = Collections.singletonList(new StationVO());
        when(stationService.queryAll(any(StationQO.class))).thenReturn(list);

        ResultUtil<List<StationVO>> result = stationController.queryAll(new StationQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(stationService, times(1)).queryAll(any(StationQO.class));
    }

    @Test
    void testDeleteById() {
        when(stationService.removeById(anyInt())).thenReturn(true);

        ResultUtil<Boolean> result = stationController.deleteById(1);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(stationService, times(1)).removeById(1);
    }

    @Test
    void testDeleteByIds() {
        List<Integer> ids = Collections.singletonList(1);
        when(stationService.removeByIds(ids)).thenReturn(true);

        ResultUtil<Boolean> result = stationController.deleteByIds(ids);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(stationService, times(1)).removeByIds(ids);
    }

    @Test
    void testSave() {
        when(stationService.save(any(StationSaveBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = stationController.save(new StationSaveBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(stationService, times(1)).save(any(StationSaveBO.class));
    }

    @Test
    void testUpdateById() {
        when(stationService.updateById(any(StationUpdateBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = stationController.updateById(new StationUpdateBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(stationService, times(1)).updateById(any(StationUpdateBO.class));
    }

    @Test
    void testAnalysis() {
        MultipartFile file = mock(MultipartFile.class);
        doNothing().when(stationService).analysis(any(MultipartFile.class));

        ResultUtil<Void> result = stationController.analysis(file);

        assertEquals(200, result.getCode());
        verify(stationService, times(1)).analysis(file);
    }

    @Test
    void testExport() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(stationService).export(any(StationQO.class), any());

        ResultUtil<Void> result = stationController.export(response, new StationQO());

        assertEquals(200, result.getCode());
        verify(stationService, times(1)).export(any(StationQO.class), eq(response));
    }

    @Test
    void testExportTemplate() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(stationService).exportTemplate(any());

        ResultUtil<Void> result = stationController.exportTemplate(response);

        assertEquals(200, result.getCode());
        verify(stationService, times(1)).exportTemplate(eq(response));
    }

    @Test
    void testGetStationDownList() {
        List<DropDownListVO> list = Collections.singletonList(new DropDownListVO());
        when(stationService.getStationDownList(1L)).thenReturn(list);

        ResultUtil<List<DropDownListVO>> result = stationController.getStationDownList(1L);

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(stationService, times(1)).getStationDownList(1L);
    }
}
