package org.simple.shop.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.simple.shop.service.FastService;
import org.simple.shop.mapper.FastMapper;
import org.simple.shop.entity.Fast;

/**
 * @Copyright: simple
 * @Date: 2022-12-05 16:37:00
 * @Author: frsimple
 */


@Service
public class FastServiceImpl
        extends ServiceImpl<FastMapper, Fast>
        implements FastService {

}