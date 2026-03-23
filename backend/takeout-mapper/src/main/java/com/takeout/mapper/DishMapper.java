package com.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.takeout.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品 Mapper 接口
 *
 * @author 小好
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
