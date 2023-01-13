package org.simple.shop.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.simple.shop.dto.UserDto;
import org.simple.shop.entity.User;
import org.simple.shop.mapper.UserMapper;
import org.simple.shop.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:37:58
 * @Author: frsimple
 */


@Service
public class UserServiceImpl
        extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public IPage<List<UserDto>> querUserList(Page page, UserDto userDto) {
        return baseMapper.querUserList(page,userDto);
    }
}