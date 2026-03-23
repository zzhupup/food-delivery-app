package com.takeout.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 购物车项实体类（Cart 和 Dish 的关联表）
 *
 * @author 小好
 */
@Data
@TableName("cart_item")
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 购物车 ID（关联 cart 表）
     */
    private Long cartId;

    /**
     * 菜品 ID（关联 dish 表）
     */
    private Long dishId;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
