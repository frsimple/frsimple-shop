package org.simple.shop.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.simple.common.storage.OssUtil;
import org.simple.common.utils.ComUtil;
import org.simple.common.utils.CommonResult;
import org.simple.common.utils.RedomUtil;
import org.simple.security.annotation.SimpleLog;
import org.simple.security.utils.AuthUtils;
import org.simple.shop.entity.Info;
import org.simple.shop.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;

/**
 * @Copyright: simple
 * @Desc:
 * @Date: 2022-09-10 21:37:22
 * @Author: frsimple
 */

@RestController
@RequestMapping("/info")
public class InfoController {

    @Autowired
    private InfoService infoService;

    @Autowired
    private RedisTemplate redisTemplate;


    @GetMapping("list")
    @SimpleLog("查询商品信息")
    @PreAuthorize("hasAnyAuthority('shop:info:query')")
    public CommonResult list(Page page, Info shopinfo) {
        shopinfo.setTenantid(AuthUtils.getUser().getTenantId());
        if (StringUtils.isNotBlank(shopinfo.getName())) {
            String name = shopinfo.getName();
            shopinfo.setName(null);
            return CommonResult.success(infoService.page(
                    page, Wrappers.query(shopinfo).in("status", "00", "01").like("name", name)));
        } else {
            return CommonResult.success(infoService.page(page, Wrappers.query(shopinfo).in("status", "00", "01")));
        }
    }

    @GetMapping("infoCount")
    @SimpleLog("查询商品总量")
    @PreAuthorize("hasAnyAuthority('shop:info:query')")
    public CommonResult infoCount() {
        return infoService.queryShopCount();
    }

    @GetMapping("delList")
    @SimpleLog("查询已删除的商品信息")
    @PreAuthorize("hasAnyAuthority('shop:info:query')")
    public CommonResult delList(Page page, Info shopinfo) {
        shopinfo.setTenantid(AuthUtils.getUser().getTenantId());
        return CommonResult.success(infoService.page(page, Wrappers.query(shopinfo).in("status", "-1")));
    }

    @PostMapping("add")
    @SimpleLog("新增商品")
    @PreAuthorize("hasAnyAuthority('shop:info:add')")
    public CommonResult add(@RequestBody Info shopinfo) {
        shopinfo.setId(RedomUtil.getProductId());
        shopinfo.setEditor(AuthUtils.getUser().getId());
        shopinfo.setCreatetime(LocalDateTime.now());
        shopinfo.setUpdatetime(LocalDateTime.now());
        shopinfo.setCollection(new Integer("0"));
        shopinfo.setSales(new Integer("0"));
        shopinfo.setTenantid(AuthUtils.getUser().getTenantId());
        try {
            infoService.save(shopinfo);
            return CommonResult.successNodata("保存成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            return CommonResult.failed(ex.getMessage());
        }
    }

    @PostMapping("edit")
    @SimpleLog("修改商品")
    @PreAuthorize("hasAnyAuthority('shop:info:edit')")
    public CommonResult edit(@RequestBody Info shopinfo) {
        shopinfo.setUpdatetime(LocalDateTime.now());
        return CommonResult.success(infoService.updateById(shopinfo));
    }

    @DeleteMapping("del/{id}")
    @SimpleLog("删除商品")
    @PreAuthorize("hasAnyAuthority('shop:info:del')")
    public CommonResult edit(@PathVariable("id") String id) {
        return CommonResult.success(infoService.removeById(id));
    }


    @PostMapping("uploadImgs")
    //@SimpleLog("上传商品图片")
    @PreAuthorize("hasAnyAuthority('shop:info:add')")
    public CommonResult updateAvatar(@RequestParam("file") MultipartFile file) {
        File file1 = ComUtil.MultipartToFile(file);
        //上传图片
        CommonResult result =
                OssUtil.getAliOss(redisTemplate).fileUpload(file1, false, AuthUtils.getUser().getId());
        if (result.getCode() == 0) {
            return CommonResult.successNodata(result.getData().toString());
        } else {
            return result;
        }
    }


    @GetMapping("upProduct/{id}")
    @SimpleLog("恢复商品到待上架状态")
    @PreAuthorize("hasAnyAuthority('shop:info:add')")
    public CommonResult upProduct(@PathVariable("id") String id) {
        if(!infoService.getById(id).getStatus().equals("-1")){
            return CommonResult.failed("只能恢复删除状态的商品");
        }
        Info shopinfo = new Info();
        shopinfo.setId(id);
        shopinfo.setStatus("00");
        infoService.updateById(shopinfo);
        return CommonResult.success("修改成功");
    }

    @GetMapping("delProduct/{id}")
    @SimpleLog("删除商品")
    @PreAuthorize("hasAnyAuthority('shop:info:add')")
    public CommonResult delProduct(@PathVariable("id") String id) {
        if(!infoService.getById(id).getStatus().equals("00")){
            return CommonResult.failed("只能删除待上架状态的商品");
        }
        Info shopinfo = new Info();
        shopinfo.setId(id);
        shopinfo.setStatus("-1");
        infoService.updateById(shopinfo);
        return CommonResult.success("修改成功");
    }

    @GetMapping("upProduct1/{id}")
    @SimpleLog("上架商品")
    @PreAuthorize("hasAnyAuthority('shop:info:add')")
    public CommonResult upProduct1(@PathVariable("id") String id) {
        if(!infoService.getById(id).getStatus().equals("00")){
            return CommonResult.failed("只能上架待上架的商品");
        }
        Info s = new Info();
        s.setId(id);
        s.setStatus("01");
        infoService.updateById(s);
        return CommonResult.success("操作成功");
    }

    @GetMapping("downProduct/{id}")
    @SimpleLog("下架商品")
    @PreAuthorize("hasAnyAuthority('shop:info:add')")
    public CommonResult downProduct(@PathVariable("id") String id) {
        if(!infoService.getById(id).getStatus().equals("01")){
            return CommonResult.failed("只能下架已上架的商品");
        }
        Info s = new Info();
        s.setId(id);
        s.setStatus("00");
        infoService.updateById(s);
        return CommonResult.success("操作成功");
    }

}