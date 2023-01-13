package org.simple.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @Copyright: simple
 * @Desc: 热门搜索实体
 * @Date: 2022-09-21 21:37:28
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "shop_search")
public class Search implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String tab;

    private Integer sort;

    private LocalDateTime createtime;

}
