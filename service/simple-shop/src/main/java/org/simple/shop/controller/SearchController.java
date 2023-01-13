package org.simple.shop.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.simple.common.utils.CommonResult;
import org.simple.security.annotation.SimpleLog;
import org.simple.shop.entity.Search;
import org.simple.shop.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:37:28
 * @Author: frsimple
 */

@RestController
@RequestMapping("/shopSearch")
public class SearchController {

    @Autowired
    private SearchService searchService;


    @GetMapping("list")
    @SimpleLog("查询热门搜索")
    public CommonResult list(Page page, Search shopSearch) {
        return CommonResult.success(searchService.page(page, Wrappers.query(shopSearch)));
    }


}