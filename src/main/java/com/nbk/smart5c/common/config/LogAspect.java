package com.nbk.smart5c.common.config;

import cn.hutool.core.date.StopWatch;
import cn.hutool.json.JSONUtil;
import com.nbk.smart5c.common.utils.ApiUtils;
import com.nbk.smart5c.entity.OperateLog;
import com.nbk.smart5c.mapper.OperateLogMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class LogAspect {
    @Autowired
    private OperateLogMapper operateLogMapper;
    @Autowired
    private ApiUtils apiUtils;

    @Around("@annotation(com.nbk.smart5c.common.annotations.AroundLog)")
    public Object recordAroundLog(ProceedingJoinPoint joinPoint) throws Throwable {
        OperateLog operateLog = new OperateLog();
        operateLog.setOperateUser(apiUtils.currentAccount());
        operateLog.setOperateTime(LocalDateTime.now());
        //获取操作类名
        operateLog.setClassName(joinPoint.getTarget().getClass().getName());
        //获取操作方法名
        operateLog.setMethodName(joinPoint.getSignature().getName());
        //获取操作方法参数
        operateLog.setMethodParams(Arrays.toString(joinPoint.getArgs()));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //获取操作方法返回值
        Object result = joinPoint.proceed();
        String returnValue = JSONUtil.toJsonStr(result);
        operateLog.setReturnValue(returnValue);
        stopWatch.stop();
        operateLog.setCostTime(stopWatch.getTotalTimeMillis());
        operateLogMapper.insert(operateLog);
        return result;
    }

    @Before("@annotation(com.nbk.smart5c.common.annotations.BeforeLog)")
    public void recordBeforeLog(JoinPoint joinPoint) throws Throwable {
        OperateLog operateLog = new OperateLog();
        operateLog.setOperateTime(LocalDateTime.now());
        //获取操作类名
        operateLog.setClassName(joinPoint.getTarget().getClass().getName());
        //获取操作方法名
        operateLog.setMethodName(joinPoint.getSignature().getName());
        Object[] params = joinPoint.getArgs();
        //获取操作方法参数
        operateLog.setMethodParams(Arrays.toString(params));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        stopWatch.stop();
        operateLog.setCostTime(stopWatch.getTotalTimeMillis());
        operateLogMapper.insert(operateLog);
    }
}
