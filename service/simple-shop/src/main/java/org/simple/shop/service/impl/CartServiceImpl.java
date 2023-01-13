package org.simple.shop.service.impl;

import org.simple.security.utils.AuthUtils;
import org.simple.shop.dto.CartDto;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.simple.shop.service.CartService;
import org.simple.shop.mapper.CartMapper;
import org.simple.shop.entity.Cart;

import java.util.List;

/**
 * @Copyright: simple
 * @Date: 2022-12-23 11:32:18
 * @Author: frsimple
 */


@Service
public class CartServiceImpl
        extends ServiceImpl<CartMapper, Cart>
        implements CartService {

    @Override
    public List<CartDto> queryCartList() {
        return baseMapper.queryCartList(AuthUtils.getUser().getId());
    }

    @Override
    public void deleteCart(String ids) {
        baseMapper.deleteCart(AuthUtils.getUser().getId(),ids);
    }
}