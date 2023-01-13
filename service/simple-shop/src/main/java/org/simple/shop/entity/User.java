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
 * @Desc: 会员信息表实体
 * @Date: 2022-09-21 21:37:58
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@TableName(value = "shop_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String name;

    private String avatar;

    private String phone;

    private LocalDateTime createtime;

    private String datasource;

    private String openid;
    private String uniopenid;

}
