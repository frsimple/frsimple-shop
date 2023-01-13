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
 * @Desc: 标签管理实体
 * @Date: 2022-09-21 21:37:44
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@TableName(value = "shop_tabs")
public class Tabs implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String tabs;

    private String icon;

    private String type;

    private LocalDateTime createtime;

    private String tenantid;

    private String title;

}
