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
 * @Desc: 用户购物车信息表实体
 * @Date: 2022-12-23 11:32:18
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@TableName(value = "shop_cart")
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String prodId;

    private String skuId;

    private String userId;

    private LocalDateTime createTime;

    private String status;

    private String primaryImage;

    private Long num;

}
