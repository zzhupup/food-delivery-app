<template>
  <div class="dish-page">
    <el-card class="dish-card">
      <template #header>
        <div class="card-header">
          <h2>🍔 菜品列表</h2>
          <el-input
            v-model="searchText"
            placeholder="搜索菜品..."
            style="width: 200px"
            clearable
            prefix-icon="Search"
          />
        </div>
      </template>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- 菜品列表 -->
      <div v-else-if="dishList.length > 0" class="dish-list">
        <el-row :gutter="20">
          <el-col 
            v-for="dish in filteredDishes" 
            :key="dish.id" 
            :xs="24" 
            :sm="12" 
            :md="8" 
            :lg="6"
          >
            <el-card class="dish-item" shadow="hover">
              <div class="dish-image">
                <el-image 
                  :src="dish.image || '/placeholder.png'" 
                  fit="cover"
                  class="dish-img">
                  <template #error>
                    <div class="image-error">
                      <el-icon><Food /></el-icon>
                    </div>
                  </template>
                </el-image>
              </div>
              
              <div class="dish-info">
                <div class="dish-name">{{ dish.name }}</div>
                <div class="dish-desc">{{ dish.description }}</div>
                <div class="dish-price-row">
                  <span class="price">¥{{ dish.price.toFixed(2) }}</span>
                  <span v-if="dish.originalPrice > dish.price" class="original-price">
                    ¥{{ dish.originalPrice.toFixed(2) }}
                  </span>
                </div>
                <div class="dish-sales">已售 {{ dish.sales }}</div>
                
                <el-button 
                  type="primary" 
                  class="add-to-cart-btn"
                  @click="addToCart(dish)"
                  :loading="addingId === dish.id">
                  <el-icon><Plus /></el-icon>
                  加入购物车
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 空状态 -->
      <el-empty v-else description="暂无菜品" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Food, Plus, Search } from '@element-plus/icons-vue'
import { useCartStore } from '../../stores/cart'

const cartStore = useCartStore()

const loading = ref(true)
const dishList = ref([])
const searchText = ref('')
const addingId = ref(null)

// 加载菜品列表
onMounted(async () => {
  await loadDishes()
})

async function loadDishes() {
  try {
    loading.value = true
    const response = await fetch('http://localhost:8080/dish/list')
    const result = await response.json()
    
    if (result.code === 200) {
      dishList.value = result.data
    } else {
      ElMessage.error(result.message || '加载菜品失败')
    }
  } catch (error) {
    console.error('加载菜品失败:', error)
    ElMessage.error('加载菜品失败，请检查后端服务')
  } finally {
    loading.value = false
  }
}

// 搜索过滤
const filteredDishes = computed(() => {
  if (!searchText.value) return dishList.value
  return dishList.value.filter(dish => 
    dish.name.toLowerCase().includes(searchText.value.toLowerCase()) ||
    dish.description.toLowerCase().includes(searchText.value.toLowerCase())
  )
})

// 加入购物车
async function addToCart(dish) {
  try {
    addingId.value = dish.id
    await cartStore.addCartItem(dish.id, 1)
    ElMessage.success(`已添加 ${dish.name} 到购物车`)
  } catch (error) {
    console.error('添加失败:', error)
    ElMessage.error('添加失败，请重试')
  } finally {
    addingId.value = null
  }
}
</script>

<style scoped>
.dish-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.dish-card {
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

.loading {
  padding: 20px;
}

.dish-list {
  margin-top: 20px;
}

.dish-item {
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.dish-item:hover {
  transform: translateY(-5px);
}

.dish-image {
  height: 200px;
  overflow: hidden;
  border-radius: 8px 8px 0 0;
  margin: -16px -16px 0 -16px;
}

.dish-img {
  width: 100%;
  height: 100%;
}

.image-error {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 200px;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 50px;
}

.dish-info {
  padding: 12px;
}

.dish-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #303133;
}

.dish-desc {
  font-size: 12px;
  color: #909399;
  margin-bottom: 12px;
  height: 34px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.dish-price-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 8px;
}

.price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}

.original-price {
  color: #909399;
  font-size: 14px;
  text-decoration: line-through;
}

.dish-sales {
  font-size: 12px;
  color: #909399;
  margin-bottom: 12px;
}

.add-to-cart-btn {
  width: 100%;
}
</style>
