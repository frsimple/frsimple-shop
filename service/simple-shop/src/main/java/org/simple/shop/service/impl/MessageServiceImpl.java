package org.simple.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.simple.common.utils.CommonResult;
import org.simple.shop.entity.Message;
import org.simple.shop.mapper.MessageMapper;
import org.simple.shop.service.MessageService;
import org.springframework.stereotype.Service;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:36:13
 * @Author: frsimple
 */


@Service
public class MessageServiceImpl
        extends ServiceImpl<MessageMapper, Message>
        implements MessageService {

    @Override
    public CommonResult updateStatusToClose(String id) {
        baseMapper.updateStatusToClose();
        Message shopMessage = new Message();
        shopMessage.setId(id);
        shopMessage.setStatus("1");
        baseMapper.updateById(shopMessage);
        return CommonResult.successNodata("启用成功");
    }
}