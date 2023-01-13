package org.simple.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.simple.shop.entity.Info;

/**
 * @Copyright: simple
 * @Desc:
 * @Date: 2022-09-10 21:37:22
 * @Author: frsimple
 */


public interface InfoMapper
        extends BaseMapper<Info> {

    @Select("select count(1) from shop_info where status in ('01','00') and tenantid = #{tenantid}")
    Integer queryAllProduct(@Param("tenantid")String tenantid);

    @Select("select count(1) from shop_info where status in ('01') and tenantid = #{tenantid}")
    Integer queryOnProduct(@Param("tenantid")String tenantid);
    @Select("select count(1) from shop_info where status in ('00') and tenantid = #{tenantid}")
    Integer queryCloseProduct(@Param("tenantid")String tenantid);

    @Select("select count(1) from shop_info where status in ('-1') and tenantid = #{tenantid}")
    Integer queryDelProduct(@Param("tenantid")String tenantid);
}
