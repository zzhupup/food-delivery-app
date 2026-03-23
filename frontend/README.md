# 外卖平台前端 - Takeout Frontend

基于 Vue3 + Vite + Element Plus 的外卖平台前端

## 🚀 技术栈

- **框架**: Vue 3.4+
- **构建工具**: Vite 5+
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **HTTP 客户端**: Axios
- **路由**: Vue Router 4

## 📦 项目结构

```
takeout-frontend/
├── src/
│   ├── api/              # API 接口
│   │   └── cart.js       # 购物车接口
│   ├── components/       # 公共组件
│   ├── views/            # 页面
│   │   └── cart/         # 购物车页面
│   ├── stores/           # Pinia 状态管理
│   │   └── cart.js       # 购物车状态
│   ├── utils/            # 工具函数
│   │   └── request.js    # Axios 封装
│   ├── App.vue           # 根组件
│   └── main.js           # 入口文件
├── package.json
└── vite.config.js
```

## 🛠️ 快速开始

### 1. 安装依赖
```bash
cd takeout-frontend
npm install
```

### 2. 启动开发服务器
```bash
npm run dev
```

### 3. 访问
```
http://localhost:5173
```

## 📝 功能清单

- ✅ 购物车列表展示
- ✅ 添加商品到购物车
- ✅ 修改商品数量
- ✅ 删除购物车项
- ✅ 批量删除
- ✅ 清空购物车
- ✅ 计算总金额

## 🔌 API 配置

后端地址：`http://localhost:8080`

在 `src/utils/request.js` 中可配置：
```javascript
const baseURL = 'http://localhost:8080'
```
