package com.nbk.smart5c.common.utils;

import org.apache.catalina.connector.ClientAbortException;

import java.io.IOException;

/**
 * 判断异常是否由客户端中断连接引起（浏览器取消下载、代理超时断链等）。
 * <p>
 * Hutool/POI 等在写出响应时会把 IOException 包装成 IORuntimeException，
 * 因此不能只比较异常的运行时类型，需要沿 cause 链查找。
 */
public final class ClientAbortUtil {

    private static final String[] ABORT_MESSAGES = {
            "Broken pipe", "Connection reset", "Connection aborted", "断开的管道", "连接被重置"
    };

    private ClientAbortUtil() {
    }

    public static boolean isClientAbort(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof ClientAbortException) {
                return true;
            }
            if (t instanceof IOException && t.getMessage() != null) {
                for (String message : ABORT_MESSAGES) {
                    if (t.getMessage().contains(message)) {
                        return true;
                    }
                }
            }
            if (t.getCause() == t) {
                break;
            }
        }
        return false;
    }
}
