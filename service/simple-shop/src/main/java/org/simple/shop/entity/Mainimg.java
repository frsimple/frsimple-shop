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
 * @Desc: 首页轮播图实体
 * @Date: 2022-09-21 21:35:44
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "shop_mainimg")
public class Mainimg implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String title;

    private String url;

    private String img;

    private LocalDateTime createtime;

    private String status;

    private String type;

    private Integer sort;

    private String tenantid;

}
