package com.nbk.smart5c.common.utils;

/**
 * 持有 inspect.basepath 配置值，供静态工具类（LinuxUtil 等）做路径校验使用。
 * 由 PathCheckConfig 在启动时注入。
 */
public class BasePathHolder {

    private static volatile String basePath = "";

    private BasePathHolder() {
    }

    public static String get() {
        return basePath;
    }

    public static void set(String basePath) {
        BasePathHolder.basePath = basePath == null ? "" : basePath;
    }
}
