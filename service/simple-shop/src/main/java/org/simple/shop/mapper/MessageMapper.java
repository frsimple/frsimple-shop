package org.simple.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;
import org.simple.shop.entity.Message;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:36:13
 * @Author: frsimple
 */


public interface MessageMapper
        extends BaseMapper<Message> {

    //修改所有状态为停用
    @Update("update shop_message set status = '0'")
    void updateStatusToClose();
}

