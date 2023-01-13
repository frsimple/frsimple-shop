package org.simple.shop.service.impl;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.simple.shop.dto.ClassDto;
import org.simple.shop.entity.Class;
import org.simple.shop.mapper.ClassMapper;
import org.simple.shop.service.ClassService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:34:57
 * @Author: frsimple
 */


@Service
public class ClassServiceImpl
        extends ServiceImpl<ClassMapper, Class>
        implements ClassService {

    @Override
    public List<Tree<String>> queryClassTree() {
        List<ClassDto> treeDtoList = new ArrayList<>();
        treeDtoList = baseMapper.queryClassTree();
        TreeNodeConfig config = new TreeNodeConfig();
        config.setWeightKey("sort");
        List<Tree<String>> treeNodes = TreeUtil.build(treeDtoList, "0", config,
                (object, tree) -> {
                    tree.setName(object.getName());
                    tree.setId(object.getId());
                    tree.setParentId(object.getParentid());
                    tree.setWeight(StringUtils.isEmpty(object.getSort()) ? 0 : Integer.valueOf(object.getSort()));
                    tree.putExtra("createtime", object.getCreatetime());
                    tree.putExtra("tenantid", object.getTenantid());
                    tree.putExtra("parentname", object.getParentname());
                    tree.putExtra("level",object.getLevel());
                    tree.putExtra("url",object.getUrl());
                });
        return treeNodes;
    }
}