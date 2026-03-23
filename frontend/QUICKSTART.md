# 🚀 快速启动指南

## 第一步：安装依赖

```bash
cd /home/admin/openclaw/workspace/takeout-frontend
npm install
```

**如果安装慢，可以用淘宝镜像：**
```bash
npm install --registry=https://registry.npmmirror.com
```

---

## 第二步：启动后端

确保后端已启动（端口 8080）：

```bash
cd /home/admin/openclaw/workspace/takeout-platform
mvn spring-boot:run
```

或者在 IDEA 中运行 `TakeoutApplication.java`

---

## 第三步：启动前端

```bash
cd /home/admin/openclaw/workspace/takeout-frontend
npm run dev
```

启动后访问：**http://localhost:5173**

---

## 第四步：测试购物车功能

### 1. 访问首页
打开浏览器：`http://localhost:5173`

### 2. 进入购物车
点击「进入购物车」按钮

### 3. 测试功能

| 功能 | 操作 | 预期结果 |
|------|------|----------|
| 添加商品 | 调用 API 或从菜品页添加 | 购物车显示新商品 |
| 修改数量 | 点击 +/- 按钮 | 数量更新，小计自动计算 |
| 删除商品 | 点击「删除」按钮 | 商品从列表移除 |
| 批量删除 | 勾选多个商品 → 点击「删除选中」 | 批量删除成功 |
| 清空购物车 | 点击「清空购物车」 | 购物车变空 |
| 全选 | 点击「全选」复选框 | 所有商品被选中 |

---

## 🔧 常见问题

### 问题 1：跨域错误
**现象**：前端请求后端报 CORS 错误

**解决**：在后端 `WebConfig.java` 中添加跨域配置：

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
    }
}
```

### 问题 2：401 未授权
**现象**：所有接口返回 401

**解决**：检查后端拦截器配置，或者暂时关闭登录验证

### 问题 3：npm install 失败
**现象**：安装依赖时报错

**解决**：
```bash
# 清理缓存
npm cache clean --force

# 删除 node_modules 和 package-lock.json
rm -rf node_modules package-lock.json

# 重新安装
npm install
```

---

## 📁 项目结构

```
takeout-frontend/
├── src/
│   ├── api/              # API 接口
│   │   └── cart.js       ← 购物车 API
│   ├── stores/           # Pinia 状态管理
│   │   └── cart.js       ← 购物车状态
│   ├── views/            # 页面组件
│   │   ├── HomeView.vue  ← 首页
│   │   └── cart/
│   │       └── CartView.vue  ← 购物车页面
│   ├── utils/
│   │   └── request.js    # Axios 封装
│   ├── router.js         # 路由配置
│   ├── App.vue           # 根组件
│   └── main.js           # 入口文件
├── index.html
├── package.json
├── vite.config.js
└── README.md
```

---

## 🎯 下一步开发

1. **菜品列表页** - 展示菜品，支持添加到购物车
2. **订单确认页** - 选择地址、支付方式
3. **订单列表页** - 查看历史订单
4. **登录/注册** - 用户认证

---

## 📞 需要帮助？

查看完整文档：`/home/admin/openclaw/workspace/takeout-frontend/README.md`
