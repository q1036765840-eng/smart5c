package com.nbk.smart5c.common.config;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Log4j2
@Aspect
@Component
public class ForestInterceptor {

    @Around("execution(* com.nbk.smart5c.client.AiClient.*(..))")
    public Object aroundAiClient(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info("========== AI Client 调用开始 ==========");
        log.info("方法: {}", methodName);
        log.info("请求参数: {}", args.length > 0 ? args[0] : "无参数");

        long startTime = System.currentTimeMillis();
        Object result = null;

        try {
            result = joinPoint.proceed();
            long costTime = System.currentTimeMillis() - startTime;

            log.info("调用成功!");
            log.info("耗时: {}ms", costTime);
            log.info("响应结果: {}", result);
            log.info("=======================================");

            return result;

        } catch (Exception e) {
            long costTime = System.currentTimeMillis() - startTime;

            log.error("调用失败!");
            log.error("耗时: {}ms", costTime);
            log.error("异常类型: {}", e.getClass().getName());
            log.error("异常信息: {}", e.getMessage());
            log.error("完整堆栈:", e);
            log.error("=======================================");

            throw e;
        }
    }
}