# 购物车模块重构 - 完成总结

## 📊 重构前后对比

### 旧设计（不规范）
```
Cart 表（混杂了购物车和菜品信息）
├── id (cartId)
├── userId
├── dishId ❌ 冗余
├── dishName ❌ 冗余
├── dishImage ❌ 冗余
├── price ❌ 冗余
└── count
```

### 新设计（符合第三范式）
```
Cart 表（只存购物车本身）
├── id
├── userId
├── create_time
└── update_time

CartItem 表（关联购物车和菜品）
├── id (cartItemId)
├── cartId → Cart.id
├── dishId → Dish.id
├── count
├── create_time
└── update_time

Dish 表（菜品信息）
├── id
├── name
├── image
└── price
```

---

## 📁 文件清单

### 新增文件（6 个）
| 文件 | 说明 |
|------|------|
| `CartItem.java` | 购物车项实体类 |
| `CartItemMapper.java` | 购物车项 Mapper |
| `CartItemService.java` | 购物车项 Service 接口 |
| `CartItemServiceImpl.java` | 购物车项 Service 实现 |
| `CartItemVO.java` | 购物车项 VO（含菜品信息） |
| `cart-migration.sql` | 数据库迁移脚本 |

### 修改文件（4 个）
| 文件 | 修改内容 |
|------|----------|
| `Cart.java` | 移除 dishId、dishName、dishImage、price、shopId、count 字段 |
| `CartService.java` | 接口方法签名调整 |
| `CartServiceImpl.java` | 重构为委托给 CartItemService |
| `CartController.java` | 接口路径和参数调整 |

---

## 🔌 API 接口变化

### 接口路径调整

| 旧接口 | 新接口 | 说明 |
|--------|--------|------|
| `POST /cart` | `POST /cart/add?dishId=1&count=2` | 添加商品 |
| `GET /cart/list` | `GET /cart/list` | 查询购物车项（返回 VO） |
| `PUT /cart` | `PUT /cart` | 参数从 `cartId` 改为 `cartItemId` |
| `DELETE /cart/{id}` | `DELETE /cart/{id}` | 删除的是 cartItemId |
| `DELETE /cart/batch` | `DELETE /cart/batch` | 参数是 cartItemIds 列表 |

### 返回值变化

**旧版 `/cart/list`**：
```json
{
  "code": 1,
  "data": [
    { "id": 101, "dishId": 1, "dishName": "宫保鸡丁", "count": 2, "price": 38 }
  ]
}
```

**新版 `/cart/list`**：
```json
{
  "code": 1,
  "data": [
    { 
      "id": 101, 
      "cartId": 1,
      "dishId": 1, 
      "dishName": "宫保鸡丁", 
      "dishImage": "a.jpg",
      "price": 38,
      "count": 2,
      "amount": 76  // 新增：小计金额
    }
  ]
}
```

---

## 🎯 核心优化点

### 1. 数据不冗余
- ✅ 菜品信息只存一份在 Dish 表
- ✅ 菜品改名/改价，购物车自动同步

### 2. 查询优化
```java
// 批量查询菜品信息，避免 N+1 问题
List<Dish> dishes = dishService.listByIds(dishIds);
Map<Long, Dish> dishMap = dishes.stream()
    .collect(Collectors.toMap(Dish::getId, d -> d));
```

### 3. Stream API 应用
```java
// 计算总金额
BigDecimal totalAmount = cartItems.stream()
    .map(CartItemVO::getAmount)
    .reduce(BigDecimal.ZERO, BigDecimal::add);

// 验证权限
itemList.stream()
    .map(CartItem::getCartId)
    .distinct()
    .forEach(cartId -> { /* 验证 */ });
```

---

## 📝 下一步操作

### 1. 执行数据库迁移
```bash
mysql -u root -p takeout < docs/cart-migration.sql
```

### 2. 编译项目
```bash
cd takeout-platform
mvn clean compile
```

### 3. 测试接口
```bash
# 添加商品
curl -X POST "http://localhost:8080/cart/add?dishId=1&count=2"

# 查询购物车
curl http://localhost:8080/cart/list

# 计算总金额
curl http://localhost:8080/cart/total
```

---

## 🎓 学到的知识点

1. ✅ 数据库第三范式（减少冗余）
2. ✅ 关联表设计（Cart-CartItem-Dish）
3. ✅ VO 模式（返回对象封装）
4. ✅ Stream API 批量操作
5. ✅ 避免 N+1 查询问题
