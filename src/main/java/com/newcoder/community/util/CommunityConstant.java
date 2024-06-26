package com.newcoder.community.util;

public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态下登录凭证的超时时间(12h)
     */
    int DEFAULT_EXPIRED_SECOND = 3600 * 12;

    /**
     * 记住密码状态下登录凭证的超时时间(100d)
     */
    int REMEMBER_EXPIRED_SECOND = 3600 * 24 * 100;
}
