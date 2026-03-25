package com.takeout.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车项 DTO（数据传输对象）
 * 用于服务层之间传递购物车数据（包含菜品信息）
 *
 * @author 小好
 */
@Data
public class CartItemDTO {

    /**
     * 购物车项 ID
     */
    private Long id;

    /**
     * 购物车 ID
     */
    private Long cartId;

    /**
     * 菜品 ID
     */
    private Long dishId;

    /**
     * 店铺 ID（从菜品关联）
     */
    private Long shopId;

    /**
     * 菜品名称
     */
    private String dishName;

    /**
     * 菜品图片
     */
    private String dishImage;

    /**
     * 菜品单价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 小计金额（price × count）
     */
    private BigDecimal amount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
