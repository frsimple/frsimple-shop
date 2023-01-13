package org.simple.shop.service;

import cn.hutool.core.lang.tree.Tree;
import org.simple.common.utils.CommonResult;

import java.util.List;

public interface CommonService {


    List<Tree<String>> queryRegionTree();

    CommonResult updateOrInsertHotSearch(String content);

    String selectHotSearch();

    CommonResult homeTopData();

}
