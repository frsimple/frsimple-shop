package org.simple.shop.controller;


import org.simple.common.utils.CommonResult;
import org.simple.shop.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dataDic")
public class DataController {

    @Autowired
    private CommonService commonService;


    /**
     * 查询省市区树形结构
     * */
    @GetMapping("regTree")
    public CommonResult regTree(){
        return CommonResult.success(commonService.queryRegionTree());
    }

    @GetMapping("homeTop")
    public CommonResult homeTop(){
      return  commonService.homeTopData();
    }
}
