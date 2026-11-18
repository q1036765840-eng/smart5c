package com.nbk.smart5c;


import com.dtflys.forest.springboot.annotation.ForestScan;
import com.nbk.smart5c.mapper.UserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@ComponentScan(value = "com.nbk.smart5c")
@MapperScan("com.nbk.smart5c.mapper")
@ForestScan("com.nbk.smart5c.client")
@EnableAsync
@EnableAspectJAutoProxy
@SpringBootApplication
public class Application { //extends SpringBootServletInitializer {
    public static String port;
    public static String host;
    @Autowired
    private UserInfoMapper userInfoMapper;

    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(Application.class, args);
        Environment env = application.getEnvironment();
        try {
            host = InetAddress.getLocalHost().getHostAddress();
            port = env.getProperty("server.port");
            log.info("\n----------------------------------------------------------\n\t" +
                            "Application '{}' is running! Access URLs:\n\t" +
                            "Local: \t\thttp://localhost:{}\n\t" +
                            "External: \thttp://{}:{}\n\t" +
                            "Doc: \thttp://{}:{}/doc.html\n\t" +
                            "----------------------------------------------------------",
                    env.getProperty("spring.application.name"),
                    env.getProperty("server.port"),
                    host, port,
                    host, port);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

}