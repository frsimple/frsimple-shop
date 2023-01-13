package org.simple.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.simple.shop.dto.OrderDto;
import org.simple.shop.dto.OrderParams;
import org.simple.shop.entity.Order;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:36:30
 * @Author: frsimple
 */


public interface OrderMapper
        extends BaseMapper<Order> {

    //个人中心退款数量查询： 待付款/待发货/待收货/待评价/退款&&售后
    @Select("select count(1) from shop_order where  status = '00' and isrefund = '0' and userid = #{userid}")
    Integer queryUserStatusCount0(@Param("userid")String userid);

    @Select("select count(1) from shop_order where  status = '01' and isrefund = '0' and userid = #{userid}")
    Integer queryUserStatusCount1(@Param("userid")String userid);

    @Select("select count(1) from shop_order where  status = '02' and isrefund = '0' and userid = #{userid}")
    Integer queryUserStatusCount2(@Param("userid")String userid);

    @Select("select count(1) from shop_order where  status = '03' and isrefund = '0' and userid = #{userid}")
    Integer queryUserStatusCount3(@Param("userid")String userid);

    @Select("select count(1) from shop_aftersales where status = '0' and userid = #{userid}")
    Integer queryUserStatusCount4(@Param("userid")String userid);

    IPage<List<OrderDto>> queryOrders(Page page, @Param("params")OrderParams orderParams);


    @Select("select count(1) from shop_order where  status = '00' and isrefund = '0' ")
    Integer queryUserStatusCount5();

    @Select("select count(1) from shop_order where  status = '01' and isrefund = '0' ")
    Integer queryUserStatusCount6();

    @Select("select count(1) from shop_aftersales where status = '0' ")
    Integer queryUserStatusCount7();

    @Select("select count(1) from shop_order  where status <> '-1' " +
            " and date_format(createtime,'%Y-%m-%d') = DATE_SUB(curdate( ), INTERVAL 1 DAY)")
    Integer yesterDayOrderCount();
    @Select("select count(1) from shop_order  where status <> '-1'  " +
            "and date_format(createtime,'%Y-%m-%d') = date_format(SYSDATE(),'%Y-%m-%d')")
    Integer todayOrderCount();

    @Select("select IFNULL(SUM(CAST(IFNULL(nprice,price) AS DECIMAL(10,2))),0.00)  " +
            "from shop_order where status <> '-1' and " +
            "date_format(createtime,'%Y-%m-%d') = DATE_SUB(curdate( ), " +
            "INTERVAL 1 DAY) and paytime is not null")
    BigDecimal yesterDayPayMoneyCount();
    @Select("select IFNULL(SUM(CAST(IFNULL(nprice,price) AS DECIMAL(10,2))),0.00) " +
            "  from shop_order where status <> '-1' and " +
            "date_format(createtime,'%Y-%m-%d') = date_format(SYSDATE(),'%Y-%m-%d') " +
            " and paytime is not null")
    BigDecimal todayPayMoneyCount();

    @Update("update shop_order set apply_no = '' where id =  #{id} ")
    void updateOrderApplyService(@Param("id")String id);

}
