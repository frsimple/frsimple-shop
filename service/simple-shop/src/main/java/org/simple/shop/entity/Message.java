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
 * @Desc: 消息通知实体
 * @Date: 2022-09-21 21:36:13
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@TableName(value = "shop_message")
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String content;

    private LocalDateTime createtime;

    private String status;

    private String tenantid;

}
