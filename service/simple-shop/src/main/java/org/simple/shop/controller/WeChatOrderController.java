package org.simple.shop.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.DesensitizedUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import org.apache.commons.lang.StringUtils;
import org.simple.common.storage.OssUtil;
import org.simple.common.utils.ComUtil;
import org.simple.common.utils.CommonResult;
import org.simple.common.utils.RedomUtil;
import org.simple.security.feign.FeignCenterServer;
import org.simple.security.utils.AuthUtils;
import org.simple.shop.constant.ShopContant;
import org.simple.shop.dto.CartDto;
import org.simple.shop.dto.MarkDto;
import org.simple.shop.dto.OrderDto;
import org.simple.shop.dto.WxPayParams;
import org.simple.shop.entity.*;
import org.simple.shop.service.*;
import org.simple.shop.utils.WxPayUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/wechat/order")
public class WeChatOrderController {

    @Resource
    private ReinfoService reinfoService;

    @Resource
    private RatetemService ratetemService;

    @Resource
    private CartService cartService;

    @Resource
    private InfoService infoService;

    @Resource
    private OrderService orderService;

    @Resource
    private TransService transService;
    @Resource
    private UserService userService;

    @Resource
    private OrderDetailsService orderDetailsService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private FeignCenterServer feignCenterServer;

    @Resource
    private MarkService markService;

    @Resource
    private AftersalesService aftersalesService;


    /**
     * 创建订单并生成支付单号
     */
    @PostMapping("create")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult orderCreate() {

        return null;
    }

    /**
     * 查询默认收货地址/查询单个收货地址
     */
    @GetMapping("queryDefaultRevAddress")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult queryDefaultRevAddress(@RequestParam(required = false,
            value = "id") String id) {
        if (StringUtils.isNotEmpty(id)) {
            Reinfo reinfo = reinfoService.getById(id);
            reinfo.setRphone(DesensitizedUtil.mobilePhone(reinfo.getRphone()));
            return CommonResult.success(reinfo);
        } else {
            Reinfo reinfo = new Reinfo();
            reinfo.setUserid(AuthUtils.getUser().getId());
            reinfo.setIsDefault("1");
            List<Reinfo> list = reinfoService.list(Wrappers.query(reinfo));
            if (list.size() == 0) {
                return CommonResult.success(null);
            } else {
                Reinfo reinfo1 = new Reinfo();
                reinfo1 = list.get(0);
                reinfo1.setRphone(DesensitizedUtil.mobilePhone(reinfo1.getRphone()));
                return CommonResult.success(reinfo1);
            }
        }
    }

    /**
     * 加入购物车
     */
    @PostMapping("addCart")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult addCart(@RequestBody Cart cart) {
        Cart c = new Cart();
        c.setProdId(cart.getProdId());
        c.setUserId(AuthUtils.getUser().getId());
        if(StringUtils.isNotEmpty(cart.getSkuId())){
            c.setSkuId(cart.getSkuId());
        }
        List<Cart> list = cartService.list(Wrappers.query(c));
        if(list.size() == 0){
            cart.setStatus("1");
            cart.setCreateTime(LocalDateTime.now());
            cart.setUserId(AuthUtils.getUser().getId());
            cartService.save(cart);
        }else{
            c = list.get(0);
            c.setNum(new BigDecimal(cart.getNum()).add(new BigDecimal(c.getNum())).longValue());
            cartService.updateById(c);
        }
        return CommonResult.success("添加成功");
    }

    /**
     * 查询购物车信息
     */

