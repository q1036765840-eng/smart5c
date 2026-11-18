package com.nbk.smart5c.common.config;

import com.nbk.smart5c.common.utils.BasePathHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class PathCheckConfig {

    @Value("${inspect.basepath}")
    private String basePath;

    @PostConstruct
    public void init() {
        BasePathHolder.set(basePath);
    }
}
