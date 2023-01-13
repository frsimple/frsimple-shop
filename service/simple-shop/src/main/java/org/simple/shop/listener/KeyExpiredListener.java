package org.simple.shop.listener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.simple.security.utils.AuthUtils;
import org.simple.shop.constant.ShopContant;
import org.simple.shop.entity.*;
import org.simple.shop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * redis过期监听
 */

@Component
public class KeyExpiredListener extends KeyExpirationEventMessageListener {

    @Autowired
    public RedisTemplate<String, String> redisTemplate;

    @Resource
    private OrderService orderService;

    @Resource
    private OrderDetailsService orderDetailsService;

    @Resource
    private TransService transService;

    @Resource
    private InfoService infoService;

    @Resource
    private MarkService markService;


    public KeyExpiredListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        //获取失效key名称
        String expireKey = new String(message.getBody(), StandardCharsets.UTF_8);
        String expireKeyValue = redisTemplate.opsForValue().get(expireKey);
        System.out.println("过期key:" + expireKey);
        //订单超时未支付
        if (expireKey.startsWith(ShopContant.SHOP_ORDER_KEY)) {
            //1.先取消订单，修改订单状态
            String orderId = expireKey.replace(ShopContant.SHOP_ORDER_KEY, "");
            Order shopOrder = orderService.getById(orderId);
            if (shopOrder.getStatus().equals("00")) {
                Order s = new Order();
                s.setId(orderId);
                s.setStatus("-1");
                orderService.updateById(s);
                //2.记录订单修改流水
                OrderDetails orderDetails = new OrderDetails();
                orderDetails.setOrderId(orderId);
                orderDetails.setRemark("超时未支付，系统自动取消");
                orderDetails.setCreateTime(LocalDateTime.now());
                orderDetails.setType("-1");
                orderDetailsService.save(orderDetails);
                //3.更改支付流水状态
                Trans trans = transService.getById(shopOrder.getPayId());
                trans.setStatus("-1");
                transService.updateById(trans);
                //4.回退库存和销量
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
            }

        }
        //订单发货超时系统自动收货
        if (expireKey.startsWith(ShopContant.SHOP_ORDER_TASKOVERKEY)) {
            String orderId = expireKey.replace(ShopContant.SHOP_ORDER_KEY, "");
            Order shopOrder = orderService.getById(orderId);
            if (shopOrder.getStatus().equals("01")) {
                shopOrder.setStatus("03");
                orderService.updateById(shopOrder);
                //添加订单流水信息
                OrderDetails orderDetails = new OrderDetails();
                orderDetails.setCreateTime(LocalDateTime.now());
                orderDetails.setOrderId(orderId);
                orderDetails.setType("03");//系统确认收货
                orderDetails.setRemark("系统自动确认收货");
                orderDetailsService.save(orderDetails);
                //添加redis，5天自动好评。
                redisTemplate.opsForValue().set(ShopContant.SHOP_ORDER_COMMENT + orderId,
                        orderId, ShopContant.SHOP_ORDER_EXPIRE_COMMENT, TimeUnit.DAYS);
            }
        }

        //订单超时未评价，系统默认好评
        if (expireKey.startsWith(ShopContant.SHOP_ORDER_COMMENT)) {
            String orderId = expireKey.replace(ShopContant.SHOP_ORDER_COMMENT, "");
            Order order = orderService.getById(orderId);
            if (order.getStatus().equals("03")) {
                order.setStatus("99");
                orderService.updateById(order);
                JSONArray goodList = order.getGoods();
                for (int i = 0; i < goodList.size(); i++) {
                    JSONObject obj = goodList.getJSONObject(i);
                    JSONObject good = new JSONObject();
                    good.put("buyNum", obj.getIntValue("num"));
                    good.put("price", obj.getBigDecimal("price"));
                    if (null != obj.get("selectedSkuInfo")) {
                        good.put("skuId", obj.getJSONObject("selectedSkuInfo")
                                .getString("id"));
                        good.put("specs", obj.getJSONObject("selectedSkuInfo")
                                .getString("rule"));
                    } else {
                        good.put("skuId", "");
                        good.put("specs", "");
                    }
                    good.put("goodsName", obj.getString("goodsName")); //商标标题
                    good.put("primaryImage", obj.getString("primaryImage")); //商品图片
                    Mark m = new Mark();
                    m.setOrderid(orderId);
                    m.setContent("系统默认好评");
                    m.setMark(5);
                    m.setSid(obj.getString("spuId"));
                    m.setGood(good);
                    m.setParentid("0");
                    m.setStatus("1");
                    m.setCreateTime(LocalDateTime.now());
                    m.setUserid(AuthUtils.getUser().getId());
                    m.setIsReply("0");
                    m.setIsShowname("0");
                    markService.save(m);
                }

                //添加订单流水信息
                OrderDetails orderDetails = new OrderDetails();
                orderDetails.setCreateTime(LocalDateTime.now());
                orderDetails.setOrderId(orderId);
                orderDetails.setType("99");//系统确认收货
                orderDetails.setRemark("系统默认好评");
                orderDetailsService.save(orderDetails);
            }
        }
    }
}
