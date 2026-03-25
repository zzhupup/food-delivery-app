<template>
  <div class="order-list-page">
    <el-card class="order-card">
      <template #header>
        <div class="card-header">
          <h2>📦 我的订单</h2>
          <el-button @click="goHome" plain size="small">
            <el-icon><HomeFilled /></el-icon>
            返回首页
          </el-button>
        </div>
      </template>

      <!-- 空状态 -->
      <el-empty v-if="orders.length === 0" description="还没有订单哦~">
        <el-button type="primary" @click="goHome">去下单</el-button>
      </el-empty>

      <!-- 订单列表 -->
      <div v-else class="order-list">
        <div v-for="order in orders" :key="order.id" class="order-item" @click="viewOrderDetail(order.id)">
          <!-- 订单头部 -->
          <div class="order-header">
            <span class="order-number">订单号：{{ order.orderNumber }}</span>
            <el-tag :type="getStatusType(order.status)" size="small">
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>

          <!-- 订单信息 -->
          <div class="order-info">
            <div class="info-row">
              <span class="label">下单时间：</span>
              <span class="value">{{ formatTime(order.createTime) }}</span>
            </div>
            <div class="info-row">
              <span class="label">收货地址：</span>
              <span class="value">{{ order.address }}</span>
            </div>
            <div class="info-row">
              <span class="label">收货人：</span>
              <span class="value">{{ order.consignee }} {{ order.phone }}</span>
            </div>
          </div>

          <!-- 订单金额 -->
          <div class="order-footer">
            <div class="amount-info">
              <span class="label">订单金额：</span>
              <span class="amount">¥{{ order.payAmount?.toFixed(2) || '0.00' }}</span>
            </div>
            <el-button type="primary" size="small" @click.stop="viewOrderDetail(order.id)">
              查看详情
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { HomeFilled } from '@element-plus/icons-vue'
import { orderApi } from '../../api/order'

const router = useRouter()
const orders = ref([])
const loading = ref(false)

// 加载订单列表
onMounted(async () => {
  await loadOrders()
})

async function loadOrders() {
  try {
    loading.value = true
    const result = await orderApi.getOrderList()
    orders.value = result || []
  } catch (error) {
    console.error('加载订单列表失败:', error)
    ElMessage.error('加载订单失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 查看订单详情
function viewOrderDetail(orderId) {
  router.push(`/order/detail/${orderId}`)
}

// 返回首页
function goHome() {
  router.push('/')
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

// 格式化时间
function formatTime(timeStr) {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.order-list-page {
  padding: 20px;
  max-width: 1000px;
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

.order-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-item {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.order-item:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.order-number {
  color: #606266;
  font-size: 14px;
}

.order-info {
  margin-bottom: 15px;
}

.info-row {
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-row .label {
  color: #909399;
  width: 80px;
  flex-shrink: 0;
}

.info-row .value {
  color: #303133;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.amount-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.amount-info .label {
  color: #606266;
  font-size: 14px;
}

.amount-info .amount {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}
</style>
