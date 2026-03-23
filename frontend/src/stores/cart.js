import { defineStore } from 'pinia'
import cartApi from '../api/cart'

export const useCartStore = defineStore('cart', {
  state: () => ({
    cartId: null,
    items: [],
    totalAmount: 0
  }),

  getters: {
    // 购物车是否为空
    isEmpty: (state) => state.items.length === 0,

    // 购物车商品总数
    totalCount: (state) => {
      return state.items.reduce((sum, item) => sum + item.count, 0)
    },

    // 格式化总金额
    formattedTotal: (state) => {
      return `¥${state.totalAmount.toFixed(2)}`
    }
  },

  actions: {
    /**
     * 加载购物车数据
     */
    async loadCart() {
      try {
        const items = await cartApi.getCartItems()
        this.items = items
        this.calculateTotal()
        
        if (items.length > 0) {
          this.cartId = items[0].cartId
        }
      } catch (error) {
        console.error('加载购物车失败:', error)
      }
    },

    /**
     * 添加商品到购物车
     */
    async addCartItem(dishId, count = 1) {
      try {
        await cartApi.addToCart(dishId, count)
        await this.loadCart() // 重新加载
      } catch (error) {
        throw error
      }
    },

    /**
     * 更新购物车项数量
     */
    async updateItemQuantity(cartItemId, count) {
      try {
        if (count <= 0) {
          await this.deleteCartItem(cartItemId)
        } else {
          await cartApi.updateCount(cartItemId, count)
          await this.loadCart()
        }
      } catch (error) {
        throw error
      }
    },

    /**
     * 删除购物车项
     */
    async deleteCartItem(cartItemId) {
      try {
        await cartApi.deleteCartItem(cartItemId)
        await this.loadCart()
      } catch (error) {
        throw error
      }
    },

    /**
     * 批量删除
     */
    async batchDelete(cartItemIds) {
      try {
        await cartApi.batchDelete(cartItemIds)
        await this.loadCart()
      } catch (error) {
        throw error
      }
    },

    /**
     * 清空购物车
     */
    async clear() {
      try {
        await cartApi.clearCart()
        this.items = []
        this.totalAmount = 0
      } catch (error) {
        throw error
      }
    },

    /**
     * 计算总金额
     */
    calculateTotal() {
      this.totalAmount = this.items.reduce((sum, item) => {
        return sum + (item.price * item.count)
      }, 0)
    },

    /**
     * 选中/取消选中商品
     */
    toggleSelection(cartItemId) {
      const item = this.items.find(i => i.id === cartItemId)
      if (item) {
        item.selected = !item.selected
        this.calculateTotal()
      }
    }
  }
})
