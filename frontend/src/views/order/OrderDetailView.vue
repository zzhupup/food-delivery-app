<template>
  <div class="order-detail-page">
    <el-card class="order-card">
      <template #header>
        <div class="card-header">
          <h2>📋 订单详情</h2>
          <div class="header-actions">
            <el-button @click="goBack" plain size="small">
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
          </div>
        </div>
      </template>

      <div v-loading="loading" class="order-content">
        <!-- 订单状态 -->
        <div class="order-status">
          <el-result
            :icon="getStatusIcon(order.status)"
            :title="getStatusText(order.status)"
            :sub-title="`订单号：${order.orderNumber}`"
          >
          </el-result>
        </div>

        <!-- 订单信息 -->
        <el-descriptions title="订单信息" :column="2" border>
          <el-descriptions-item label="订单号">{{ order.orderNumber }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(order.status)">
              {{ getStatusText(order.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ formatTime(order.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ getPayMethodText(order.payMethod) }}</el-descriptions-item>
        </el-descriptions>

        <!-- 商品清单 -->
        <el-card class="section-card" style="margin-top: 20px;">
          <template #header>
            <div class="section-header">
              <el-icon><ShoppingCart /></el-icon>
              <span>商品清单</span>
              <span class="item-count">共 {{ orderDetails?.length || 0 }} 件商品</span>
            </div>
          </template>

          <el-empty v-if="!orderDetails || orderDetails.length === 0" description="没有商品信息" />

          <div v-else class="order-items">
            <div v-for="item in orderDetails" :key="item.id" class="order-item">
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
                <div class="dish-price">¥{{ item.price?.toFixed(2) }}</div>
              </div>

              <!-- 数量 -->
              <div class="item-quantity">
                <span class="label">数量：</span>
                <span class="count">×{{ item.number }}</span>
              </div>

              <!-- 小计 -->
              <div class="item-subtotal">
                <span class="label">小计：</span>
                <span class="amount">¥{{ item.amount?.toFixed(2) }}</span>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 收货信息 -->
        <el-card class="section-card" style="margin-top: 20px;">
          <template #header>
            <div class="section-header">
              <el-icon><Location /></el-icon>
              <span>收货信息</span>
            </div>
          </template>
          <div class="address-info">
            <p><strong>收货人：</strong>{{ order.consignee }}</p>
            <p><strong>联系电话：</strong>{{ order.phone }}</p>
            <p><strong>收货地址：</strong>{{ order.address }}</p>
          </div>
        </el-card>

        <!-- 金额信息 -->
        <el-card class="section-card" style="margin-top: 20px;">
          <template #header>
            <div class="section-header">
              <el-icon><Money /></el-icon>
              <span>金额信息</span>
            </div>
          </template>
          <div class="amount-info">
            <div class="amount-row">
              <span class="label">订单总额：</span>
              <span class="value">¥{{ order.totalAmount?.toFixed(2) || '0.00' }}</span>
            </div>
            <div class="amount-row">
              <span class="label">配送费：</span>
              <span class="value">¥{{ order.deliveryFee?.toFixed(2) || '0.00' }}</span>
            </div>
            <div class="amount-row">
              <span class="label">优惠减免：</span>
              <span class="value discount">-¥{{ order.discountAmount?.toFixed(2) || '0.00' }}</span>
            </div>
            <el-divider />
            <div class="amount-row grand-total">
              <span class="label">实付金额：</span>
              <span class="value">¥{{ order.payAmount?.toFixed(2) || '0.00' }}</span>
            </div>
          </div>
        </el-card>

        <!-- 订单备注 -->
        <el-card v-if="order.remark" class="section-card" style="margin-top: 20px;">
          <template #header>
            <div class="section-header">
              <el-icon><Memo /></el-icon>
              <span>订单备注</span>
            </div>
          </template>
          <p class="remark-text">{{ order.remark }}</p>
        </el-card>

        <!-- 操作按钮 -->
        <div class="action-buttons" v-if="order.status === 0">
          <el-button type="primary" size="large" @click="cancelOrder">
            取消订单
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Location, Money, Memo, CircleCheck, CircleClose, Clock, ShoppingCart } from '@element-plus/icons-vue'
import { orderApi } from '../../api/order'

const router = useRouter()
const route = useRoute()

const order = ref({})
const orderDetails = ref([])
const loading = ref(true)

// 加载订单详情
onMounted(async () => {
  const orderId = route.params.id
  if (!orderId) {
    ElMessage.error('订单 ID 不能为空')
    router.push('/order/list')
    return
  }
  
  await loadOrderDetail(orderId)
})

async function loadOrderDetail(orderId) {
  try {
    loading.value = true
    const result = await orderApi.getOrderDetail(orderId)
    // 后端返回的是 OrderDetailDTO，包含 order 和 orderDetails
    order.value = result.order || result
    orderDetails.value = result.orderDetails || []
  } catch (error) {
    console.error('加载订单详情失败:', error)
    ElMessage.error('加载订单详情失败')
  } finally {
    loading.value = false
  }
}

// 取消订单
async function cancelOrder() {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await orderApi.cancelOrder(order.value.id)
    ElMessage.success('订单已取消')
    
    // 重新加载订单详情
    await loadOrderDetail(order.value.id)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

// 返回
function goBack() {
  router.back()
}

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

// 获取订单状态文本
function getStatusText(status) {
  const statusMap = {
    0: '待支付',
    1: '待接单',
    2: '配送中',
    3: '已完成',
    4: '已取消',
    5: '退款中',
    6: '已退款'
  }
  return statusMap[status] || '未知状态'
}

// 获取订单状态图标
function getStatusIcon(status) {
  const iconMap = {
    0: Clock,
    1: Clock,
    2: Clock,
    3: CircleCheck,
    4: CircleClose,
    5: Clock,
    6: CircleClose
  }
  return iconMap[status] || Clock
}

// 获取订单状态标签类型
function getStatusType(status) {
  const typeMap = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'success',
    4: 'info',
    5: 'warning',
    6: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取支付方式文本
function getPayMethodText(method) {
  const methodMap = {
    1: '微信支付',
    2: '支付宝',
    3: '余额支付'
  }
  return methodMap[method] || '未知支付方式'
}

// 格式化时间
function formatTime(timeStr) {
  if (!timeStr) return '暂无数据'
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}
</script>

<style scoped>
.order-detail-page {
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

.order-status {
  text-align: center;
  padding: 20px 0;
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

.address-info p {
  margin: 10px 0;
  font-size: 14px;
  color: #303133;
}

.amount-info {
  padding: 10px 0;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.amount-row .label {
  color: #606266;
  font-size: 14px;
}

.amount-row .value {
  color: #303133;
  font-size: 16px;
  font-weight: 500;
}

.amount-row .value.discount {
  color: #f56c6c;
}

.amount-row.grand-total {
  padding-top: 15px;
}

.amount-row.grand-total .label {
  font-size: 16px;
  font-weight: 600;
}

.amount-row.grand-total .value {
  font-size: 22px;
  font-weight: bold;
  color: #f56c6c;
}

:deep(.el-divider__horizontal) {
  background-color: #e0e0e0;
}

.remark-text {
  margin: 0;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  color: #606266;
}

.action-buttons {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}
</style>
