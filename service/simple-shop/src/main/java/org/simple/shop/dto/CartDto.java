package org.simple.shop.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.simple.shop.entity.Ratetem;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CartDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer buyNum;
    private String spuId;
    private String goodsName;
    private String skuId;
    private String primaryImage;
    private JSONObject selectedSkuInfo;
    private Ratetem rateTemInfo;
    private BigDecimal weight;
    private Integer stock;
    private Boolean isCanBuy;
    private Boolean isSelected;
    private BigDecimal price;

    private String ids;


}
