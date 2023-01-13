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
 * @Desc: 商品分类信息表实体
 * @Date: 2022-09-21 21:34:57
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "shop_class")
public class Class implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String name;

    private String parentid;

    private LocalDateTime createtime;

    private Integer sort;

    private String tenantid;

    private Integer level;

    private String url;

}
