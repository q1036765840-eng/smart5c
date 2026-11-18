package com.nbk.smart5c.common.config;

import com.nbk.smart5c.common.utils.ClientAbortUtil;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Desc 全局异常处理
 * ＠Date 2024-05-24
 * @Author jxb@nuobikan.com
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 全局异常处理：对手动抛出的runtimeException进行处理
     *
     * @return
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    /**
     * 400 - Internal Server Error 运行时异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public ResultUtil<?> handleRuntimeException(RuntimeException e, HttpServletResponse response) {
        if (ClientAbortUtil.isClientAbort(e) || response.isCommitted()) {
            // 客户端已断开或响应已开始输出，无法再写错误信息，仅记录警告
            log.warn("客户端已断开，忽略异常: {}", e.getMessage());
            return null;
        }
        log.error("运行时异常: {}", e.getMessage(), e);
        return ResultUtil.error("系统异常，请稍后重试");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultUtil<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException be) {
        List<FieldError> fieldError = be.getFieldErrors();
        StringBuffer stringBuffer = new StringBuffer();
        fieldError.forEach(s -> stringBuffer.append(s.getDefaultMessage()));
        log.error("参数校验异常: {}", stringBuffer, be);
        return ResultUtil.error(stringBuffer.toString());
    }

    @ExceptionHandler(BindException.class)
    public ResultUtil<?> handleBindException(BindException be) {
        List<FieldError> fieldError = be.getFieldErrors();
        StringBuffer stringBuffer = new StringBuffer();
        fieldError.forEach(s -> stringBuffer.append(s.getDefaultMessage()));
        log.error("参数绑定异常: {}", stringBuffer, be);
        return ResultUtil.error(stringBuffer.toString());
    }


    /**
     * @param e 自定义业务异常
     * @return 封装结果
     * @Desc 业务异常处理
     */
    @ExceptionHandler(ServiceException.class)
    public ResultUtil<?> handleServiceException(ServiceException e, HttpServletResponse response) {
        if (ClientAbortUtil.isClientAbort(e) || response.isCommitted()) {
            // 客户端已断开或响应已开始输出，无法再写错误信息
            log.warn("客户端已断开，忽略异常: {}", e.getMessage());
            return null;
        }
        log.error("业务异常: {}", e.getMessage(), e);
        if (e.getCode() == null) {
            return ResultUtil.error(e.getMessage());
        }
        return ResultUtil.error(e.getCode(), e.getMessage());
    }
}