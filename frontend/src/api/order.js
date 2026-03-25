import request from '../utils/request'

/**
 * 订单 API
 */
export const orderApi = {
  /**
   * 创建订单
   * @param {Object} data 订单数据
   * @returns {Promise}
   */
  createOrder(data) {
    return request({
      url: '/order/create',
      method: 'post',
      data
    })
  },

  /**
   * 获取订单详情
   * @param {number} orderId 订单 ID
   * @returns {Promise}
   */
  getOrderDetail(orderId) {
    return request({
      url: `/order/detail?id=${orderId}`,
      method: 'get'
    })
  },

  /**
   * 取消订单
   * @param {number} orderId 订单 ID
   * @returns {Promise}
   */
  cancelOrder(orderId) {
    return request({
      url: `/order/cancel?id=${orderId}`,
      method: 'post'
    })
  },

  /**
   * 获取订单列表
   * @param {Object} params 查询参数
   * @returns {Promise}
   */
  getOrderList(params) {
    return request({
      url: '/order/list',
      method: 'get',
      params
    })
  }
}

export default orderApi
