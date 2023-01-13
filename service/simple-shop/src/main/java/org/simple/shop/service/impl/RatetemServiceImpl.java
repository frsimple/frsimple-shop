package org.simple.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.simple.common.utils.CommonResult;
import org.simple.security.utils.AuthUtils;
import org.simple.shop.dto.RateTemDto;
import org.simple.shop.dto.RegionTreeDto;
import org.simple.shop.entity.Ratetem;
import org.simple.shop.mapper.RatetemMapper;
import org.simple.shop.service.RatetemService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Copyright: simple
 * @Date: 2022-11-20 19:02:44
 * @Author: frsimple
 */


@Service
public class RatetemServiceImpl
        extends ServiceImpl<RatetemMapper, Ratetem>
        implements RatetemService {


    @Override
    public List<RegionTreeDto> queryRegionTree() {
        return baseMapper.queryRegionTree();
    }

    @Override
    public List<RateTemDto> queryRataTemList() {
        return baseMapper.queryRateTemList(AuthUtils.getUser().getTenantId());
    }

    @Override
    public CommonResult saveOrUpdateTem(RateTemDto rateTemDto) {
        rateTemDto.setTenantid(AuthUtils.getUser().getTenantId());
        //解析数据将nosendArray解析中文
        List<String> idNames = new ArrayList<>();
        for(String str:rateTemDto.getNosendArray().split(",")){
            idNames.add(getRegText(str));
        }
        JSONObject nosend = new JSONObject();
        nosend.put("value",rateTemDto.getNosendArray());
        nosend.put("label",StringUtils.join(idNames,","));
        rateTemDto.setNosend(nosend);
        if(StringUtils.isEmpty(rateTemDto.getId())){
            baseMapper.insert(rateTemDto);
        }else{
            baseMapper.updateById(rateTemDto);
        }
        return CommonResult.successNodata("操作成功");
    }


    private String getRegText(String id) {
        List<String> ids = new ArrayList<>();
        ids.add(id);
        boolean isNext = true;
        while (isNext) {
            String parentId = baseMapper.getParentId(id);
            if (StringUtils.isEmpty(parentId)) {
                isNext = false;
                continue;
            }
            if (parentId.equals("1")) {
                isNext = false;
                continue;
            } else {
                id = parentId;
                ids.add(parentId);
            }
        }
        return baseMapper.queryRegTreeName(StringUtils.join(ids,","));
    }
}