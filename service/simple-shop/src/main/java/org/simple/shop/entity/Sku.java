package org.simple.shop.entity;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @Copyright: simple
 * @Desc: sku模板信息实体
 * @Date: 2022-12-03 21:04:14
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@TableName(value = "shop_sku", autoResultMap = true)
public class Sku implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String title;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONArray content;

    private String tenantid;
    private LocalDateTime createtime;

}