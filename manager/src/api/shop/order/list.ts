import request from '@/utils/request';

//查询订单信息列表
export function queryOrderList(params) {
  return request({
    url: '/shop/order/list',
    method: 'get',
    params: params,
  });
}

//查询订单统计数量
export function queryDataCount() {
  return request({
    url: '/shop/order/dataCount',
    method: 'get',
  });
}

//修改订单金额
export function editOrderPrice(data) {
  return request({
    url: '/shop/order/editOrderPrice',
    method: 'post',
    data: data,
  });
}

//订单发货
export function orderSend(data) {
  return request({
    url: '/shop/order/orderSend',
    method: 'post',
    data: data,
  });
}

//修改订单号
export function editOrderExpress(data) {
  return request({
    url: '/shop/order/editOrderExpress',
    method: 'post',
    data: data,
  });
}

//确定收货
export function confirmOrder(id) {
  return request({
    url: '/shop/order/confirmOrder/' + id,
    method: 'get',
  });
}

//查询订单流水
export function queryOrderDetails(id) {
  return request({
    url: '/shop/order/orderDetails/' + id,
    method: 'get',
  });
}

//查询订单评价
export function queryMarkList(id) {
  return request({
    url: '/shop/order/markList/' + id,
    method: 'get',
  });
}

//查询售后申请
export function queryAftersalesList(params) {
  return request({
    url: '/shop/aftersales/list',
    method: 'get',
    params: params,
  });
}

//查询售后申请
export function editAftersales(data) {
  return request({
    url: '/shop/aftersales/editApplyExpress',
    method: 'post',
    data: data,
  });
}

//撤销售后申请
export function delAftersales(id) {
  return request({
    url: '/shop/aftersales/delApplyService/' + id,
    method: 'get',
  });
}

//查询售后申请
export function checkAftersales(data) {
  return request({
    url: '/shop/aftersales/checkApplyService',
    method: 'post',
    data: data,
  });
}
