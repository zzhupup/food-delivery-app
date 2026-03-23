package com.takeout.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 *
 * @author 小好
 */
@Data
@TableName("orders")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderNumber;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 店铺 ID
     */
    private Long shopId;

    /**
     * 订单状态 0-待支付 1-待接单 2-配送中 3-已完成 4-已取消 5-退款中 6-已退款
     */
    private Integer status;

    /**
     * 收货人
     */
    private String consignee;

    /**
     * 收货电话
     */
    private String phone;

    /**
     * 收货地址
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
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 配送费
     */
    private BigDecimal deliveryFee;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    private BigDecimal payAmount;

    /**
     * 支付方式 1-微信 2-支付宝 3-余额
     */
    private Integer payMethod;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 订单备注
     */
    private String remark;

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
