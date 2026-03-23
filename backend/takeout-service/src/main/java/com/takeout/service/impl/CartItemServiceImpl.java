package com.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.common.BaseContext;
import com.takeout.dto.CartItemDTO;
import com.takeout.entity.Cart;
import com.takeout.entity.CartItem;
import com.takeout.entity.Dish;
import com.takeout.mapper.CartItemMapper;
import com.takeout.service.CartItemService;
import com.takeout.service.CartService;
import com.takeout.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 购物车项 Service 实现类
 *
 * @author 小好
 */
@Slf4j
@Service
public class CartItemServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartItemService {

    @Autowired
    private CartService cartService;

    @Autowired
    private DishService dishService;

    @Override
    @Transactional
    public void addCartItem(Long cartId, Long dishId, Integer count) {
        // 1. 获取当前用户 ID
        Long userId = BaseContext.getCurrentId();
        log.info("【添加购物车项】userId={}, cartId={}, dishId={}", userId, cartId, dishId);

        // 2. 验证购物车是否存在且属于当前用户
        Cart cart = cartService.getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车不存在或无权操作");
        }

        // 3. 查询购物车中是否已有该菜品
        CartItem existItem = lambdaQuery()
                .eq(CartItem::getCartId, cartId)
                .eq(CartItem::getDishId, dishId)
                .one();

        if (existItem != null) {
            // 4. 已有该菜品，数量 +1
            existItem.setCount(existItem.getCount() + count);
            this.updateById(existItem);
            log.info("【购物车项已存在】cartItemId={}, 数量 +{}", existItem.getId(), count);
        } else {
            // 5. 没有该菜品，新增
            Dish dish = dishService.getById(dishId);
            if (dish == null) {
                throw new RuntimeException("菜品不存在");
            }

            CartItem cartItem = new CartItem();
            cartItem.setCartId(cartId);
            cartItem.setDishId(dishId);
            cartItem.setCount(count);
            this.save(cartItem);
            log.info("【新增购物车项】cartItemId={}", cartItem.getId());
        }
    }

    @Override
    public List<CartItemDTO> getCartItemsByCartId(Long cartId) {
        // 1. 获取当前用户 ID
        Long userId = BaseContext.getCurrentId();
        log.info("【查询购物车项】userId={}, cartId={}", userId, cartId);

        // 2. 验证购物车是否存在且属于当前用户
        Cart cart = cartService.getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车不存在或无权操作");
        }

        // 3. 查询购物车的所有项
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getCartId, cartId)
                .orderByDesc(CartItem::getCreateTime);

        List<CartItem> cartItems = this.list(queryWrapper);
        if (cartItems == null || cartItems.isEmpty()) {
            return new ArrayList<>();
        }

        // 4. 批量查询菜品信息（一次查询，避免 N+1 问题）
        List<Long> dishIds = cartItems.stream()
                .map(CartItem::getDishId)
                .collect(Collectors.toList());

        List<Dish> dishes = dishService.listByIds(dishIds);
        Map<Long, Dish> dishMap = dishes.stream()
                .collect(Collectors.toMap(Dish::getId, d -> d));

        // 5. 组装 VO 对象
        List<CartItemDTO> dtoList = new ArrayList<>();
        for (CartItem item : cartItems) {
            Dish dish = dishMap.get(item.getDishId());
            if (dish != null) {
                CartItemDTO dto = new CartItemDTO();
                dto.setId(item.getId());
                dto.setCartId(item.getCartId());
                dto.setDishId(item.getDishId());
                dto.setDishName(dish.getName());
                dto.setDishImage(dish.getImage());
                dto.setPrice(dish.getPrice());
                dto.setCount(item.getCount());
                dto.setAmount(dish.getPrice().multiply(BigDecimal.valueOf(item.getCount())));
                dto.setCreateTime(item.getCreateTime());
                dto.setUpdateTime(item.getUpdateTime());
                dtoList.add(vo);
            }
        }

        log.info("【查询完成】cartId={}, 共 {} 项", cartId, dtoList.size());
        return dtoList;
    }

    @Override
    @Transactional
    public void updateCount(Long cartItemId, Integer count) {
        // 1. 获取当前用户 ID
        Long userId = BaseContext.getCurrentId();

        // 2. 查询购物车项
        CartItem item = this.getById(cartItemId);
        if (item == null) {
            throw new RuntimeException("购物车项不存在");
        }

        // 3. 验证购物车是否属于当前用户
        Cart cart = cartService.getById(item.getCartId());
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该购物车项");
        }

        // 4. 更新数量
        if (count <= 0) {
            this.removeById(cartItemId);
            log.info("【购物车项数量为 0，删除】cartItemId={}", cartItemId);
        } else {
            item.setCount(count);
            this.updateById(item);
            log.info("【更新购物车项数量】cartItemId={}, count={}", cartItemId, count);
        }
    }

    @Override
    @Transactional
    public void deleteCartItem(Long cartItemId) {
        // 1. 获取当前用户 ID
        Long userId = BaseContext.getCurrentId();

        // 2. 查询购物车项
        CartItem item = this.getById(cartItemId);
        if (item == null) {
            throw new RuntimeException("购物车项不存在");
        }

        // 3. 验证购物车是否属于当前用户
        Cart cart = cartService.getById(item.getCartId());
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该购物车项");
        }

        // 4. 删除
        this.removeById(cartItemId);
        log.info("【删除购物车项】cartItemId={}", cartItemId);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> cartItemIds) {
        // 1. 获取当前用户 ID
        Long userId = BaseContext.getCurrentId();
        log.info("【批量删除购物车项】userId={}, cartItemIds={}", userId, cartItemIds);

        // 2. 验证参数
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            throw new RuntimeException("购物车项 ID 不能为空");
        }

        // 3. 批量查询购物车项
        List<CartItem> itemList = this.listByIds(cartItemIds);
        if (itemList == null || itemList.isEmpty()) {
            throw new RuntimeException("购物车项不存在");
        }

        // 4. 验证所有购物车项是否都属于当前用户（Stream API）
        itemList.stream()
                .map(CartItem::getCartId)
                .distinct()
                .forEach(cartId -> {
                    Cart cart = cartService.getById(cartId);
                    if (cart == null || !cart.getUserId().equals(userId)) {
                        throw new RuntimeException("无权操作该购物车项");
                    }
                });

        // 5. 批量删除
        this.removeByIds(cartItemIds);
        log.info("【批量删除完成】删除 {} 条记录", cartItemIds.size());
    }

    @Override
    @Transactional
    public void clearCartItems(Long cartId) {
        // 1. 获取当前用户 ID
        Long userId = BaseContext.getCurrentId();
        log.info("【清空购物车项】userId={}, cartId={}", userId, cartId);

        // 2. 验证购物车是否存在且属于当前用户
        Cart cart = cartService.getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车不存在或无权操作");
        }

        // 3. 删除该购物车的所有项
        LambdaQueryWrapper<CartItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartItem::getCartId, cartId);
        this.remove(queryWrapper);
        log.info("【清空完成】cartId={}", cartId);
    }
}
