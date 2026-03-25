# DBeaver 连接 MySQL 数据库指南

**安装版本**: DBeaver CE 26.0.1  
**安装方式**: snap

---

## 🚀 启动 DBeaver

### 方法 1: 命令行启动
```bash
/snap/bin/dbeaver-ce
```

### 方法 2: 应用程序菜单
在 Ubuntu 应用程序菜单中搜索 "DBeaver"

---

## 📝 新建数据库连接

### 步骤 1: 新建连接
1. 打开 DBeaver
2. 点击左上角 **数据库** 图标（或按 `Ctrl+N`）
3. 选择 **MySQL** → 点击 **下一步**

### 步骤 2: 填写连接信息

| 字段 | 值 |
|------|-----|
| **Host** | `localhost` |
| **Port** | `3306` |
| **Database** | `takeout_db` |
| **Username** | `root` |
| **Password** | `root123` |

### 步骤 3: 测试连接
1. 点击左下角 **测试连接** 按钮
2. 如果显示 "Connected"，说明配置正确
3. 点击 **完成**

---

## 🔍 常用操作

### 查看表数据
1. 在左侧导航栏展开 `takeout_db`
2. 展开 **Tables**
3. 双击任意表（如 `orders`）
4. 右键 → **View Data** → **View All Data**

### 执行 SQL 查询
1. 选中 `takeout_db` 数据库
2. 按 `F3` 或点击 **SQL Editor** 图标
3. 输入 SQL 语句
4. 按 `Ctrl+Enter` 执行

### 常用查询语句

```sql
-- 查看所有订单
SELECT * FROM orders ORDER BY create_time DESC;

-- 查看订单详情
SELECT o.*, od.dish_id, od.dish_name, od.count, od.price 
FROM orders o
LEFT JOIN order_detail od ON o.id = od.order_id
WHERE o.id = 6;

-- 查看菜品
SELECT id, name, price, status, shop_id FROM dish;

-- 查看用户
SELECT * FROM user;

-- 统计每个用户的订单数
SELECT user_id, COUNT(*) as order_count, SUM(pay_amount) as total_amount
FROM orders 
GROUP BY user_id;

-- 查看购物车
SELECT ci.*, d.name as dish_name 
FROM cart_item ci
LEFT JOIN dish d ON ci.dish_id = d.id;
```

---

## 📊 数据库表结构

### 核心表

| 表名 | 说明 |
|------|------|
| `user` | 用户表 |
| `dish` | 菜品表 |
| `shop` | 店铺表 |
| `cart` | 购物车表 |
| `cart_item` | 购物车项表 |
| `orders` | 订单表 |
| `order_detail` | 订单明细表 |
| `category` | 分类表 |
| `user_address` | 用户地址表 |

---

## 🔧 常见问题

### 问题 1: 连接失败
**错误**: "Communications link failure"  
**解决**: 
1. 检查 MySQL 是否运行：`sudo systemctl status mysql`
2. 检查端口：`netstat -tlnp | grep 3306`

### 问题 2: 认证失败
**错误**: "Access denied for user 'root'@'localhost'"  
**解决**: 检查密码是否正确（默认：`root123`）

### 问题 3: DBeaver 启动慢
**解决**: 
```bash
# 增加 JVM 内存
echo "-Xmx2048m" >> ~/.dbeaver-ce/dbeaver.ini
```

---

## 💡 快捷键

| 快捷键 | 功能 |
|--------|------|
| `F3` | 打开 SQL 编辑器 |
| `Ctrl+Enter` | 执行 SQL |
| `Ctrl+Shift+R` | 刷新连接 |
| `Ctrl+D` | 删除选中行 |
| `Ctrl+/` | 注释/取消注释 |

---

## 📸 截图示例

### 新建连接
![新建连接](https://dbeaver.io/wp-content/uploads/2016/03/create-new-connection.png)

### 查看数据
![查看数据](https://dbeaver.io/wp-content/uploads/2016/03/data-viewer.png)

---

**连接信息总结**:
- **主机**: localhost
- **端口**: 3306
- **数据库**: takeout_db
- **用户**: root
- **密码**: root123

祝你使用愉快！🎉
