package org.simple.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.simple.shop.dto.ClassDto;
import org.simple.shop.entity.Class;

import java.util.List;

/**
 * @Copyright: simple
 * @Date: 2022-09-21 21:34:57
 * @Author: frsimple
 */


public interface ClassMapper
        extends BaseMapper<Class> {

    @Select("select t1.url,t1.id,t1.name,t1.parentid,t1.createtime,t1.sort,t1.tenantid,t1.level,if(t1.parentid = '0','顶级', " +
            "(select  t.name from shop_class t  where t.id = t1.parentid)) as parentname from shop_class  t1")
     List<ClassDto> queryClassTree();

}
