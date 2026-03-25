<template>
  <div id="app">
    <el-container>
      <el-header>
        <h1>🍔 外卖平台</h1>
        <el-menu 
          mode="horizontal" 
          :ellipsis="false"
          :default-active="$route.path"
          router>
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/dish">
            <el-icon><Food /></el-icon>
            <span>菜品</span>
          </el-menu-item>
          <el-menu-item index="/cart">
            <el-icon><ShoppingCart /></el-icon>
            <span>购物车</span>
          </el-menu-item>
          <el-menu-item index="/order/list">
            <el-icon><Document /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
        </el-menu>
      </el-header>
      
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { HomeFilled, ShoppingCart, User, Food, Document } from '@element-plus/icons-vue'
</script>

<style>
#app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.el-header {
  background-color: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.el-header h1 {
  margin-right: 40px;
  font-size: 24px;
}

.el-menu {
  border-bottom: none;
  background-color: #409EFF;
}

.el-menu-item {
  color: white !important;
}

.el-menu-item:hover {
  background-color: #66b1ff !important;
}

.el-main {
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}
</style>
