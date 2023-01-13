/* eslint-disable no-param-reassign */
import { sendRequest } from '../../utils/request';

/** 获取搜索历史 */
export function getSearchResult(data) {
  return sendRequest({
    url: '/shop/wechat/shop/api/resultGoods',
    method: 'get',
    data: data,
    headers: {
      isAuth: true,
    },
  });
}
