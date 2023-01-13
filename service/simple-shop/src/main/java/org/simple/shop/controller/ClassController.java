package org.simple.shop.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.simple.common.utils.CommonResult;
import org.simple.common.utils.RedomUtil;
import org.simple.security.annotation.SimpleLog;
import org.simple.security.utils.AuthUtils;
import org.simple.shop.entity.Class;
import org.simple.shop.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:34:57
 * @Author: frsimple
 */

@RestController
@RequestMapping("/class")
public class ClassController {

    @Autowired
    private ClassService classService;


    @GetMapping("list")
    @SimpleLog("查询商品分类信息表")
    @PreAuthorize("hasAnyAuthority('shop:class:query')")
    public CommonResult list(Page page, Class shopClass) {
        shopClass.setTenantid(AuthUtils.getUser().getTenantId());
        return CommonResult.success(classService.page(page, Wrappers.query(shopClass)));
    }


    @GetMapping("listTree")
    @SimpleLog("查询分类树形")
    @PreAuthorize("hasAnyAuthority('shop:class:query')")
    public CommonResult listTree() {
        return CommonResult.success(classService.queryClassTree());
    }


    @PostMapping("edit")
    @SimpleLog("修改分类信息")
    @PreAuthorize("hasAnyAuthority('shop:class:edit')")
    public CommonResult edit(@RequestBody Class shopClass) {
        return CommonResult.success(classService.updateById(shopClass));
    }

    @PostMapping("add")
    @SimpleLog("新增分类信息")
    @PreAuthorize("hasAnyAuthority('shop:class:add')")
    public CommonResult add(@RequestBody Class shopClass) {
        shopClass.setId(RedomUtil.getFlUuid());
        shopClass.setCreatetime(LocalDateTime.now());
        shopClass.setTenantid(AuthUtils.getUser().getTenantId());
        return CommonResult.success(classService.save(shopClass));
    }


    @DeleteMapping("del/{id}")
    @SimpleLog("删除分类信息")
    @PreAuthorize("hasAnyAuthority('shop:class:del')")
    public CommonResult del(@PathVariable("id") String id) {
        Class shopClass = new Class();
        shopClass.setParentid(id);
        if (classService.list(Wrappers.query(shopClass)).size() != 0) {
            return CommonResult.failed("存在关联的下级分类，请先删除所有下级分类");
        }
        return CommonResult.success(classService.removeById(id));
    }


}