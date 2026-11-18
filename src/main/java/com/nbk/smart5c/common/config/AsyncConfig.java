package com.nbk.smart5c.common.config;

import cn.hutool.core.thread.NamedThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync // 启用异步执行
public class AsyncConfig {

    // Bean 的名字可以自定义，如果不指定name，使用方法名
    @Bean(name = "taskExecutor")
    public ScheduledThreadPoolExecutor taskExecutor() {
        ThreadFactory factory = new NamedThreadFactory("5c-async-", false);
        return new ScheduledThreadPoolExecutor(8, factory);
    }

    /**
     * 外部推送数据的解析线程池：定时任务从队列取文件夹路径后交给该池解析
     */
    @Bean(name = "pushParseExecutor")
    public ExecutorService pushParseExecutor() {
        ThreadFactory factory = new NamedThreadFactory("5c-push-parse-", false);
        return new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), factory);
    }

    // Bean 的名字可以自定义，如果不指定name，使用方法名
    @Bean(name = "timeExecutor")
    public ScheduledThreadPoolExecutor timeExecutor() {
        ThreadFactory factory = new NamedThreadFactory("schedule-async-", false);
        return new ScheduledThreadPoolExecutor(8, factory);
    }

}