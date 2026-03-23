# 外卖平台项目实战指南

## 项目概述

**项目名称：** Takeout Platform（外卖平台）
**技术栈：** Spring Boot + MyBatis-Plus + MySQL + Redis
**架构：** 分层架构（Controller → Service → Mapper → Entity）

---

## 模块 1：用户模块

### 1.1 功能清单

| 接口 | 方法 | URL | 说明 | 状态 |
|------|------|-----|------|------|
| 发送验证码 | POST | `/user/codes` | 发送登录验证码 | ⏳ 待完成 |
| 登录 | POST | `/user/tokens` | 验证码登录，返回 Token | ⏳ 待完成 |
| 退出登录 | DELETE | `/user/tokens` | 使 Token 失效 | ⏳ 待完成 |
| 获取当前用户 | GET | `/user/me` | 获取登录用户信息 | ⏳ 待完成 |
| 更新用户信息 | PUT | `/user/me` | 更新昵称、头像等 | ⏳ 待完成 |

### 1.2 数据库表结构

```sql
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `phone` varchar(11) NOT NULL COMMENT '手机号',
  `name` varchar(50) DEFAULT NULL COMMENT '用户昵称',
  `gender` tinyint(4) DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像 URL',
  `status` tinyint(4) DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `deleted` tinyint(4) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 1.3 实现步骤

#### 步骤 1：完善 UserService 接口

```java
public interface UserService extends IService<User> {
    /**
     * 发送登录验证码
     */
    void sendCode(String phone);
    
    /**
     * 验证码登录
     */
    User login(String phone, String code);
    
    /**
     * 获取当前登录用户
     */
    User getCurrentUser();
}
```

#### 步骤 2：实现 UserService

```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Override
    public void sendCode(String phone) {
        // 1. 生成 6 位验证码
        String code = RandomStringUtils.randomNumeric(6);
        
        // 2. 存入 Redis，5 分钟有效期
        redisTemplate.opsForValue().set("code:" + phone, code, 5, TimeUnit.MINUTES);
        
        // 3. 发送短信（实际项目调用短信服务）
        log.info("发送验证码：phone={}, code={}", phone, code);
    }
    
    @Override
    public User login(String phone, String code) {
        // 1. 验证验证码
        String storedCode = redisTemplate.opsForValue().get("code:" + phone);
        if (storedCode == null || !storedCode.equals(code)) {
            throw new BusinessException("验证码错误");
        }
        
        // 2. 查询用户
        User user = lambdaQuery()
            .eq(User::getPhone, phone)
            .one();
        
        // 3. 新用户自动注册
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setName("新用户" + phone.substring(7));
            user.setStatus(1);
            this.save(user);
        }
        
        // 4. 删除验证码
        redisTemplate.delete("code:" + phone);
        
        return user;
    }
    
    @Override
    public User getCurrentUser() {
        // 从 ThreadLocal 或 Token 中获取当前用户 ID
        Long userId = BaseContext.getCurrentId();
        return this.getById(userId);
    }
}
```

#### 步骤 3：改造 UserController

```java
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
        return Result.success("6666"); // 测试环境直接返回
    }
    
    /**
     * 登录
     * POST /user/tokens
     */
    @PostMapping("/tokens")
    @Operation(summary = "用户登录")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String code = params.get("code");
        
        User user = userService.login(phone, code);
        
        // 生成 JWT Token（后续实现）
        String token = JwtUtil.createToken(user.getId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        
        return Result.success(result);
    }
    
    /**
     * 退出登录
     * DELETE /user/tokens
     */
    @DeleteMapping("/tokens")
    @Operation(summary = "退出登录")
    public Result<String> logout() {
        // 使 Token 失效（后续实现）
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
        userService.updateById(user);
        return Result.success(user);
    }
}
```

---

## 模块 2：购物车模块

### 2.1 功能清单

| 接口 | 方法 | URL | 说明 | 状态 |
|------|------|-----|------|------|
| 添加购物车 | POST | `/cart` | 添加菜品到购物车 | ⏳ 待完成 |
| 查询购物车 | GET | `/cart/list` | 查询当前用户购物车 | ⏳ 待完成 |
| 修改数量 | PUT | `/cart` | 修改菜品数量 | ⏳ 待完成 |
| 删除购物车 | DELETE | `/cart/{id}` | 删除购物车中的菜品 | ⏳ 待完成 |
| 清空购物车 | DELETE | `/cart/clear` | 清空当前用户购物车 | ⏳ 待完成 |

### 2.2 数据库表结构

```sql
CREATE TABLE `cart` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `dish_id` bigint(20) DEFAULT NULL COMMENT '菜品 ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺 ID',
  `count` int(11) NOT NULL DEFAULT 1 COMMENT '数量',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_dish` (`user_id`, `dish_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';
```

### 2.3 实体类

```java
@Data
@TableName("cart")
public class Cart implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long dishId;
    private Long shopId;
    private Integer count;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

### 2.4 Service 实现

