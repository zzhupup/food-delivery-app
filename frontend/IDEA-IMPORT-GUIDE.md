# IntelliJ IDEA / WebStorm 导入指南

## 📌 方法一：通过 IDEA 菜单导入（推荐）

### 步骤 1：打开 IDEA
- 如果已打开项目：`File` → `Open...`
- 如果是启动界面：点击 `Open`

### 步骤 2：选择前端项目目录
```
/home/admin/openclaw/workspace/takeout-frontend
```

### 步骤 3：选择项目类型
IDEA 会识别为 **Vue.js 项目** 或 **JavaScript 项目**

### 步骤 4：等待依赖安装
IDEA 会自动检测 `package.json` 并提示安装依赖

---

## 📌 方法二：通过命令行打开

### 如果已安装 IDEA 命令行工具：
```bash
# 在 takeout-frontend 目录下
cd /home/admin/openclaw/workspace/takeout-frontend
idea .
```

### 如果已安装 WebStorm：
```bash
webstorm .
```

---

## ⚙️ IDEA 配置建议

### 1. 安装 Vue 插件（如果没有）
- `File` → `Settings` → `Plugins`
- 搜索 `Vue.js`
- 点击 `Install`

### 2. 配置 Node.js 解释器
- `File` → `Settings` → `Languages & Frameworks` → `Node.js`
- 选择 Node.js 路径（通常是 `/usr/bin/node` 或 `~/.nvm/versions/node/v24.14.0/bin/node`）

### 3. 配置 npm
- `File` → `Settings` → `Languages & Frameworks` → `Node.js` → `npm`
- 确保 npm 路径正确

### 4. 启用 ESLint（可选）
- `File` → `Settings` → `Languages & Frameworks` → `JavaScript` → `Code Quality Tools` → `ESLint`
- 勾选 `Enable`

---

## 🚀 在 IDEA 中运行项目

### 方式 1：使用 npm 脚本
1. 右侧打开 `npm` 面板
2. 双击 `dev` 脚本运行

### 方式 2：使用终端
1. `View` → `Tool Windows` → `Terminal`
2. 输入：
```bash
npm install
npm run dev
```

### 方式 3：创建运行配置
1. `Run` → `Edit Configurations...`
2. 点击 `+` → `npm`
3. 配置：
   - **Name**: `Vue Dev Server`
   - **Command**: `run dev`
   - **Package.json**: `$ProjectFileDir$/package.json`
4. 点击 `Run` 按钮

---

## 📁 项目结构（IDEA 视图）

```
takeout-frontend/
├── 📁 .idea/              ← IDEA 配置目录
├── 📁 node_modules/       ← 依赖包（安装后出现）
├── 📁 public/             ← 静态资源
├── 📁 src/
│   ├── 📁 api/            ← API 接口
│   ├── 📁 assets/         ← 资源文件
│   ├── 📁 components/     ← 组件
│   ├── 📁 stores/         ← Pinia 状态管理
│   ├── 📁 utils/          ← 工具函数
│   ├── 📁 views/          ← 页面组件
│   ├── App.vue            ← 根组件
│   ├── main.js            ← 入口文件
│   └── router.js          ← 路由配置
├── 📄 index.html          ← HTML 模板
├── 📄 package.json        ← 项目配置
├── 📄 vite.config.js      ← Vite 配置
└── 📄 README.md           ← 说明文档
```

---

## 🔧 常见问题

### 问题 1：IDEA 不识别 Vue 文件
**解决**：
- `File` → `Settings` → `Languages & Frameworks` → `JavaScript`
- 确保 `JavaScript language version` 选择 `ECMAScript 6+`

### 问题 2：代码没有语法高亮
**解决**：
- 右键 `package.json` → `Synchronize 'package.json'`
- 或 `File` → `Invalidate Caches / Restart`

### 问题 3：npm 脚本不显示
**解决**：
- 右侧找到 `npm` 面板（如果没有：`View` → `Tool Windows` → `npm`）
- 点击刷新按钮

### 问题 4：无法运行 npm 命令
**解决**：
```bash
# 检查 npm 是否安装
npm -v

# 如果没有安装，安装 Node.js
# Ubuntu/Debian:
sudo apt install nodejs npm

# 或使用 nvm（推荐）
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
nvm install 24.14.0
```

---

## 💡 IDEA 快捷键（Vue 开发）

| 快捷键 | 功能 |
|--------|------|
| `Ctrl + Space` | 代码补全 |
| `Ctrl + Click` | 跳转到定义 |
| `Ctrl + Alt + L` | 格式化代码 |
| `Ctrl + /` | 注释/取消注释 |
| `Shift + Shift` | 搜索任意内容 |
| `Ctrl + F` | 查找 |
| `Ctrl + R` | 替换 |
| `Alt + Insert` | 生成代码 |

---

## 🎯 下一步

1. **打开 IDEA**
2. **导入项目**：选择 `/home/admin/openclaw/workspace/takeout-frontend`
3. **安装依赖**：IDEA 会自动提示，或在终端运行 `npm install`
4. **运行项目**：`npm run dev`
5. **访问**：http://localhost:5173

---

**祝开发愉快！** 🚀
