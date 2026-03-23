package com.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.entity.Orders;
import com.takeout.dto.OrderDTO;

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

    void cancelOrder(Long orderId);
}
