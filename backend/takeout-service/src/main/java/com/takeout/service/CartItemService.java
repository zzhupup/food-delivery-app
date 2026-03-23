package com.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.dto.CartItemDTO;
import com.takeout.entity.CartItem;

import java.util.List;

/**
 * 购物车项 Service 接口
 *
 * @author 小好
 */
public interface CartItemService extends IService<CartItem> {

    /**
     * 添加购物车项
     *
     * @param cartId   购物车 ID
     * @param dishId   菜品 ID
     * @param count    数量
     */
    void addCartItem(Long cartId, Long dishId, Integer count);

    /**
     * 查询购物车的所有项（关联 Dish 表）
     *
     * @param cartId  购物车 ID
     * @return 购物车项列表（含菜品信息）
     */
    List<CartItemDTO> getCartItemsByCartId(Long cartId);

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
    void deleteCartItem(Long cartItemId);

    /**
     * 批量删除购物车项
     *
     * @param cartItemIds  购物车项 ID 列表
     */
    void batchDelete(List<Long> cartItemIds);

    /**
     * 清空购物车的所有项
     *
     * @param cartId  购物车 ID
     */
    void clearCartItems(Long cartId);
}
