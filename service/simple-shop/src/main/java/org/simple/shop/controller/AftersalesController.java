package org.simple.shop.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import org.apache.commons.lang.StringUtils;
import org.simple.common.utils.CommonResult;
import org.simple.common.utils.RedomUtil;
import org.simple.security.annotation.SimpleLog;
import org.simple.shop.dto.AfterSalesDto;
import org.simple.shop.dto.WxPayParams;
import org.simple.shop.entity.Aftersales;
import org.simple.shop.entity.Order;
import org.simple.shop.entity.Trans;
import org.simple.shop.service.AftersalesService;
import org.simple.shop.service.OrderService;
import org.simple.shop.service.UserService;
import org.simple.shop.service.TransService;
import org.simple.shop.utils.WxPayUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Copyright: simple
 * @Date: 2023-01-06 15:37:34
 * @Author: frsimple
 */

@RestController
@RequestMapping("/aftersales")
public class AftersalesController {

    @Resource
    private AftersalesService aftersalesService;

    @Resource
    private OrderService orderService;

    @Resource
    private UserService userService;

    @Resource
    private TransService transService;


    @GetMapping("list")
    @SimpleLog("查询售后申请列表")
    @PreAuthorize("hasAnyAuthority('shop:orderservice:query')")
    public CommonResult list(Page page, Aftersales aftersales) {
        if(StringUtils.isEmpty(aftersales.getId())){
            aftersales.setId(null);
        }
        IPage pageList = aftersalesService.page(page, Wrappers.query(aftersales));
        List<Aftersales> list = pageList.getRecords();
        List<AfterSalesDto> result = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(list)){
             list.stream().forEach(obj -> {
                 AfterSalesDto dto = new AfterSalesDto();
                 BeanUtil.copyProperties(obj,dto);
                 dto.setOrder(orderService.getById(dto.getOrderId()));
                 dto.setUser(userService.getById(dto.getUserid()));
                 result.add(dto);
             });
        }
        pageList.setRecords(result);
        return CommonResult.success(pageList);
    }

    /**
     * 修改运单号
     * */
    @PostMapping("editApplyExpress")
    @SimpleLog("修改运单号")
    @PreAuthorize("hasAnyAuthority('shop:orderservice:edit')")
    public CommonResult editApplyExpress(@RequestBody Aftersales aftersales) {
        return CommonResult.success(aftersalesService.updateById(aftersales));
    }

    /**
     * 撤销申请售后
     * */
    @GetMapping("delApplyService/{id}")
    @SimpleLog("撤销售后申请订单")
    @PreAuthorize("hasAnyAuthority('shop:orderservice:del')")
    public CommonResult delApplyService(@PathVariable("id")String id) {
        Aftersales aftersales = new Aftersales();
        aftersales.setId(id);
        aftersales.setStatus("-1");
        aftersalesService.updateById(aftersales);
        //同时删除订单表中的关联状态
        orderService.updateOrderApplyService(aftersalesService.getById(id).getOrderId());
        return CommonResult.successNodata("撤回成功");
    }

    /**
     * 审核
     * */
    @PostMapping("checkApplyService")
    @SimpleLog("审核售后单子")
    @PreAuthorize("hasAnyAuthority('shop:orderservice:check')")
    public CommonResult checkApplyService(@RequestBody Aftersales aftersales) {
        aftersales.setStatus("99");
        Aftersales a =  aftersalesService.getById(aftersales.getId());
        Order order = orderService.getById(a.getOrderId());
        Trans trans = transService.getById(order.getPayId());
        if(aftersales.getResult().equals("1")){
            //先退款，退款成功之后在进行后续操作
            String refNo = RedomUtil.getUuid();
            WxPayParams params = new WxPayParams();
            params.setOrderId(order.getPayId());
            params.setOutRefundNo(refNo);
            params.setRefundPrice(a.getPrice());
            params.setPayMoney(new BigDecimal(
                    StringUtils.isNotEmpty(order.getNprice())?order.getNprice():
                            order.getPrice()
            ));
            CommonResult refResult =
                    WxPayUtil.getInstance().transJsapiRef(params);
            JSONArray refJson = trans.getReffund();
            if (refResult.getCode() == 0) {
                WxPayRefundV3Result wxResult =
                        (WxPayRefundV3Result) refResult.getData();
                if (wxResult.getStatus().equals("SUCCESS")) {
                    JSONObject refObj = new JSONObject();
                    refObj.put("refNo", refNo);
                    refObj.put("isReceived", "0");
                    refJson.add(refObj);
                } else {
                    return CommonResult.failed("订单取消失败:退款异常");
                }
            } else {
                return CommonResult.failed(refResult.getMsg());
            }
            trans.setReffund(refJson);
            transService.updateById(trans);
            aftersalesService.updateById(aftersales);
        }else if(aftersales.getResult().equals("0")){ //审核不通过
          aftersalesService.updateById(aftersales);
        }
        return CommonResult.successNodata("撤回成功");
    }
}