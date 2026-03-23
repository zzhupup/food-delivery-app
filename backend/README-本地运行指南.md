# 🚀 外卖平台 - 本地运行指南

> 按照以下步骤，10 分钟内在本地跑起来！

---

## 📋 第一步：安装必要软件

### 1.1 下载 IDEA Community（免费）

**官网**：https://www.jetbrains.com/idea/download/

| 选项 | 选择 |
|------|------|
| Operating System | 你的系统（Windows/macOS/Linux） |
| Edition | **Community**（免费社区版）✅ |

下载后双击安装，一路 Next 即可。

---

### 1.2 安装 JDK 11

**Windows 用户**：
1. 访问：https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html
2. 下载 `Windows x64 Installer`
3. 双击安装，一路 Next
4. 验证：打开命令提示符，输入 `java -version`

**macOS 用户**：
```bash
brew install openjdk@11
```

**Linux 用户**：
```bash
sudo apt update && sudo apt install -y openjdk-11-jdk
```

---

### 1.3 安装 MySQL 8.0

**Windows 用户**：
1. 访问：https://dev.mysql.com/downloads/installer/
2. 下载 `mysql-installer-community-8.0.x.msi`
3. 安装时记住设置的 **root 密码**（后面要用）

**macOS 用户**：
```bash
brew install mysql@8.0
brew services start mysql
```

**Linux 用户**：
```bash
sudo apt install -y mysql-server
sudo mysql_secure_installation
```

---

### 1.4 安装 Redis

**Windows 用户**：
- 访问：https://github.com/microsoftarchive/redis/releases
- 下载 `Redis-x64-3.0.504.msi` 安装

**macOS 用户**：
```bash
brew install redis
brew services start redis
```

**Linux 用户**：
```bash
sudo apt install -y redis-server
sudo systemctl start redis-server
```

---

## 📦 第二步：导入项目到 IDEA

### 2.1 解压项目

把 `takeout-platform.zip` 解压到你喜欢的目录，比如：
- Windows: `C:\Projects\takeout-platform`
- macOS/Linux: `~/Projects/takeout-platform`

---

### 2.2 用 IDEA 打开项目

1. 打开 IDEA
2. 点击 **Open**（或 File → Open）
3. 选择解压后的 `takeout-platform` 文件夹
4. 等待 Maven 自动下载依赖（第一次可能需要几分钟）

---

### 2.3 配置项目 JDK

1. IDEA 顶部菜单：**File → Project Structure**
2. 左侧选择 **Project**
3. **Project SDK**: 选择 JDK 11（如果没有，点 New → JDK → 选择 JDK 11 安装目录）
4. **Project language level**: 选择 11
5. 点击 OK

---

### 2.4 配置 Maven（可选）

IDEA 自带 Maven，通常不需要额外配置。

如果你想用本地 Maven：
1. **File → Settings**（macOS: IDEA → Preferences）
2. **Build, Execution, Deployment → Build Tools → Maven**
3. **Maven home directory**: 选择你的 Maven 安装目录

---

## 🗄️ 第三步：初始化数据库

### 3.1 连接 MySQL

**用命令行**：
```bash
mysql -u root -p
# 输入你安装 MySQL 时设置的密码
```

**或用图形工具**（推荐）：
- **MySQL Workbench**（官方免费）：https://www.mysql.com/products/workbench/
- **Navicat**（付费，有试用）
- **DBeaver**（免费开源）：https://dbeaver.io/

---

### 3.2 执行初始化脚本

在 IDEA 中：
1. 找到文件：`takeout-controller/src/main/resources/sql/init.sql`
2. 右键 → **Run 'init.sql'**（需要配置数据库连接）

**或手动执行**：
```bash
# 方法 1：命令行
mysql -u root -p < /path/to/takeout-platform/takeout-controller/src/main/resources/sql/init.sql

# 方法 2：MySQL Workbench
# 打开 init.sql 文件，点击 Execute 按钮
```

---

### 3.3 修改数据库密码（如果需要）

打开 `takeout-controller/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    username: root
    password: root123  # ← 改成你的 MySQL 密码
```

---

### 3.4 检查 Redis

**命令行检查**：
```bash
redis-cli ping
# 应该返回：PONG
```

如果 Redis 没启动：
```bash
# macOS
brew services start redis

# Linux
sudo systemctl start redis-server

# Windows
# 在服务管理器中启动 Redis 服务
```

---

## ▶️ 第四步：启动应用

### 4.1 找到启动类

在 IDEA 中：
1. 找到文件：`takeout-controller/src/main/java/com/takeout/TakeoutApplication.java`
2. 右键 → **Run 'TakeoutApplication'**

---

### 4.2 查看启动日志

成功启动后，你会看到：
```
========================================
    外卖平台启动成功！🎉
    API 文档：http://localhost:8080/doc.html
========================================
```

---

### 4.3 测试 API

打开浏览器访问：

**API 文档界面**：http://localhost:8080/doc.html

**或直接测试接口**：
- 商家列表：http://localhost:8080/shop/list
- 菜品列表：http://localhost:8080/dish/list?shopId=1

---

## 🛠️ 第五步：常见问题

### 问题 1：端口 8080 被占用

**错误信息**：`Port 8080 was already in use`

**解决方案**：
修改 `application.yml`：
```yaml
server:
  port: 8081  # 改成其他端口
```

---

### 问题 2：数据库连接失败

**错误信息**：`Communications link failure`

**检查清单**：
- [ ] MySQL 服务是否启动？
- [ ] 用户名密码是否正确？
- [ ] 数据库 `takeout_db` 是否创建？

---

### 问题 3：Redis 连接失败

**错误信息**：`Unable to connect to Redis`

**解决方案**：
```bash
# 检查 Redis 是否运行
redis-cli ping

# 如果没反应，启动 Redis
# macOS: brew services start redis
# Linux: sudo systemctl start redis-server
```

---

### 问题 4：Maven 依赖下载慢

**解决方案**：配置阿里云镜像

编辑 `~/.m2/settings.xml`（没有就创建一个）：
```xml
<settings>
  <mirrors>
    <mirror>
      <id>aliyun-maven</id>
      <mirrorOf>central</mirrorOf>
      <name>阿里云公共仓库</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
```

---

## 📚 项目结构说明

```
takeout-platform/
├── takeout-common/        # 公共模块（工具类、异常、统一响应）
├── takeout-pojo/          # 实体类模块
├── takeout-mapper/        # 数据访问层（DAO）
├── takeout-service/       # 业务逻辑层
├── takeout-controller/    # 控制层 + 启动类
│   ├── src/main/java/com/takeout/
│   │   ├── TakeoutApplication.java  ← 启动类
│   │   ├── config/                   ← 配置类
│   │   ├── controller/               ← API 接口
│   │   ├── service/                  ← 业务逻辑
│   │   ├── mapper/                   ← 数据访问
│   │   └── entity/                   ← 实体类
│   └── src/main/resources/
│       ├── application.yml           ← 配置文件
│       └── sql/init.sql              ← 数据库初始化脚本
└── pom.xml                          ← Maven 父项目配置
```

---

## 🎯 下一步学习

项目跑起来后，你可以：

1. **阅读代码**：从 `TakeoutApplication.java` 开始，顺着 Controller → Service → Mapper 看
2. **修改代码**：试试改改接口返回值，重启看看效果
3. **添加功能**：比如加一个购物车接口
4. **学习架构**：理解 Spring Boot 的分层设计

---

## 📞 需要帮助？

遇到问题随时问我！把错误信息完整发给我就行。

祝你学习愉快！🎉
