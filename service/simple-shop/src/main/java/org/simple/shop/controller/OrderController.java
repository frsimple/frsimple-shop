package org.simple.shop.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.simple.common.utils.CommonResult;
import org.simple.common.utils.RedomUtil;
import org.simple.security.annotation.SimpleLog;
import org.simple.shop.constant.ShopContant;
import org.simple.shop.dto.MarkDto;
import org.simple.shop.dto.OrderParams;
import org.simple.shop.dto.WxPayParams;
import org.simple.shop.entity.*;
import org.simple.shop.service.*;
import org.simple.shop.utils.WxPayUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:36:30
 * @Author: frsimple
 */

@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private TransService transService;

    @Resource
    private UserService userService;

    @Resource
    private OrderDetailsService orderDetailsService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private MarkService markService;


    @GetMapping("list")
    @SimpleLog("查询订单信息表")
    @PreAuthorize("hasAnyAuthority('shop:orderlist:query')")
    public CommonResult list(Page page, OrderParams params) {
        return CommonResult.success(orderService.queryOrders(page, params));
    }

    /**
     * 查询订单操作流水信息
     */
    @GetMapping("orderDetails/{id}")
    @SimpleLog("查询订单操作流水信息")
    @PreAuthorize("hasAnyAuthority('shop:orderlist:query')")
    public CommonResult orderDetails(@PathVariable("id") String id) {
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrderId(id);
        return CommonResult.success(orderDetailsService.list(Wrappers.query(orderDetails).orderByDesc("create_time")));
    }

    @GetMapping("dataCount")
    @SimpleLog("统计订单数量")
    @PreAuthorize("hasAnyAuthority('shop:orderlist:query')")
    public CommonResult dataCount() {
        return orderService.queryDataOrder();
    }

    @PostMapping("editOrderPrice")
    @SimpleLog("修改订单金额")
    @PreAuthorize("hasAnyAuthority('shop:orderlist:query')")
    public CommonResult editOrderPrice(@RequestBody Order shopOrder) {
        //修改付款信息
        String Uuid = RedomUtil.getTransId();
        Order order = orderService.getById(shopOrder.getId());
        Trans trans = transService.getById(order.getPayId());
        shopOrder.setPayId(Uuid);
        trans.setId(Uuid);
        trans.setSmoney(shopOrder.getNprice());
        trans.setMoney(shopOrder.getNprice());
        //调用微信接口返回预支付订单ID
        WxPayParams wxPayParams = new WxPayParams();
        wxPayParams.setBody("商品[" + order.getGoods()
                .getJSONObject(0).getString("goodsName") + "]");
        wxPayParams.setPayMoney(new BigDecimal(shopOrder.getNprice()));
        wxPayParams.setOrderId(Uuid);
        wxPayParams.setOpenId(userService.getById(order.getUserid()).getOpenid());
        CommonResult result =
                WxPayUtil.getInstance().transJsapi(wxPayParams);
        if (result.getCode() != 0) {
            return CommonResult.failed(result.getMsg());
        }
        JSONObject payJson = (JSONObject) result.getData();
        trans.setPayJson(payJson);
        trans.setCreateTime(LocalDateTime.now());
        transService.save(trans);
        orderService.updateById(shopOrder);
        //记录修改流水
        //添加订单流水信息
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCreateTime(LocalDateTime.now());
        orderDetails.setOrderId(shopOrder.getId());
        orderDetails.setType("88");//修改订单金额
        orderDetails.setRemark("修改订单金额：原金额：" + order.getPrice() + "元，修改为：" + shopOrder.getNprice() + "元");
        orderDetailsService.save(orderDetails);
        return CommonResult.success("修改成功");
    }

    /**
     * 订单发货
     */
    @PostMapping("orderSend")
    @SimpleLog("订单发货")
    @PreAuthorize("hasAnyAuthority('shop:orderlist:query')")
    public CommonResult orderSend(@RequestBody Order order) {
        Order o = orderService.getById(order.getId());
        if (!o.getStatus().equals("01")) {
            return CommonResult.failed("非法操作");
        }
        o.setExpress(order.getExpress());
        o.setStatus("02");
        orderService.updateById(o);
        //添加订单流水信息
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCreateTime(LocalDateTime.now());
        orderDetails.setOrderId(order.getId());
        orderDetails.setType("02");//订单已发货
        orderDetails.setRemark("订单已发货");
        orderDetailsService.save(orderDetails);

        //添加redis，15天自动收货操作。
        redisTemplate.opsForValue().set(ShopContant.SHOP_ORDER_TASKOVERKEY + order.getId(),
                order.getId(), ShopContant.SHOP_ORDER_EXPIRE_TASKOVER, TimeUnit.DAYS);
        return CommonResult.successNodata("发货成功");
    }


    /**
     * 修改订单号
     */
    @PostMapping("editOrderExpress")
    @SimpleLog("修改订单号")
    @PreAuthorize("hasAnyAuthority('shop:orderlist:query')")
    public CommonResult editOrderExpress(@RequestBody Order order) {
        Order o = orderService.getById(order.getId());
        if (!o.getStatus().equals("02")) {
            return CommonResult.failed("非法操作");
        }
        o.setExpress(order.getExpress());
        orderService.updateById(o);
        return CommonResult.successNodata("订单号修改成功");
    }

    /**
     * 确定收货
     */
    @GetMapping("confirmOrder/{id}")
    @SimpleLog("确定收货")
    @PreAuthorize("hasAnyAuthority('shop:orderlist:query')")
    public CommonResult confirmOrder(@PathVariable("id") String id) {
        Order o = orderService.getById(id);
        if (!o.getStatus().equals("02")) {
            return CommonResult.failed("非法操作");
        }
        o.setStatus("03");
        orderService.updateById(o);
        //添加订单流水信息
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCreateTime(LocalDateTime.now());
        orderDetails.setOrderId(id);
        orderDetails.setType("03");//系统确认收货
        orderDetails.setRemark("系统确认收货");
        orderDetailsService.save(orderDetails);

        //添加redis，5天自动好评。
        redisTemplate.opsForValue().set(ShopContant.SHOP_ORDER_COMMENT + id,
                id, ShopContant.SHOP_ORDER_EXPIRE_COMMENT, TimeUnit.DAYS);
        return CommonResult.successNodata("订单确认收货成功");
    }

    @GetMapping("/markList/{id}")
    @SimpleLog("订单评价查询")
    @PreAuthorize("hasAnyAuthority('shop:orderlist:query')")
    public CommonResult markList(@PathVariable("id") String id) {
        Mark mark = new Mark();
        mark.setStatus("1");
        mark.setOrderid(id);
        mark.setParentid("0");
        mark.setOrderid(id);
        List<Mark> list =  markService.list(Wrappers.query(mark).orderByDesc("create_time"));
        List<MarkDto> result = new ArrayList<MarkDto>();
        if (CollectionUtil.isNotEmpty(list)) {
            result =
                    list.stream().map(obj -> {
                        MarkDto markDto = new MarkDto();
                        User shopUser = userService.getById(obj.getUserid());
                        BeanUtil.copyProperties(obj, markDto);
                        markDto.setAvatar(shopUser.getAvatar());
                        markDto.setNickName(shopUser.getName());
                        Mark m = new Mark();
                        m.setParentid(obj.getId());
                        markDto.setMarkList(markService.list(Wrappers.query(m)));
                        return markDto;
                    }).collect(Collectors.toList());
        }
        return CommonResult.success(result);
    }
}