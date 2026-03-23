<template>
  <div class="cart-page">
    <el-card class="cart-card">
      <template #header>
        <div class="cart-header">
          <h2>🛒 购物车</h2>
          <div class="header-actions">
            <el-button 
              v-if="!cartStore.isEmpty" 
              @click="handleClear" 
              type="danger" 
              plain
              size="small">
              <el-icon><Delete /></el-icon>
              清空购物车
            </el-button>
          </div>
        </div>
      </template>

      <!-- 购物车为空 -->
      <el-empty 
        v-if="cartStore.isEmpty" 
        description="购物车空空如也，快去选购吧~"
        :image-size="200">
        <el-button type="primary" @click="goShopping">去购物</el-button>
      </el-empty>

      <!-- 购物车列表 -->
      <div v-else>
        <!-- 批量操作栏 -->
        <div class="batch-actions" v-if="selectedItems.length > 0">
          <el-checkbox 
            :model-value="isAllSelected" 
            @change="toggleAllSelection">
            全选 ({{ selectedItems.length }})
          </el-checkbox>
          <el-button 
            type="danger" 
            size="small" 
            @click="handleBatchDelete"
            :loading="batchLoading">
            <el-icon><Delete /></el-icon>
            删除选中
          </el-button>
        </div>

        <!-- 购物车项列表 -->
        <div class="cart-items">
          <div 
            v-for="item in cartStore.items" 
            :key="item.id" 
            class="cart-item"
            :class="{ selected: item.selected }">
            
            <!-- 选择框 -->
            <el-checkbox 
              :model-value="item.selected || false" 
              @change="toggleSelection(item.id)"
              size="large" />
            
            <!-- 商品图片 -->
            <div class="item-image">
              <el-image 
                :src="item.dishImage || generateDishImage(item.dishName)" 
                fit="cover"
                loading="lazy"
                class="dish-image">
                <template #placeholder>
                  <div class="image-placeholder">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
            
            <!-- 商品信息 -->
            <div class="item-info">
              <div class="dish-name">{{ item.dishName }}</div>
              <div class="dish-price">
                <span class="price">¥{{ item.price.toFixed(2) }}</span>
              </div>
            </div>
            
            <!-- 数量控制 -->
            <div class="item-quantity">
              <el-input-number 
                v-model="item.count" 
                :min="1" 
                :max="99" 
                size="small"
                @change="handleQuantityChange(item.id, $event)"
                controls-position="right" />
            </div>
            
            <!-- 小计 -->
            <div class="item-subtotal">
              <span class="label">小计：</span>
              <span class="amount">¥{{ (item.price * item.count).toFixed(2) }}</span>
            </div>
            
            <!-- 删除按钮 -->
            <div class="item-actions">
              <el-button 
                type="danger" 
                link 
                @click="handleDelete(item.id)"
                :loading="deletingId === item.id">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </div>

        <!-- 结算栏 -->
        <div class="cart-footer">
          <div class="footer-left">
            <el-checkbox 
              :model-value="isAllSelected" 
              @change="toggleAllSelection">
              全选
            </el-checkbox>
            <span class="selected-count">已选 {{ selectedItems.length }} 件商品</span>
          </div>
          <div class="footer-right">
            <span class="total-label">合计：</span>
            <span class="total-amount">¥{{ selectedTotal.toFixed(2) }}</span>
            <el-button 
              type="primary" 
              size="large" 
              @click="handleCheckout"
              :disabled="selectedItems.length === 0">
              去结算
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Picture } from '@element-plus/icons-vue'
import { useCartStore } from '../../stores/cart'
import { useRouter } from 'vue-router'

const cartStore = useCartStore()
const router = useRouter()