    @GetMapping("queryCart")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult queryCart() {
        List<CartDto> list = cartService.queryCartList();
        if (list.size() != 0) {
            List<CartDto> result = new ArrayList<CartDto>();
            for (int l = 0; l < list.size(); l++) {
                CartDto obj = list.get(l);
                Info shopinfo =
                        infoService.getById(obj.getSpuId());
                obj.setIsSelected(false);
                obj.setGoodsName(shopinfo.getName());
                obj.setRateTemInfo(ratetemService.
                        getById(shopinfo.getRateTem()));
                if (shopinfo.getIsSingle().equals("0")) {
                    if (StringUtils.isEmpty(obj.getSkuId())) {
                        obj.setIsCanBuy(false);
                        result.add(obj);
                        continue;
                    }
                    JSONArray skuList = shopinfo.getSkulist();
                    for (int i = 0; i < skuList.size(); i++) {
                        JSONObject sku =
                                skuList.getJSONObject(i);
                        if (sku.getString("id").equals(obj.getSkuId())) {
                            obj.setSelectedSkuInfo(sku);
                            obj.setStock(sku.getInteger("inventory"));
                            obj.setWeight(sku.getBigDecimal("weight"));
                            obj.setIsCanBuy(
                                    sku.getInteger("inventory").intValue()
                                            > 0
                            );
                            obj.setPrice(sku.getBigDecimal("price"));
                            break;
                        }
                    }
                } else {
                    obj.setPrimaryImage(shopinfo.getMainimg());
                    obj.setPrice(new BigDecimal(shopinfo.getPrice()));
                    if (StringUtils.isNotEmpty(obj.getSkuId())) {
                        obj.setIsCanBuy(false);
                        result.add(obj);
                        continue;
                    }
                    obj.setWeight(new BigDecimal(shopinfo.getWeight()));
                    obj.setStock(Integer.valueOf(shopinfo.getInventory()));
                    obj.setIsCanBuy(
                            Integer.valueOf(shopinfo.getInventory()).intValue()
                                    > 0
                    );
                }
                result.add(obj);
            }
            return CommonResult.success(result);
        } else {
            return CommonResult.success(new ArrayList<>());
        }
    }

    @DeleteMapping("delCart")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult delCart(@RequestBody JSONObject jsonObject) {
        cartService.deleteCart(jsonObject.getString("ids"));
        return CommonResult.successNodata("删除成功");
    }

