package com.nbk.smart5c.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;

@Configuration
public class ValidatorConfig {
    @Bean
    public Validator validator() {
        return Validation.byDefaultProvider()
                .configure()
                .buildValidatorFactory()
                .getValidator();
    }
}