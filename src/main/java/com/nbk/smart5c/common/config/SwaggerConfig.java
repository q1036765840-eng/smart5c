package com.nbk.smart5c.common.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

@Configuration
@EnableOpenApi
public class SwaggerConfig implements WebMvcConfigurer {
    /**
     * 用于读取配置文件 application.properties 中 swagger 属性是否开启
     */
    @Value("${swagger.enabled}")
    Boolean swaggerEnabled;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                // 是否开启swagger（由 swagger.enabled 配置控制）
                .select()
                // 过滤条件，扫描指定路径下的文件
                .apis(RequestHandlerSelectors.basePackage("com.nbk.smart5c"))
                // 指定路径处理，PathSelectors.any()代表不过滤任何路径
                //.paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .enable(swaggerEnabled)
//                .ignoredParameterTypes(MultipartFile.class)
                .consumes(new HashSet<>(Arrays.asList(MediaType.MULTIPART_FORM_DATA_VALUE)));
//                .globalRequestParameters(Arrays.asList(new RequestParameterBuilder().name("file")
//                        .description("文件").required(true).in(ParameterType.FORM)
//                        .build()));
    }

    private ApiInfo apiInfo() {
        /*作者信息*/
        Contact contact = new Contact("jxb", "https://xx.xx.xx", "jiaxuebin@nuobikan.com");
        return new ApiInfo(
                "智能5C系统接口文档",
                "Spring Boot 集成 Swagger3 测试接口文档",
                "v1.0",
                "https://xxx.xx.xx",
                contact,
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList()
        );
    }
}
