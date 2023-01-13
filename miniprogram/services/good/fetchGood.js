import { sendRequest } from '../../utils/request';

/** 获取商品详情信息 */
export function fetchGood(id) {
  return sendRequest({
    url: '/shop/wechat/home/api/good/' + id,
    method: 'get',
    headers: {
      isAuth: true,
    },
  });
}

/** 获取商品评价信息 */
export function fetchGoodMark(id) {
  return sendRequest({
    url: '/shop/wechat/shop/api/markInfo/' + id,
    method: 'get',
    headers: {
      isAuth: true,
    },
  });
}

/** 添加商品到购物车 */
export function fetchAddCart(data) {
  return sendRequest({
    url: '/shop/wechat/order/addCart',
    method: 'post',
    data: data,
  });
}

/** 添加评价 **/
export function fetchAddComment(data) {
  return sendRequest({
    url: '/shop/wechat/order/markOrder',
    method: 'post',
    data: data,
  });
}
