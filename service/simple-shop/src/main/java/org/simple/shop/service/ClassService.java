package org.simple.shop.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.service.IService;
import org.simple.shop.entity.Class;

import java.util.List;


/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:34:57
 * @Author: frsimple
 */
public interface ClassService extends IService<Class> {


    List<Tree<String>> queryClassTree();
}