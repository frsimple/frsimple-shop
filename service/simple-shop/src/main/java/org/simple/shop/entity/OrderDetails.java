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
 * @Desc: 订单操作流水表实体
 * @Date: 2022-12-15 18:01:36
 * @Author: frsimple
 */

@Data
@NoArgsConstructor
@TableName(value = "shop_order_details")
public class OrderDetails implements Serializable {
    private static final long serialVersionUID = 1L;


    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String orderId;

    private String remark;

    private LocalDateTime createTime;

    private String type;

}
