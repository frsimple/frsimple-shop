package org.simple.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.simple.center.entity.Menu;

import java.util.List;

public interface MenuMapper extends BaseMapper<Menu> {

    @Select("select t1.* from center_menu t1 where t1.type = 'c' order by t1.sort asc")
    @Results({
            @Result(column = "meta", property = "meta", typeHandler = JacksonTypeHandler.class)
    })
    List<Menu> getTreeMenuAll();


    @Select("select t1.* from center_menu t1 order by t1.sort asc")
    @Results({
            @Result(column = "meta", property = "meta", typeHandler = JacksonTypeHandler.class)
    })
    List<Menu> getRoleMenuAll();

    void delRoleMenuByMenu(@Param("menuList") List<String> menuList);
}
