package com.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.common.BaseContext;
import com.takeout.entity.User;
import com.takeout.mapper.UserMapper;
import com.takeout.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 用户 Service 实现类
 *
 * @author 小好
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void sendCode(String phone) {
        // 1. 生成 6 位数字验证码
        String code = RandomStringUtils.randomNumeric(6);

        // 2. 存入 Redis，5 分钟有效期
        String key = "login_code:" + phone;
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

        // 3. 实际项目中需要调用短信服务发送验证码
        // 这里只打印日志，测试环境可以直接用返回的验证码
        log.info("发送登录验证码：phone={}, code={}", phone, code);
    }

    @Override
    @Transactional
    public User login(String phone, String code) {
        // 1. 验证验证码
        String key = "login_code:" + phone;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            throw new RuntimeException("验证码已过期");
        }

        if (!storedCode.equals(code)) {
            throw new RuntimeException("验证码错误");
        }

        // 2. 查询用户
        User user = lambdaQuery()
                .eq(User::getPhone, phone)
                .one();

        // 3. 新用户自动注册
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setName("用户" + phone.substring(7));
            user.setGender(0);
            user.setStatus(1);
            this.save(user);
            log.info("新用户注册成功：userId={}, phone={}", user.getId(), phone);
        }

        // 4. 删除验证码（登录成功后删除，防止重复使用）
        redisTemplate.delete(key);

        return user;
    }

    @Override
    public User getCurrentUser() {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        return this.getById(userId);
    }
}
