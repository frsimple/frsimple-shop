package org.simple.shop.utils;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.analysis.WxMaSummaryTrend;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import me.chanjar.weixin.common.error.WxErrorException;
import org.simple.common.utils.CommonResult;
import org.simple.shop.dto.WxPayParams;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class WxPayUtil {
    private static WxPayUtil wxPayUtil = null;

    public static WxPayUtil getInstance() {
        if (null == wxPayUtil) {
            return new WxPayUtil();
        } else {
            return wxPayUtil;
        }
    }

    private WxPayConfig initWxConfig() {
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setAppId(org.simple.shop.config.WxPayConfig.APPID);
        wxPayConfig.setMchId(org.simple.shop.config.WxPayConfig.MCHID);
        wxPayConfig.setCertSerialNo(org.simple.shop.config.WxPayConfig.MCHSERIALNO);
        wxPayConfig.setApiV3Key(org.simple.shop.config.WxPayConfig.APIV3KEY);
        wxPayConfig.setPrivateCertPath(org.simple.shop.config.WxPayConfig.PRIVATECERTPATH);
        wxPayConfig.setPrivateKeyPath(org.simple.shop.config.WxPayConfig.PRIVATEKEYPATH);
        wxPayConfig.setNotifyUrl(org.simple.shop.config.WxPayConfig.NOTIFY_URL);
        return wxPayConfig;
    }

    /**
     * 调用预支付接口
     *
     * @return 预支付订单ID
     */
    public CommonResult transJsapi(WxPayParams wxPayParams) {
        WxPayConfig wxPayConfig = initWxConfig();
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayConfig);
        WxPayUnifiedOrderV3Request request =
                new WxPayUnifiedOrderV3Request();
        //购买者
        WxPayUnifiedOrderV3Request.Payer payer = new WxPayUnifiedOrderV3Request.Payer();
        payer.setOpenid(wxPayParams.getOpenId());
        request.setPayer(payer);
        //金额信息
        WxPayUnifiedOrderV3Request.Amount amount = new WxPayUnifiedOrderV3Request.Amount();
        amount.setCurrency("CNY");
        amount.setTotal(Integer.valueOf(wxPayParams.getPayMoney().multiply(new BigDecimal(100)).intValue()));
        request.setAmount(amount);

        //订单基本信息
        request.setOutTradeNo(wxPayParams.getOrderId());
        request.setDescription(wxPayParams.getBody());
        request.setTimeExpire(DateUtil.format(
                DateUtil.offsetMinute(new Date(), 15),
                "yyyy-MM-dd'T'HH:mm:ssXXX"));
        try {
            WxPayUnifiedOrderV3Result.JsapiResult result =
                    wxPayService.createOrderV3(TradeTypeEnum.JSAPI, request);
            JSONObject object = new JSONObject();
            object.put("timeStamp", result.getTimeStamp());
            object.put("nonceStr", result.getNonceStr());
            object.put("package", result.getPackageValue());
            object.put("signType", result.getSignType());
            object.put("paySign", result.getPaySign());
            return CommonResult.success(object);
        } catch (Exception ex) {
            ex.printStackTrace();
            return CommonResult.failed(ex.getMessage());
        }
    }

    /**
     * 解析支付成功结果回调数据
     */
    public WxPayOrderNotifyV3Result parseJsApiReturnBody(String body, SignatureHeader signatureHeader) throws WxPayException {
        WxPayConfig wxPayConfig = initWxConfig();
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayConfig);
        WxPayOrderNotifyV3Result result =
                wxPayService.parseOrderNotifyV3Result(body, signatureHeader);
        return result;
    }

    /**
     * 解析退款结果回调数据
     */
    public WxPayRefundNotifyV3Result parseJsApiRefReturnBody(String body, SignatureHeader signatureHeader) throws WxPayException {
        WxPayConfig wxPayConfig = initWxConfig();
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayConfig);
        WxPayRefundNotifyV3Result result =
                wxPayService.parseRefundNotifyV3Result(body, signatureHeader);
        return result;
    }

    /**
     * jsapi退款接口
     */
    public CommonResult transJsapiRef(WxPayParams wxPayParams) {
        WxPayConfig wxPayConfig = initWxConfig();
        wxPayConfig.setNotifyUrl(org.simple.shop.config.WxPayConfig.REF_NOTIFY_URL);
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayConfig);
        WxPayRefundV3Request request = new WxPayRefundV3Request();
        request.setOutTradeNo(wxPayParams.getOrderId());
        request.setOutRefundNo(wxPayParams.getOutRefundNo());
        WxPayRefundV3Request.Amount amount = new WxPayRefundV3Request.Amount();
        amount.setRefund(Integer.valueOf(wxPayParams.getRefundPrice().multiply(new BigDecimal(100)).intValue()));
        amount.setCurrency("CNY");
        amount.setTotal(Integer.valueOf(
                wxPayParams.getPayMoney().multiply(new BigDecimal(100)).intValue()
        ));
        try {
            WxPayRefundV3Result result =
                    wxPayService.refundV3(request);
            return CommonResult.success(result);
        } catch (Exception ex) {
            return CommonResult.failed(ex.getMessage());
        }
    }

    public CommonResult getDailySummaryTrend() {
        WxMaService wxMaService = new WxMaServiceImpl();
        WxMaDefaultConfigImpl wxMaConfig = new WxMaDefaultConfigImpl();
        wxMaConfig.setAppid(org.simple.shop.config.WxPayConfig.APPID);
        wxMaConfig.setSecret(org.simple.shop.config.WxPayConfig.APPSECRET);
        wxMaService.setWxMaConfig(wxMaConfig);
        try {
            //获取上一天的日期
            LocalDate minusDay =
                    LocalDate.now().minusDays(1);
            Instant instant = minusDay.atTime(LocalTime.MIDNIGHT).
                    atZone(ZoneId.systemDefault()).toInstant();
            List<WxMaSummaryTrend> list =
                    wxMaService.getAnalysisService().getDailySummaryTrend(Date.from(instant), Date.from(instant));
            return CommonResult.success(list);
        } catch (WxErrorException e) {
            e.printStackTrace();
            return CommonResult.failed(e.getMessage());
        }
    }

}
