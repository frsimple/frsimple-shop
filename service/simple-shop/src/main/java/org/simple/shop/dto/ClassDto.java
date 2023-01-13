package org.simple.shop.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClassDto {


    private String id;
    private String name;
    private String parentid;
    private String parentname;
    private String tenantid;
    private String sort;
    private LocalDateTime createtime;
    private Integer level;
    private String url;

}
