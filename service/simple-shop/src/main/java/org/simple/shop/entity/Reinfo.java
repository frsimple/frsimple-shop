package org.simple.shop.entity;

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
 * @Desc: 收货人信息实体
 * @Date: 2022-12-15 18:35:28
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@TableName(value = "shop_reinfo",autoResultMap = true)
public class Reinfo implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String rname;

    private String rphone;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONObject rarea;

    private String raddress;

    private String isDefault;

    private String tab;

    private String userid;

}
