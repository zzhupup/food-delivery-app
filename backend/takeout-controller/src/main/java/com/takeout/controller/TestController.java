package com.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.entity.User;
import com.takeout.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试 Controller - 用于学习 MyBatis-Plus
 *
 * @author 小好
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserMapper userMapper;

    /**
     * 查询所有用户
     * GET http://localhost:8080/test/users
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }

    /**
     * 根据 ID 查询用户
     * GET http://localhost:8080/test/users/1
     */
    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 条件查询用户
     * GET http://localhost:8080/test/users/search?status=1&gender=1
     */
    @GetMapping("/users/search")
    public List<User> searchUsers(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer gender,
            @RequestParam(required = false) String phone) {
        
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        
        if (status != null) {
            wrapper.eq("status", status);
        }
        if (gender != null) {
            wrapper.eq("gender", gender);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.like("phone", phone);
        }
        
        return userMapper.selectList(wrapper);
    }

    /**
     * 分页查询用户
     * GET http://localhost:8080/test/users/page?page=1&size=10
     */
    @GetMapping("/users/page")
    public Map<String, Object> getPageUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Page<User> pageParam = new Page<>(page, size);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        
        Page<User> result = userMapper.selectPage(pageParam, wrapper);
        
        Map<String, Object> response = new HashMap<>();
        response.put("total", result.getTotal());      // 总记录数
        response.put("pages", result.getPages());      // 总页数
        response.put("current", result.getCurrent());  // 当前页
        response.put("size", result.getSize());        // 每页大小
        response.put("records", result.getRecords());  // 当前页数据
        
        return response;
    }

    /**
     * 新增用户
     * POST http://localhost:8080/test/users
     * Body: {"phone": "13800138000", "name": "张三", "gender": 1, "status": 1}
     */
    @PostMapping("/users")
    public Map<String, Object> addUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        int result = userMapper.insert(user);
        
        response.put("success", result > 0);
        response.put("rows", result);
        response.put("userId", user.getId());
        
        return response;
    }

    /**
     * 更新用户
     * PUT http://localhost:8080/test/users
     * Body: {"id": 1, "name": "新名字", "phone": "13800000000"}
     */
    @PutMapping("/users")
    public Map<String, Object> updateUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        int result = userMapper.updateById(user);
        
        response.put("success", result > 0);
        response.put("rows", result);
        
        return response;
    }

    /**
     * 删除用户（逻辑删除）
     * DELETE http://localhost:8080/test/users/1
     */
    @DeleteMapping("/users/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        int result = userMapper.deleteById(id);
        
        response.put("success", result > 0);
        response.put("rows", result);
        
        return response;
    }

    /**
     * 测试接口健康检查
     * GET http://localhost:8080/test/health
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "MyBatis-Plus 测试接口运行正常！");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
