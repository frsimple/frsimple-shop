import { sendRequest } from '../../utils/request';

/** 获取购物车数据 */
export function fetchCartGroupData() {
  return sendRequest({
    url: '/shop/wechat/order/queryCart',
    method: 'get',
  });
}

/** 删除购物车数据 */
export function fetchDelCart(data) {
  return sendRequest({
    url: '/shop/wechat/order/delCart',
    method: 'delete',
    data: data,
  });
}
