package com.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.common.BaseContext;
import com.takeout.dto.CartItemDTO;
import com.takeout.entity.Cart;
import com.takeout.mapper.CartMapper;
import com.takeout.service.CartItemService;
import com.takeout.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车 Service 实现类（重构后）
 *
 * @author 小好
 */
@Slf4j
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    @org.springframework.context.annotation.Lazy
    private CartItemService cartItemService;

    @Override
    public Cart getCurrentCart() {
        // 1. 获取当前用户 ID
        Long userId = BaseContext.getCurrentId();
        log.info("【获取当前用户购物车】userId={}", userId);

        try {
            // 2. 查询用户的购物车
            LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Cart::getUserId, userId);
            Cart cart = this.getOne(queryWrapper);

            // 3. 如果没有购物车，创建一个
            if (cart == null) {
                cart = new Cart();
                cart.setUserId(userId);
                this.save(cart);
                log.info("【创建新购物车】cartId={}", cart.getId());
            }

            return cart;
        } catch (Exception e) {
            log.error("【获取购物车失败】userId={}, error={}", userId, e.getMessage());
            // 返回一个临时购物车（不保存）
            Cart tempCart = new Cart();
            tempCart.setUserId(userId);
            tempCart.setId(0L);
            return tempCart;
        }
    }

    @Override
    public List<CartItemDTO> getCurrentCartItems() {
        try {
            // 1. 获取或创建购物车
            Cart cart = getCurrentCart();
            log.info("【查询当前用户购物车项】userId={}, cartId={}", BaseContext.getCurrentId(), cart.getId());

            // 2. 如果是临时购物车（ID=0），返回空列表
            if (cart.getId() == 0L) {
                return new ArrayList<>();
            }

            // 3. 查询购物车项（关联菜品信息）
            return cartItemService.getCartItemsByCartId(cart.getId());
        } catch (Exception e) {
            log.error("【查询购物车项失败】error={}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public void addToCart(Long dishId, Integer count) {
        // 1. 获取或创建购物车
        Cart cart = getCurrentCart();
        log.info("【添加商品到购物车】userId={}, cartId={}, dishId={}, count={}", 
                BaseContext.getCurrentId(), cart.getId(), dishId, count);

        // 2. 添加到购物车项
        cartItemService.addCartItem(cart.getId(), dishId, count);
    }

    @Override
    @Transactional
    public void updateCount(Long cartItemId, Integer count) {
        log.info("【更新购物车项数量】cartItemId={}, count={}", cartItemId, count);
        cartItemService.updateCount(cartItemId, count);
    }

    @Override
    @Transactional
    public void deleteFromCart(Long cartItemId) {
        log.info("【删除购物车项】cartItemId={}", cartItemId);
        cartItemService.deleteCartItem(cartItemId);
    }

    @Override
    @Transactional
    public void clearCart() {
        // 1. 获取或创建购物车
        Cart cart = getCurrentCart();
        log.info("【清空购物车】userId={}, cartId={}", BaseContext.getCurrentId(), cart.getId());

        // 2. 清空购物车项
        cartItemService.clearCartItems(cart.getId());
    }

    @Override
    public BigDecimal cartTotalAmount() {
        // 1. 获取或创建购物车
        Cart cart = getCurrentCart();
        Long userId = BaseContext.getCurrentId();
        log.info("【计算购物车总金额】userId={}, cartId={}", userId, cart.getId());

        // 2. 查询购物车项
        List<CartItemDTO> cartItems = cartItemService.getCartItemsByCartId(cart.getId());

        // 3. 计算总金额（Stream API）
        BigDecimal totalAmount = cartItems.stream()
                .map(CartItemDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("【购物车总金额】totalAmount={}", totalAmount);
        return totalAmount;
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> cartItemIds) {
        log.info("【批量删除购物车项】cartItemIds={}", cartItemIds);
        cartItemService.batchDelete(cartItemIds);
    }
}
