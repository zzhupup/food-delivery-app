package com.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.common.BaseContext;
import com.takeout.dto.CartItemDTO;
import com.takeout.dto.OrderDTO;
import com.takeout.dto.OrderDetailDTO;
import com.takeout.entity.OrderDetail;
import com.takeout.entity.Orders;
import com.takeout.mapper.OrderDetailMapper;
import com.takeout.mapper.OrdersMapper;
import com.takeout.service.CartItemService;
import com.takeout.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 订单 Service 实现类
 *
 * @author 小好
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrderService {

    // ========== 订单状态常量 ==========
    /** 待支付 */
    private static final Integer STATUS_PENDING = 0;
    /** 已支付/待发货 */
    private static final Integer STATUS_PAID = 1;
    /** 已发货 */
    private static final Integer STATUS_SHIPPED = 2;
    /** 已完成 */
    private static final Integer STATUS_COMPLETED = 3;
    /** 已取消 */
    private static final Integer STATUS_CANCELLED = 4;

    // ========== 支付方式常量 ==========
    /** 微信支付 */
    private static final Integer PAY_METHOD_WECHAT = 1;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    @Transactional
    public Orders createOrder(OrderDTO orderDTO) {
        // 1. 获取当前用户 ID
        Long userId = BaseContext.getCurrentId();
        log.info("【创建订单】userId={}, shopId={}", userId, orderDTO.getShopId());

        // 2. 获取当前用户的购物车项（重构后）
        List<CartItemDTO> cartItems = cartItemService.getCartItemsByCartId(orderDTO.getCartId());
        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("购物车不能为空");
        }

        // 3. 验证购物车是否都属于当前用户
        // （CartItemService 中已验证过，这里可以省略或添加额外验证）

        // 4. 计算订单总金额
        BigDecimal totalAmount = cartItems.stream()
                .map(CartItemDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("【订单金额】totalAmount={}", totalAmount);

        // 5. 生成订单号（时间戳 + 随机数）
        String orderNumber = generateOrderNumber();

        // 6. 创建订单对象
        Orders orders = new Orders();
        orders.setOrderNumber(orderNumber);
        orders.setUserId(userId);
        orders.setShopId(orderDTO.getShopId());
        orders.setStatus(STATUS_PENDING);
        orders.setConsignee(orderDTO.getConsignee());
        orders.setPhone(orderDTO.getPhone());
        orders.setAddress(orderDTO.getAddress());
        orders.setRemark(orderDTO.getRemark());
        orders.setTotalAmount(totalAmount);
        orders.setDeliveryFee(BigDecimal.ZERO);
        orders.setDiscountAmount(BigDecimal.ZERO);
        orders.setPayAmount(totalAmount);
        orders.setPayMethod(PAY_METHOD_WECHAT);
        orders.setCreateTime(LocalDateTime.now());
        orders.setUpdateTime(LocalDateTime.now());
        orders.setDeleted(0);

        // 7. 保存订单
        this.save(orders);
        log.info("【订单创建成功】orderId={}, orderNumber={}", orders.getId(), orderNumber);

        // 8. 保存订单明细
        for (CartItemDTO item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(orders.getId());
            detail.setDishId(item.getDishId());
            detail.setDishName(item.getDishName());
            detail.setDishImage(item.getDishImage());
            detail.setNumber(item.getCount());
            detail.setPrice(item.getPrice());
            detail.setAmount(item.getAmount());
            detail.setCreateTime(LocalDateTime.now());
            orderDetailMapper.insert(detail);
        }
        log.info("【订单明细已保存】orderId={}, 共 {} 项", orders.getId(), cartItems.size());

        // 9. 清空购物车
        cartItemService.clearCartItems(orderDTO.getCartId());
        log.info("【已清空购物车】cartId={}", orderDTO.getCartId());

        return orders;
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        // 1. 获取当前用户 ID
        Long userId = BaseContext.getCurrentId();
        log.info("【取消订单】userId={}, orderId={}", userId, orderId);

        // 2. 查询订单
        Orders order = this.getById(orderId);
        if (order == null) {
            log.warn("【取消订单失败】订单不存在，orderId={}", orderId);
            throw new RuntimeException("无该订单");
        }

        // 3. 验证订单是否都属于当前用户
        if (!order.getUserId().equals(userId)) {
            log.warn("【取消订单失败】无权操作，userId={}, orderId={}", userId, orderId);
            throw new RuntimeException("无权操作该订单");
        }

        // 4. 检查订单状态是否可取消
        Integer status = order.getStatus();
        if (status.equals(STATUS_PENDING) || status.equals(STATUS_PAID)) {
            order.setStatus(STATUS_CANCELLED);
            order.setUpdateTime(LocalDateTime.now());
            this.updateById(order);
            log.info("【订单已取消】orderId={}, 原状态={}, 新状态={}", orderId, status, STATUS_CANCELLED);
        } else {
            log.warn("【取消订单失败】当前状态不可取消，orderId={}, status={}", orderId, status);
            throw new RuntimeException("当前订单状态不可取消（仅待支付/已支付状态可取消）");
        }
    }

    @Override
    public OrderDetailDTO getOrderDetailWithDetails(Long orderId) {
        // 1. 获取当前用户 ID
        Long userId = BaseContext.getCurrentId();
        log.info("【查询订单详情】userId={}, orderId={}", userId, orderId);

        // 2. 查询订单
        Orders order = this.getById(orderId);
        if (order == null) {
            log.warn("【查询订单失败】订单不存在，orderId={}", orderId);
            throw new RuntimeException("无该订单");
        }

        // 3. 验证订单是否都属于当前用户
        if (!order.getUserId().equals(userId)) {
            log.warn("【查询订单失败】无权操作，userId={}, orderId={}", userId, orderId);
            throw new RuntimeException("无权操作该订单");
        }

        // 4. 查询订单明细
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, orderId);
        List<OrderDetail> details = orderDetailMapper.selectList(queryWrapper);
        log.info("【查询完成】orderId={}, 明细数={}", orderId, details.size());

        // 5. 组装 DTO
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrder(order);
        dto.setOrderDetails(details);

        return dto;
    }

    @Override
    public List<Orders> getUserOrders() {
        // 1. 获取当前用户 ID
        Long userId = BaseContext.getCurrentId();
        log.info("【查询用户订单列表】userId={}", userId);

        // 2. 查询当前用户的所有订单（按创建时间倒序）
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, userId)
                .orderByDesc(Orders::getCreateTime);

        List<Orders> orders = this.list(queryWrapper);
        log.info("【查询完成】userId={}, 共 {} 个订单", userId, orders.size());

        return orders;
    }

    /**
     * 生成订单号
     * 格式：yyyyMMddHHmmss + 6 位随机数
     */
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        
        String random = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();
        
        return timestamp + random;
    }
}
