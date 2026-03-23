package com.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.entity.User;
import com.takeout.mapper.UserMapper;
import com.takeout.TakeoutApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * MyBatis-Plus 快速测试（main 方法方式）
 * 比单元测试更直观，适合学习
 *
 * @author 小好
 */
public class MybatisPlusQuickTest {

    public static void main(String[] args) {
        // 1. 启动 Spring 容器
        ConfigurableApplicationContext context = SpringApplication.run(TakeoutApplication.class, args);
        
        // 2. 获取 Mapper
        UserMapper userMapper = context.getBean(UserMapper.class);
        
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║     MyBatis-Plus CRUD 测试开始！🚀          ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("\n");
        
        try {
            // ===== 测试 1: 查询所有用户 =====
            System.out.println("【测试 1】查询所有用户");
            System.out.println("─────────────────────────────────────");
            var users = userMapper.selectList(null);
            System.out.println("共查询到 " + users.size() + " 个用户:");
            for (User user : users) {
                System.out.println("  [" + user.getId() + "] " + user.getName() + " - " + user.getPhone());
            }
            System.out.println("✓ 完成\n");
            
            // ===== 测试 2: 根据 ID 查询 =====
            System.out.println("【测试 2】根据 ID 查询（ID=1）");
            System.out.println("─────────────────────────────────────");
            User user = userMapper.selectById(1L);
            if (user != null) {
                System.out.println("  姓名：" + user.getName());
                System.out.println("  手机号：" + user.getPhone());
                System.out.println("  性别：" + user.getGender());
                System.out.println("  状态：" + user.getStatus());
            } else {
                System.out.println("  未找到用户");
            }
            System.out.println("✓ 完成\n");
            
            // ===== 测试 3: 新增用户 =====
            System.out.println("【测试 3】新增用户");
            System.out.println("─────────────────────────────────────");
            User newUser = new User();
            newUser.setPhone("13800138000");
            newUser.setName("测试用户" + System.currentTimeMillis());
            newUser.setGender(1);
            newUser.setStatus(1);
            
            int insertResult = userMapper.insert(newUser);
            System.out.println("  插入结果：" + insertResult + " 行");
            System.out.println("  生成的 ID: " + newUser.getId());
            System.out.println("  创建时间：" + newUser.getCreateTime());
            System.out.println("✓ 完成\n");
            
            // ===== 测试 4: 条件查询 =====
            System.out.println("【测试 4】条件查询（状态=1，按创建时间降序）");
            System.out.println("─────────────────────────────────────");
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("status", 1).orderByDesc("create_time");
            
            var conditionUsers = userMapper.selectList(wrapper);
            System.out.println("正常状态的用户共 " + conditionUsers.size() + " 个:");
            for (User u : conditionUsers) {
                System.out.println("  [" + u.getId() + "] " + u.getName() + " - " + u.getPhone());
            }
            System.out.println("✓ 完成\n");
            
            // ===== 测试 5: 模糊查询 =====
            System.out.println("【测试 5】模糊查询（手机号包含 138）");
            System.out.println("─────────────────────────────────────");
            QueryWrapper<User> likeWrapper = new QueryWrapper<>();
            likeWrapper.like("phone", "138");
            
            var likeUsers = userMapper.selectList(likeWrapper);
            System.out.println("手机号包含 138 的用户共 " + likeUsers.size() + " 个:");
            for (User u : likeUsers) {
                System.out.println("  [" + u.getId() + "] " + u.getName() + " - " + u.getPhone());
            }
            System.out.println("✓ 完成\n");
            
            // ===== 测试 6: 分页查询 =====
            System.out.println("【测试 6】分页查询（第 1 页，每页 10 条）");
            System.out.println("─────────────────────────────────────");
            Page<User> page = new Page<>(1, 10);
            QueryWrapper<User> pageWrapper = new QueryWrapper<>();
            pageWrapper.eq("status", 1);
            
            Page<User> resultPage = userMapper.selectPage(page, pageWrapper);
            System.out.println("  总记录数：" + resultPage.getTotal());
            System.out.println("  总页数：" + resultPage.getPages());
            System.out.println("  当前页：" + resultPage.getCurrent());
            System.out.println("  当前页数据:");
            for (User u : resultPage.getRecords()) {
                System.out.println("    [" + u.getId() + "] " + u.getName() + " - " + u.getPhone());
            }
            System.out.println("✓ 完成\n");
            
            // ===== 测试 7: 更新用户 =====
            System.out.println("【测试 7】更新用户（ID=" + newUser.getId() + "）");
            System.out.println("─────────────────────────────────────");
            System.out.println("  更新前姓名：" + newUser.getName());
            newUser.setName("更新后的名字");
            int updateResult = userMapper.updateById(newUser);
            System.out.println("  更新结果：" + updateResult + " 行");
            System.out.println("  更新时间：" + newUser.getUpdateTime());
            System.out.println("✓ 完成\n");
            
            // ===== 测试 8: 逻辑删除 =====
            System.out.println("【测试 8】逻辑删除（ID=" + newUser.getId() + "）");
            System.out.println("─────────────────────────────────────");
            int deleteResult = userMapper.deleteById(newUser.getId());
            System.out.println("  删除结果：" + deleteResult + " 行");
            
            User deletedUser = userMapper.selectById(newUser.getId());
            if (deletedUser == null) {
                System.out.println("  ✓ 逻辑删除成功！查询不到已删除的用户");
            } else {
                System.out.println("  ✗ 异常：还能查到已删除的用户");
            }
            System.out.println("✓ 完成\n");
            
        } catch (Exception e) {
            System.err.println("❌ 测试出错：" + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭 Spring 容器
            context.close();
        }
        
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║     MyBatis-Plus CRUD 测试完成！✅          ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("\n");
    }
}
