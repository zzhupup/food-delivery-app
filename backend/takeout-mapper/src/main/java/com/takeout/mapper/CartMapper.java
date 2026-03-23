package com.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.takeout.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车 Mapper 接口
 *
 * @author 小好
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {
}