    /**
     * 用户下单
     */
    @PostMapping("createOrder")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult createOrder(@RequestBody Order shopOrder) {
        //1.先校验是否有库存
        JSONArray jsonArray = shopOrder.getGoods();
        String body = "";
        List<Info> fileter = new ArrayList<Info>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            int buyNum = obj.getIntValue("buyNum");
            String skuId = obj.getString("skuId");
            Info shopinfo = infoService.getById(obj.getString("spuId"));
            body = shopinfo.getName();
            if (shopinfo.getIsSingle().equals("1")) {
                if (buyNum > Integer.valueOf(shopinfo.getInventory()).intValue()) {
                    return CommonResult.failed("库存不足[" + shopinfo.getName() + "]");
                }
                shopinfo.setInventory(
                        String.valueOf(Integer.valueOf(shopinfo.getInventory()).intValue() - buyNum));
            } else {
                JSONArray skuList = shopinfo.getSkulist();
                for (int j = 0; j < skuList.size(); j++) {
                    JSONObject sku = skuList.getJSONObject(j);
                    if (sku.getString("id").equals(skuId)) {
                        if (sku.getIntValue("inventory") < buyNum) {
                            return CommonResult.failed("库存不足[" + shopinfo.getName() + "]");
                        }
                        sku.put("inventory", sku.getIntValue("inventory") - buyNum);
                        skuList.set(j, sku);
                        break;
                    }
                }
                shopinfo.setSkulist(skuList);
            }
            shopinfo.setSales(Integer.valueOf(shopinfo.getSales().intValue() + buyNum));
            fileter.add(shopinfo);
        }
        fileter.stream().forEach(obj -> {
            //修改商品销量和库存等数据
            Info s = new Info();
            s.setInventory(obj.getInventory());
            s.setSkulist(obj.getSkulist());
            s.setId(obj.getId());
            s.setSales(obj.getSales());
            infoService.updateById(s);
        });
        //2.生成订单数据
        //循环商品生产订单关联商品唯一ID
        JSONArray goodArray = new JSONArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject good = jsonArray.getJSONObject(i);
            JSONObject nGood = new JSONObject();
            nGood.put("price", good.get("price"));
            nGood.put("skuId", good.get("skuId"));
            nGood.put("spuId", good.get("spuId"));
            nGood.put("buyNum", good.get("buyNum"));
            nGood.put("weight", good.get("weight"));
            nGood.put("goodsName", good.get("goodsName"));
            nGood.put("rateTemInfo", good.get("rateTemInfo"));
            nGood.put("primaryImage", good.get("primaryImage"));
            nGood.put("selectedSkuInfo", good.get("selectedSkuInfo"));
            nGood.put("id", RedomUtil.getUuid());
            goodArray.add(nGood);
        }
        //获取未加密的手机号
        JSONObject takeInfo = shopOrder.getTakeInfo();
        Reinfo r = reinfoService.getById(takeInfo.getString("id"));
        takeInfo.put("rphonedec", r.getRphone());
        shopOrder.setTakeInfo(takeInfo);
        String orderId = RedomUtil.getOrderId();
        shopOrder.setCreatetime(LocalDateTime.now());
        shopOrder.setUserid(AuthUtils.getUser().getId());
        shopOrder.setStatus("00");  //已下单待付款
        shopOrder.setId(orderId);
        shopOrder.setIsrefund("0");
        shopOrder.setGoods(goodArray);
        //3.创建订单支付
        Trans trans = new Trans();
        String Uuid = RedomUtil.getTransId();
        trans.setId(Uuid);
        trans.setOrderId(orderId);
        trans.setUserid(AuthUtils.getUser().getId());
        trans.setPaytype(shopOrder.getPaytype());
        trans.setDatasource(shopOrder.getDatasource());
        trans.setCreateTime(shopOrder.getCreatetime());
        trans.setStatus("00");//默认未支付
        trans.setMoney(shopOrder.getRprice());
        //调用微信接口返回预支付订单ID
        WxPayParams wxPayParams = new WxPayParams();
        wxPayParams.setBody("商品[" + body + "]");
        wxPayParams.setPayMoney(new BigDecimal(shopOrder.getPrice()));
        wxPayParams.setOrderId(Uuid);
        wxPayParams.setOpenId(userService.getById(AuthUtils.getUser().getId()).getOpenid());
        CommonResult result =
                WxPayUtil.getInstance().transJsapi(wxPayParams);
        if (result.getCode() != 0) {
            return CommonResult.failed(result.getMsg());
        }
        JSONObject payJson = (JSONObject) result.getData();
        trans.setPayJson(payJson);
        transService.save(trans);
        shopOrder.setPayId(Uuid);
        orderService.save(shopOrder);
        //4.生成订单流水信息
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCreateTime(LocalDateTime.now());
        orderDetails.setType("00"); //创建订单
        orderDetails.setRemark("生成订单");
        orderDetails.setOrderId(orderId);
        orderDetailsService.save(orderDetails);
        //5.返回订单ID和支付的preid
        JSONObject returnObj = new JSONObject();
        returnObj.put("orderId", orderId);
        returnObj.put("payJson", result.getData());

        //添加支付过期监听，如果订单超过15分钟未支付则自动取消订单
        redisTemplate.opsForValue().set(ShopContant.SHOP_ORDER_KEY + orderId,
                orderId, ShopContant.SHOP_ORDER_EXPIRE_TIME, TimeUnit.MINUTES);
        return CommonResult.success(returnObj);
    }

    @GetMapping("queryOrder")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult queryOrder(Page page, Order shopOrder) {
        shopOrder.setUserid(AuthUtils.getUser().getId());
        if (StringUtils.isNotEmpty(shopOrder.getStatus()) &&
                shopOrder.getStatus().equals("99")) {
            shopOrder.setStatus(null);
            return CommonResult.success(orderService.page(page,
                    Wrappers.query(shopOrder).in("status", "99").orderByDesc("createtime")));
        } else {
            return CommonResult.success(orderService.page(page,
                    Wrappers.query(shopOrder).orderByDesc("createtime")));
        }

    }

    @GetMapping("getOneOrder/{id}")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult getOneOrder(@PathVariable("id") String id) {
        OrderDto orderDto = new OrderDto();
        Order shopOrder = orderService.getById(id);
        BeanUtil.copyProperties(shopOrder, orderDto);
        OrderDetails o = new OrderDetails();
        o.setOrderId(id);
        orderDto.setOrderDetailsList(orderDetailsService.list(
                Wrappers.query(o).orderByDesc("create_time")));
        return CommonResult.success(orderDto);
    }

    @GetMapping("dict/{code}")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult queryDict(@PathVariable("code") String code) {
        return feignCenterServer.listValues(code);
    }

    /**
     * 取消订单（未支付）
     */
    @GetMapping("canlOrder/{id}")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult canlOrder(@PathVariable("id") String id) {
        //先获取订单
        Order shopOrder = orderService.getById(id);
        if (null == shopOrder || !shopOrder.getUserid().equals(AuthUtils.getUser().getId())) {
            return CommonResult.failed("非法请求!");
        }
        if (!shopOrder.getStatus().equals("00")) {
            return CommonResult.failed("非法请求!");
        }
        shopOrder.setStatus("-1");
        orderService.updateById(shopOrder);
        //记录操作流水
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrderId(id);
        orderDetails.setRemark("用户主动取消订单");
        orderDetails.setCreateTime(LocalDateTime.now());
        orderDetails.setType("-1");
        orderDetailsService.save(orderDetails);
        //更改支付流水状态改为作废
        Trans trans = transService.getById(shopOrder.getPayId());
        trans.setStatus("-1");
        transService.updateById(trans);
        //回退库存和销量
        JSONArray jsonArray = shopOrder.getGoods();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            int buyNum = obj.getIntValue("buyNum");
            String skuId = obj.getString("skuId");
            Info shopinfo = infoService.getById(obj.getString("spuId"));
            if (shopinfo.getIsSingle().equals("1")) {
                shopinfo.setInventory(
                        String.valueOf(Integer.valueOf(shopinfo.getInventory()).intValue() + buyNum));
            } else {
                JSONArray skuList = shopinfo.getSkulist();
                for (int j = 0; j < skuList.size(); j++) {
                    JSONObject sku = skuList.getJSONObject(j);
                    if (sku.getString("id").equals(skuId)) {
                        sku.put("inventory", sku.getIntValue("inventory") + buyNum);
                        skuList.set(j, sku);
                        break;
                    }
                }
                shopinfo.setSkulist(skuList);
            }
            shopinfo.setSales(Integer.valueOf(shopinfo.getSales().intValue() - buyNum));
            //修改商品销量和库存等数据
            Info ss = new Info();
            ss.setInventory(shopinfo.getInventory());
            ss.setSkulist(shopinfo.getSkulist());
            ss.setId(shopinfo.getId());
            ss.setSales(shopinfo.getSales());
            infoService.updateById(ss);
            //删除redis自动取消订单key
            redisTemplate.delete(ShopContant.SHOP_ORDER_KEY + id);
        }
        return CommonResult.success("订单取消成功");
    }

    /**
     * 取消订单(已支付未发货)
     */
    @GetMapping("canlOrder1/{id}")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult canlOrder1(@PathVariable("id") String id) {
        //先获取订单
        Order shopOrder = orderService.getById(id);
        if (null == shopOrder || !shopOrder.getUserid().equals(AuthUtils.getUser().getId())) {
            return CommonResult.failed("非法请求!");
        }
        if (!shopOrder.getStatus().equals("01")) {
            return CommonResult.failed("非法请求!");
        }
        //先退款，退款成功之后在进行后续操作
        String refNo = RedomUtil.getUuid();
        WxPayParams params = new WxPayParams();
        params.setOrderId(shopOrder.getPayId());
        params.setOutRefundNo(refNo);
        params.setRefundPrice(new BigDecimal(
                StringUtils.isNotEmpty(shopOrder.getNprice()) ? shopOrder.getNprice() :
                        shopOrder.getPrice()
        ));
        params.setPayMoney(new BigDecimal(
                StringUtils.isNotEmpty(shopOrder.getNprice()) ? shopOrder.getNprice() :
                        shopOrder.getPrice()
        ));
        CommonResult refResult =
                WxPayUtil.getInstance().transJsapiRef(params);
        JSONArray refJson = new JSONArray();
        if (refResult.getCode() == 0) {
            WxPayRefundV3Result wxResult =
                    (WxPayRefundV3Result) refResult.getData();
            if (!wxResult.getStatus().equals("ABNORMAL")) {
                JSONObject refObj = new JSONObject();
                refObj.put("refNo", refNo);
                refObj.put("isReceived", "0");
                refJson.add(refObj);
            } else {
                return CommonResult.failed("订单取消失败:退款异常");
            }
        } else {
            return CommonResult.failed(refResult.getMsg());
        }
        shopOrder.setTstatus("0");
        shopOrder.setIsrefund("2");
        shopOrder.setStatus("-1");
        JSONArray goods = shopOrder.getGoods();
        for (int i = 0; i < goods.size(); i++) {
            JSONObject good = goods.getJSONObject(i);
            good.put("refNo", refNo);
            good.put("isReceived", "0");
            good.put("refPrice", new BigDecimal(
                    StringUtils.isNotEmpty(shopOrder.getNprice()) ? shopOrder.getNprice() :
                            shopOrder.getPrice()));
            goods.set(i, good);
        }
        shopOrder.setGoods(goods);
        orderService.updateById(shopOrder);
        //记录操作流水
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrderId(id);
        orderDetails.setRemark("用户主动取消订单且系统自动退款!");
        orderDetails.setCreateTime(LocalDateTime.now());
        orderDetails.setType("-2");
        orderDetailsService.save(orderDetails);
        //更改支付流水状态改为作废
        Trans trans = transService.getById(shopOrder.getPayId());
        //trans.setStatus("-1");
        trans.setReffund(refJson);
        trans.setRefstatus("2");
        transService.updateById(trans);
        //回退库存和销量
        JSONArray jsonArray = shopOrder.getGoods();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            int buyNum = obj.getIntValue("buyNum");
            String skuId = obj.getString("skuId");
            Info shopinfo = infoService.getById(obj.getString("spuId"));
            if (shopinfo.getIsSingle().equals("1")) {
                shopinfo.setInventory(
                        String.valueOf(Integer.valueOf(shopinfo.getInventory()).intValue() + buyNum));
            } else {
                JSONArray skuList = shopinfo.getSkulist();
                for (int j = 0; j < skuList.size(); j++) {
                    JSONObject sku = skuList.getJSONObject(j);
                    if (sku.getString("id").equals(skuId)) {
                        sku.put("inventory", sku.getIntValue("inventory") + buyNum);
                        skuList.set(j, sku);
                        break;
                    }
                }
                shopinfo.setSkulist(skuList);
            }
            shopinfo.setSales(Integer.valueOf(shopinfo.getSales().intValue() - buyNum));
            //修改商品销量和库存等数据
            Info ss = new Info();
            ss.setInventory(shopinfo.getInventory());
            ss.setSkulist(shopinfo.getSkulist());
            ss.setId(shopinfo.getId());
            ss.setSales(shopinfo.getSales());
            infoService.updateById(ss);
        }
        return CommonResult.success("订单取消成功");
    }

    /**
     * 确认收货
     */
    @GetMapping("confirmOrder/{id}")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult confirmOrder(@PathVariable("id") String id) {
        Order shopOrder = orderService.getById(id);
        if (!shopOrder.getStatus().equals("02") ||
                !shopOrder.getUserid().equals(AuthUtils.getUser().getId())) {
            return CommonResult.failed("非法操作！");
        }
        shopOrder.setStatus("03"); //已收货待评价
        orderService.updateById(shopOrder);
        //记录操作流水
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setType("02");
        orderDetails.setRemark("确认收货，待评价");
        orderDetails.setCreateTime(LocalDateTime.now());
        orderDetails.setOrderId(id);
        orderDetailsService.save(orderDetails);
        //添加redis，5天自动好评。
        redisTemplate.opsForValue().set(ShopContant.SHOP_ORDER_COMMENT + id,
                id, ShopContant.SHOP_ORDER_EXPIRE_COMMENT, TimeUnit.DAYS);
        return CommonResult.successNodata("确认收货成功");
    }

    /**
     * 商品评价
     */
    @PostMapping("markOrder")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult markOrder(@RequestBody MarkDto mark) {
        Order shopOrder = orderService.getById(mark.getOrderid());
        if (!shopOrder.getStatus().equals("03") ||
                !shopOrder.getUserid().equals(AuthUtils.getUser().getId())) {
            return CommonResult.failed("非法操作！");
        }
        JSONArray goodList = mark.getGoodList();
        for (int i = 0; i < goodList.size(); i++) {
            JSONObject obj = goodList.getJSONObject(i);
            JSONObject good = new JSONObject();
            good.put("buyNum", obj.getIntValue("num"));
            good.put("price", obj.getBigDecimal("price"));
            good.put("skuId", StringUtils.isNotEmpty(obj.getString("skuId")) ?
                    obj.getString("skuId") : "");
            good.put("specs", obj.getString("specs"));
            good.put("goodsName", obj.getString("title")); //商标标题
            good.put("primaryImage", obj.getString("thumb")); //商品图片
            Mark m = new Mark();
            m.setOrderid(mark.getOrderid());
            m.setContent(obj.getString("remark"));
            m.setMark(obj.getIntValue("rate"));
            m.setImgs(obj.getJSONArray("imgs").size() != 0 ?
                    StringUtils.join(obj.getJSONArray("imgs"), ",") : "");
            m.setSid(obj.getString("spuId"));
            m.setGood(good);
            m.setParentid("0");
            m.setStatus("1");
            m.setCreateTime(LocalDateTime.now());
            m.setUserid(AuthUtils.getUser().getId());
            m.setIsReply("0");
            m.setIsShowname(mark.getIsShowname());
            markService.save(m);
        }
        //更新订单状态为  已完成
        shopOrder.setStatus("99");
        orderService.updateById(shopOrder);

        //记录操作流水
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setType("99");
        orderDetails.setRemark("用户评价完成");
        orderDetails.setCreateTime(LocalDateTime.now());
        orderDetails.setOrderId(mark.getOrderid());
        orderDetailsService.save(orderDetails);
        return CommonResult.successNodata("评价完成");
    }

    /**
     * 查询订单支付json
     */
    @GetMapping("queryPayJson/{id}")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult queryPayJson(@PathVariable("id") String id) {
        return CommonResult.success(
                transService.getById(orderService.getById(id).getPayId()).getPayJson()
        );
    }


    /**
     * 上传图片
     */
    @PostMapping("uploadImg")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult updateAvatar(@RequestParam("file") MultipartFile file) {
        File file1 = ComUtil.MultipartToFile(file);
        //上传图片
        CommonResult result =
                OssUtil.getAliOss(redisTemplate).fileUpload(file1, false, AuthUtils.getUser().getId());
        if (result.getCode() == 0) {
            return CommonResult.successNodata(result.getData().toString());
        } else {
            return result;
        }
    }

    /**
     * 申请售后
     */
    @PostMapping("applyService")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult updateAvatar(@RequestBody Aftersales aftersales) {
        aftersales.setUserid(AuthUtils.getUser().getId());
        aftersales.setStatus("0");
        aftersales.setCreateTime(LocalDateTime.now());
        aftersalesService.save(aftersales);
        //记录关联订单售后单号
        Order shopOrder = new Order();
        shopOrder.setId(aftersales.getOrderId());
        shopOrder.setApplyNo(aftersales.getId());
        orderService.updateById(shopOrder);
        return CommonResult.success(aftersales);
    }


    /**
     * 查询我的售后列表
     */
    @GetMapping("queryApplyService")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult queryApplyService(Page page) {
        Aftersales aftersales = new Aftersales();
        aftersales.setUserid(AuthUtils.getUser().getId());
        return CommonResult.success(aftersalesService.page(page, Wrappers.query(aftersales)));
    }

    /**
     * 查询我的售后列表
     */
    @GetMapping("getApplyService/{id}")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult getApplyService(@PathVariable("id") String id) {
        return CommonResult.success(aftersalesService.getById(id));
    }

    /**
     * 修改运单号
     */
    @PostMapping("editApplyExpress")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult editApplyExpress(@RequestBody Aftersales aftersales) {
        return CommonResult.success(aftersalesService.updateById(aftersales));
    }

    /**
     * 撤销申请售后
     */
    @GetMapping("delApplyService/{id}")
    @PreAuthorize("hasAnyAuthority('wechat:user')")
    public CommonResult delApplyService(@PathVariable("id") String id) {
        Aftersales aftersales = new Aftersales();
        aftersales.setId(id);
        aftersales.setStatus("-1");
        aftersalesService.updateById(aftersales);
        //同时删除订单表中的关联状态
        orderService.updateOrderApplyService(aftersalesService.getById(id).getOrderId());
        return CommonResult.successNodata("撤回成功");
    }
}
