package org.simple.shop.service.impl;

import cn.binarywang.wx.miniapp.bean.analysis.WxMaSummaryTrend;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.simple.common.utils.CommonResult;
import org.simple.common.utils.RedomUtil;
import org.simple.shop.dto.RegionTreeDto;
import org.simple.shop.mapper.MainimgMapper;
import org.simple.shop.mapper.OrderMapper;
import org.simple.shop.mapper.UserMapper;
import org.simple.shop.service.CommonService;
import org.simple.shop.service.RatetemService;
import org.simple.shop.utils.WxPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private RatetemService ratetemService;

    @Resource
    private MainimgMapper mainimgMapper;

    @Resource
    private OrderMapper  orderMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public List<Tree<String>> queryRegionTree() {
        List<RegionTreeDto> treeDtoList = new ArrayList<>();
        treeDtoList = ratetemService.queryRegionTree();
        TreeNodeConfig config = new TreeNodeConfig();
        config.setWeightKey("sort");
        List<Tree<String>> treeNodes = TreeUtil.build(treeDtoList, "1", config,
                (object, tree) -> {
                    tree.setName(object.getName());
                    tree.setId(object.getId());
                    tree.setParentId(object.getParentid());
                    tree.setWeight(StringUtils.isEmpty(object.getSort()) ? 0 : Integer.valueOf(object.getSort()));
                    tree.putExtra("value", object.getId());
                    tree.putExtra("label", object.getName());
                });
        return treeNodes;
    }

    @Override
    public CommonResult updateOrInsertHotSearch(String content) {
        List<Map<String, String>> list = mainimgMapper.selectOneHotSearch();
        if (null != list && list.size() != 0) {
            mainimgMapper.updateHotSearch(content, list.get(0).get("id").toString());
        } else {
            mainimgMapper.insertHotSearch(content, RedomUtil.getUuid());
        }
        return CommonResult.successNodata("操作成功");
    }

    @Override
    public String selectHotSearch() {
        List<Map<String, String>> list = mainimgMapper.selectOneHotSearch();
        if (null != list && list.size() != 0) {
            return list.get(0).get("content").toString();
        } else {
            return "";
        }
    }

    /**
     * 计算首页顶部数据
     */
    @Override
    public CommonResult homeTopData() {
        BigDecimal todayPayMoney =
                orderMapper.todayPayMoneyCount();
        BigDecimal yesterDayPayMoeny = orderMapper.yesterDayPayMoneyCount();
        Integer todayOrderCount = orderMapper.todayOrderCount();
        Integer yesterdayOrderCount = orderMapper.yesterDayOrderCount();
        Integer todayAddUserCount = userMapper.todayAddUserCount();
        Integer yesterDayAddUserCount = userMapper.yesterDayAddUserCount();
        BigDecimal payMoeny = todayPayMoney.subtract(yesterDayPayMoeny);
        BigDecimal orderCount =
                new BigDecimal(todayOrderCount).subtract(new BigDecimal(yesterdayOrderCount));
        BigDecimal addUserCount =
                new BigDecimal(todayAddUserCount).subtract(new BigDecimal(yesterDayAddUserCount));

        //调用微信接口获取访客数量
        CommonResult dataResult =
                WxPayUtil.getInstance().getDailySummaryTrend();
        Long todayUserCount = null;
        Long yesterDayUserCount = null;
        BigDecimal userCount = new BigDecimal(0);
        if (dataResult.getCode() == 0) {
            List<WxMaSummaryTrend> thrend = (List<WxMaSummaryTrend>) dataResult.getData();
            todayUserCount = Long.valueOf(0);
            yesterDayUserCount = thrend.get(0).getVisitTotal();
            userCount =
                    new BigDecimal(todayUserCount).subtract(new BigDecimal(yesterDayUserCount));
        }

        JSONObject result = new JSONObject();
        result.put("todayPayMoney", todayPayMoney);
        result.put("yesterDayPayMoeny", yesterDayPayMoeny);
        result.put("todayOrderCount", todayOrderCount);
        result.put("yesterdayOrderCount", yesterdayOrderCount);
        result.put("todayAddUserCount", todayAddUserCount);
        result.put("yesterDayAddUserCount", yesterDayAddUserCount);
        result.put("todayUserCount", todayUserCount);
        result.put("yesterDayUserCount", yesterDayUserCount);


        result.put("payMoeny", payMoeny);
        result.put("orderCount", orderCount);
        result.put("addUserCount", addUserCount);
        result.put("userCount", userCount);

        return CommonResult.success(result);
    }
}
