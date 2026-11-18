package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.pojo.vo.LabelValueVO;
import com.nbk.smart5c.pojo.vo.LatestAlarmListVO;
import com.nbk.smart5c.pojo.vo.TodayCheckDataVO;
import com.nbk.smart5c.service.HomePageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * HomePageController 单元测试（Mock Service 层）
 */
@ExtendWith(MockitoExtension.class)
class HomePageControllerTest {

    @Mock
    private HomePageService homePageService;

    @InjectMocks
    private HomePageController homePageController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCountAlarmsToday() {
        when(homePageService.countAlarmsToday()).thenReturn(5L);

        ResultUtil<Long> result = homePageController.countAlarmsToday();

        assertEquals(200, result.getCode());
        assertEquals(5L, result.getResult());
        verify(homePageService, times(1)).countAlarmsToday();
    }

    @Test
    void testLastWeekAlarmsTrend() {
        List<LabelValueVO> trend = Collections.singletonList(new LabelValueVO());
        when(homePageService.lastWeekAlarmsTrend()).thenReturn(trend);

        ResultUtil<List<LabelValueVO>> result = homePageController.lastWeekAlarmsTrend();

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(homePageService, times(1)).lastWeekAlarmsTrend();
    }

    @Test
    void testTodayCheckDataCount() {
        when(homePageService.todayCheckDataCount()).thenReturn(new TodayCheckDataVO());

        ResultUtil<TodayCheckDataVO> result = homePageController.todayCheckDataCount();

        assertEquals(200, result.getCode());
        assertNotNull(result.getResult());
        verify(homePageService, times(1)).todayCheckDataCount();
    }

    @Test
    void testLastAlarmPaging() {
        Page<LatestAlarmListVO> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new LatestAlarmListVO()));
        page.setTotal(1);
        when(homePageService.lastAlarmPaging(1, 10)).thenReturn(page);

        ResultUtil<Page<LatestAlarmListVO>> result = homePageController.lastAlarmPaging(1, 10);

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().getTotal());
        verify(homePageService, times(1)).lastAlarmPaging(1, 10);
    }

    @Test
    void testLastAlarmPaging_CustomParams() {
        Page<LatestAlarmListVO> page = new Page<>(2, 20);
        page.setTotal(1);
        when(homePageService.lastAlarmPaging(2, 20)).thenReturn(page);

        ResultUtil<Page<LatestAlarmListVO>> result = homePageController.lastAlarmPaging(2, 20);

        assertEquals(200, result.getCode());
        verify(homePageService, times(1)).lastAlarmPaging(2, 20);
    }

    @Test
    void testGenerateReport() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(homePageService).generateReport(any());

        homePageController.generateReport(response);

        verify(homePageService, times(1)).generateReport(eq(response));
    }
}
