package com.nbk.smart5c.common.utils.holder;

import com.nbk.smart5c.entity.UserInfo;

/**
 * 当前用户线程上下文持有器。
 *
 * @author heyunfei
 */
public class CurrentUserHolder {
    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 绑定当前线程用户。
     *
     * @param user 当前用户
     */
    public static void set(UserInfo user) {
        THREAD_LOCAL.set(user);
    }

    /**
     * 获取当前线程用户。
     *
     * @return 当前用户
     */
    public static UserInfo get() {
        return THREAD_LOCAL.get();
    }

    /**
     * 清理当前线程用户。
     */
    public static void clear() {
        THREAD_LOCAL.remove();
    }
}
