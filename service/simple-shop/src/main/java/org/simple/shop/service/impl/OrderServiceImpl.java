package org.simple.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.simple.common.utils.CommonResult;
import org.simple.security.utils.AuthUtils;
import org.simple.shop.dto.OrderDto;
import org.simple.shop.dto.OrderParams;
import org.simple.shop.entity.Order;
import org.simple.shop.mapper.OrderMapper;
import org.simple.shop.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:36:30
 * @Author: frsimple
 */


@Service
public class OrderServiceImpl
        extends ServiceImpl<OrderMapper, Order>
        implements OrderService {

    @Override
    public CommonResult queryOrderCount() {
        String userId = AuthUtils.getUser().getId();
        Integer count0 = baseMapper.queryUserStatusCount0(userId);
        Integer count1 = baseMapper.queryUserStatusCount1(userId);
        Integer count2 = baseMapper.queryUserStatusCount2(userId);
        Integer count3 = baseMapper.queryUserStatusCount3(userId);
        Integer count4 = baseMapper.queryUserStatusCount4(userId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count0", count0);
        jsonObject.put("count1", count1);
        jsonObject.put("count2", count2);
        jsonObject.put("count3", count3);
        jsonObject.put("count4", count4);
        return CommonResult.success(jsonObject);
    }

    @Override
    public IPage<List<OrderDto>> queryOrders(Page page, OrderParams orderParams) {
        return baseMapper.queryOrders(page,orderParams);
    }

    @Override
    public CommonResult queryDataOrder() {
        Integer count0 = baseMapper.queryUserStatusCount5();
        Integer count1 = baseMapper.queryUserStatusCount6();
        Integer count2 = baseMapper.queryUserStatusCount7();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count0", count0);
        jsonObject.put("count1", count1);
        jsonObject.put("count2", count2);
        return CommonResult.success(jsonObject);
    }

    @Override
    public void updateOrderApplyService(String id) {
         baseMapper.updateOrderApplyService(id);
    }
}