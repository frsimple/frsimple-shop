package org.simple.shop.dto;

import lombok.Data;
import org.simple.shop.entity.Aftersales;
import org.simple.shop.entity.Order;
import org.simple.shop.entity.User;

@Data
public class AfterSalesDto extends Aftersales {
    private Order order;

    private User user;
}
