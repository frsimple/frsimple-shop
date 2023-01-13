package org.simple.shop.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.simple.common.utils.CommonResult;
import org.simple.shop.dto.ShopInfoDto;
import org.simple.shop.entity.Fast;
import org.simple.shop.entity.Mainimg;
import org.simple.shop.entity.Message;
import org.simple.shop.entity.Info;
import org.simple.shop.service.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/wechat/home")
public class WeChatHomeController {

    @Resource
    private MainimgService mainimgService;

    @Resource
    private MessageService shopMessageService;

    @Resource
    private FastService fastService;

    @Resource
    private InfoService infoService;

    @Resource
    private ClassService classService;

    @Resource
    private RatetemService ratetemService;

    @Resource
    private CommonService commonService;



    /**
     * 查询轮播图
     */
    @GetMapping("/api/mainImg")
    public CommonResult getMainImg() {
        Mainimg shopMainimg = new Mainimg();
        shopMainimg.setStatus("1");
        return CommonResult.success(mainimgService.list(Wrappers.query(shopMainimg)));
    }

    /**
     * 查询通知公告
     */
    @GetMapping("/api/msg")
    public CommonResult getMsg() {
        Message shopMessage = new Message();
        shopMessage.setStatus("1");
        return CommonResult.success(shopMessageService.list(Wrappers.query(shopMessage)));
    }

    /**
     * 查询快捷菜单
     */
    @GetMapping("/api/fast")
    public CommonResult getFast() {
        Fast fast = new Fast();
        fast.setStatus("1");
        return CommonResult.success(fastService.list(Wrappers.query(fast)));
    }

    /**
     * 查询好物推荐列表
     */
    @GetMapping("/api/goodShop")
    public CommonResult goodShop(Page page) {
        Info shopinfo = new Info();
        shopinfo.setIsgood("1");
        shopinfo.setStatus("01");
        return CommonResult.success(infoService.page(page, Wrappers.query(shopinfo)));
    }


    /**
     * 查询商品详情
     */
    @GetMapping("/api/good/{id}")
    public CommonResult goodShop(@PathVariable("id")String id) {
        Info shopinfo =  infoService.getById(id) ;
        ShopInfoDto shopInfoDto = new ShopInfoDto();
        BeanUtil.copyProperties(shopinfo,shopInfoDto);
        shopInfoDto.setRateTemInfo(ratetemService.getById(shopinfo.getRateTem()));
        if(null == shopinfo){
            return CommonResult.failed("商品不存在/已下架");
        }
        if(!shopinfo.getStatus().equals("01")){
            return CommonResult.failed("商品不存在/已下架");
        }
        return CommonResult.success(shopInfoDto);
    }


    /**
     *  查询分类树形信息
     */
    @GetMapping("/api/classTree")
    public CommonResult listTree() {
        return CommonResult.success(classService.queryClassTree());
    }


    /**
     * 搜索热词
     * */
    @GetMapping("/api/hotSearch")
    public CommonResult hotSearch(){
        return CommonResult.success(commonService.selectHotSearch());
    }

}
