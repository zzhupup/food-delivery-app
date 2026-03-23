package com.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.takeout.Result;
import com.takeout.entity.Shop;
import com.takeout.mapper.ShopMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 商家 Controller
 *
 * @author 小好
 */
@Slf4j
@RestController
@RequestMapping("/shop")
@Tag(name = "商家相关接口")
public class ShopController {

    @Autowired
    private ShopMapper shopMapper;

    /**
     * 查询商家列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询商家列表")
    public Result<List<Shop>> list(String name) {
        log.info("查询商家列表，name={}", name);
        
        LambdaQueryWrapper<Shop> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Shop::getStatus, 1); // 只查询营业中的商家
        queryWrapper.orderByDesc(Shop::getSales); // 按销量排序
        
        if (name != null && !name.isEmpty()) {
            queryWrapper.like(Shop::getName, name);
        }
        
        List<Shop> list = shopMapper.selectList(queryWrapper);
        return Result.success(list);
    }

    /**
     * 根据 ID 查询商家详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 查询商家详情")
    public Result<Shop> getById(@PathVariable Long id) {
        log.info("查询商家详情，id={}", id);
        Shop shop = shopMapper.selectById(id);
        return shop != null ? Result.success(shop) : Result.error("商家不存在");
    }
}
