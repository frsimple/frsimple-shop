package org.simple.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.simple.shop.dto.UserDto;
import org.simple.shop.entity.User;

import java.util.List;


/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:37:58
 * @Author: frsimple
 */
public interface UserService extends IService<User> {

    IPage<List<UserDto>> querUserList(Page page,UserDto userDto);
}