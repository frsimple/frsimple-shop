package org.simple.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.simple.shop.dto.RateTemDto;
import org.simple.shop.dto.RegionTreeDto;
import org.simple.shop.entity.Ratetem;

import java.util.List;

/**
 * @Copyright: simple
 * @Date: 2022-11-20 19:02:44
 * @Author: frsimple
 */


public interface RatetemMapper
        extends BaseMapper<Ratetem> {

    @Select("select id,name,parentid,id as sort from shop_region")
    List<RegionTreeDto> queryRegionTree();

    @Select("select t.*,(select REPLACE(group_concat(name),',','/')  from shop_region  where FIND_IN_SET(id,t.sendregion) order by id asc) as " +
            "send_reg_name  from shop_ratetem t  where t.tenantid = #{tenantid}")
    @Results({
            @Result(column = "payjson", property = "payjson", typeHandler = JacksonTypeHandler.class),
            @Result(column = "nosend", property = "nosend", typeHandler = JacksonTypeHandler.class),
            @Result(column = "conjson", property = "conjson", typeHandler = JacksonTypeHandler.class)
    })
    List<RateTemDto> queryRateTemList(@Param("tenantid") String tenantid);

    @Select("select REPLACE(group_concat(name),',','/')  from shop_region  where FIND_IN_SET(id,#{ids}) order by id asc")
    String queryRegTreeName(@Param("ids")String ids);

    @Select("select parentid from  shop_region where id = #{id}")
    String getParentId(@Param("id")String id);

    @Select("select name from  shop_region where id = #{id}")
    String getRegName(@Param("id")String id);

}
