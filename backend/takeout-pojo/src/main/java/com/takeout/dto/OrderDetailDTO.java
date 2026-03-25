package com.takeout.dto;

import com.takeout.entity.OrderDetail;
import com.takeout.entity.Orders;
import lombok.Data;
import java.util.List;

/**
 * 订单详情 DTO（包含订单信息和明细列表）
 *
 * @author 小好
 */
@Data
public class OrderDetailDTO {

    /**
     * 订单基本信息
     */
    private Orders order;

    /**
     * 订单明细列表
     */
    private List<OrderDetail> orderDetails;
}
