package org.simple.shop.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.hutool.core.util.DesensitizedUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang.StringUtils;
import org.simple.common.storage.OssUtil;
import org.simple.common.utils.ComUtil;
import org.simple.common.utils.CommonResult;
import org.simple.security.utils.AuthUtils;
import org.simple.shop.config.WxPayConfig;
import org.simple.shop.dto.WeChatPhoneDto;
import org.simple.shop.entity.Reinfo;
import org.simple.shop.entity.User;
import org.simple.shop.service.ReinfoService;
import org.simple.shop.service.OrderService;
import org.simple.shop.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/wechat/user")
public class WeChatUserController {


    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private OrderService orderService;

    @Resource
    private ReinfoService reinfoService;

    /**
     * 查询用户信息
     */
    @GetMapping("info")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult getUser() {
        User shopUser =
                userService.getById(AuthUtils.getUser().getId());
        shopUser.setPhone(StringUtils.isNotEmpty(shopUser.getPhone()) ?
                DesensitizedUtil.mobilePhone(shopUser.getPhone()) : shopUser.getPhone());
        return CommonResult.success(shopUser);
    }

    /**
     * 修改用户头像
     */
    @PostMapping("upAvatar")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult upAvatar(@RequestParam("file") MultipartFile file) {
        File file1 = ComUtil.MultipartToFile(file);
        //上传图片
        CommonResult result =
                OssUtil.getAliOss(redisTemplate).fileUpload(file1, false, AuthUtils.getUser().getId());
        if (result.getCode() == 0) {
            User s = new User();
            s.setId(AuthUtils.getUser().getId());
            s.setAvatar(result.getData().toString());
            userService.updateById(s);
            return CommonResult.successNodata(result.getData().toString());
        } else {
            return CommonResult.failed(result.getMsg());
        }
    }

    /**
     * 修改用户昵称
     */
    @PostMapping("upName")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult upName(@RequestBody User shopUser) {
        User s = new User();
        s.setName(shopUser.getName());
        s.setId(AuthUtils.getUser().getId());
        userService.updateById(s);
        return CommonResult.success(s);
    }

    /**
     * 绑定用户手机号
     */
    @PostMapping("/upPhone")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult upPhone(@RequestBody WeChatPhoneDto weChatPhoneDto) {
        WxMaDefaultConfigImpl wxMaConfig = new WxMaDefaultConfigImpl();
        wxMaConfig.setAppid(WxPayConfig.APPID);
        wxMaConfig.setSecret(WxPayConfig.APPSECRET);
        wxMaConfig.setMsgDataFormat("JSON");
        WxMaService wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(wxMaConfig);
        String phoneNum = "";
        try {
            WxMaJscode2SessionResult jscode2session =
                    wxMaService.jsCode2SessionInfo(weChatPhoneDto.getSessionKey());
            String sessionk = jscode2session.getSessionKey();
            WxMaPhoneNumberInfo wxMaPhoneNumberInfo
                    = wxMaService.getUserService().getPhoneNoInfo(sessionk
                    , weChatPhoneDto.getEncryptedData()
                    , weChatPhoneDto.getIvStr());
            phoneNum = wxMaPhoneNumberInfo.getPhoneNumber();
            //更新手机号信息
            User user = new User();
            user.setId(AuthUtils.getUser().getId());
            user.setPhone(phoneNum);
            userService.updateById(user);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.failed(e.getMessage());
        }
        return CommonResult.success(DesensitizedUtil.mobilePhone(phoneNum));
    }

    /**
     * 查询用户订单状态总数
     */
    @GetMapping("orderCount")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult queryOrderCount() {
        return orderService.queryOrderCount();
    }

    /**
     * 查询收货地址列表
     */
    @GetMapping("queryRevAddress")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult queryRevAddress() {
        Reinfo reinfo = new Reinfo();
        reinfo.setUserid(AuthUtils.getUser().getId());
        List<Reinfo> reinfoList = reinfoService.list(Wrappers.query(reinfo).orderByDesc("is_default","id"));
        reinfoList = reinfoList.stream().map(obj ->{
            obj.setRphone(DesensitizedUtil.mobilePhone(obj.getRphone()));
           return obj;
        }).collect(Collectors.toList());
        return CommonResult.success(reinfoList);
    }

    /**
     * 新增收货地址
     */
    @PostMapping("addRevAddress")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult addRevAddress(@RequestBody Reinfo reinfo) {
        if(reinfoService.list().size() >= 20){
             return CommonResult.failed("最多添加20个地址");
        }
        if(StringUtils.isNotEmpty(reinfo.getIsDefault())&&reinfo.getIsDefault().equals("1")){
            reinfoService.updateIsDefault();
        }
        reinfo.setUserid(AuthUtils.getUser().getId());
        return CommonResult.success(reinfoService.save(reinfo));
    }

    /**
     * 修改收货地址
     */
    @PostMapping("editRevAddress")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult editRevAddress(@RequestBody Reinfo reinfo) {
        reinfo.setUserid(AuthUtils.getUser().getId());
        if(StringUtils.isNotEmpty(reinfo.getIsDefault())&&reinfo.getIsDefault().equals("1")){
            reinfoService.updateIsDefault();
        }
        return CommonResult.success(reinfoService.updateById(reinfo));
    }

    /**
     * 删除收货地址
     */
    @GetMapping("delRevAddress/{id}")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult delRevAddress(@PathVariable("id") String id) {
        reinfoService.removeById(id);
        return CommonResult.successNodata("删除成功");
    }

    /**
     * 查询单个收货地址
     */
    @GetMapping("getRevAddress/{id}")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult getRevAddress(@PathVariable("id") String id) {
        return CommonResult.success(reinfoService.getById(id));
    }
}