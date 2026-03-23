package com.takeout.controller;

import com.takeout.Result;
import com.takeout.common.BaseContext;
import com.takeout.entity.User;
import com.takeout.service.UserService;
import com.takeout.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户 Controller
 *
 * @author 小好
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送验证码
     * POST /user/codes
     */
    @PostMapping("/codes")
    @Operation(summary = "发送登录验证码")
    public Result<String> sendCode(@RequestBody User user) {
        log.info("发送登录验证码，手机号：{}", user.getPhone());
        userService.sendCode(user.getPhone());
        // 测试环境直接返回验证码，实际项目应该只返回成功消息
        return Result.success("验证码已发送（测试环境：" + "6666" + "）");
    }

    /**
     * 登录（验证码登录）
     * POST /user/tokens
     */
    @PostMapping("/tokens")
    @Operation(summary = "用户登录")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String code = params.get("code");

        log.info("用户登录，手机号：{}", phone);

        // 1. 验证码登录
        User user = userService.login(phone, code);

        // 2. 生成 JWT Token
        String token = JwtUtil.createToken(user.getId());

        // 3. 返回 Token 和用户信息
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);

        log.info("用户登录成功，userId={}, token={}", user.getId(), token.substring(0, 20) + "...");

        return Result.success(result);
    }

    /**
     * 退出登录
     * DELETE /user/tokens
     */
    @DeleteMapping("/tokens")
    @Operation(summary = "退出登录")
    public Result<String> logout() {
        Long userId = BaseContext.getCurrentId();
        log.info("用户退出登录，userId={}", userId);
        // TODO: 实际项目中需要使 Token 失效（加入黑名单或等待过期）
        return Result.success("退出成功");
    }

    /**
     * 获取当前用户信息
     * GET /user/me
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息")
    public Result<User> getCurrentUser() {
        User user = userService.getCurrentUser();
        log.info("获取当前用户信息，userId={}", user.getId());
        return Result.success(user);
    }

    /**
     * 更新用户信息
     * PUT /user/me
     */
    @PutMapping("/me")
    @Operation(summary = "更新用户信息")
    public Result<User> updateUser(@RequestBody User user) {
        Long userId = BaseContext.getCurrentId();
        user.setId(userId);

        log.info("更新用户信息，userId={}", userId);

        userService.updateById(user);

        // 返回更新后的用户信息
        User updatedUser = userService.getById(userId);
        return Result.success(updatedUser);
    }
}
