package com.takeout.dto;

import lombok.Data;
import java.util.List;

/**
 * 订单创建 DTO（数据传输对象）
 *
 * @author 小好
 */
@Data
public class OrderDTO {

    /**
     * 购物车 ID（重构后使用）
     */
    private Long cartId;

    /**
     * 店铺 ID
     */
    private Long shopId;

    /**
     * 购物车 ID 列表（要买哪些菜）- 旧版兼容字段
     */
    private List<Long> cartIds;

    /**
     * 收货人姓名
     */
    private String consignee;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 订单备注（可选）
     */
    private String remark;


}
