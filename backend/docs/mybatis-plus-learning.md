# MyBatis-Plus 学习指南

## 📚 已完成的配置分析

### 1. 项目结构
```
takeout-platform/
├── takeout-common/      # 工具类
├── takeout-pojo/        # 实体类 (Entity)
├── takeout-mapper/      # Mapper 接口
├── takeout-service/     # 业务逻辑
└── takeout-controller/  # 控制器 + 启动类
```

### 2. 核心配置

**application.yml:**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/takeout_db
    username: root
    password: root123

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true  # 下划线转驼峰
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # SQL 日志
  global-config:
    db-config:
      id-type: auto  # 主键自增
      logic-delete-field: deleted  # 逻辑删除字段
```

### 3. 实体类示例 (User.java)
```java
@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String phone;
    private String name;
    private Integer gender;
    private Integer status;
    
    @TableLogic  // 逻辑删除
    private Integer deleted;
}
```

### 4. Mapper 接口
```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承 BaseMapper 就有 17 个 CRUD 方法！
}
```

---

## 🎯 MyBatis-Plus 核心 API（17 个方法）

### 一、Insert 插入

```java
// 1. 插入一条记录
User user = new User();
user.setPhone("13800138000");
user.setName("张三");
int result = userMapper.insert(user);
// result = 1 (影响行数)
// user.getId() 会自动返回生成的主键 ID
```

### 二、Delete 删除

```java
// 2. 根据 ID 删除（逻辑删除）
int result = userMapper.deleteById(1L);

// 3. 根据条件删除
QueryWrapper<User> wrapper = new QueryWrapper<>();
wrapper.eq("status", 0);  // 删除状态为 0 的用户
int result = userMapper.delete(wrapper);

// 4. 批量删除
List<Long> ids = Arrays.asList(1L, 2L, 3L);
int result = userMapper.deleteBatchIds(ids);
```

### 三、Update 更新

```java
// 5. 根据 ID 更新
User user = userMapper.selectById(1L);
user.setName("新名字");
int result = userMapper.updateById(user);

// 6. 根据条件更新
User updateUser = new User();
updateUser.setStatus(0);  // 改为禁用

QueryWrapper<User> wrapper = new QueryWrapper<>();
wrapper.eq("gender", 1);  // 所有男性用户

int result = userMapper.update(updateUser, wrapper);
```

### 四、Select 查询

```java
// 7. 根据 ID 查询
User user = userMapper.selectById(1L);

// 8. 查询所有
List<User> users = userMapper.selectList(null);

// 9. 根据条件查询
QueryWrapper<User> wrapper = new QueryWrapper<>();
wrapper.eq("status", 1)        // 状态=正常
       .like("phone", "138")   // 手机号包含 138
       .orderByDesc("create_time");  // 按创建时间降序

List<User> users = userMapper.selectList(wrapper);

// 10. 分页查询
Page<User> page = new Page<>(1, 10);  // 第 1 页，每页 10 条
QueryWrapper<User> wrapper = new QueryWrapper<>();
wrapper.eq("status", 1);

Page<User> result = userMapper.selectPage(page, wrapper);
System.out.println("总数：" + result.getTotal());
System.out.println("总页数：" + result.getPages());
System.out.println("当前页数据：" + result.getRecords());

// 11. 根据 ID 批量查询
List<Long> ids = Arrays.asList(1L, 2L, 3L);
List<User> users = userMapper.selectBatchIds(ids);

// 12. 查询总数
int count = userMapper.selectCount(null);  // 查询所有

QueryWrapper<User> wrapper = new QueryWrapper<>();
wrapper.eq("status", 1);
int count = userMapper.selectCount(wrapper);  // 条件查询
```

---

## 🔥 条件构造器 QueryWrapper（重点！）

### 常用方法

| 方法 | 说明 | 举例 |
|------|------|------|
| `eq` | 等于 | `eq("status", 1)` → `status = 1` |
| `ne` | 不等于 | `ne("gender", 0)` → `gender != 0` |
| `gt` | 大于 | `gt("age", 18)` → `age > 18` |
| `ge` | 大于等于 | `ge("age", 18)` → `age >= 18` |
| `lt` | 小于 | `lt("age", 60)` → `age < 60` |
| `le` | 小于等于 | `le("age", 60)` → `age <= 60` |
| `like` | 模糊查询 | `like("name", "张")` → `name LIKE '%张%'` |
| `likeRight` | 右模糊 | `likeRight("phone", "138")` → `phone LIKE '138%'` |
| `likeLeft` | 左模糊 | `likeLeft("name", "三")` → `name LIKE '%三'` |
| `in` | IN 查询 | `in("status", 1, 2, 3)` |
| `isNull` | 为空 | `isNull("email")` |
| `isNotNull` | 不为空 | `isNotNull("phone")` |
| `orderByAsc` | 升序 | `orderByAsc("create_time")` |
| `orderByDesc` | 降序 | `orderByDesc("create_time")` |
| `between` | 范围 | `between("age", 18, 60)` → `age BETWEEN 18 AND 60` |

### 实战例子

```java
// 例子 1: 查询手机号以 138 开头的男性用户，按年龄升序
QueryWrapper<User> wrapper1 = new QueryWrapper<>();
wrapper1.likeRight("phone", "138")
        .eq("gender", 1)
        .orderByAsc("age");
