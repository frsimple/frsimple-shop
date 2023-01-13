package org.simple.shop.controller;

import org.simple.common.utils.CommonResult;
import org.simple.security.annotation.SimpleLog;
import org.simple.shop.dto.RateTemDto;
import org.simple.shop.service.RatetemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @Copyright: simple
 * @Date: 2022-11-20 19:02:44
 * @Author: frsimple
 */

@RestController
@RequestMapping("/ratetem")
public class RatetemController {

    @Autowired
    private RatetemService ratetemService;


    @GetMapping("list")
    @SimpleLog("查询运费模板表")
    @PreAuthorize("hasAnyAuthority('shop:ratetem:query')")
    public CommonResult list() {
        return CommonResult.success(ratetemService.queryRataTemList());
    }

    @PostMapping("add")
    @SimpleLog("新增运费模板")
    @PreAuthorize("hasAnyAuthority('shop:ratetem:add')")
    public CommonResult add(@RequestBody RateTemDto ratetem) {
         return ratetemService.saveOrUpdateTem(ratetem);
    }

    @PostMapping("edit")
    @SimpleLog("修改运费模板")
    @PreAuthorize("hasAnyAuthority('shop:ratetem:edit')")
    public CommonResult edit(@RequestBody RateTemDto ratetem) {
        return ratetemService.saveOrUpdateTem(ratetem);
    }


    @DeleteMapping("del/{id}")
    @SimpleLog("删除运费模板")
    @PreAuthorize("hasAnyAuthority('shop:ratetem:del')")
    public CommonResult del(@PathVariable String id) {
        return CommonResult.success(ratetemService.removeById(id));
    }

}