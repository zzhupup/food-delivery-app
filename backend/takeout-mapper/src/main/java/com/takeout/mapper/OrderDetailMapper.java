package com.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.takeout.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细 Mapper
 *
 * @author 小好
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
