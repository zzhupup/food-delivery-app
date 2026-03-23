package com.takeout.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.entity.User;
import com.takeout.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * MyBatis-Plus 快速测试
 * 启动应用后自动执行
 *
 * @author 小好
 * 
 * 注意：如果启动报错，可以临时注释掉 @Component 注解，禁用自动测试
 */
// @Component  // 暂时禁用自动测试，避免启动报错
public class MybatisPlusTest implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(String... args) {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║     MyBatis-Plus CRUD 测试开始！🚀          ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("\n");

        try {
            // ===== 测试 1: 查询所有用户 =====
            testSelectAll();

            // ===== 测试 2: 根据 ID 查询 =====
            testSelectById();

            // ===== 测试 3: 新增用户 =====
            testInsert();

            // ===== 测试 4: 条件查询 =====
            testSelectByCondition();

            // ===== 测试 5: 模糊查询 =====
            testSelectLike();

            // ===== 测试 6: 分页查询 =====
            testSelectPage();

            // ===== 测试 7: 更新用户 =====
            testUpdate();

            // ===== 测试 8: 逻辑删除 =====
            testLogicDelete();

        } catch (Exception e) {
            System.err.println("❌ 测试出错：" + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║     MyBatis-Plus CRUD 测试完成！✅          ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("\n");
    }

    private void testSelectAll() {
        System.out.println("【测试 1】查询所有用户");
        System.out.println("─────────────────────────────────────");
        var users = userMapper.selectList(null);
        System.out.println("共查询到 " + users.size() + " 个用户:");
        for (User user : users) {
            System.out.println("  [" + user.getId() + "] " + user.getName() + " - " + user.getPhone());
        }
        System.out.println("✓ 完成\n");
    }

    private void testSelectById() {
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
    }

    private void testInsert() {
        System.out.println("【测试 3】新增用户");
        System.out.println("─────────────────────────────────────");
        User newUser = new User();
        // 使用时间戳生成唯一手机号，避免重复
        newUser.setPhone("138" + System.currentTimeMillis());
        newUser.setName("测试用户" + System.currentTimeMillis());
        newUser.setGender(1);
        newUser.setStatus(1);

        int result = userMapper.insert(newUser);
        System.out.println("  插入结果：" + result + " 行");
        System.out.println("  生成的 ID: " + newUser.getId());
        System.out.println("  创建时间：" + newUser.getCreateTime());
        System.out.println("✓ 完成\n");
    }

    private void testSelectByCondition() {
        System.out.println("【测试 4】条件查询（状态=1，按创建时间降序）");
        System.out.println("─────────────────────────────────────");
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1).orderByDesc("create_time");

        var users = userMapper.selectList(wrapper);
        System.out.println("正常状态的用户共 " + users.size() + " 个:");
        for (User user : users) {
            System.out.println("  [" + user.getId() + "] " + user.getName() + " - " + user.getPhone());
        }
        System.out.println("✓ 完成\n");
    }

    private void testSelectLike() {
        System.out.println("【测试 5】模糊查询（手机号包含 138）");
        System.out.println("─────────────────────────────────────");
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("phone", "138");

        var users = userMapper.selectList(wrapper);
        System.out.println("手机号包含 138 的用户共 " + users.size() + " 个:");
        for (User user : users) {
            System.out.println("  [" + user.getId() + "] " + user.getName() + " - " + user.getPhone());
        }
        System.out.println("✓ 完成\n");
    }

    private void testSelectPage() {
        System.out.println("【测试 6】分页查询（第 1 页，每页 10 条）");
        System.out.println("─────────────────────────────────────");
        Page<User> page = new Page<>(1, 10);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);

        Page<User> resultPage = userMapper.selectPage(page, wrapper);
        System.out.println("  总记录数：" + resultPage.getTotal());
        System.out.println("  总页数：" + resultPage.getPages());
        System.out.println("  当前页：" + resultPage.getCurrent());
        System.out.println("  当前页数据:");
        for (User user : resultPage.getRecords()) {
            System.out.println("    [" + user.getId() + "] " + user.getName() + " - " + user.getPhone());
        }
        System.out.println("✓ 完成\n");
    }

    private void testUpdate() {
        System.out.println("【测试 7】更新用户");
        System.out.println("─────────────────────────────────────");
        User user = userMapper.selectById(1L);
        if (user != null) {
            System.out.println("  更新前姓名：" + user.getName());
            user.setName("更新后的名字" + System.currentTimeMillis());
            int result = userMapper.updateById(user);
            System.out.println("  更新结果：" + result + " 行");
            System.out.println("  更新时间：" + user.getUpdateTime());
        } else {
            System.out.println("  用户不存在");
        }
        System.out.println("✓ 完成\n");
    }

    private void testLogicDelete() {
        System.out.println("【测试 8】逻辑删除");
        System.out.println("─────────────────────────────────────");
        // 先新增一个用户（使用时间戳避免重复）
        User user = new User();
        user.setPhone("139" + System.currentTimeMillis());
        user.setName("待删除用户");
        user.setGender(0);
        user.setStatus(1);
        userMapper.insert(user);

        System.out.println("  新增用户 ID: " + user.getId());

        // 逻辑删除
        int result = userMapper.deleteById(user.getId());
        System.out.println("  删除结果：" + result + " 行");

        // 验证：应该查不到了
        User deletedUser = userMapper.selectById(user.getId());
        if (deletedUser == null) {
            System.out.println("  ✓ 逻辑删除成功！查询不到已删除的用户");
        } else {
            System.out.println("  ✗ 异常：还能查到已删除的用户");
        }
        System.out.println("✓ 完成\n");
    }
}
