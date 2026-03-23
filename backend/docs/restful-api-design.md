# RESTful API 设计规范

## 1. 核心原则

### 1.1 资源导向

> 把一切都看成资源，用 URL 定位资源，用 HTTP 方法操作资源。

**资源示例：**
- 用户：`/users`
- 订单：`/orders`
- 商品：`/dishes`
- 店铺：`/shops`

---

## 2. URL 设计规范

### 2.1 用名词，不用动词

❌ **错误：**
```
/getUser
/createOrder
/deleteProduct
/updateUser
```

✅ **正确：**
```
/users      # 用户集合
/orders     # 订单集合
/products   # 商品集合
```

### 2.2 用复数名词

❌ **错误：**
```
/user/1
/order/123
```

✅ **正确：**
```
/users/1
/orders/123
```

**原因：** `/users` 表示集合，`/users/1` 表示集合中的元素

### 2.3 用小写字母，单词间用连字符

❌ **错误：**
```
/UserOrders
/userOrders
/USER_ORDERS
```

✅ **正确：**
```
/user-orders
```

**原因：** URL 大小写敏感，连字符最清晰

### 2.4 用路径表示资源关系

```
# 查询用户 1 的订单
GET /users/1/orders

# 查询订单 123 的明细
GET /orders/123/details

# 查询店铺 1 的商品
GET /shops/1/dishes

# 查询商品的分类
GET /dishes/1/category
```

### 2.5 用查询参数过滤、排序、分页

```
# 过滤
GET /users?status=1&gender=1

# 模糊查询
GET /users?name=张三

# 排序
GET /users?sort=createTime,desc
GET /users?sort=createTime,asc&sort=name,asc

# 分页
GET /users?page=1&size=10

# 范围查询
GET /orders?createTimeMin=2024-01-01&createTimeMax=2024-12-31

# 组合查询
GET /users?status=1&sort=createTime,desc&page=1&size=10
```

---

## 3. HTTP 方法使用规范

### 3.1 方法说明

| 方法 | 操作 | 幂等性 | 安全性 | 示例 |
|------|------|--------|--------|------|
| `GET` | 查询 | ✅ | ✅ | 获取用户列表 |
| `POST` | 新增 | ❌ | ❌ | 创建新用户 |
| `PUT` | 全量更新 | ✅ | ❌ | 更新用户全部信息 |
| `PATCH` | 部分更新 | ✅ | ❌ | 更新用户手机号 |
| `DELETE` | 删除 | ✅ | ❌ | 删除用户 |

### 3.2 幂等性

**幂等：** 多次执行结果相同

- `GET /users/1` - 查询 1 次和 100 次结果一样 ✅
- `DELETE /users/1` - 删除 1 次和 100 次结果一样 ✅
- `POST /users` - 创建 1 次和 100 次结果不一样（会创建 100 个用户）❌

### 3.3 实战示例

**用户资源：**

| 操作 | 方法 | URL | 请求体 | 响应 |
|------|------|-----|--------|------|
| 查询所有用户 | GET | `/users` | - | `[{id:1,name:"张三"}, ...]` |
| 查询单个用户 | GET | `/users/1` | - | `{id:1,name:"张三"}` |
| 创建用户 | POST | `/users` | `{name:"张三",phone:"138..."}` | `{id:1,...}` |
| 全量更新 | PUT | `/users/1` | `{name:"李四",phone:"139..."}` | `{id:1,...}` |
| 部分更新 | PATCH | `/users/1` | `{name:"李四"}` | `{id:1,...}` |
| 删除用户 | DELETE | `/users/1` | - | `204 No Content` |

---

## 4. 状态码使用规范

### 4.1 成功状态码

| 状态码 | 含义 | 使用场景 |
|--------|------|---------|
| `200 OK` | 成功 | GET/PUT/PATCH 成功 |
| `201 Created` | 已创建 | POST 成功（创建资源） |
| `204 No Content` | 无内容 | DELETE 成功 |

### 4.2 客户端错误状态码

| 状态码 | 含义 | 使用场景 |
|--------|------|---------|
| `400 Bad Request` | 请求错误 | 参数错误、JSON 格式错误 |
| `401 Unauthorized` | 未授权 | 未登录、Token 过期 |
| `403 Forbidden` | 禁止访问 | 无权限 |
| `404 Not Found` | 不存在 | 资源不存在 |
| `409 Conflict` | 冲突 | 资源已存在（如手机号重复） |
| `422 Unprocessable Entity` | 参数验证失败 | 字段格式错误 |

### 4.3 服务器错误状态码

| 状态码 | 含义 | 使用场景 |
|--------|------|---------|
| `500 Internal Server Error` | 服务器错误 | 代码 bug、数据库异常 |
| `502 Bad Gateway` | 网关错误 | 上游服务不可用 |
| `503 Service Unavailable` | 服务不可用 | 服务过载、维护中 |

### 4.4 实战示例

