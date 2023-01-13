package org.simple.shop.dto;

import lombok.Data;
import org.simple.shop.entity.OrderDetails;
import org.simple.shop.entity.Order;

import java.util.List;

@Data
public class OrderDto extends Order {

    private List<OrderDetails> orderDetailsList;

    private String userName;
    private String userPhone;

    private String userOpenId;

    private String userAvatar;

    private String userUniopenId;
}
