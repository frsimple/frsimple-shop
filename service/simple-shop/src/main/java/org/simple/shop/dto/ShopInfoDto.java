package org.simple.shop.dto;

import lombok.Data;
import org.simple.shop.entity.Ratetem;
import org.simple.shop.entity.Info;

@Data
public class ShopInfoDto extends Info {

    private String isAll;

    private Ratetem rateTemInfo;
}
