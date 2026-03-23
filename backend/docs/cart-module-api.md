# 购物车模块 API 文档

## 模块概述

**功能：** 购物车增删改查  
**认证方式：** JWT Token（需要登录）  
**基础路径：** `/cart`

---

## 接口列表

### 1. 添加购物车

**接口：** `POST /cart`

**说明：** 添加菜品到购物车（如果已存在则数量 +1）

**请求头：**
```
Authorization: Bearer <token>
```

**请求参数：**
```json
{
  "dishId": 1
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "添加成功",
  "data": null
}
```

**说明：**
- 自动获取当前用户 ID
- 如果购物车中已有该菜品，数量 +1
- 如果不存在，新增一条记录
- 自动填充菜品名称、图片、单价

---

### 2. 查询购物车

**接口：** `GET /cart/list`

**说明：** 查询当前用户的购物车列表

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "dishId": 1,
      "shopId": 1,
      "count": 2,
      "dishName": "宫保鸡丁",
      "dishImage": "https://example.com/dish1.jpg",
      "price": 28.00,
      "createTime": "2024-03-18T10:00:00",
      "updateTime": "2024-03-18T10:05:00"
    },
    {
      "id": 2,
      "userId": 1,
      "dishId": 2,
      "shopId": 1,
      "count": 1,
      "dishName": "鱼香肉丝",
      "dishImage": "https://example.com/dish2.jpg",
      "price": 25.00,
      "createTime": "2024-03-18T10:01:00",
      "updateTime": "2024-03-18T10:01:00"
    }
  ]
}
```

**说明：**
- 按创建时间降序排列
- 只返回当前用户的购物车

---

### 3. 修改购物车数量

**接口：** `PUT /cart`

**说明：** 修改购物车中菜品的数量

**请求头：**
```
Authorization: Bearer <token>
```

**请求参数：**
```json
{
  "cartId": 1,
  "count": 5
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "修改成功",
  "data": null
}
```

**说明：**
- count <= 0 时，自动删除该记录
- 会验证购物车记录是否属于当前用户

---

### 4. 删除购物车

**接口：** `DELETE /cart/{id}`

**说明：** 删除购物车中的某个菜品

**请求头：**
```
Authorization: Bearer <token>
```

**路径参数：**
```
id: 购物车记录 ID
```

**响应示例：**
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 5. 清空购物车

**接口：** `DELETE /cart/clear`

**说明：** 清空当前用户的所有购物车记录

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**
```json
{
  "code": 200,
  "message": "清空成功",
  "data": null
}
```

---

### 6. 批量删除

**接口：** `DELETE /cart/batch`

**说明：** 批量删除购物车记录

**请求头：**
```
Authorization: Bearer <token>
```

**请求参数：**
```json
[1, 2, 3]
```

**响应示例：**
```json
{
  "code": 200,
  "message": "批量删除成功",
  "data": null
}
```

---

## 完整测试流程

### 步骤 1：登录获取 Token

```bash
curl -X POST http://localhost:8080/user/tokens \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","code":"6666"}'
```

记录返回的 `token`

---

### 步骤 2：添加购物车

```bash
# 添加菜品 1 到购物车
curl -X POST http://localhost:8080/cart \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"dishId":1}'

# 再次添加菜品 1（数量应该变为 2）
curl -X POST http://localhost:8080/cart \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"dishId":1}'

# 添加菜品 2 到购物车
curl -X POST http://localhost:8080/cart \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"dishId":2}'
```

---

### 步骤 3：查询购物车

```bash
curl -X GET http://localhost:8080/cart/list \
  -H "Authorization: Bearer <token>"
```

应该能看到：
- 菜品 1：count=2
- 菜品 2：count=1

---

### 步骤 4：修改数量

```bash
# 把菜品 1 的数量改为 5
curl -X PUT http://localhost:8080/cart \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"cartId":1,"count":5}'

# 把菜品 2 的数量改为 0（会自动删除）
curl -X PUT http://localhost:8080/cart \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"cartId":2,"count":0}'
```

---

### 步骤 5：删除购物车

```bash
# 删除菜品 1
curl -X DELETE http://localhost:8080/cart/1 \
  -H "Authorization: Bearer <token>"
```

---

### 步骤 6：清空购物车

```bash
curl -X DELETE http://localhost:8080/cart/clear \
  -H "Authorization: Bearer <token>"
```

---

## 技术要点

### 1. 自动填充菜品信息

添加购物车时，会自动查询菜品信息并填充：

```java
// 查询菜品信息
Dish dish = dishService.getById(cart.getDishId());

// 填充冗余字段
cart.setShopId(dish.getShopId());
cart.setDishName(dish.getName());
cart.setDishImage(dish.getImage());
cart.setPrice(dish.getPrice());
```

**为什么冗余存储？**
- 避免每次查询都 JOIN 菜品表
- 即使菜品下架/删除，购物车记录依然完整
- 价格快照（下单时的价格）

---

### 2. 数量 +1 逻辑

```java
// 查询是否已有该菜品
Cart existCart = lambdaQuery()
    .eq(Cart::getUserId, userId)
    .eq(Cart::getDishId, cart.getDishId())
    .one();

if (existCart != null) {
    // 已有，数量 +1
    existCart.setCount(existCart.getCount() + 1);
    this.updateById(existCart);
} else {
    // 没有，新增
    cart.setCount(1);
    this.save(cart);
}
```

---

### 3. 用户权限验证

```java
// 验证购物车记录是否属于当前用户
if (!cart.getUserId().equals(userId)) {
    throw new RuntimeException("无权操作该购物车记录");
}
```

**为什么需要验证？**
- 防止用户操作他人的购物车
- 虽然 ID 是递增的，但不能依赖"难猜"作为安全手段

---

### 4. ThreadLocal 获取用户 ID

```java
// Service 层直接获取
Long userId = BaseContext.getCurrentId();
```

**好处：**
- Controller 不需要传递 userId
- 所有 Service 都能获取当前用户
- 代码更简洁

---

## 数据库表结构

```sql
CREATE TABLE `cart` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户 ID',
  `dish_id` bigint(20) DEFAULT NULL COMMENT '菜品 ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺 ID',
  `count` int(11) NOT NULL DEFAULT 1 COMMENT '数量',
  `dish_name` varchar(50) DEFAULT NULL COMMENT '菜品名称',
  `dish_image` varchar(255) DEFAULT NULL COMMENT '菜品图片',
  `price` decimal(10,2) DEFAULT NULL COMMENT '菜品单价',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_dish_id` (`dish_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**索引说明：**
- `idx_user_id`：查询用户购物车时用到
- `idx_dish_id`：统计菜品销量时用到

---

## 常见问题

### Q1: 为什么购物车要冗余菜品信息？

**A:** 避免关联查询，提高性能；同时保证菜品下架后购物车记录依然完整。

### Q2: 不同店铺的商品能一起结算吗？

**A:** 当前设计允许，但实际业务中可能需要限制（比如外卖只能一个店铺）。

### Q3: 购物车有数量限制吗？

**A:** 当前没有，实际项目可以限制最多 99 件。

### Q4: 用户未登录时购物车怎么处理？

**A:** 两种方案：
1. 存入 Redis，登录后合并到数据库
2. 强制登录后才能使用购物车

---

## 下一步

购物车模块已完成！接下来可以：

A) 测试购物车模块所有接口
B) 继续学习订单模块
C) 继续学习地址模块

---

**记住：**
- 购物车是下单的前置步骤
- 冗余字段是常见的性能优化手段
- 权限验证必不可少
