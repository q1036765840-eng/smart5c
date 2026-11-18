package com.nbk.smart5c.common.utils;

import cn.hutool.core.util.StrUtil;
import com.nbk.smart5c.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * 文件操作路径安全校验：所有来自前端的路径必须落在 inspect.basepath 之内
 */
@Slf4j
public class PathCheckUtil {

    private PathCheckUtil() {
    }

    /**
     * 校验 target 在 base 目录内（canonical path 规范化，防 ../ 穿越），越界抛 ServiceException
     *
     * @param base   允许操作的根目录（如 inspect.basepath）
     * @param target 前端传入的文件/目录路径，允许为空（为空时返回原值，由调用方走默认路径逻辑）
     */
    public static String requireUnderBase(String base, String target) throws ServiceException {
        if (StrUtil.isBlank(target)) {
            return target;
        }
        String baseCanonical = canonical(base);
        String targetCanonical = canonical(target);
        if (StrUtil.isBlank(baseCanonical) || targetCanonical == null) {
            throw new ServiceException(520, "非法的文件路径");
        }
        if (!targetCanonical.equals(baseCanonical) && !targetCanonical.startsWith(baseCanonical + File.separator)) {
            log.warn("拒绝越界文件路径访问: {}", target);
            throw new ServiceException(520, "文件路径超出允许的操作范围");
        }
        return target;
    }

    private static String canonical(String path) {
        if (StrUtil.isBlank(path)) {
            return "";
        }
        try {
            return new File(path).getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalArgumentException("非法的文件路径");
        }
    }
}
