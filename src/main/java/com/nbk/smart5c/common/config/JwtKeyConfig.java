package com.nbk.smart5c.common.config;

import com.nbk.smart5c.common.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class JwtKeyConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostConstruct
    public void init() {
        JwtTokenUtil.setTokenEncryptionKey(jwtSecret);
    }
}
