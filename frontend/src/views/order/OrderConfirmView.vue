<template>
  <div class="order-confirm-page">
    <el-card class="order-card">
      <template #header>
        <div class="card-header">
          <h2>📝 确认订单</h2>
          <el-button @click="goBack" plain size="small">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
        </div>
      </template>

      <el-form :model="form" label-width="100px" class="order-form">
        <!-- 收货信息 -->
        <el-card class="section-card" shadow="hover">
          <template #header>
            <div class="section-header">
              <el-icon><Location /></el-icon>
              <span>收货信息</span>
            </div>
          </template>

          <el-form-item label="收货人" required>
            <el-input 
              v-model="form.consignee" 
              placeholder="请输入收货人姓名" 
              maxlength="20"
              clearable />
          </el-form-item>

          <el-form-item label="联系电话" required>
            <el-input 
              v-model="form.phone" 
              placeholder="请输入联系电话" 
              maxlength="11"
              clearable />
          </el-form-item>

          <el-form-item label="收货地址" required>
            <el-input 
              v-model="form.address" 
              type="textarea" 
              :rows="3"
              placeholder="请输入详细收货地址（省/市/区/街道）" 
              maxlength="200"
              show-word-limit />
          </el-form-item>
        </el-card>

        <!-- 商品清单 -->
        <el-card class="section-card" shadow="hover" style="margin-top: 20px;">
          <template #header>
            <div class="section-header">
              <el-icon><ShoppingCart /></el-icon>
              <span>商品清单</span>
              <span class="item-count">共 {{ selectedItems.length }} 件商品</span>
            </div>
          </template>

          <el-empty v-if="selectedItems.length === 0" description="没有选购的商品" />

          <div v-else class="order-items">
            <div v-for="item in selectedItems" :key="item.id" class="order-item">
              <!-- 商品图片 -->
              <div class="item-image">
                <el-image 
                  :src="item.dishImage || generateDishImage(item.dishName)" 
                  fit="cover"
                  class="dish-image" />
              </div>

              <!-- 商品信息 -->
              <div class="item-info">
                <div class="dish-name">{{ item.dishName }}</div>
                <div class="dish-price">¥{{ item.price.toFixed(2) }}</div>
              </div>

              <!-- 数量 -->
              <div class="item-quantity">
                <span class="label">数量：</span>
                <span class="count">×{{ item.count }}</span>
              </div>

              <!-- 小计 -->
              <div class="item-subtotal">
                <span class="label">小计：</span>
                <span class="amount">¥{{ (item.price * item.count).toFixed(2) }}</span>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 订单备注 -->
        <el-card class="section-card" shadow="hover" style="margin-top: 20px;">
          <template #header>
            <div class="section-header">
              <el-icon><Memo /></el-icon>
              <span>订单备注</span>
            </div>
          </template>

          <el-input 
            v-model="form.remark" 
            type="textarea" 
            :rows="2"
            placeholder="选填：对订单的特殊说明（如：不要辣、少放盐等）" 
            maxlength="200"
            show-word-limit />
        </el-card>

        <!-- 金额汇总 -->
        <el-card class="section-card total-card" shadow="hover" style="margin-top: 20px;">
          <div class="total-row">
            <span class="label">商品总额：</span>
            <span class="value">¥{{ itemsTotal.toFixed(2) }}</span>
          </div>
          <div class="total-row">
            <span class="label">配送费：</span>
            <span class="value">¥{{ deliveryFee.toFixed(2) }}</span>
          </div>
          <div class="total-row">
            <span class="label">优惠减免：</span>
            <span class="value discount">-¥{{ discountAmount.toFixed(2) }}</span>
          </div>
          <el-divider />
          <div class="total-row grand-total">
            <span class="label">实付金额：</span>
            <span class="value">¥{{ payAmount.toFixed(2) }}</span>
          </div>
        </el-card>

        <!-- 提交按钮 -->
        <div class="submit-section">
          <el-button @click="goBack" size="large" plain>返回修改</el-button>
          <el-button 
            type="primary" 
            size="large" 
            @click="submitOrder"
            :loading="submitLoading"
            class="submit-btn">
            {{ submitLoading ? '提交中...' : '提交订单' }}
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Location, ShoppingCart, Memo } from '@element-plus/icons-vue'
import { useCartStore } from '../../stores/cart'
import { orderApi } from '../../api/order'

const router = useRouter()
const cartStore = useCartStore()

