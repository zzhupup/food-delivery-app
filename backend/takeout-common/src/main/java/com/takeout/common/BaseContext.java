package com.takeout.common;

/**
 * 基于 ThreadLocal 的用户 ID 上下文
 *
 * @author 小好
 */
public class BaseContext {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前用户 ID
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取当前用户 ID
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }

    /**
     * 移除当前用户 ID（请求结束后必须调用）
     */
    public static void removeCurrentId() {
        threadLocal.remove();
    }
}
