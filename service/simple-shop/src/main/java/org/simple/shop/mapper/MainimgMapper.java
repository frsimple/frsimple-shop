package org.simple.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.simple.shop.entity.Mainimg;

import java.util.List;
import java.util.Map;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:35:44
 * @Author: frsimple
 */


public interface MainimgMapper
        extends BaseMapper<Mainimg> {

    @Update("update shop_hotsearch set content = #{content} where id = #{id}")
    void updateHotSearch(@Param("content") String content, @Param("id") String id);

    @Insert("insert into shop_hotsearch(id,content) values(#{id},#{content})")
    void insertHotSearch(@Param("content") String content,@Param("id") String id);

    @Select("select * from shop_hotsearch")
    List<Map<String,String>> selectOneHotSearch();

}
