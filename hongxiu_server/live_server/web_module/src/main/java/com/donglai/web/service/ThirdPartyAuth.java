package com.donglai.web.service;

import me.zhyd.oauth.model.AuthResponse;

/**
 * @author Moon
 * @date 2021-10-26 17:00
 */
public interface ThirdPartyAuth {
    /**
     * 第三方平台统一操作
     *
     * @param data 第三方用户数据信息
     * @return 返回响应
     */
    String login(Object data);

    /**
     * 获取用户登录后的信息
     *
     * @param id 用户获取信息的key
     * @return 返回用户信息
     */
    AuthResponse getUserLoginInfo(String id);
}
