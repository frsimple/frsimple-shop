package org.simple.shop.dto;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import org.simple.shop.entity.Mark;
import org.simple.shop.entity.User;
import org.simple.shop.entity.Info;

import java.util.List;

@Data
public class MarkDto extends Mark {

    private String nickName;
    private String avatar;
    private Info goodInfo;
    private User user;
    private JSONArray goodList;
    private List<Mark> markList;
}
