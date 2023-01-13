package org.simple.shop.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.simple.common.utils.CommonResult;
import org.simple.security.annotation.SimpleLog;
import org.simple.security.utils.AuthUtils;
import org.simple.shop.dto.MarkDto;
import org.simple.shop.entity.Mark;
import org.simple.shop.service.MarkService;
import org.simple.shop.service.UserService;
import org.simple.shop.service.InfoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Copyright: simple
 * @Date: 2022-12-18 15:23:43
 * @Author: frsimple
 */

@RestController
@RequestMapping("/mark")
public class MarkController {

    @Resource
    private MarkService markService;

    @Resource
    private InfoService infoService;

    @Resource
    private UserService userService;



    @GetMapping("list")
    @SimpleLog("查询商品评价表")
    @PreAuthorize("hasAnyAuthority('shop:ordermark:query')")
    public CommonResult list(Page page, Mark mark) {
        IPage pageMark = null;
        if(StringUtils.isNotEmpty(mark.getOrderid())){
            String orderId = mark.getOrderid();
            mark.setOrderid(null);
            pageMark = markService.page(page,
                    Wrappers.query(mark).like("orderid",orderId).orderByDesc("create_time"));
        }else{
            pageMark = markService.page(page,
                    Wrappers.query(mark).orderByDesc("create_time"));
        }

        List<Mark> markList = pageMark.getRecords();
        List<MarkDto> setList = new ArrayList<>();
        markList.stream().forEach(obj -> {
            MarkDto o = new MarkDto();
            BeanUtil.copyProperties(obj, o);
            o.setGoodInfo(infoService.getById(obj.getSid()));
            o.setUser(userService.getById(obj.getUserid()));
            Mark r  = new Mark();
            r.setParentid(obj.getId());
            o.setMarkList(markService.list(Wrappers.query(r).orderByDesc("create_time")));
            setList.add(o);
        });
        pageMark.setRecords(setList);
        return CommonResult.success(pageMark);
    }

    @PostMapping("add")
    @SimpleLog("添加回复")
    @PreAuthorize("hasAnyAuthority('shop:ordermark:add')")
    public CommonResult add(@RequestBody Mark mark){
        mark.setUserid(AuthUtils.getUser().getId());
        mark.setCreateTime(LocalDateTime.now());
        mark.setStatus("1");
        mark.setIsShowname("0");
        markService.save(mark);

        //修改用户回复状态
        Mark m = markService.getById(mark.getParentid());
        m.setIsReply("1");
        m.setReplyTime(LocalDateTime.now());
        markService.updateById(m);
        return CommonResult.successNodata("添加回复成功");
    }

    @DeleteMapping("del/{id}")
    @SimpleLog("删除评价")
    @PreAuthorize("hasAnyAuthority('shop:ordermark:del')")
    public CommonResult del(@PathVariable("id")String id){
        Mark mark = markService.getById(id);
        if(mark.getParentid().equals("0")){
            Mark m = new Mark();
            m.setParentid(id);
            markService.remove(Wrappers.query(m));
            markService.removeById(id);
        }else{
            Mark m = new Mark();
            m.setParentid(mark.getParentid());
            //判断是否最后一个评论，如果最后一个评论
            if(markService.list(Wrappers.query(m)).size() == 1){
                m = markService.getById(mark.getParentid());
                m.setIsReply("0");
                m.setReplyTime(null);
                markService.updateById(m);
            }
            markService.removeById(id);
        }
        return CommonResult.successNodata("删除评价成功");
    }

}