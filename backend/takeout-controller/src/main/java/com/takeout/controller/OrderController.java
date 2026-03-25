package com.takeout.controller;

import com.takeout.Result;
import com.takeout.dto.OrderDTO;
import com.takeout.entity.Orders;
import com.takeout.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单 Controller
 *
 * @author 小好
 */
@Slf4j
@RestController
@RequestMapping("/order")
@Tag(name = "订单相关接口", description = "订单创建、取消、查询等操作")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     * POST /order/create
     */
    @PostMapping("create")
    @Operation(summary = "创建订单", description = "根据购物车 ID 列表创建新订单")
    public Result<Orders> createOrder(@RequestBody OrderDTO orderDTO) {
        log.info("创建订单，shopId={}, userId={}", orderDTO.getShopId(), orderDTO.getConsignee());
        
        Orders orders = orderService.createOrder(orderDTO);
        
        return Result.success(orders);
    }

    /**
     * 取消订单
     * POST /order/cancel?id=xxx
     */
    @PostMapping("cancel")
    @Operation(summary = "取消订单", description = "用户取消订单，仅待支付和已支付状态可取消")
    public Result<String> cancelOrder(
            @Parameter(description = "订单 ID", required = true) 
            @RequestParam Long id) {
        log.info("取消订单，orderId={}", id);
        orderService.cancelOrder(id);
        return Result.success("取消成功");
    }

    /**
     * 获取订单详情
     * GET /order/detail?id=xxx
     */
    @GetMapping("detail")
    @Operation(summary = "获取订单详情", description = "根据订单 ID 查询订单详细信息（包含订单信息和明细列表）")
    public Result<com.takeout.dto.OrderDetailDTO> getOrderDetail(
            @Parameter(description = "订单 ID", required = true)
            @RequestParam Long id) {
        log.info("查询订单，orderId={}", id);
        com.takeout.dto.OrderDetailDTO dto = orderService.getOrderDetailWithDetails(id);
        return Result.success(dto);
    }

    /**
     * 获取用户订单列表
     * GET /order/list
     */
    @GetMapping("list")
    @Operation(summary = "获取用户订单列表", description = "查询当前用户的所有订单列表")
    public Result<List<Orders>> getOrderList() {
        log.info("查询用户订单列表");
        List<Orders> orders = orderService.getUserOrders();
        return Result.success(orders);
    }

    /**
     * 测试接口
     * GET /order/test
     */
    @GetMapping("test")
    @Operation(summary = "测试订单接口", description = "测试接口是否正常")
    public Result<String> test() {
        return Result.success("订单接口正常");
    }
}
