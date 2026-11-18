package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.Device;
import com.nbk.smart5c.pojo.bo.DeviceSaveBO;
import com.nbk.smart5c.pojo.bo.DeviceUpdateBO;
import com.nbk.smart5c.pojo.qo.DeviceQO;
import com.nbk.smart5c.pojo.vo.DeviceVO;
import com.nbk.smart5c.service.DeviceService;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DeviceController 单元测试（Mock Service 层）
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class DeviceControllerTest {

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private DeviceController deviceController;

    private Device device;

    @BeforeEach
    void setUp() {
        device = new Device();
    }

    @Test
    void testQueryById() {
        when(deviceService.queryById("1")).thenReturn(device);

        ResultUtil<Device> result = deviceController.queryById("1");

        assertEquals(200, result.getCode());
        assertEquals(device, result.getResult());
        verify(deviceService, times(1)).queryById("1");
    }

    @Test
    void testQueryAndPaging() {
        Page<Device> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new Device()));
        page.setTotal(1);
        when(deviceService.queryAndPaging(any(DeviceQO.class))).thenReturn(page);

        ResultUtil<IPage<Device>> result = deviceController.queryAndPaging(new DeviceQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().getTotal());
        verify(deviceService, times(1)).queryAndPaging(any(DeviceQO.class));
    }

    @Test
    void testQueryAll() {
        List<DeviceVO> list = Collections.singletonList(new DeviceVO());
        when(deviceService.queryAll(any(DeviceQO.class))).thenReturn(list);

        ResultUtil<List<DeviceVO>> result = deviceController.queryAll(new DeviceQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(deviceService, times(1)).queryAll(any(DeviceQO.class));
    }

    @Test
    void testDeleteById() {
        when(deviceService.removeById(anyInt())).thenReturn(true);

        ResultUtil<Boolean> result = deviceController.deleteById(1);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(deviceService, times(1)).removeById(1);
    }

    @Test
    void testDeleteByIds() {
        List<Integer> ids = Collections.singletonList(1);
        when(deviceService.removeByIds(ids)).thenReturn(true);

        ResultUtil<Boolean> result = deviceController.deleteByIds(ids);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(deviceService, times(1)).removeByIds(ids);
    }

    @Test
    void testSave() {
        when(deviceService.save(any(DeviceSaveBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = deviceController.save(new DeviceSaveBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(deviceService, times(1)).save(any(DeviceSaveBO.class));
    }

    @Test
    void testUpdateById() {
        when(deviceService.updateById(any(DeviceUpdateBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = deviceController.updateById(new DeviceUpdateBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(deviceService, times(1)).updateById(any(DeviceUpdateBO.class));
    }

    @Test
    void testAnalysis() {
        MultipartFile file = mock(MultipartFile.class);
        doNothing().when(deviceService).analysis(any(MultipartFile.class));

        ResultUtil<Void> result = deviceController.analysis(file);

        assertEquals(200, result.getCode());
        verify(deviceService, times(1)).analysis(file);
    }

    @Test
    void testExport() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(deviceService).export(any(DeviceQO.class), any());

        ResultUtil<Void> result = deviceController.export(response, new DeviceQO());

        assertEquals(200, result.getCode());
        verify(deviceService, times(1)).export(any(DeviceQO.class), eq(response));
    }

    @Test
    void testExportTemplate() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(deviceService).exportTemplate(any());

        ResultUtil<Void> result = deviceController.exportTemplate(response);

        assertEquals(200, result.getCode());
        verify(deviceService, times(1)).exportTemplate(eq(response));
    }

    @Test
    void testDeviceStatusDownList() {
        ResultUtil<List<Map<String, Object>>> result = deviceController.deviceStatusDownList();

        assertEquals(200, result.getCode());
        assertNotNull(result.getResult());
        assertFalse(result.getResult().isEmpty());
    }
}
