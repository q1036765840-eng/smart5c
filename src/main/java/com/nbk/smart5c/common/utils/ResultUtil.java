package com.nbk.smart5c.common.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 接口返回数据格式
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "接口返回对象", description = "接口返回对象")
public class ResultUtil<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功标志
     */
    @ApiModelProperty(value = "成功标志")
    private boolean success = true;

    /**
     * 返回处理消息
     */
    @ApiModelProperty(value = "返回处理消息")
    private String message = "";

    /**
     * 返回代码
     */
    @ApiModelProperty(value = "返回代码")
    private Integer code = 0;
    /**
     * 返回数据对象 data
     */
    @ApiModelProperty(value = "返回数据对象")
    private T result;

    /**
     * 时间戳
     */
    @ApiModelProperty(value = "时间戳")
    private long timestamp = System.currentTimeMillis();


    /**
     * 兼容VUE3版token失效不跳转登录页面
     *
     * @param code
     * @param message
     */
    public ResultUtil(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultUtil<T> success(String message) {
        this.message = message;
        this.code = 200;
        this.success = true;
        return this;
    }


    public static <T> ResultUtil<T> ok() {
        ResultUtil<T> r = new ResultUtil<T>();
        r.setSuccess(true);
        r.setCode(200);
        return r;
    }

    public static <T> ResultUtil<T> ok(String msg) {
        ResultUtil<T> r = new ResultUtil<T>();
        r.setSuccess(true);
        r.setCode(200);
        //Result OK(String msg)方法会造成兼容性问题 issues/I4IP3D，
        // 数据在data和message中都有可解决
        r.setResult((T) msg);
        r.setMessage(msg);
        return r;
    }

    public static <T> ResultUtil<T> OK() {
        ResultUtil<T> r = new ResultUtil<T>();
        r.setSuccess(true);
        r.setCode(200);
        return r;
    }

    /**
     * 此方法是为了兼容升级所创建
     *
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> ResultUtil<T> OK(String msg) {
        ResultUtil<T> r = new ResultUtil<T>();
        r.setSuccess(true);
        r.setCode(200);
        r.setMessage(msg);
        //Result OK(String msg)方法会造成兼容性问题 issues/I4IP3D
        r.setResult((T) msg);
        return r;
    }

    public static <T> ResultUtil<T> ok(T data) {
        ResultUtil<T> r = new ResultUtil<T>();
        r.setSuccess(true);
        r.setCode(200);
        r.setResult(data);
        return r;
    }

    public static <T> ResultUtil<T> OK(T data) {
        ResultUtil<T> r = new ResultUtil<T>();
        r.setSuccess(true);
        r.setCode(200);
        r.setResult(data);
        return r;
    }

    public static <T> ResultUtil<T> OK(String msg, T data) {
        ResultUtil<T> r = new ResultUtil<T>();
        r.setSuccess(true);
        r.setCode(200);
        r.setMessage(msg);
        r.setResult(data);
        return r;
    }

    public static <T> ResultUtil<T> OK(int code, String msg, T data) {
        ResultUtil<T> r = new ResultUtil<T>();
        r.setSuccess(true);
        r.setCode(code);
        r.setMessage(msg);
        r.setResult(data);
        return r;
    }

    public static <T> ResultUtil<T> error(int code, String msg, T data) {
        ResultUtil<T> r = new ResultUtil<T>();
        r.setSuccess(false);
        r.setCode(code);
        r.setMessage(msg);
        r.setResult(data);
        return r;
    }

    public static <T> ResultUtil<T> error(String msg, T data) {
        ResultUtil<T> r = new ResultUtil<T>();
        r.setSuccess(false);
        r.setCode(500);
        r.setMessage(msg);
        r.setResult(data);
        return r;
    }

    /**
     * 未登录返回结果
     */
    public static <T> CommonResult<T> unauthorized(T data) {
        return new CommonResult<T>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
    }

    public static <T> ResultUtil<T> error(String msg) {
        return error(500, msg);
    }

    public static <T> ResultUtil<T> error(int code, String msg) {
        ResultUtil<T> r = new ResultUtil<T>();
        r.setCode(code);
        r.setMessage(msg);
        r.setSuccess(false);
        return r;
    }

    public ResultUtil<T> error500(String message) {
        this.message = message;
        this.code = 500;
        this.success = false;
        return this;
    }


    @JsonIgnore
    private String onlTable;

}