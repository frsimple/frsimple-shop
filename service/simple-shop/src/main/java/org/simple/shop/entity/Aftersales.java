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
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * @Copyright: simple
 * @Desc: 售后申请信息实体
 * @Date: 2023-01-06 15:37:34
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@TableName(value = "shop_aftersales",autoResultMap = true)
public class Aftersales implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String type;

    private String orderId;

    private String status;

    private String result;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONArray goods;

    private LocalDateTime createTime;

    private String remark;

    private String userid;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONArray imgs;

    private String reason;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONObject express;

    private BigDecimal price;

    private String isGet;

    private String resultMsg;

}
