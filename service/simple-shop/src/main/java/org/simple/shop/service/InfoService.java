package org.simple.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.simple.common.utils.CommonResult;
import org.simple.shop.entity.Info;


/**
 * @Copyright: simple
 * @Desc:
 * @Date: 2022-09-10 21:37:22
 * @Author: frsimple
 */
public interface InfoService extends IService<Info> {

    //统计商品总量
    CommonResult queryShopCount();
}