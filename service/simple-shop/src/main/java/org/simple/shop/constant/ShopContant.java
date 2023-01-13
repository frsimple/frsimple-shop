package org.simple.shop.constant;

public interface ShopContant {

    //订单超时时间
    long SHOP_ORDER_EXPIRE_TIME = 15;
    String SHOP_ORDER_KEY = "order_unpaid_";

    //订单自动收货时长
    long SHOP_ORDER_EXPIRE_TASKOVER = 15;
    String SHOP_ORDER_TASKOVERKEY = "order_taskover_";

    //订单自动好评时长
    long SHOP_ORDER_EXPIRE_COMMENT = 5;
    String SHOP_ORDER_COMMENT = "order_comment_";
}
