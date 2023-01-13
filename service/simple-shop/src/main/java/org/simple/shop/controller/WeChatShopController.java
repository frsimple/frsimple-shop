package org.simple.shop.controller;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.simple.common.utils.CommonResult;
import org.simple.shop.dto.MarkDto;
import org.simple.shop.dto.MarkParams;
import org.simple.shop.entity.Mark;
import org.simple.shop.entity.Class;
import org.simple.shop.entity.User;
import org.simple.shop.entity.Info;
import org.simple.shop.service.MarkService;
import org.simple.shop.service.ClassService;
import org.simple.shop.service.UserService;
import org.simple.shop.service.InfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wechat/shop")
public class WeChatShopController {


    @Resource
    private MarkService markService;


    @Resource
    private UserService userService;

    @Resource
    private InfoService infoService;

    @Resource
    private ClassService classService;

    /**
     * 查询商品评价信息
     */
    @GetMapping("/api/markInfo/{id}")
    public CommonResult markInfo(@PathVariable("id") String id) {
        Mark mark = new Mark();
        mark.setStatus("1");
        mark.setSid(id);
        mark.setParentid("0");
        Page page = new Page();
        page.setCurrent(1);
        page.setSize(5);
        //查询最近5条评论
        List<Mark> marks =
                markService.page(page, Wrappers.query(mark).orderByDesc("create_time"))
                        .getRecords();
        List<MarkDto> markList = new ArrayList<MarkDto>();
        if (null != marks && marks.size() != 0) {
            markList =
                    marks.stream().map(obj -> {
                        MarkDto markDto = new MarkDto();
                        User shopUser = userService.getById(obj.getUserid());
                        BeanUtil.copyProperties(obj, markDto);
                        markDto.setAvatar(shopUser.getAvatar());
                        markDto.setNickName(shopUser.getName());
                        return markDto;
                    }).collect(Collectors.toList());
        }
        //计算评价总数
        String markCountStr = "";
        BigDecimal markCount = new BigDecimal(markService.list(Wrappers.query(mark)).size());
        if (markCount.compareTo(new BigDecimal("999")) > 0) {
            markCountStr = "999+";
        } else {
            markCountStr = markCount.toString();
        }
        //查询好评率
        String markRate = "0";
        BigDecimal goodMarks = new BigDecimal(markService.
                list(Wrappers.query(mark).ge("mark", 4)).size());
        if (goodMarks.compareTo(new BigDecimal(0)) != 0 &&
                markCount.compareTo(new BigDecimal(0)) != 0) {
            markRate = (goodMarks.divide(markCount, 2, BigDecimal.ROUND_HALF_DOWN).
                    multiply(new BigDecimal(100))).toString();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("markCount", markCountStr);
        jsonObject.put("markList", markList);
        jsonObject.put("markRate", markRate);
        return CommonResult.success(jsonObject);
    }


    /**
     * 查询商品评论统计数据
     */
    @GetMapping("/api/markData/{id}")
    public CommonResult markData(@PathVariable("id") String id) {
        Mark mark = new Mark();
        mark.setStatus("1");
        mark.setSid(id);
        mark.setParentid("0");
        List<Mark> allMark = markService.list(Wrappers.query(mark));
        List<Mark> goodMark = markService.list(Wrappers.query(mark).ge("mark", 4));
        List<Mark> midMark = markService.list(Wrappers.query(mark).eq("mark", 3));
        List<Mark> lowMark = markService.list(Wrappers.query(mark).le("mark", 2));
        JSONObject result = new JSONObject();
        result.put("good", goodMark.size());
        result.put("mid", midMark.size());
        result.put("low", lowMark.size());
        result.put("all",allMark.size());
        return CommonResult.success(result);
    }

    /**
     * 查询商品评价
     */
    @GetMapping("/api/markList/{id}")
    public CommonResult markList(@PathVariable("id") String id,Page page, MarkParams params) {
        Mark mark = new Mark();
        mark.setStatus("1");
        mark.setSid(id);
        mark.setParentid("0");
        IPage pageMark = null;
        if (null != params.getMark()) {
            if(3 == params.getMark().intValue()){
                pageMark = markService.page(page,Wrappers.query(mark)
                        .eq("mark", params.getMark().intValue()).orderByDesc("create_time"));
            }else if(2 == params.getMark().intValue()){
                pageMark = markService.page(page,Wrappers.query(mark)
                        .le("mark", params.getMark().intValue()).orderByDesc("create_time"));
            } else{
                pageMark = markService.page(page,Wrappers.query(mark)
                        .ge("mark", params.getMark().intValue()).orderByDesc("create_time"));
            }

        } else {
            pageMark = markService.page(page,Wrappers.query(mark).orderByDesc("create_time"));
        }
        List<Mark> list = pageMark.getRecords();
        List<MarkDto> markList = new ArrayList<>();
        if (null != list && list.size() != 0) {
            markList =
                    list.stream().map(obj -> {
                        MarkDto markDto = new MarkDto();
                        User shopUser = userService.getById(obj.getUserid());
                        BeanUtil.copyProperties(obj, markDto);
                        markDto.setAvatar(shopUser.getAvatar());
                        markDto.setNickName(shopUser.getName());
                        Mark m = new Mark();
                        m.setParentid(obj.getId());
                        markDto.setMarkList(markService.list(Wrappers.query(m)));
                        return markDto;
                    }).collect(Collectors.toList());
        }
        pageMark.setRecords(markList);
        return CommonResult.success(pageMark);
    }

    /**
     * 搜索商品
     */
    @GetMapping("/api/resultGoods")
    public CommonResult resultGoods(Page page, Info shopParams) {
        if (StringUtils.isNotEmpty(shopParams.getName())) {
            String name = shopParams.getName();
            shopParams.setName(null);
            Class shopClass = new Class();
            shopClass.setName(name);
            shopParams.setStatus("01");
            List<Class> list = classService.list(Wrappers.query(shopClass));
            if (list.size() != 0) {
                String cate = list.get(0).getId();
                return CommonResult.success(infoService.page(page,
                        Wrappers.query(shopParams).like("name", name).
                                or().like("tabs", name).or().eq("cate", cate)));
            }
            return CommonResult.success(infoService.page(page,
                    Wrappers.query(shopParams).like("name", name).
                            or().like("tabs", name)));
        }
        return CommonResult.success(infoService.page(page, Wrappers.query(shopParams)));
    }
}
