package com.nbk.smart5c.common.utils;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;

import java.util.List;

public class ExcelMessageConverter extends AbstractHttpMessageConverter<List<ResultUtil>> {

    public ExcelMessageConverter() {
        super(MediaType.parseMediaType("application/vnd.ms-excel"));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return List.class.isAssignableFrom(clazz);
    }

    @Override
    protected List<ResultUtil> readInternal(Class<? extends List<ResultUtil>> clazz, HttpInputMessage inputMessage) {
        // 反序列化逻辑（若需支持 Excel 作为请求体）
        return null;
    }

    @Override
    protected void writeInternal(List<ResultUtil> results, HttpOutputMessage outputMessage) {
    }
}