package org.simple.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WxPayConfig {


    public static String APPID;

    public static String APPSECRET;

    public static String MCHID;

    public static String MCHSERIALNO;

    public static String PRIVATECERTPATH;

    public static String PRIVATEKEYPATH;

    public static String APIV3KEY;

    public static String NOTIFY_URL;

    public static String REF_NOTIFY_URL;

    @Value("${wxpay.appid}")
    public void setAppId(String appid) {
        APPID = appid;
    }

    @Value("${wxpay.appSecret}")
    public void setAppSecret(String appSecret) {
        APPSECRET = appSecret;
    }

    @Value("${wxpay.mchid}")
    public void setMchid(String mchid) {
        MCHID = mchid;
    }

    @Value("${wxpay.certPath}")
    public void setCertPath(String certPath) {
        PRIVATECERTPATH = certPath;
    }
    @Value("${wxpay.keyPath}")
    public void setKeyPath(String keyPath) {
        PRIVATEKEYPATH = keyPath;
    }

    @Value("${wxpay.apiv3key}")
    public void setApiV3Key(String apiv3key) {
        APIV3KEY = apiv3key;
    }

    @Value("${wxpay.mchserialno}")
    public void setMchSerialno(String mchserialno) {
        MCHSERIALNO = mchserialno;
    }

    @Value("${wxpay.notifyurl}")
    public void setNotifyurl(String notifyurl) {
        NOTIFY_URL = notifyurl;
    }

    @Value("${wxpay.refnotifyurl}")
    public void setRefNotifyurl(String refnotifyurl) {
        REF_NOTIFY_URL = refnotifyurl;
    }
}
