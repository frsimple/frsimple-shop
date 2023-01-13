package org.simple.shop.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.simple.shop.service.AftersalesService;
import org.simple.shop.mapper.AftersalesMapper;
import org.simple.shop.entity.Aftersales;

/**
 * @Copyright: simple
 * @Date: 2023-01-06 15:37:34
 * @Author: frsimple
 */


@Service
public class AftersalesServiceImpl
        extends ServiceImpl<AftersalesMapper, Aftersales>
        implements AftersalesService {

}