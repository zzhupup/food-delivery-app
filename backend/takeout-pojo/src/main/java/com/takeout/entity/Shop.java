package com.takeout.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商家实体类
 *
 * @author 小好
 */
@Data
@TableName("shop")
public class Shop implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 店铺描述
     */
    private String description;

    /**
     * 店铺头像
     */
    private String avatar;

    /**
     * 店铺封面
     */
    private String cover;

    /**
     * 商家电话
     */
    private String phone;

    /**
     * 商家地址
     */
    private String address;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 配送费
     */
    private BigDecimal deliveryFee;

    /**
     * 起送价
     */
    private BigDecimal minPrice;

    /**
     * 月销量
     */
    private Integer sales;

    /**
     * 评分
     */
    private BigDecimal score;

    /**
     * 营业状态 0-休息 1-营业
     */
    private Integer status;

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
