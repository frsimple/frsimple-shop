package org.simple.grant.feign;

import cn.hutool.json.JSONObject;
import org.simple.common.utils.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "feignShopServer", value = "simple-shop")
public interface FeignShopServer {
    @PostMapping("/inward/user")
    CommonResult getUser(@RequestBody JSONObject jsonObject);
}
