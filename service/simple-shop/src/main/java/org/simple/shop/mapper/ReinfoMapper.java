package org.simple.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.simple.shop.entity.Reinfo;

/**
 * @Copyright: simple
 * @Date: 2022-12-15 18:35:28
 * @Author: frsimple
 */


public interface ReinfoMapper
        extends BaseMapper<Reinfo> {

    @Update("update shop_reinfo set is_default = '0' where userid = #{userid}")
    void updateIsDefault(@Param("userid")String userid);
}
