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
 * @Desc: 商品信息表实体
 * @Date: 2022-09-10 22:17:41
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@TableName(value = "shop_info",autoResultMap = true)
public class Info implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.INPUT)
    private String id;

    private String name;

    private String des;

    private String tabs;

    private String security;

    private String mainimg;

    private String imgs;

    private String content;

    private LocalDateTime createtime;

    private String editor;

    private LocalDateTime updatetime;

    private String defaultSku;

    private String isnew;

    private String isgood;

    private String cate;

    /**
     * 单规格：售价
     * 多规格：售价区间
     */
    private String price;

    /**
     * 单规格：划线价格
     * 多规格：划线价格区间
     */
    private String nprice;

    /**
     * demo : {"颜色":["红色","白色","绿色""],"尺码":["X","M","XL"]}
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONObject sku;

    /**
     * demo : [{"id":"223232323","rule":"红色/X","price":"100.00","nprice":"120.00","skuid":"12121212","count":10000,"img":"https://wew.com"}]
     * sku选项，原价，划线价格，库存，图片
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONArray skulist;

    private Integer collection;
    private Integer sales;
    private String status;
    private String rateTem;

    private String inventory;

    private String isSingle;

    private String weight;

    private String tenantid;

    private BigDecimal minprice;
    private BigDecimal maxprice;

}