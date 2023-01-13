import { sendRequest } from '../../utils/request';

/** 获取首页数据 */
export function fetchHome() {
  return sendRequest({
    url: '/shop/wechat/home/api/mainImg',
    method: 'get',
    headers: {
      isAuth: true,
    },
  });
}

/** 查询通知公告 */
export function fetchMsg() {
  return sendRequest({
    url: '/shop/wechat/home/api/msg',
    method: 'get',
    headers: {
      isAuth: true,
    },
  });
}

/** 查询快捷菜单 */
export function fetchFast() {
  return sendRequest({
    url: '/shop/wechat/home/api/fast',
    method: 'get',
    headers: {
      isAuth: true,
    },
  });
}

/** 查询好物推荐 */
export function fetchGoodList(data) {
  return sendRequest({
    url: '/shop/wechat/home/api/goodShop',
    method: 'get',
    data: data,
    headers: {
      isAuth: true,
    },
  });
}
