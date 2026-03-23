package com.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.takeout.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper 接口
 *
 * @author 小好
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
