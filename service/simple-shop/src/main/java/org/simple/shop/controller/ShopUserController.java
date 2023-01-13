package org.simple.shop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.simple.common.utils.CommonResult;
import org.simple.security.annotation.SimpleLog;
import org.simple.shop.dto.UserDto;
import org.simple.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:37:58
 * @Author: frsimple
 */

@RestController
@RequestMapping("/user")
public class ShopUserController {

    @Autowired
    private UserService userService;

    @GetMapping("list")
    @SimpleLog("查询会员信息表")
    @PreAuthorize("hasAnyAuthority('shop:user:query')")
    public CommonResult list(Page page, UserDto userDto) {
        return CommonResult.success(userService.querUserList(page,userDto));
    }
}