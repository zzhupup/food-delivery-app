package com.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.dto.OrderDetailDTO;
import com.takeout.entity.Orders;
import com.takeout.dto.OrderDTO;

import java.util.List;

/**
 * 订单 Service 接口
 *
 * @author 小好
 */
public interface OrderService extends IService<Orders> {

    /**
     * 创建订单
     *
     * @param orderDTO 订单信息
     * @return 创建的订单（包含订单号）
     */
    Orders createOrder(OrderDTO orderDTO);

    /**
     * 取消订单
     * @param orderId 订单 ID
     */
    void cancelOrder(Long orderId);

    /**
     * 获取订单详情
     * @param orderId 订单 ID
     * @return 订单详情（包含订单信息和明细列表）
     */
    OrderDetailDTO getOrderDetailWithDetails(Long orderId);

    /**
     * 获取当前用户的订单列表
     * @return 订单列表
     */
    List<Orders> getUserOrders();
}
