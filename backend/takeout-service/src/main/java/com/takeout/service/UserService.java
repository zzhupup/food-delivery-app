package com.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.entity.User;

/**
 * 用户 Service 接口
 *
 * @author 小好
 */
public interface UserService extends IService<User> {

    /**
     * 发送登录验证码
     *
     * @param phone 手机号
     */
    void sendCode(String phone);

    /**
     * 验证码登录
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 用户信息
     */
    User login(String phone, String code);

    /**
     * 获取当前登录用户
     *
     * @return 用户信息
     */
    User getCurrentUser();
}
