package org.simple.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @Copyright: simple
 * @Desc: 快捷菜单配信实体
 * @Date: 2022-12-05 16:37:00
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@TableName(value = "shop_fast")
public class Fast implements Serializable {
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
