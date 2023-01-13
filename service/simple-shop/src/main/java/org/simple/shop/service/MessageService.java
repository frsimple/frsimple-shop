package org.simple.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.simple.common.utils.CommonResult;
import org.simple.shop.entity.Message;


/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:36:13
 * @Author: frsimple
 */
public interface MessageService extends IService<Message> {

    CommonResult updateStatusToClose(String id);

}