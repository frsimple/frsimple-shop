package org.simple.shop.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @Copyright: simple
 * @Desc: 运费模板表实体
 * @Date: 2022-11-20 19:02:44
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@TableName(value = "shop_ratetem",autoResultMap = true)
public class Ratetem implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String name;

    private String type;

    private String paytype;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONArray payjson;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONArray conjson;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONObject nosend;

    private String sendtime;

    private String sendregion;

    private String tenantid;

}
