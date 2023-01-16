package org.simple.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.simple.shop.entity.Reinfo;


/**
 * @Copyright: simple
 * @Date: 2022-12-15 18:35:28
 * @Author: frsimple
 */
public interface ReinfoService extends IService<Reinfo> {

    void updateIsDefault();
}