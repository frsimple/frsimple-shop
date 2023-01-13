package org.simple.shop.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.simple.common.utils.CommonResult;
import org.simple.security.annotation.SimpleLog;
import org.simple.shop.entity.Reason;
import org.simple.shop.service.ReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:37:14
 * @Author: frsimple
 */

@RestController
@RequestMapping("/shopReason")
public class ReasonController {

    @Autowired
    private ReasonService reasonService;


    @GetMapping("list")
    @SimpleLog("查询商品关系表")
    public CommonResult list(Page page, Reason shopReason) {
        return CommonResult.success(reasonService.page(page, Wrappers.query(shopReason)));
    }


}