package org.simple.grant.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@TableName("center_user")
public class UserEntity {

    @TableId
    private String id;

    private String username;

    private String password;

    private String nickname;

    private String phone;

    private String email;

    private String status;

    private String avatar;

    private LocalDateTime createdate;

    private LocalDateTime updatedate;

    private LocalDateTime logindate;

    /**
     * 主机构
     */
    private String tenant;
    private Integer error;
}
