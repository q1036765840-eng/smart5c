package com.nbk.smart5c.common.utils;


import lombok.Getter;

/**
 * 枚举了一些常用API操作码
 */
@Getter
public enum ResultCode implements IErrorCode {
    SUCCESS(200, "操作成功"),
    ACCEPTED(202, "操作进行中"),
    FAILED(500, "操作失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    VALIDATE_FAILED(404, "参数检验失败"),
    TOMANY_REQUEST_ERROR(429, "后端服务触发流控"),
    BAD_GATEWAY(502, "网关服务异常"),
    AUTHORIZATION_HEADER_IS_EMPTY(600, "请求头中的token为空"),
    LOGIN_ERROR(601, "登录错误"),
    GEN_PUBLIC_KEY_ERROR(602, "生成公钥异常"),
    JWT_TOKEN_EXPIRE(603, "token校验异常"),
    BACKGROUD_DEGRADE_ERROR(604, "后端服务触发降级"),
    CONNECT_TEST_FAIL(605, "连接测试失败"),
    ANALYSIS_FAIL(606, "解析异常"),
    EMPTY_FAIL(607, "空异常"),
    FILE_UPLOAD_FAILED(608, "文件上传失败"),
    FILE_REMOVE_FAILED(609, "文件删除失败"),
    BUSI_FORBIDDEN(610, "业务异常"),
    EXPORT_FAIL(611, "导出失败"),
    FILE_PARSE_FAIL(612, "文件解析失败"),
    REVIEW_FAIL(613, "审核失败"),
    REMOVE_FAIL(614, "删除失败"),
    ;

    private int code;
    private String message;

    private ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
