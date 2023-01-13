package org.simple.shop.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.simple.security.annotation.SimpleLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.simple.common.utils.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.simple.shop.service.CartService;
import org.simple.shop.entity.Cart;

/**
 * @Copyright: simple
 * @Date: 2022-12-23 11:32:18
 * @Author: frsimple
 */

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;


    @GetMapping("list")
    @SimpleLog("查询用户购物车信息表")
    public CommonResult list(Page page, Cart cart) {
        return CommonResult.success(cartService.page(page, Wrappers.query(cart)));
    }


}