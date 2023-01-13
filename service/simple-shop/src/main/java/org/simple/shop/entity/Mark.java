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
import java.time.LocalDateTime;


/**
 * @Copyright: simple
 * @Desc: 商品评价表实体
 * @Date: 2022-12-18 15:23:43
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@TableName(value = "shop_mark",autoResultMap = true)
public class Mark implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String parentid;

    private String sid;

    private String orderid;

    private String userid;

    private String content;

    private Integer mark;

    private LocalDateTime createTime;

    private String status;

    private String isReply;
    private LocalDateTime replyTime;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private JSONObject good;

    private String isShowname;

    private String imgs;

}
