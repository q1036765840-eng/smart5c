package com.nbk.smart5c.exception;

import com.nbk.smart5c.common.utils.IErrorCode;
import com.nbk.smart5c.common.utils.ResultCode;
import lombok.Data;

@Data
public class ServiceException extends Exception {
    private Integer code;
    private String message;

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
        this.message = message;
    }

    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ServiceException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    public ServiceException(IErrorCode iErrorCode) {
        this.code = iErrorCode.getCode();
        this.message = iErrorCode.getMessage();
    }

    public ServiceException(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
}
