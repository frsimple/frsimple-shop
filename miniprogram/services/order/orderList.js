import { sendRequest } from '../../utils/request';

/** 获取订单列表数据 */
export function fetchOrders(data) {
  return sendRequest({
    url: '/shop/wechat/order/queryOrder',
    method: 'get',
    data: data,
  });
}

/** 获取订单列表统计 */
export function fetchOrdersCount() {
  return sendRequest({
    url: '/shop/wechat/user/orderCount',
    method: 'get',
  });
}

/** 查询字典 */
export function fetchDict(code) {
  return sendRequest({
    url: '/shop/wechat/order/dict/' + code,
    method: 'get',
  });
}
