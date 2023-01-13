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
import java.time.LocalDateTime;


/**
 * @Copyright: simple
 * @Desc: 订单交易信息实体
 * @Date: 2022-12-26 10:42:13
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@TableName(value = "shop_trans",autoResultMap = true)
public class Trans implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.INPUT)
    private String id;

    private String orderId;

    private String userid;

    private String paytype;

    private String datasource;

    private LocalDateTime createTime;

    private LocalDateTime payTime;

    private String status;

    private String outno;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONArray reffund;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONObject payJson;

    private String money;

    private String smoney;

    private String payReason;

    private String refstatus;

}
