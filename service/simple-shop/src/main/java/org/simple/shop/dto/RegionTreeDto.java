package org.simple.shop.dto;


import lombok.Data;

@Data
public class RegionTreeDto {

    private String id;
    private String name;
    private String parentid;
    private String sort;
}
