import { sendRequest } from '../../utils/request';

/** 获取订单详情数据 */
export function fetchOrderDetail(id) {
  return sendRequest({
    url: '/shop/wechat/order/getOneOrder/' + id,
    method: 'get',
  });
}

/** 查询支付payjson信息 **/
export function fetchQueryPayJson(id) {
  return sendRequest({
    url: '/shop/wechat/order/queryPayJson/' + id,
    method: 'get',
  });
}

/** 确认收货 **/
export function fetchConfirmOrder(id) {
  return sendRequest({
    url: '/shop/wechat/order/confirmOrder/' + id,
    method: 'get',
  });
}

/** 取消订单（未支付） **/
export function fetchCanlOrder(id) {
  return sendRequest({
    url: '/shop/wechat/order/canlOrder/' + id,
    method: 'get',
  });
}

/** 取消订单（已支付） **/
export function fetchCanlOrder1(id) {
  return sendRequest({
    url: '/shop/wechat/order/canlOrder1/' + id,
    method: 'get',
  });
}

export const buttonArray = {
  '00': [
    { primary: false, type: 0, name: '取消订单' },
    { primary: true, type: 1, name: '付款' },
  ],
  '01': [
    {
      primary: false,
      type: 0,
      name: '取消订单',
    },
    {
      primary: true,
      type: 9,
      name: '再次购买',
    },
  ],
  '02': [
    {
      primary: true,
      type: 3,
      name: '确认收货',
    },
  ],
  '03': [
    { primary: false, type: -1, name: '申请售后' },
    { primary: true, type: 4, name: '评价' },
  ],
  99: [{ primary: false, type: -1, name: '申请售后' }],
  '-1': [
    //{ primary: false, type: 4, name: '申请售后' },
    {
      primary: false,
      type: 9,
      name: '再次购买',
    },
  ],
};
