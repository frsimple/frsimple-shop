package org.simple.shop.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.simple.common.utils.CommonResult;
import org.simple.common.utils.RedomUtil;
import org.simple.security.annotation.SimpleLog;
import org.simple.security.utils.AuthUtils;
import org.simple.shop.entity.Mainimg;
import org.simple.shop.service.CommonService;
import org.simple.shop.service.MainimgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;


/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:35:44
 * @Author: frsimple
 */

@RestController
@RequestMapping("/mainimg")
public class MainimgController {

    @Autowired
    private MainimgService mainimgService;

    @Resource
    private CommonService  commonService;


    @GetMapping("list")
    @SimpleLog("查询首页轮播图")
    @PreAuthorize("hasAnyAuthority('shop:main:query')")
    public CommonResult list(Page page, Mainimg shopMainimg) {
        shopMainimg.setTenantid(AuthUtils.getUser().getTenantId());
        if (StringUtils.isNotEmpty(shopMainimg.getUrl())) {
            String url = shopMainimg.getUrl();
            shopMainimg.setUrl(null);
            return CommonResult.success(mainimgService.page(page, Wrappers.query(shopMainimg).like("url", url)));
        } else {
            return CommonResult.success(mainimgService.page(page, Wrappers.query(shopMainimg)));
        }

    }

    @PostMapping("editMain")
    @SimpleLog("修改首页轮播图")
    @PreAuthorize("hasAnyAuthority('shop:main:edit')")
    public CommonResult editMain(@RequestBody Mainimg shopMainimg) {
        return CommonResult.success(mainimgService.updateById(shopMainimg));
    }

    @PostMapping("addMain")
    @SimpleLog("新增首页轮播图")
    @PreAuthorize("hasAnyAuthority('shop:main:add')")
    public CommonResult addMain(@RequestBody Mainimg shopMainimg) {
        shopMainimg.setId(RedomUtil.getUuid());
        shopMainimg.setCreatetime(LocalDateTime.now());
        shopMainimg.setTenantid(AuthUtils.getUser().getTenantId());
        return CommonResult.success(mainimgService.save(shopMainimg));
    }

    @GetMapping("delMain/{id}")
    @SimpleLog("删除首页轮播图")
    @PreAuthorize("hasAnyAuthority('shop:main:del')")
    public CommonResult delMain(@PathVariable String id) {
        return CommonResult.success(mainimgService.removeById(id));
    }

    @GetMapping("open/{id}")
    @SimpleLog("启用首页轮播图")
    @PreAuthorize("hasAnyAuthority('shop:main:openorclose')")
    public CommonResult openMain(@PathVariable String id) {
        Mainimg shopMainimg = mainimgService.getById(id);
        shopMainimg.setStatus("1");
        return CommonResult.success(mainimgService.updateById(shopMainimg));
    }

    @GetMapping("close/{id}")
    @SimpleLog("停用首页轮播图")
    @PreAuthorize("hasAnyAuthority('shop:main:openorclose')")
    public CommonResult closeMain(@PathVariable String id) {
        Mainimg shopMainimg = mainimgService.getById(id);
        shopMainimg.setStatus("0");
        return CommonResult.success(mainimgService.updateById(shopMainimg));
    }
    @PostMapping("get/hotSearch")
    @SimpleLog("设置搜索热词")
    @PreAuthorize("hasAnyAuthority('shop:main:openorclose')")
    public CommonResult getHotSearch(){
        return CommonResult.success(commonService.selectHotSearch());
    }

    @PostMapping("set/hotSearch")
    @SimpleLog("设置搜索热词")
    @PreAuthorize("hasAnyAuthority('shop:main:openorclose')")
    public CommonResult setHotSearch(@RequestBody JSONObject jsonObject){
        return commonService.updateOrInsertHotSearch(jsonObject.getString("content"));
    }
}