import { sendRequest } from '../../utils/request';

/** 获取商品列表 */
export function getCategoryList() {
  return sendRequest({
    url: '/shop/wechat/home/api/classTree',
    method: 'get',
    headers: {
      isAuth: true,
    },
  });
}
