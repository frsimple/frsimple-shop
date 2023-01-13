package org.simple.shop.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.simple.shop.service.OrderDetailsService;
import org.simple.shop.mapper.OrderDetailsMapper;
import org.simple.shop.entity.OrderDetails;

/**
 * @Copyright: simple
 * @Date: 2022-12-15 18:01:36
 * @Author: frsimple
 */


@Service
public class OrderDetailsServiceImpl
        extends ServiceImpl<OrderDetailsMapper, OrderDetails>
        implements OrderDetailsService {

}