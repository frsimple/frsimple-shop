package org.simple.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.simple.shop.dto.CartDto;
import org.simple.shop.entity.Cart;

import java.util.List;

/**
 * @Copyright: simple
 * @Date: 2022-12-23 11:32:18
 * @Author: frsimple
 */


public interface CartMapper
        extends BaseMapper<Cart> {

    @Select("select primary_image as primaryImage, prod_id as spuId,sku_id as skuId,user_id userId,num as buyNum ,id as ids from " +
            " shop_cart where status = '1' and user_id = #{userid}")
    List<CartDto> queryCartList(@Param("userid") String userid);

    @Delete("delete from shop_cart where  FIND_IN_SET(id,#{ids}) > 0 and user_id = #{userid}")
    void deleteCart(@Param("userid") String userid,@Param("ids")String ids);
}
