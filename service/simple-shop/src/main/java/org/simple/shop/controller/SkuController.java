package org.simple.shop.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.simple.common.utils.CommonResult;
import org.simple.security.annotation.SimpleLog;
import org.simple.security.utils.AuthUtils;
import org.simple.shop.entity.Sku;
import org.simple.shop.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @Copyright: simple
 * @Date: 2022-12-03 21:04:14
 * @Author: frsimple
 */

@RestController
@RequestMapping("/sku")
public class SkuController {

    @Autowired
    private SkuService skuService;


    @GetMapping("list")
    @SimpleLog("查询sku模板信息")
    @PreAuthorize("hasAnyAuthority('shop:sku:query')")
    public CommonResult list(Page page, Sku sku) {
        if(StringUtils.isNotEmpty(sku.getTitle())){
            String title = sku.getTitle();
            sku.setTitle(null);
            return CommonResult.success(skuService.page(page, Wrappers.query(sku).like("title",title)));
        }else{
            return CommonResult.success(skuService.page(page, Wrappers.query(sku)));
        }
    }

    @PostMapping("add")
    @SimpleLog("新增sku模板")
    @PreAuthorize("hasAnyAuthority('shop:ratetem:add')")
    public CommonResult add(@RequestBody Sku sku) {
        sku.setTenantid(AuthUtils.getUser().getTenantId());
        sku.setCreatetime(LocalDateTime.now());
        return CommonResult.success(skuService.save(sku));
    }

    @PostMapping("edit")
    @SimpleLog("修改sku模板")
    @PreAuthorize("hasAnyAuthority('shop:ratetem:edit')")
    public CommonResult edit(@RequestBody Sku sku) {
        return CommonResult.success(skuService.updateById(sku));
    }


    @DeleteMapping("del/{id}")
    @SimpleLog("删除sku模板")
    @PreAuthorize("hasAnyAuthority('shop:ratetem:del')")
    public CommonResult del(@PathVariable String id) {
        return CommonResult.success(skuService.removeById(id));
    }



}