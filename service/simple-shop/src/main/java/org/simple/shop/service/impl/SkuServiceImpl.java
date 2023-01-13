package org.simple.shop.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.simple.shop.service.SkuService;
import org.simple.shop.mapper.SkuMapper;
import org.simple.shop.entity.Sku;

/**
 * @Copyright: simple
 * @Date: 2022-12-03 21:04:14
 * @Author: frsimple
 */


@Service
public class SkuServiceImpl
        extends ServiceImpl<SkuMapper, Sku>
        implements SkuService {

}