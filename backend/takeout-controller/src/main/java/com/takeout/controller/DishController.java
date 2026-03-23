package com.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.takeout.Result;
import com.takeout.entity.Dish;
import com.takeout.mapper.DishMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 菜品 Controller
 *
 * @author 小好
 */
@Slf4j
@RestController
@RequestMapping("/dish")
@Tag(name = "菜品相关接口")
public class DishController {

    @Autowired
    private DishMapper dishMapper;

    /**
     * 根据分类查询菜品列表
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类查询菜品列表")
    public Result<List<Dish>> list(Long categoryId, Long shopId) {
        log.info("查询菜品列表，categoryId={}, shopId={}", categoryId, shopId);
        
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getStatus, 1); // 只查询上架的菜品
        queryWrapper.orderByAsc(Dish::getSort); // 按排序字段排序
        
        if (categoryId != null) {
            queryWrapper.eq(Dish::getCategoryId, categoryId);
        }
        if (shopId != null) {
            queryWrapper.eq(Dish::getShopId, shopId);
        }
        
        List<Dish> list = dishMapper.selectList(queryWrapper);
        return Result.success(list);
    }

    /**
     * 根据 ID 查询菜品详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 查询菜品详情")
    public Result<Dish> getById(@PathVariable Long id) {
        log.info("查询菜品详情，id={}", id);
        Dish dish = dishMapper.selectById(id);
        return dish != null ? Result.success(dish) : Result.error("菜品不存在");
    }
}
