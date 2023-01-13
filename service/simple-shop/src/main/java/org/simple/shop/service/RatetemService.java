package org.simple.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.simple.common.utils.CommonResult;
import org.simple.shop.dto.RateTemDto;
import org.simple.shop.dto.RegionTreeDto;
import org.simple.shop.entity.Ratetem;

import java.util.List;


/**
 * @Copyright: simple
 * @Date: 2022-11-20 19:02:44
 * @Author: frsimple
 */
public interface RatetemService extends IService<Ratetem> {

    List<RegionTreeDto> queryRegionTree();

    List<RateTemDto> queryRataTemList();

    CommonResult  saveOrUpdateTem(RateTemDto rateTemDto);

}