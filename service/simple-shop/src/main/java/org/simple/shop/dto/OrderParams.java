package org.simple.shop.dto;

import lombok.Data;

@Data
public class OrderParams {
    private String orderId;
    private String status;
    private String phone;

    private String startTime;
    private String endTime;
}
