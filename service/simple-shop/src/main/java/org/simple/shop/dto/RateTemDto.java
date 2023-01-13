package org.simple.shop.dto;

import lombok.Data;
import org.simple.shop.entity.Ratetem;

import java.io.Serializable;


@Data
public class RateTemDto extends Ratetem implements Serializable {

    private String sendRegName;
    private String nosendArray;

}
