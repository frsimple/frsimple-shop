import { sendRequest } from '../../utils/request';

/** 查询评价列表 */
export function fetchComments(id, parmas) {
  return sendRequest({
    url: '/shop/wechat/shop/api/markList/' + id,
    method: 'get',
    headers: {
      isAuth: true,
    },
    data: parmas,
  });
}

/** 查询评价列表 */
export function fetchCommentsCount(id) {
  return sendRequest({
    url: '/shop/wechat/shop/api/markData/' + id,
    method: 'get',
    headers: {
      isAuth: true,
    },
  });
}
