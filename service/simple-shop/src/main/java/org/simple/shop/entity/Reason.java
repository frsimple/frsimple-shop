package org.simple.shop.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @Copyright: simple
 * @Desc: 商品关系表实体
 * @Date: 2022-09-21 21:37:14
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "shop_reason")
public class Reason implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String shopid;

    private String skuid;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONObject sku;

    private LocalDateTime createtime;

    private String userid;

    private String type;

}
