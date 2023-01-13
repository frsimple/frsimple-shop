package org.simple.shop.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.simple.common.utils.CommonResult;
import org.simple.common.utils.RedomUtil;
import org.simple.security.annotation.SimpleLog;
import org.simple.security.utils.AuthUtils;
import org.simple.shop.entity.Tabs;
import org.simple.shop.service.TabsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:37:44
 * @Author: frsimple
 */

@RestController
@RequestMapping("/tabs")
public class TabsController {

    @Autowired
    private TabsService tabsService;


    @GetMapping("list")
    @SimpleLog("查询标签管理")
    public CommonResult list(Page page, Tabs shopTabs) {
        shopTabs.setTenantid(AuthUtils.getUser().getTenantId());
        return CommonResult.success(tabsService.page(page, Wrappers.query(shopTabs)));
    }

    @PostMapping("edit")
    @SimpleLog("修改标签信息")
    @PreAuthorize("hasAnyAuthority('shop:tabs:edit')")
    public CommonResult edit(@RequestBody Tabs shopTabs) {
        return CommonResult.success(tabsService.updateById(shopTabs));
    }

    @PostMapping("add")
    @SimpleLog("新增标签信息")
    @PreAuthorize("hasAnyAuthority('shop:tabs:add')")
    public CommonResult add(@RequestBody Tabs shopTabs) {
        shopTabs.setId(RedomUtil.getUuid());
        shopTabs.setCreatetime(LocalDateTime.now());
        shopTabs.setTenantid(AuthUtils.getUser().getTenantId());
        return CommonResult.success(tabsService.save(shopTabs));
    }

    @DeleteMapping("del/{id}")
    @SimpleLog("删除标签信息")
    @PreAuthorize("hasAnyAuthority('shop:tabs:del')")
    public CommonResult del(@PathVariable String id) {
        return CommonResult.success(tabsService.removeById(id));
    }


}