package com.nbk.smart5c.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.pojo.vo.LabelValueVO;
import com.nbk.smart5c.pojo.vo.LatestAlarmListVO;
import com.nbk.smart5c.pojo.vo.TodayCheckDataVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface HomePageService {
    Long countAlarmsToday();

    List<LabelValueVO> lastWeekAlarmsTrend();

    TodayCheckDataVO todayCheckDataCount();

    Page<LatestAlarmListVO> lastAlarmPaging(Integer pageNo, Integer pageSize);

    List<List<Object>> getGenerateReportData() throws ServiceException;

    void generateReport(HttpServletResponse response) throws ServiceException;
}
