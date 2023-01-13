package org.simple.shop.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.simple.shop.service.MarkService;
import org.simple.shop.mapper.MarkMapper;
import org.simple.shop.entity.Mark;

/**
 * @Copyright: simple
 * @Date: 2022-12-18 15:23:43
 * @Author: frsimple
 */


@Service
public class MarkServiceImpl
        extends ServiceImpl<MarkMapper, Mark>
        implements MarkService {

}