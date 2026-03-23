package com.takeout.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车项 VO（包含菜品信息）
 *
 * @author 小好
 */
@Data
public class CartItemVO {

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
