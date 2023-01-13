package org.simple.shop.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.simple.common.utils.CommonResult;
import org.simple.common.utils.RedomUtil;
import org.simple.security.annotation.SimpleLog;
import org.simple.security.utils.AuthUtils;
import org.simple.shop.entity.Message;
import org.simple.shop.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:36:13
 * @Author: frsimple
 */

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;


    @GetMapping("list")
    @SimpleLog("查询消息通知")
    @PreAuthorize("hasAnyAuthority('shop:message:query')")
    public CommonResult list(Page page, Message shopMessage) {
        shopMessage.setTenantid(AuthUtils.getUser().getTenantId());
        return CommonResult.success(messageService.page(page, Wrappers.query(shopMessage)));
    }

    @PostMapping("editMessage")
    @SimpleLog("修改消息通知")
    @PreAuthorize("hasAnyAuthority('shop:message:edit')")
    public CommonResult editMessage(@RequestBody Message shopMessage) {
        return CommonResult.success(messageService.updateById(shopMessage));
    }

    @PostMapping("addMessage")
    @SimpleLog("新增消息通知")
    @PreAuthorize("hasAnyAuthority('shop:message:add')")
    public CommonResult addMessage(@RequestBody Message shopMessage) {
        shopMessage.setId(RedomUtil.getUuid());
        shopMessage.setCreatetime(LocalDateTime.now());
        shopMessage.setTenantid(AuthUtils.getUser().getTenantId());
        return CommonResult.success(messageService.save(shopMessage));
    }

    @GetMapping("delMessage/{id}")
    @SimpleLog("删除消息通知")
    @PreAuthorize("hasAnyAuthority('shop:message:del')")
    public CommonResult delMessage(@PathVariable String id) {
        return CommonResult.success(messageService.removeById(id));
    }

    @GetMapping("open/{id}")
    @SimpleLog("启用消息通知")
    @PreAuthorize("hasAnyAuthority('shop:message:openorclose')")
    public CommonResult openMessage(@PathVariable String id) {
        //先把所有状态更新为停用状态
        return messageService.updateStatusToClose(id);
    }

    @GetMapping("close/{id}")
    @SimpleLog("启用消息通知")
    @PreAuthorize("hasAnyAuthority('shop:message:openorclose')")
    public CommonResult closeMessage(@PathVariable String id) {
        Message shopMessage = new Message();
        shopMessage.setId(id);
        shopMessage.setStatus("0");
        try {
            messageService.updateById(shopMessage);
            return CommonResult.successNodata("停用成功");
        } catch (Exception ex) {
            return CommonResult.failed("停用失败!");
        }
    }


}