package org.simple.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.simple.common.utils.CommonResult;
import org.simple.shop.dto.OrderDto;
import org.simple.shop.dto.OrderParams;
import org.simple.shop.entity.Order;

import java.util.List;


/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:36:30
 * @Author: frsimple
 */
public interface OrderService extends IService<Order> {
    CommonResult queryOrderCount();
    IPage<List<OrderDto>> queryOrders(Page page,OrderParams orderParams);

    CommonResult queryDataOrder();

    void updateOrderApplyService(String id);
}