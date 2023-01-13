package org.simple.shop.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class WxPayParams implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal payMoney;
    private String orderId;
    private String openId;
    private String outRefundNo;
    private BigDecimal refundPrice;
    private String body;

}
