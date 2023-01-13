package org.simple.shop.entity;

import com.alibaba.fastjson.JSONArray;
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
 * @Desc: 订单信息表实体
 * @Date: 2022-09-21 21:36:30
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "shop_order", autoResultMap = true)
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.INPUT)
    private String id;

    private String userid;

    private LocalDateTime createtime;

    private String status;

    private String paytype;

    private LocalDateTime paytime;

    private String price;

    private String rprice;

    private String isrefund;

    private String tstatus;

    private String payId;

    private String freight;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONObject express;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONObject takeInfo;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONArray goods;

    private String datasource;

    private String treason;

    private String treasonRef;

    private String remark;

    private String nprice;

    private String applyNo;

}
