package org.simple.center.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.simple.center.entity.Dictionary;
import org.simple.center.entity.Logs;
import org.simple.center.service.DictionaryService;
import org.simple.center.service.LogsService;
import org.simple.common.utils.CommonResult;
import org.simple.common.utils.RedomUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/inward")
@AllArgsConstructor
public class InwardController {

    private final LogsService centerLogsService;

    private final RedisTemplate redisTemplate;

    private final DictionaryService dictionaryService;

    @PostMapping("saveLogs")
    public CommonResult saveSimpleLog(@RequestBody org.simple.common.dto.Logs centerLogs) {
        Logs log = new Logs();
        BeanUtil.copyProperties(centerLogs, log);
        System.out.println("-----全局日志处理start------");
        log.setId(RedomUtil.getLogsId());
        log.setCreatetime(LocalDateTime.now());
        centerLogsService.save(log);
        System.out.println("-----全局日志处理end------");
        return CommonResult.success("成功");
    }

    @GetMapping("vals/{code}")
    public CommonResult listValues(@PathVariable("code") String code) {
        //先从缓存中拿
        Object o = redisTemplate.opsForValue().get(code);
        if(o != null){
            List array =  (List)o;
            if(array.size() ==0 ){
                return CommonResult.success(getVals(code));
            }else{
                return CommonResult.success(array);
            }
        }else{
            return CommonResult.success(getVals(code));
        }
    }

    private  List<Dictionary> getVals(String code){
        Dictionary dictionary = new Dictionary();
        dictionary.setCode(code);
        List<Dictionary> dists =  dictionaryService.list(
                Wrappers.query(dictionary).notIn("value", "#"));
        return dists;
    }

}
