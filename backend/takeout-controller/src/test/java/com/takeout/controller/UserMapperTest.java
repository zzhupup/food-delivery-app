package com.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.entity.User;
import com.takeout.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * UserMapper 测试类
 * 测试 MyBatis-Plus 的 CRUD 功能
 *
 * @author 小好
 */
@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * 测试：新增用户
     */
    @Test
    public void testInsert() {
        System.out.println("===== 测试新增用户 =====");
        
        User user = new User();
        user.setPhone("13800138000");
        user.setName("测试用户" + System.currentTimeMillis());
        user.setGender(1);
        user.setStatus(1);
        
        int result = userMapper.insert(user);
        
        System.out.println("插入结果：" + result + " 行");
        System.out.println("生成的 ID: " + user.getId());
        System.out.println("创建时间：" + user.getCreateTime());
        System.out.println("更新时间：" + user.getUpdateTime());
    }

    /**
     * 测试：根据 ID 查询
     */
    @Test
    public void testSelectById() {
        System.out.println("===== 测试根据 ID 查询 =====");
        
        User user = userMapper.selectById(1L);
        
        if (user != null) {
            System.out.println("用户信息:");
            System.out.println("  ID: " + user.getId());
            System.out.println("  姓名：" + user.getName());
            System.out.println("  手机号：" + user.getPhone());
            System.out.println("  性别：" + user.getGender());
            System.out.println("  状态：" + user.getStatus());
        } else {
            System.out.println("未找到用户");
        }
    }

    /**
     * 测试：查询所有用户
     */
    @Test
    public void testSelectAll() {
        System.out.println("===== 测试查询所有用户 =====");
        
        List<User> users = userMapper.selectList(null);
        
        System.out.println("共查询到 " + users.size() + " 个用户:");
        for (User user : users) {
            System.out.println("  [" + user.getId() + "] " + user.getName() + " - " + user.getPhone());
        }
    }

    /**
     * 测试：条件查询
     */
    @Test
    public void testSelectByCondition() {
        System.out.println("===== 测试条件查询 =====");
        
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1)
                .orderByDesc("create_time");
        
        List<User> users = userMapper.selectList(wrapper);
        
        System.out.println("正常状态的用户共 " + users.size() + " 个:");
        for (User user : users) {
            System.out.println("  [" + user.getId() + "] " + user.getName() + " - " + user.getPhone());
        }
    }

    /**
     * 测试：模糊查询
     */
    @Test
    public void testSelectLike() {
        System.out.println("===== 测试模糊查询 =====");
        
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("phone", "138");
        
        List<User> users = userMapper.selectList(wrapper);
        
        System.out.println("手机号包含 138 的用户共 " + users.size() + " 个:");
        for (User user : users) {
            System.out.println("  [" + user.getId() + "] " + user.getName() + " - " + user.getPhone());
        }
    }

    /**
     * 测试：分页查询
     */
    @Test
    public void testSelectPage() {
        System.out.println("===== 测试分页查询 =====");
        
        Page<User> page = new Page<>(1, 10);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        
        Page<User> resultPage = userMapper.selectPage(page, wrapper);
        
        System.out.println("总记录数：" + resultPage.getTotal());
        System.out.println("总页数：" + resultPage.getPages());
        System.out.println("当前页：" + resultPage.getCurrent());
        System.out.println("每页大小：" + resultPage.getSize());
        System.out.println("当前页数据:");
        
        for (User user : resultPage.getRecords()) {
            System.out.println("  [" + user.getId() + "] " + user.getName() + " - " + user.getPhone());
        }
    }

    /**
     * 测试：更新用户
     */
    @Test
    public void testUpdate() {
        System.out.println("===== 测试更新用户 =====");
        
        User user = userMapper.selectById(1L);
        if (user != null) {
            System.out.println("更新前姓名：" + user.getName());
            
            user.setName("更新后的名字" + System.currentTimeMillis());
            int result = userMapper.updateById(user);
            
            System.out.println("更新结果：" + result + " 行");
            System.out.println("更新时间：" + user.getUpdateTime());
        } else {
            System.out.println("用户不存在");
        }
    }

    /**
     * 测试：逻辑删除
     */
    @Test
    public void testLogicDelete() {
        System.out.println("===== 测试逻辑删除 =====");
        
        User user = new User();
        user.setPhone("13900139000");
        user.setName("待删除用户");
        user.setGender(0);
        user.setStatus(1);
        userMapper.insert(user);
        
        System.out.println("新增用户 ID: " + user.getId());
        
        int result = userMapper.deleteById(user.getId());
        System.out.println("删除结果：" + result + " 行");
        
        User deletedUser = userMapper.selectById(user.getId());
        if (deletedUser == null) {
            System.out.println("✓ 逻辑删除成功！查询不到已删除的用户");
        } else {
            System.out.println("✗ 异常：还能查到已删除的用户");
        }
    }

    /**
     * 测试：批量新增
     */
    @Test
    public void testBatchInsert() {
        System.out.println("===== 测试批量新增 =====");
        
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setPhone("1380013800" + i);
            user.setName("批量用户" + i);
            user.setGender(i % 2);
            user.setStatus(1);
            userMapper.insert(user);
        }
        
        System.out.println("✓ 批量新增 5 个用户完成");
        
        List<User> users = userMapper.selectList(null);
        System.out.println("当前总用户数：" + users.size());
    }
}
