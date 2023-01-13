package org.simple.shop.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import me.chanjar.weixin.common.error.WxErrorException;
import org.simple.common.utils.CommonResult;
import org.simple.common.utils.RedomUtil;
import org.simple.shop.config.WxPayConfig;
import org.simple.shop.entity.User;
import org.simple.shop.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/inward")
public class InwardController {
    @Resource
    private UserService userService;

    /**
     * 根据openid查询用户信息
     *
     * @return 用户信息
     */
    @PostMapping("/user")
    public CommonResult info(@RequestBody JSONObject object) {
        JSONObject jsonObject = new JSONObject();
        //先查询是否存在该用户，若不存在则进行新增操作
        String openid = "";
        String uniopenId = "";
        WxMaService wxMaService = new WxMaServiceImpl();
        WxMaDefaultConfigImpl wxMaConfig = new WxMaDefaultConfigImpl();
        wxMaConfig.setAppid(WxPayConfig.APPID);
        wxMaConfig.setSecret(WxPayConfig.APPSECRET);
        wxMaService.setWxMaConfig(wxMaConfig);
        try {
            WxMaJscode2SessionResult jscode2session = wxMaService.jsCode2SessionInfo(object.getStr("authcode"));
            openid = jscode2session.getOpenid();
            uniopenId = jscode2session.getUnionid();
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        //根据openid来查询微信用户信息
        User shopUser = new User();
        shopUser.setOpenid(openid);
        shopUser.setUniopenid(uniopenId);
        List<User> list =
                userService.list(Wrappers.query(shopUser));
        if (list.size() == 0) {
            shopUser.setId(RedomUtil.getWxUserid());
            shopUser.setCreatetime(LocalDateTime.now());
            shopUser.setName(object.getStr("username"));
            shopUser.setAvatar(object.getStr("avatar"));
            shopUser.setPhone("");
            shopUser.setDatasource("01");
            userService.save(shopUser);
            jsonObject.set("userid", shopUser.getId());
            jsonObject.set("username", shopUser.getName());
        } else {
            shopUser = list.get(0);
//            shopUser.setAvatar(object.getStr("avatar"));
//            shopUser.setName(object.getStr("username"));
//            shopUserService.updateById(shopUser);
            jsonObject.set("userid", shopUser.getId());
            jsonObject.set("username", shopUser.getName());
        }
        return CommonResult.success(jsonObject);
    }

}
