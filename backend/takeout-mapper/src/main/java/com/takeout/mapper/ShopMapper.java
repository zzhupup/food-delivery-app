package com.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.takeout.entity.Shop;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商家 Mapper 接口
 *
 * @author 小好
 */
@Mapper
public interface ShopMapper extends BaseMapper<Shop> {
}