// 生成菜品图片（复用 DishList 的逻辑）
function generateDishImage(dishName) {
  const imageMap = {
    '巨无霸': 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=200&h=150&fit=crop&q=80',
    '麦香鸡': 'https://images.unsplash.com/photo-2097090/pexels-photo-2097090.jpeg?w=200&h=150&fit=crop&q=80',
    '汉堡': 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=200&h=150&fit=crop&q=80',
    '可乐': 'https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=200&h=150&fit=crop&q=80',
    '鸡翅': 'https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=200&h=150&fit=crop&q=80',
    '鸡块': '/chicken-nuggets.svg'
  }
  
  for (const [cn, url] of Object.entries(imageMap)) {
    if (dishName && dishName.includes(cn)) {
      return url
    }
  }
  
  return 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=200&h=150&fit=crop&q=80'
}

const batchLoading = ref(false)
const deletingId = ref(null)

// 加载购物车数据
onMounted(async () => {
  await cartStore.loadCart()
})

// 是否全选
const isAllSelected = computed(() => {
  if (cartStore.items.length === 0) return false
  return cartStore.items.every(item => item.selected)
})

// 选中的商品
const selectedItems = computed(() => {
  return cartStore.items.filter(item => item.selected)
})

// 选中商品总金额
const selectedTotal = computed(() => {
  return selectedItems.value.reduce((sum, item) => {
    return sum + (item.price * item.count)
  }, 0)
})

// 去购物
function goShopping() {
  router.push('/')
}

// 切换选中状态
function toggleSelection(cartItemId) {
  cartStore.toggleSelection(cartItemId)
}

// 全选/取消全选
function toggleAllSelection(checked) {
  cartStore.items.forEach(item => {
    item.selected = checked
  })
}

// 数量变化
async function handleQuantityChange(cartItemId, newCount) {
  try {
    await cartStore.updateItemQuantity(cartItemId, newCount)
  } catch (error) {
    console.error('更新数量失败:', error)
  }
}

// 删除单个商品
async function handleDelete(cartItemId) {
  try {
    deletingId.value = cartItemId
    await cartStore.deleteCartItem(cartItemId)
    ElMessage.success('删除成功')
  } catch (error) {
    console.error('删除失败:', error)
  } finally {
    deletingId.value = null
  }
}

// 批量删除
async function handleBatchDelete() {
  try {
    batchLoading.value = true
    const ids = selectedItems.value.map(item => item.id)
    await cartStore.batchDelete(ids)
    ElMessage.success(`已删除 ${ids.length} 件商品`)
  } catch (error) {
    console.error('批量删除失败:', error)
  } finally {
    batchLoading.value = false
  }
}

// 清空购物车
async function handleClear() {
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await cartStore.clear()
    ElMessage.success('购物车已清空')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空失败:', error)
    }
  }
}

// 去结算
function handleCheckout() {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  
  ElMessage.info('结算功能开发中...')
  // TODO: 跳转到订单确认页面
}
</script>

<style scoped>
.cart-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.cart-card {
  border-radius: 8px;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cart-header h2 {
  margin: 0;
  font-size: 20px;
}

.batch-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  margin-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.cart-items {
  margin: 20px 0;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 15px 10px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.3s;
}

.cart-item:hover {
  background-color: #f9f9f9;
}

.cart-item.selected {
  background-color: #ecf5ff;
}

.item-image {
  margin: 0 15px;
}

.dish-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
}

.image-placeholder,
.image-error {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 80px;
  height: 80px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  color: #909399;
  font-size: 30px;
  border-radius: 8px;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

.item-info {
  flex: 1;
  min-width: 200px;
}

.dish-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #303133;
}

.dish-price {
  color: #f56c6c;
  font-size: 14px;
}

.price {
  font-size: 18px;
  font-weight: bold;
}

.item-quantity {
  margin: 0 20px;
}

.item-subtotal {
  min-width: 120px;
  text-align: right;
  margin-right: 20px;
}

.item-subtotal .label {
  color: #909399;
  font-size: 14px;
}

.item-subtotal .amount {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.item-actions {
  min-width: 80px;
  text-align: center;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-top: 2px solid #f0f0f0;
  margin-top: 20px;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.selected-count {
  color: #909399;
  font-size: 14px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.total-label {
  color: #606266;
  font-size: 16px;
}

.total-amount {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
}
</style>