```java
// 200 OK - 查询成功
@GetMapping("/users/{id}")
public Result<User> getUser(@PathVariable Long id) {
    User user = userService.getById(id);
    return Result.success(user);
}

// 201 Created - 创建成功
@PostMapping("/users")
public Result<User> createUser(@RequestBody User user) {
    userService.save(user);
    return Result.success(user);  // 实际应该返回 201
}

// 404 Not Found - 资源不存在
@GetMapping("/users/{id}")
public Result<User> getUser(@PathVariable Long id) {
    User user = userService.getById(id);
    if (user == null) {
        return Result.error(404, "用户不存在");
    }
    return Result.success(user);
}

// 400 Bad Request - 参数错误
@PostMapping("/users")
public Result<User> createUser(@RequestBody User user) {
    if (user.getPhone() == null || user.getPhone().isEmpty()) {
        return Result.error(400, "手机号不能为空");
    }
    userService.save(user);
    return Result.success(user);
}

// 409 Conflict - 资源冲突
@PostMapping("/users")
public Result<User> createUser(@RequestBody User user) {
    User existUser = userService.getByPhone(user.getPhone());
    if (existUser != null) {
        return Result.error(409, "手机号已存在");
    }
    userService.save(user);
    return Result.success(user);
}
```

---

## 5. 统一响应格式

### 5.1 标准响应结构

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 5.2 成功响应

```json
// 无数据
{
  "code": 200,
  "message": "success",
  "data": null
}

// 有数据
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "张三",
    "phone": "13800138000"
  }
}

// 列表数据
{
  "code": 200,
  "message": "success",
  "data": [
    {"id": 1, "name": "张三"},
    {"id": 2, "name": "李四"}
  ]
}

// 分页数据
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "pages": 10,
    "current": 1,
    "size": 10,
    "records": [...]
  }
}
```

### 5.3 失败响应

```json
// 通用错误
{
  "code": 500,
  "message": "系统繁忙，请稍后再试",
  "data": null
}

// 参数错误
{
  "code": 400,
  "message": "手机号格式错误",
  "data": null
}

// 未授权
{
  "code": 401,
  "message": "未登录，请先登录",
  "data": null
}

// 资源不存在
{
  "code": 404,
  "message": "用户不存在",
  "data": null
}

// 资源冲突
{
  "code": 409,
  "message": "手机号已存在",
  "data": null
}
```

---

## 6. 版本控制

### 6.1 URL 版本控制（推荐）

```
GET /api/v1/users
GET /api/v2/users
```

**优点：** 清晰、易缓存、易测试

### 6.2 Header 版本控制

```
GET /users
Accept-Version: v1
```

**优点：** URL 简洁

**缺点：** 不够直观

### 6.3 查询参数版本控制

```
GET /users?version=v1
```

**优点：** 简单

**缺点：** 污染查询参数

---

## 7. 文档规范

### 7.1 使用 OpenAPI/Swagger

你的项目已经用了 Knife4j（Swagger 增强版），很好！

**访问文档：** `http://localhost:8080/doc.html`

### 7.2 接口文档要素

每个接口应包含：

1. **接口名称** - 简明扼要
2. **接口描述** - 功能说明
3. **请求方法** - GET/POST/PUT/DELETE
4. **请求 URL** - 完整路径
5. **请求参数** - 参数名、类型、必填、说明
6. **响应示例** - 成功/失败示例
7. **错误码说明** - 可能的错误码

---

## 8. 安全规范

### 8.1 认证

- 使用 JWT Token
- Token 放在 Header：`Authorization: Bearer <token>`
- 敏感接口必须认证

### 8.2 授权

- 基于角色的访问控制（RBAC）
- 用户只能访问自己的资源

### 8.3 限流

- 防止暴力请求
- 如：1 分钟最多发送 3 次验证码

### 8.4 数据验证

- 前端验证 + 后端验证
- 后端验证是最后一道防线

---

## 9. 性能优化

### 9.1 分页

```
# 默认分页，防止返回过多数据
GET /users?page=1&size=20
```

### 9.2 字段过滤

```
# 只返回需要的字段
GET /users/1?fields=id,name,phone
```

### 9.3 缓存

```
# 使用 ETag/Last-Modified
GET /users/1
If-None-Match: "etag-value"
```

---

## 10. 实战检查清单

设计接口时，问自己：

- [ ] URL 是否只用名词，没有动词？
- [ ] 资源名是否用复数？
- [ ] 是否用了正确的 HTTP 方法？
- [ ] 状态码是否准确？
- [ ] 响应格式是否统一？
- [ ] 是否有文档？
- [ ] 是否考虑了错误处理？
- [ ] 是否考虑了分页？
- [ ] 是否考虑了安全性？

---

## 11. 你的项目接口改进建议

### 当前接口

```
POST /user/sendLoginCode   → 发送验证码
POST /user/login           → 登录
POST /user/logout          → 退出
```

### 改进建议

```
POST /user/codes           → 发送验证码（创建验证码资源）
POST /user/tokens          → 登录（创建 Token 资源）
DELETE /user/tokens        → 退出（删除 Token 资源）
```

### 完整设计

```
# 发送验证码
POST /user/codes
{
  "phone": "13800138000"
}
响应：200 OK

# 登录（验证码登录）
POST /user/tokens
{
  "phone": "13800138000",
  "code": "6666"
}
响应：201 Created
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {...}
}

# 退出登录
DELETE /user/tokens
Authorization: Bearer <token>
响应：204 No Content

# 获取当前用户信息
GET /user/me
Authorization: Bearer <token>
响应：200 OK
{
  "id": 1,
  "name": "张三",
  "phone": "13800138000"
}
```

---

## 12. 总结

**RESTful 核心：**

1. **资源导向** - URL 只表示资源
2. **HTTP 方法** - 用 GET/POST/PUT/DELETE 表示操作
3. **统一格式** - 响应结构、状态码统一
4. **无状态** - 每次请求包含所有信息

**记住：** 好的 API 设计让人一看就懂，不需要解释！

---

**下一步：** 把你的接口改造成 RESTful 风格！
