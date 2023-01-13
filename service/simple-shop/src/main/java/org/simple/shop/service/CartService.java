package org.simple.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.simple.shop.dto.CartDto;
import org.simple.shop.entity.Cart;

import java.util.List;


/**
 * @Copyright: simple
 * @Date: 2022-12-23 11:32:18
 * @Author: frsimple
 */
public interface CartService extends IService<Cart> {


    List<CartDto> queryCartList();

    void deleteCart(String ids);
}