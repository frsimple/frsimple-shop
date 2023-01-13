package org.simple.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.simple.common.utils.CommonResult;
import org.simple.security.utils.AuthUtils;
import org.simple.shop.entity.Info;
import org.simple.shop.mapper.InfoMapper;
import org.simple.shop.service.InfoService;
import org.springframework.stereotype.Service;

/**
 * @Copyright: simple
 * @Desc:
 * @Date: 2022-09-10 21:37:22
 * @Author: frsimple
 */


@Service
public class InfoServiceImpl
        extends ServiceImpl<InfoMapper, Info>
        implements InfoService {

    @Override
    public CommonResult queryShopCount() {
        Integer onPro = baseMapper.queryOnProduct(AuthUtils.getUser().getTenantId());
        Integer allPro = baseMapper.queryAllProduct(AuthUtils.getUser().getTenantId());
        Integer closePro = baseMapper.queryCloseProduct(AuthUtils.getUser().getTenantId());
        Integer delPro = baseMapper.queryDelProduct(AuthUtils.getUser().getTenantId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("on",onPro);
        jsonObject.put("close",closePro);
        jsonObject.put("all",allPro);
        jsonObject.put("del",delPro);
        return CommonResult.success(jsonObject);
    }
}