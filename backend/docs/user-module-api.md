# 用户模块 API 文档

## 模块概述

**功能：** 用户登录、注册、信息管理  
**认证方式：** JWT Token  
**基础路径：** `/user`

---

## 接口列表

### 1. 发送验证码

**接口：** `POST /user/codes`

**说明：** 向用户手机号发送登录验证码（测试环境直接返回验证码）

**请求参数：**
```json
{
  "phone": "13800138000"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "验证码已发送（测试环境：6666）",
  "data": "6666"
}
```

**说明：**
- 验证码有效期：5 分钟
- 测试环境验证码固定为 `6666`
- 实际项目需要调用短信服务

---

### 2. 登录（获取 Token）

**接口：** `POST /user/tokens`

**说明：** 验证码登录，返回 JWT Token 和用户信息

**请求参数：**
```json
{
  "phone": "13800138000",
  "code": "6666"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInN1YiI6InRha2VvdXQtdXNlciIsImlhdCI6MTcxMDc0MjQwMCwiZXhwIjoxNzExMzQ3MjAwfQ.xxx",
    "user": {
      "id": 1,
      "phone": "13800138000",
      "name": "用户 8000",
      "gender": 0,
      "avatar": null,
      "status": 1
    }
  }
}
```

**说明：**
- Token 有效期：7 天
- 新用户会自动注册
- 登录成功后验证码会失效

---

### 3. 退出登录

**接口：** `DELETE /user/tokens`

**说明：** 退出登录（使 Token 失效）

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**
```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

**说明：**
- 当前版本 Token 不主动失效（等待自然过期）
- 实际项目可以将 Token 加入黑名单

---

### 4. 获取当前用户信息

**接口：** `GET /user/me`

**说明：** 获取当前登录用户的信息

**请求头：**
```
Authorization: Bearer <token>
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "phone": "13800138000",
    "name": "用户 8000",
    "gender": 0,
    "avatar": null,
    "status": 1,
    "createTime": "2024-03-18T10:00:00",
    "updateTime": "2024-03-18T10:00:00"
  }
}
```

**说明：**
- 需要登录认证
- 从 Token 中解析用户 ID

---

### 5. 更新用户信息

**接口：** `PUT /user/me`

**说明：** 更新当前用户的信息（昵称、头像等）

**请求头：**
```
Authorization: Bearer <token>
```

**请求参数：**
```json
{
  "name": "新昵称",
  "avatar": "https://example.com/avatar.jpg",
  "gender": 1
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "phone": "13800138000",
    "name": "新昵称",
    "gender": 1,
    "avatar": "https://example.com/avatar.jpg",
    "status": 1
  }
}
```

**说明：**
- 不能修改手机号（需要单独的流程）
- 不能修改用户 ID 和状态

---

## 认证流程

### 完整登录流程

```
1. 用户输入手机号
   ↓
2. 调用 POST /user/codes 发送验证码
   ↓
3. 用户输入验证码
   ↓
4. 调用 POST /user/tokens 登录
   ↓
5. 服务端返回 Token 和用户信息
   ↓
6. 客户端保存 Token（LocalStorage/SharedPreferences）
   ↓
7. 后续请求携带 Token：Authorization: Bearer <token>
```

### 请求示例（curl）

```bash
# 1. 发送验证码
curl -X POST http://localhost:8080/user/codes \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000"}'

# 2. 登录
curl -X POST http://localhost:8080/user/tokens \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","code":"6666"}'

# 3. 获取用户信息（需要 Token）
curl -X GET http://localhost:8080/user/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# 4. 更新用户信息
curl -X PUT http://localhost:8080/user/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{"name":"新昵称","gender":1}'

# 5. 退出登录
curl -X DELETE http://localhost:8080/user/tokens \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 错误码说明

| 错误码 | 说明 | 解决方案 |
|--------|------|---------|
| 200 | 成功 | - |
| 400 | 参数错误 | 检查请求参数格式 |
| 401 | 未登录/Token 无效 | 重新登录 |
| 500 | 服务器错误 | 联系管理员 |

**常见错误：**

```json
// 验证码错误
{
  "code": 500,
  "message": "验证码错误",
  "data": null
}

// 验证码过期
{
  "code": 500,
  "message": "验证码已过期",
  "data": null
}

// 未登录
{
  "code": 401,
  "message": "未登录，请先登录",
  "data": null
}

// Token 无效
{
  "code": 401,
  "message": "Token 无效或已过期",
  "data": null
}
```

---

## 技术实现

### 1. JWT Token 结构

```
Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload:
{
  "userId": 1,
  "sub": "takeout-user",
  "iat": 1710742400,
  "exp": 1711347200
}

Signature:
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

### 2. 拦截器流程

```
请求 → LoginInterceptor.preHandle()
         ↓
     获取 Authorization Header
         ↓
     验证 Token
         ↓
     解析用户 ID → BaseContext.setCurrentId()
         ↓
     执行 Controller
         ↓
     BaseContext.removeCurrentId()
```

### 3. ThreadLocal 使用

```java
// 拦截器中设置
BaseContext.setCurrentId(userId);

// Service 中获取
Long userId = BaseContext.getCurrentId();

// 请求结束后清理
BaseContext.removeCurrentId();
```

---

## 测试步骤

### 步骤 1：启动应用

在 IDEA 中运行 `TakeoutApplication`

### 步骤 2：发送验证码

访问 Swagger：`http://localhost:8080/doc.html`

找到 **用户相关接口 → 发送登录验证码**

点击 **Try it out**，输入：
```json
{
  "phone": "13800138000"
}
```

点击 **Execute**，记录返回的验证码（测试环境是 `6666`）

### 步骤 3：登录

找到 **用户相关接口 → 用户登录**

点击 **Try it out**，输入：
```json
{
  "phone": "13800138000",
  "code": "6666"
}
```

点击 **Execute**，记录返回的 `token`

### 步骤 4：获取用户信息

找到 **用户相关接口 → 获取当前用户信息**

点击 **Try it out**，在 **Authorization** Header 中输入：
```
Bearer <你的 token>
```

点击 **Execute**，应该能看到用户信息

### 步骤 5：更新用户信息

找到 **用户相关接口 → 更新用户信息**

点击 **Try it out**，在 **Authorization** Header 中输入：
```
Bearer <你的 token>
```

请求体输入：
```json
{
  "name": "新昵称",
  "gender": 1
}
```

点击 **Execute**

---

## 下一步

用户模块已完成！接下来可以：

A) 测试用户模块所有接口
B) 继续学习购物车模块
C) 继续学习订单模块

---

**记住：**
- Token 是用户的"身份证"，每次请求都要携带
- ThreadLocal 用于在同一请求中共享用户 ID
- 拦截器是认证的第一道防线
