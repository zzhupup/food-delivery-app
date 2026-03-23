import request from '../utils/request'

/**
 * 购物车 API
 */
export default {
  /**
   * 获取当前购物车
   */
  getCurrentCart() {
    return request({
      url: '/cart',
      method: 'get'
    })
  },

  /**
   * 查询购物车项列表
   */
  getCartItems() {
    return request({
      url: '/cart/list',
      method: 'get'
    })
  },

  /**
   * 添加商品到购物车
   * @param {number} dishId 菜品 ID
   * @param {number} count 数量
   */
  addToCart(dishId, count = 1) {
    return request({
      url: '/cart/add',
      method: 'post',
      params: { dishId, count }
    })
  },

  /**
   * 更新购物车项数量
   * @param {number} cartItemId 购物车项 ID
   * @param {number} count 数量
   */
  updateCount(cartItemId, count) {
    return request({
      url: '/cart',
      method: 'put',
      data: { cartItemId, count }
    })
  },

  /**
   * 删除购物车项
   * @param {number} cartItemId 购物车项 ID
   */
  deleteCartItem(cartItemId) {
    return request({
      url: `/cart/${cartItemId}`,
      method: 'delete'
    })
  },

  /**
   * 批量删除购物车项
   * @param {Array<number>} cartItemIds 购物车项 ID 列表
   */
  batchDelete(cartItemIds) {
    return request({
      url: '/cart/batch',
      method: 'delete',
      data: cartItemIds
    })
  },

  /**
   * 清空购物车
   */
  clearCart() {
    return request({
      url: '/cart/clear',
      method: 'delete'
    })
  },

  /**
   * 计算购物车总金额
   */
  getTotalAmount() {
    return request({
      url: '/cart/total',
      method: 'get'
    })
  }
}
