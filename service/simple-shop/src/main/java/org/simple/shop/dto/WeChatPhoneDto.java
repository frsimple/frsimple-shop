package org.simple.shop.dto;

import lombok.Data;

@Data
public class WeChatPhoneDto {

    private String sessionKey;
    private String encryptedData;
    private String ivStr;
}
