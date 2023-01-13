package org.simple.shop.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.simple.shop.service.TransService;
import org.simple.shop.mapper.TransMapper;
import org.simple.shop.entity.Trans;

/**
 * @Copyright: simple
 * @Date: 2022-12-26 10:42:13
 * @Author: frsimple
 */


@Service
public class TransServiceImpl
        extends ServiceImpl<TransMapper, Trans>
        implements TransService {

}