List<User> users1 = userMapper.selectList(wrapper1);

// 例子 2: 查询 18-60 岁之间且状态正常的用户
QueryWrapper<User> wrapper2 = new QueryWrapper<>();
wrapper2.between("age", 18, 60)
        .eq("status", 1);
List<User> users2 = userMapper.selectList(wrapper2);

// 例子 3: 查询姓名包含"张"或"王"的用户
QueryWrapper<User> wrapper3 = new QueryWrapper<>();
wrapper3.and(w -> w.like("name", "张"))
        .or()
        .like("name", "王");
List<User> users3 = userMapper.selectList(wrapper3);

// 例子 4: 查询有手机号且状态正常的用户，前 10 条
QueryWrapper<User> wrapper4 = new QueryWrapper<>();
wrapper4.isNotNull("phone")
        .eq("status", 1)
        .last("LIMIT 10");  // 直接写 SQL 片段
List<User> users4 = userMapper.selectList(wrapper4);
```

---

## 💡 分页查询（重要！）

### 配置分页插件

需要创建一个配置类：

```java
@Configuration
public class MybatisPlusConfig {
    
    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页拦截器
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

### 使用分页

```java
// 第 1 页，每页 10 条
Page<User> page = new Page<>(1, 10);
QueryWrapper<User> wrapper = new QueryWrapper<>();
wrapper.eq("status", 1);

Page<User> result = userMapper.selectPage(page, wrapper);

// 获取结果
long total = result.getTotal();      // 总记录数
long pages = result.getPages();      // 总页数
long current = result.getCurrent();  // 当前页
List<User> records = result.getRecords();  // 当前页数据
```

---

## 🎓 实战练习

### 练习 1: 创建一个测试类

在 `takeout-controller/src/main/java/com/takeout/demo/` 目录下创建：

```java
package com.takeout.demo;

import com.takeout.entity.User;
import com.takeout.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserTest implements CommandLineRunner {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public void run(String... args) {
        System.out.println("===== 开始测试 MyBatis-Plus =====");
        
        // 在这里写你的测试代码
        
        System.out.println("===== 测试完成 =====");
    }
}
```

启动应用后会自动执行 `run()` 方法！

### 练习 2: 完成以下任务

1. 查询所有状态正常的用户
2. 新增一个用户（手机号：13900139000，姓名：李四）
3. 把 ID=1 的用户手机号改成 13800000000
4. 删除 ID=2 的用户（逻辑删除）
5. 分页查询：第 2 页，每页 5 条

---

## 📝 常见问题

### Q1: 为什么查询不到数据？
- 检查数据库连接配置
- 检查表名是否正确（`@TableName`）
- 检查是否有逻辑删除字段

### Q2: 主键为什么不回显？
- 确保 `@TableId(type = IdType.AUTO)` 配置正确
- 确保数据库表主键是自增的

### Q3: 逻辑删除不生效？
- 检查 `application.yml` 中的逻辑删除配置
- 检查实体类是否有 `@TableLogic` 注解

### Q4: 分页查询返回总数为 0？
- 需要配置分页插件（MybatisPlusInterceptor）
- 确保数据库方言正确（MySQL）

---

## 🚀 下一步

1. **运行测试** - 启动应用，测试 CRUD 操作
2. **学习动态 SQL** - MyBatis XML 方式
3. **整合 Service 层** - 封装业务逻辑
4. **实战项目** - 用到外卖平台中

---

**记住：** MyBatis-Plus 的核心就是**减少 SQL 编写**，单表操作尽量用 MP，复杂查询再用 XML！
