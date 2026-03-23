package com.takeout.controller;

import com.takeout.Result;
import com.takeout.dto.CartItemVO;
import com.takeout.entity.Cart;
import com.takeout.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车 Controller（重构后）
 *
 * @author 小好
 */
@Slf4j
@RestController
@RequestMapping("/cart")
@Tag(name = "购物车相关接口", description = "购物车添加、查询、删除等操作")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加商品到购物车
     * POST /cart/add
     */
    @PostMapping("/add")
    @Operation(summary = "添加商品到购物车", description = "将指定数量的菜品添加到当前用户的购物车")
    public Result<String> addToCart(
            @Parameter(description = "菜品 ID", required = true) @RequestParam Long dishId,
            @Parameter(description = "数量", required = true) @RequestParam(defaultValue = "1") Integer count) {
        log.info("添加商品到购物车，dishId={}, count={}", dishId, count);
        cartService.addToCart(dishId, count);
        return Result.success("添加成功");
    }

    /**
     * 查询当前用户购物车项列表
     * GET /cart/list
     */
    @GetMapping("/list")
    @Operation(summary = "查询当前用户购物车", description = "返回购物车项列表，包含菜品信息")
    public Result<List<CartItemVO>> getCurrentCartItems() {
        log.info("查询当前用户购物车");
        List<CartItemVO> list = cartService.getCurrentCartItems();
        return Result.success(list);
    }

    /**
     * 获取当前购物车对象
     * GET /cart
     */
    @GetMapping
    @Operation(summary = "获取当前购物车", description = "返回购物车对象（含购物车 ID）")
    public Result<Cart> getCurrentCart() {
        log.info("获取当前购物车");
        Cart cart = cartService.getCurrentCart();
        return Result.success(cart);
    }

    /**
     * 修改购物车项数量
     * PUT /cart
     */
    @PutMapping
    @Operation(summary = "修改购物车项数量", description = "更新指定购物车项的数量")
    public Result<String> updateCount(@RequestBody Map<String, Object> params) {
        Long cartItemId = Long.valueOf(params.get("cartItemId").toString());
        Integer count = (Integer) params.get("count");

        log.info("修改购物车项数量，cartItemId={}, count={}", cartItemId, count);
        cartService.updateCount(cartItemId, count);
        return Result.success("修改成功");
    }

    /**
     * 删除购物车项
     * DELETE /cart/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除购物车项", description = "从购物车中删除指定项")
    public Result<String> deleteFromCart(@PathVariable Long id) {
        log.info("删除购物车项，cartItemId={}", id);
        cartService.deleteFromCart(id);
        return Result.success("删除成功");
    }

    /**
     * 清空购物车
     * DELETE /cart/clear
     */
    @DeleteMapping("/clear")
    @Operation(summary = "清空购物车", description = "清空当前用户购物车的所有项")
    public Result<String> clearCart() {
        log.info("清空购物车");
        cartService.clearCart();
        return Result.success("清空成功");
    }

    /**
     * 批量删除购物车项
     * DELETE /cart/batch
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除购物车项", description = "一次性批量删除多个购物车项")
    public Result<String> batchDelete(@RequestBody List<Long> cartItemIds) {
        log.info("批量删除购物车项，cartItemIds={}", cartItemIds);
        cartService.batchDelete(cartItemIds);
        return Result.success("批量删除成功");
    }

    /**
     * 计算购物车金额总和
     * GET /cart/total
     */
    @GetMapping("/total")
    @Operation(summary = "计算购物车金额总和", description = "返回当前用户购物车的总金额")
    public Result<BigDecimal> cartTotalAmount() {
        BigDecimal totalAmount = cartService.cartTotalAmount();
        return Result.success(totalAmount);
    }
}
