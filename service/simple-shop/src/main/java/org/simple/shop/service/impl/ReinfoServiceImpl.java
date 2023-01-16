package org.simple.shop.service.impl;

import org.simple.security.utils.AuthUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.simple.shop.service.ReinfoService;
import org.simple.shop.mapper.ReinfoMapper;
import org.simple.shop.entity.Reinfo;

/**
 * @Copyright: simple
 * @Date: 2022-12-15 18:35:28
 * @Author: frsimple
 */


@Service
public class ReinfoServiceImpl
        extends ServiceImpl<ReinfoMapper, Reinfo>
        implements ReinfoService {


    @Override
    public void updateIsDefault() {
        baseMapper.updateIsDefault(AuthUtils.getUser().getId());
    }
}