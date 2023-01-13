package org.simple.shop.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.simple.common.utils.CommonResult;
import org.simple.security.annotation.SimpleLog;
import org.simple.security.utils.AuthUtils;
import org.simple.shop.entity.Fast;
import org.simple.shop.service.FastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @Copyright: simple
 * @Date: 2022-12-05 16:37:00
 * @Author: frsimple
 */

@RestController
@RequestMapping("/fast")
public class FastController {

    @Autowired
    private FastService fastService;


    @GetMapping("list")
    @SimpleLog("查询快捷菜单配信")
    @PreAuthorize("hasAnyAuthority('shop:fast:query')")
    public CommonResult list(Page page, Fast fast) {
        fast.setTenantid(AuthUtils.getUser().getTenantId());
        if (StringUtils.isNotEmpty(fast.getTitle())) {
            String title = fast.getTitle();
            fast.setTitle(null);
            return CommonResult.success(fastService.page(page, Wrappers.query(fast).like("title", title)));
        } else {
            return CommonResult.success(fastService.page(page, Wrappers.query(fast)));
        }
    }


    @PostMapping("edit")
    @SimpleLog("修改快捷菜单配信")
    @PreAuthorize("hasAnyAuthority('shop:fast:edit')")
    public CommonResult edit(@RequestBody Fast fast) {
        return CommonResult.success(fastService.updateById(fast));
    }

    @PostMapping("add")
    @SimpleLog("新增快捷菜单配信")
    @PreAuthorize("hasAnyAuthority('shop:fast:add')")
    public CommonResult add(@RequestBody Fast fast) {
        fast.setCreatetime(LocalDateTime.now());
        fast.setTenantid(AuthUtils.getUser().getTenantId());
        return CommonResult.success(fastService.save(fast));
    }

    @GetMapping("del/{id}")
    @SimpleLog("删除快捷菜单配信")
    @PreAuthorize("hasAnyAuthority('shop:fast:del')")
    public CommonResult del(@PathVariable String id) {
        return CommonResult.success(fastService.removeById(id));
    }

    @GetMapping("open/{id}")
    @SimpleLog("启用快捷菜单配信")
    @PreAuthorize("hasAnyAuthority('shop:fast:openorclose')")
    public CommonResult openMain(@PathVariable String id) {
        Fast fast = fastService.getById(id);
        fast.setStatus("1");
        return CommonResult.success(fastService.updateById(fast));
    }

    @GetMapping("close/{id}")
    @SimpleLog("停用快捷菜单配信")
    @PreAuthorize("hasAnyAuthority('shop:fast:openorclose')")
    public CommonResult closeMain(@PathVariable String id) {
        Fast fast = fastService.getById(id);
        fast.setStatus("0");
        return CommonResult.success(fastService.updateById(fast));
    }


}