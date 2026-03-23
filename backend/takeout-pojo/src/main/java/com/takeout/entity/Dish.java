package com.takeout.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品实体类
 *
 * @author 小好
 */
@Data
@TableName("dish")
public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 店铺 ID
     */
    private Long shopId;

    /**
     * 分类 ID
     */
    private Long categoryId;

    /**
     * 菜品名称
     */
    private String name;

    /**
     * 菜品描述
     */
    private String description;

    /**
     * 菜品图片
     */
    private String image;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * 状态 0-下架 1-上架
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

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

    /**
     * 逻辑删除 0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
