package org.simple.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.simple.shop.entity.Reason;
import org.simple.shop.mapper.ReasonMapper;
import org.simple.shop.service.ReasonService;
import org.springframework.stereotype.Service;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:37:14
 * @Author: frsimple
 */


@Service
public class ReasonServiceImpl
        extends ServiceImpl<ReasonMapper, Reason>
        implements ReasonService {

}