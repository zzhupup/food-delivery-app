<template>
  <div class="home-page">
    <el-card class="welcome-card">
      <template #header>
        <div class="card-header">
          <h2>🍔 外卖平台 - 首页</h2>
        </div>
      </template>
      
      <div class="welcome-content">
        <el-result
          icon="success"
          title="前端项目已创建"
          sub-title="购物车模块已完成，点击按钮进入购物车页面">
          <template #extra>
            <el-button type="primary" @click="goDish">
              <el-icon><Food /></el-icon>
              浏览菜品
            </el-button>
            <el-button @click="goCart">
              <el-icon><ShoppingCart /></el-icon>
              进入购物车
            </el-button>
          </template>
        </el-result>

        <el-divider>功能清单</el-divider>
        
        <el-descriptions :column="1" border>
          <el-descriptions-item label="框架">Vue 3.4 + Vite 5</el-descriptions-item>
          <el-descriptions-item label="UI 组件">Element Plus</el-descriptions-item>
          <el-descriptions-item label="状态管理">Pinia</el-descriptions-item>
          <el-descriptions-item label="HTTP 客户端">Axios</el-descriptions-item>
          <el-descriptions-item label="配套后端">Spring Boot + MyBatis-Plus</el-descriptions-item>
        </el-descriptions>

        <el-divider>购物车功能</el-divider>

        <el-row :gutter="20">
          <el-col :span="12" v-for="feature in features" :key="feature">
            <el-tag type="success" effect="plain" size="large">
              <el-icon><Check /></el-icon>
              {{ feature }}
            </el-tag>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ShoppingCart, Document, Check } from '@element-plus/icons-vue'

const router = useRouter()

const features = [
  '购物车列表展示',
  '添加商品到购物车',
  '修改商品数量',
  '删除购物车项',
  '批量删除',
  '清空购物车',
  '计算总金额',
  '全选/单选功能'
]

function goDish() {
  router.push('/dish')
}

function goCart() {
  router.push('/cart')
}
</script>

<style scoped>
.home-page {
  padding: 40px 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.welcome-card {
  border-radius: 12px;
}

.card-header h2 {
  margin: 0;
  font-size: 24px;
}

.welcome-content {
  padding: 20px 0;
}

.el-result {
  padding: 40px 0;
}

.el-descriptions {
  margin: 20px 0;
}

.el-row {
  margin-top: 20px;
}

.el-tag {
  margin: 10px 0;
  display: inline-flex;
  align-items: center;
  gap: 5px;
}
</style>
