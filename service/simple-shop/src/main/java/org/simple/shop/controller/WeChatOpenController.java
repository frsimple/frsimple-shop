package org.simple.shop.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.exception.WxPayException;
import org.simple.shop.entity.OrderDetails;
import org.simple.shop.entity.Order;
import org.simple.shop.entity.Trans;
import org.simple.shop.service.OrderDetailsService;
import org.simple.shop.service.OrderService;
import org.simple.shop.service.TransService;
import org.simple.shop.utils.WxPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/wechat/open")
public class WeChatOpenController {

    @Resource
    private OrderService orderService;

    @Resource
    private OrderDetailsService orderDetailsService;

    @Resource
    private TransService transService;
    final Logger log = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/api/jsapi")
    public ResponseEntity returnTransJsApi(@RequestBody String body, HttpServletRequest request) throws WxPayException {
        WxPayOrderNotifyV3Result result =
                WxPayUtil.getInstance().parseJsApiReturnBody(body, getRequestHeader(request));
        WxPayOrderNotifyV3Result.DecryptNotifyResult decryptNotifyResult
                = result.getResult();
        WxPayOrderNotifyV3Result.Amount amount = decryptNotifyResult.getAmount();
        //支付成功
        if (result.getRawData().getEventType().equals("TRANSACTION.SUCCESS")) {
            if (decryptNotifyResult.getTradeState().equals("SUCCESS")) {
                String orderId = decryptNotifyResult.getOutTradeNo();
                String outOrderId = decryptNotifyResult.getTransactionId();
                String sMoney = new BigDecimal(amount.getPayerTotal().intValue())
                        .divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_DOWN)
                        .toString();
                LocalDateTime payTime =
                        LocalDateTimeUtil.parse(decryptNotifyResult.getSuccessTime(), "yyyy-MM-dd'T'HH:mm:ssXXX");
                //更改订单流水信息
                Trans trans = transService.getById(orderId);
                trans.setPayTime(payTime);
                trans.setStatus("99");
                trans.setOutno(outOrderId);
                trans.setSmoney(sMoney);
                trans.setPayReason(body);
                transService.updateById(trans);

                Order shopOrder = orderService.getById(trans.getOrderId());
                shopOrder.setPaytime(payTime);
                shopOrder.setStatus("01");  //已付款待发货
                shopOrder.setRprice(sMoney);
                orderService.updateById(shopOrder);
                //添加订单流水信息
                OrderDetails orderDetails = new OrderDetails();
                orderDetails.setCreateTime(payTime);
                orderDetails.setOrderId(shopOrder.getId());
                orderDetails.setType("01");//支付成功
                orderDetails.setRemark("订单支付成功");
                orderDetailsService.save(orderDetails);
                return new ResponseEntity<>("", HttpStatus.OK);
            }
        }
        JSONObject returnJson = new JSONObject();
        returnJson.put("code", "FAIL");
        returnJson.put("message", "接收付款信息失败");
        return new ResponseEntity<>(returnJson.toJSONString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * 获取回调请求头：签名相关
     *
     * @param request HttpServletRequest
     * @return SignatureHeader
     */
    private SignatureHeader getRequestHeader(HttpServletRequest request) {
        // 获取通知签名
        String signature = request.getHeader("wechatpay-signature");
        String nonce = request.getHeader("wechatpay-nonce");
        String serial = request.getHeader("wechatpay-serial");
        String timestamp = request.getHeader("wechatpay-timestamp");
        SignatureHeader signatureHeader = new SignatureHeader();
        signatureHeader.setSignature(signature);
        signatureHeader.setNonce(nonce);
        signatureHeader.setSerial(serial);
        signatureHeader.setTimeStamp(timestamp);
        return signatureHeader;
    }

    @PostMapping("/api/refjsapi")
    public ResponseEntity returnTransRefJsApi(@RequestBody String body, HttpServletRequest request) throws WxPayException {
        log.info(body);
        WxPayRefundNotifyV3Result result =
                WxPayUtil.getInstance().parseJsApiRefReturnBody(body, getRequestHeader(request));
        //退款到账通知
        WxPayRefundNotifyV3Result.DecryptNotifyResult decryptNotifyResult = result.getResult();
        WxPayRefundNotifyV3Result.Amount amount = decryptNotifyResult.getAmount();
        if (result.getRawData().getEventType().equals("REFUND.SUCCESS")) {
            if (decryptNotifyResult.getRefundStatus().equals("SUCCESS")) {
                String orderId = decryptNotifyResult.getOutTradeNo();
                //String outOrderId = decryptNotifyResult.getTransactionId();
                String outRefNo = decryptNotifyResult.getOutRefundNo();
                LocalDateTime refTime =
                        LocalDateTimeUtil.parse(decryptNotifyResult.getSuccessTime(), "yyyy-MM-dd'T'HH:mm:ssXXX");
                Trans trans = transService.getById(orderId);
                Order shopOrder = orderService.getById(trans.getOrderId());
                JSONArray goods = shopOrder.getGoods();
                boolean isAll = shopOrder.getIsrefund().equals("1") ? false : true;
                String goodName = "";
                for (int i = 0; i < goods.size(); i++) {
                    JSONObject good = goods.getJSONObject(i);
                    if (good.containsKey("refNo") &&
                            good.getString("refNo").equals(outRefNo)) {
                        goodName = good.getString("goodsName");
                        good.remove("isReceived"); //先删除是否到账
                        good.put("isReceived", "1");    // 设置已到账
                        good.put("refStatus","99");    //设置退款成功 ； 00:退款申请中 -1：退款驳回 99：退款成功
                        good.put("refSuccessTime",     //设置到账时间
                                DateUtil.format(refTime, "yyyy-MM-dd HH:mm:ss"));
                    }
                    goods.set(i, good);
                }
                shopOrder.setGoods(goods);
                orderService.updateById(shopOrder);
                //添加订单流水信息
                OrderDetails orderDetails = new OrderDetails();
                orderDetails.setCreateTime(refTime);
                orderDetails.setOrderId(shopOrder.getId());
                orderDetails.setType("-2");//退款到账
                orderDetails.setRemark(amount.getRefund().intValue()/100+"元,退款已到账" + (isAll ? "" : "[" + goodName + "]"));
                orderDetailsService.save(orderDetails);
                //更改订单流水信息
                JSONArray refA = trans.getReffund();
                for (int i = 0; i < refA.size(); i++) {
                    JSONObject refObj = refA.getJSONObject(i);
                    if(refObj.getString("refNo").equals(outRefNo)){
                        refObj.remove("isReceived"); //先删除是否到账
                        refObj.put("isReceived", "1");    // 设置已到账
                        refObj.put("refSuccessTime",     //设置到账时间
                                DateUtil.format(refTime, "yyyy-MM-dd HH:mm:ss"));
                        refObj.put("body",JSONObject.toJSONString(result));
                        refA.set(i,refObj);
                    }
                }
                trans.setReffund(refA);
                transService.updateById(trans);
                return new ResponseEntity<>("", HttpStatus.OK);
            }
        }
        JSONObject returnJson = new JSONObject();
        returnJson.put("code", "FAIL");
        returnJson.put("message", "接收退款结果信息失败");
        return new ResponseEntity<>(returnJson.toJSONString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
