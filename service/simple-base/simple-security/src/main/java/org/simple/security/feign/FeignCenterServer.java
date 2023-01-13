package org.simple.security.feign;

import org.simple.common.dto.Logs;
import org.simple.common.utils.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "feignCenterServer", value = "simple-center")
public interface FeignCenterServer {


    @PostMapping("/inward/saveLogs")
    CommonResult saveLogs(@RequestBody Logs centerLogs);

    @GetMapping("/inward/vals/{code}")
    CommonResult listValues(@PathVariable("code") String code);

}
