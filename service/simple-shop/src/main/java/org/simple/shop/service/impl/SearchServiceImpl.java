package org.simple.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.simple.shop.entity.Search;
import org.simple.shop.mapper.SearchMapper;
import org.simple.shop.service.SearchService;
import org.springframework.stereotype.Service;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:37:28
 * @Author: frsimple
 */


@Service
public class SearchServiceImpl
        extends ServiceImpl<SearchMapper, Search>
        implements SearchService {

}