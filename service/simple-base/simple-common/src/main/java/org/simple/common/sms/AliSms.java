package org.simple.common.sms;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import org.apache.commons.lang.StringUtils;
import org.simple.common.constant.RedisConstant;
import org.simple.common.dto.SmsDto;
import org.simple.common.utils.CommonResult;
import org.springframework.data.redis.core.RedisTemplate;

public class AliSms {

    private static AliSms aliSms = null;

    private static SmsDto smsDto;

    private AliSms() {
    }

    public static AliSms getInstance(RedisTemplate template) {
        if (null == aliSms) {
            aliSms = new AliSms();
        }
        //设置配置对象
        SmsDto var = BeanUtil.fillBeanWithMap(
                template.opsForHash().entries(RedisConstant.SMS_ALI), new SmsDto(),
                false);
        smsDto = var;
        return aliSms;
    }

    public static com.aliyun.dysmsapi20170525.Client createClient() throws Exception {
        Config config = new Config()
                // 您的 AccessKey ID
                .setAccessKeyId(smsDto.getSecretid())
                // 您的 AccessKey Secret
                .setAccessKeySecret(smsDto.getSecretkey());
        // 访问的域名
        if (StringUtils.isNotEmpty(smsDto.getEndpoint())) {
            config.endpoint = smsDto.getEndpoint();
        } else {
            config.endpoint = "dysmsapi.aliyuncs.com";
        }
        if (StringUtils.isNotEmpty(smsDto.getRegion())) {
            config.regionId = smsDto.getRegion();
        }
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    /**
     * 发送同步短信
     */
    public CommonResult sendAsyncSms(String signName, String[] phoneNumbers,
                                     String temId, JSONObject temParams) throws Exception {
        try {
            com.aliyun.dysmsapi20170525.Client client = createClient();
            SendSmsRequest sendSmsRequest = new SendSmsRequest();
            sendSmsRequest.setPhoneNumbers(StringUtils.join(phoneNumbers, ","));
            sendSmsRequest.setSignName(StringUtils.isEmpty(signName) ? smsDto.getSign() : signName);
            sendSmsRequest.setTemplateCode(temId);
            sendSmsRequest.setTemplateParam(JSONObject.toJSONString(temParams));
            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
            if (!sendSmsResponse.getBody().getCode().equals("OK")) {
                return CommonResult.failed("发送失败：" + sendSmsResponse.getBody().getMessage());
            }
        } catch (Exception ex) {
            return CommonResult.failed("发送失败：" + ex.getMessage());
        }

        return CommonResult.successNodata("发送成功");
    }
}
