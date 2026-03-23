package com.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.dto.CartItemVO;
import com.takeout.entity.Cart;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车 Service 接口
 *
 * @author 小好
 */
public interface CartService extends IService<Cart> {

    /**
     * 获取或创建用户的购物车
     *
     * @return 购物车对象
     */
    Cart getCurrentCart();

    /**
     * 查询当前用户的购物车项列表（含菜品信息）
     *
     * @return 购物车项 VO 列表
     */
    List<CartItemVO> getCurrentCartItems();

    /**
     * 添加商品到购物车
     *
     * @param dishId  菜品 ID
     * @param count   数量
     */
    void addToCart(Long dishId, Integer count);

    /**
     * 更新购物车项数量
     *
     * @param cartItemId  购物车项 ID
     * @param count       数量
     */
    void updateCount(Long cartItemId, Integer count);

    /**
     * 删除购物车项
     *
     * @param cartItemId  购物车项 ID
     */
    void deleteFromCart(Long cartItemId);

    /**
     * 清空当前用户购物车
     */
    void clearCart();

    /**
     * 计算购物车金额总和
     *
     * @return 总金额
     */
    BigDecimal cartTotalAmount();

    /**
     * 批量删除购物车项
     *
     * @param cartItemIds 购物车项 ID 列表
     */
    void batchDelete(List<Long> cartItemIds);
}
