package com.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.takeout.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车项 Mapper 接口
 *
 * @author 小好
 */
@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {
}