// 表单数据
const form = ref({
  cartId: null,
  shopId: null,
  consignee: '',
  phone: '',
  address: '',
  remark: ''
})

const submitLoading = ref(false)

// 配送费和优惠
const deliveryFee = ref(0)
const discountAmount = ref(0)

// 选中的商品
const selectedItems = computed(() => {
  return cartStore.items.filter(item => item.selected)
})

// 商品总额
const itemsTotal = computed(() => {
  return selectedItems.value.reduce((sum, item) => {
    return sum + (item.price * item.count)
  }, 0)
})

// 实付金额
const payAmount = computed(() => {
  return itemsTotal.value + deliveryFee.value - discountAmount.value
})

// 生成菜品图片
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

// 页面加载时初始化
onMounted(async () => {
  // 确保购物车数据已加载
  if (cartStore.items.length === 0) {
    await cartStore.loadCart()
  }

  // 检查是否有选中的商品
  if (selectedItems.value.length === 0) {
    ElMessage.warning('没有选中的商品，请先选择商品')
    router.push('/cart')
    return
  }

  // 获取第一个选中商品的 shopId（假设所有商品来自同一家店）
  if (selectedItems.value.length > 0) {
    form.value.shopId = selectedItems.value[0].shopId
    form.value.cartId = cartStore.cartId
  }
})

// 返回
function goBack() {
  router.push('/cart')
}

// 验证表单
function validateForm() {
  if (!form.value.consignee || !form.value.consignee.trim()) {
    ElMessage.warning('请输入收货人姓名')
    return false
  }
  
  if (!form.value.phone || !form.value.phone.trim()) {
    ElMessage.warning('请输入联系电话')
    return false
  }
  
  // 简单验证手机号格式
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(form.value.phone.trim())) {
    ElMessage.warning('请输入正确的手机号')
    return false
  }
  
  if (!form.value.address || !form.value.address.trim()) {
    ElMessage.warning('请输入收货地址')
    return false
  }
  
  return true
}

// 提交订单
async function submitOrder() {
  // 验证表单
  if (!validateForm()) {
    return
  }

  try {
    submitLoading.value = true

    // 准备提交数据
    const submitData = {
      cartId: form.value.cartId,
      shopId: form.value.shopId,
      consignee: form.value.consignee.trim(),
      phone: form.value.phone.trim(),
      address: form.value.address.trim(),
      remark: form.value.remark?.trim() || ''
    }

    // 调用 API 创建订单
    const result = await orderApi.createOrder(submitData)
    
    ElMessage.success('订单提交成功！')
    
    // 跳转到订单详情页或首页
    // 这里先跳转到首页，后续可以改为订单详情页
    setTimeout(() => {
      router.push('/')
    }, 1500)
    
  } catch (error) {
    console.error('提交订单失败:', error)
    ElMessage.error('提交失败，请稍后重试')
  } finally {
    submitLoading.value = false
  }
}
</script>

<style scoped>
.order-confirm-page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.order-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 20px;
}

.order-form {
  margin-top: 10px;
}

.section-card {
  border-radius: 8px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.item-count {
  margin-left: auto;
  color: #909399;
  font-size: 14px;
  font-weight: normal;
}

.order-items {
  margin-top: 10px;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.order-item:last-child {
  border-bottom: none;
}

.item-image {
  margin-right: 15px;
}

.dish-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
}

.item-info {
  flex: 1;
  min-width: 150px;
}

.dish-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #303133;
}

.dish-price {
  color: #f56c6c;
  font-size: 16px;
  font-weight: bold;
}

.item-quantity {
  min-width: 100px;
  text-align: center;
  margin: 0 15px;
}

.item-quantity .label {
  color: #909399;
  font-size: 14px;
}

.item-quantity .count {
  color: #303133;
  font-size: 16px;
  font-weight: 500;
}

.item-subtotal {
  min-width: 120px;
  text-align: right;
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

.total-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.total-row .label {
  font-size: 16px;
}

.total-row .value {
  font-size: 18px;
  font-weight: 500;
}

.total-row .value.discount {
  color: #ffd04b;
}

.total-row.grand-total {
  padding-top: 15px;
}

.total-row.grand-total .label {
  font-size: 18px;
  font-weight: 600;
}

.total-row.grand-total .value {
  font-size: 24px;
  font-weight: bold;
  color: #ffd04b;
}

:deep(.el-divider__horizontal) {
  background-color: rgba(255, 255, 255, 0.3);
}

.submit-section {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.submit-btn {
  min-width: 150px;
}
</style>