```java
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {
    
    @Override
    @Transactional
    public void addToCart(Cart cart) {
        Long userId = BaseContext.getCurrentId();
        cart.setUserId(userId);
        
        // 查询购物车中是否已有该菜品
        Cart existCart = lambdaQuery()
            .eq(Cart::getUserId, userId)
            .eq(Cart::getDishId, cart.getDishId())
            .one();
        
        if (existCart != null) {
            // 已有该菜品，数量 +1
            existCart.setCount(existCart.getCount() + 1);
            this.updateById(existCart);
        } else {
            // 没有该菜品，新增
            cart.setCount(1);
            this.save(cart);
        }
    }
    
    @Override
    public List<Cart> getCurrentCart() {
        Long userId = BaseContext.getCurrentId();
        return lambdaQuery()
            .eq(Cart::getUserId, userId)
            .orderByDesc(Cart::getCreateTime)
            .list();
    }
    
    @Override
    @Transactional
    public void clearCart() {
        Long userId = BaseContext.getCurrentId();
        lambdaUpdate()
            .eq(Cart::getUserId, userId)
            .remove();
    }
}
```

---

## 模块 3：订单模块

### 3.1 功能清单

| 接口 | 方法 | URL | 说明 | 状态 |
|------|------|-----|------|------|
| 提交订单 | POST | `/order` | 提交订单 | ⏳ 待完成 |
| 订单列表 | GET | `/order/list` | 查询用户订单列表 | ⏳ 待完成 |
| 订单详情 | GET | `/order/{id}` | 查询订单详情 | ⏳ 待完成 |
| 取消订单 | PUT | `/order/cancel/{id}` | 取消订单 | ⏳ 待完成 |
| 确认收货 | PUT | `/order/confirm/{id}` | 确认收货 | ⏳ 待完成 |

### 3.2 订单状态流转

```
待付款 (1) → 待发货 (2) → 待收货 (3) → 已完成 (4)
    ↓
  已取消 (5)
```

### 3.3 数据库表结构

```sql
CREATE TABLE `orders` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_number` varchar(50) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺 ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单金额',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态 1-待付款 2-待发货 3-待收货 4-已完成 5-已取消',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_number` (`order_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE `order_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单 ID',
  `dish_id` bigint(20) NOT NULL COMMENT '菜品 ID',
  `dish_name` varchar(50) NOT NULL COMMENT '菜品名称',
  `count` int(11) NOT NULL COMMENT '数量',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';
```

---

## 模块 4：地址模块

### 4.1 功能清单

| 接口 | 方法 | URL | 说明 | 状态 |
|------|------|-----|------|------|
| 地址列表 | GET | `/address/list` | 查询用户地址列表 | ⏳ 待完成 |
| 新增地址 | POST | `/address` | 新增收货地址 | ⏳ 待完成 |
| 修改地址 | PUT | `/address` | 修改收货地址 | ⏳ 待完成 |
| 删除地址 | DELETE | `/address/{id}` | 删除收货地址 | ⏳ 待完成 |
| 默认地址 | GET | `/address/default` | 获取默认地址 | ⏳ 待完成 |

### 4.2 数据库表结构

```sql
CREATE TABLE `address_book` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `consignee` varchar(50) NOT NULL COMMENT '收货人',
  `phone` varchar(11) NOT NULL COMMENT '手机号',
  `province` varchar(50) DEFAULT NULL COMMENT '省',
  `city` varchar(50) DEFAULT NULL COMMENT '市',
  `district` varchar(50) DEFAULT NULL COMMENT '区',
  `detail` varchar(100) NOT NULL COMMENT '详细地址',
  `is_default` tinyint(4) DEFAULT 0 COMMENT '是否默认 0-否 1-是',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地址表';
```

---

## 实战任务清单

### 阶段 1：用户模块（本周）

- [ ] 1.1 完善 UserService（sendCode、login、getCurrentUser）
- [ ] 1.2 改造 UserController 为 RESTful 风格
- [ ] 1.3 集成 JWT Token 认证
- [ ] 1.4 实现登录拦截器

### 阶段 2：购物车模块（下周）

- [ ] 2.1 创建 Cart 实体类
- [ ] 2.2 创建 CartMapper 接口
- [ ] 2.3 创建 CartService 接口和实现
- [ ] 2.4 创建 CartController 接口

### 阶段 3：订单模块（下下周）

- [ ] 3.1 创建 Orders 和 OrderDetail 实体
- [ ] 3.2 创建订单 Service
- [ ] 3.3 实现下单逻辑
- [ ] 3.4 实现订单状态流转

### 阶段 4：地址模块（月底）

- [ ] 4.1 创建 AddressBook 实体
- [ ] 4.2 创建地址 Service
- [ ] 4.3 实现地址 CRUD 接口

---

## 技术要点

### 1. ThreadLocal 存储用户 ID

```java
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }
    
    public static Long getCurrentId() {
        return threadLocal.get();
    }
    
    public static void removeCurrentId() {
        threadLocal.remove();
    }
}
```

### 2. JWT 工具类

```java
public class JwtUtil {
    private static final String SECRET = "your-secret-key";
    
    public static String createToken(Long userId) {
        return JWT.create()
            .withClaim("userId", userId)
            .withExpiresAt(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
            .sign(Algorithm.HMAC256(SECRET));
    }
    
    public static Long verifyToken(String token) {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(SECRET))
            .build()
            .verify(token);
        return jwt.getClaim("userId").asLong();
    }
}
```

### 3. 登录拦截器

```java
@Component
public class LoginInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }
        
        try {
            Long userId = JwtUtil.verifyToken(token.substring(7));
            BaseContext.setCurrentId(userId);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        BaseContext.removeCurrentId();
    }
}
```

---

## 开始实战！

**选择你想先完成的模块：**

A) 用户模块（登录、JWT 认证）
B) 购物车模块（CRUD）
C) 订单模块（下单、状态流转）
D) 地址模块（地址管理）

告诉我你的选择，咱们开始动手！💪
