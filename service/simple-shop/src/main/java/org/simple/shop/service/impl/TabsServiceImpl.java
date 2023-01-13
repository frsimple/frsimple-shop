package org.simple.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.simple.shop.entity.Tabs;
import org.simple.shop.mapper.TabsMapper;
import org.simple.shop.service.TabsService;
import org.springframework.stereotype.Service;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:37:44
 * @Author: frsimple
 */


@Service
public class TabsServiceImpl
        extends ServiceImpl<TabsMapper, Tabs>
        implements TabsService {

}