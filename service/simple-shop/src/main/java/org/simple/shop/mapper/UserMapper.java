package org.simple.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.simple.shop.dto.UserDto;
import org.simple.shop.entity.User;

import java.util.List;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:37:58
 * @Author: frsimple
 */


public interface UserMapper
        extends BaseMapper<User> {
    IPage<List<UserDto>> querUserList(Page page, @Param("params")UserDto userDto);

    @Select("select count(1) from shop_user  where " +
            " date_format(createtime,'%Y-%m-%d') = DATE_SUB(curdate( ), INTERVAL 1 DAY)")
    Integer yesterDayAddUserCount();
    @Select("select count(1) from shop_user  " +
            " where date_format(createtime,'%Y-%m-%d') = date_format(SYSDATE(),'%Y-%m-%d')")
    Integer todayAddUserCount();
}
