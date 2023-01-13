import { sendRequest } from '../../utils/request';

const secret =
  '45e8c2cd093401b10b8da16b786c0c8c618d8a79375ef7237518aca0632a0974';

export function loginByWechat(data) {
  return sendRequest({
    url: '/auth/oauth/token',
    method: 'post',
    data: data,
    headers: {
      isAuth: true,
      sp: secret,
      grant_type: 'third_code',
      'Content-type': 'application/x-www-form-urlencoded',
    },
  });
}

export function fetchUserCenter() {
  return sendRequest({
    url: '/shop/wechat/user/info',
    method: 'get',
  });
}

export function fetchUserName(data) {
  return sendRequest({
    url: '/shop/wechat/user/upName',
    method: 'post',
    data: data,
  });
}

export function fetchUserPhone(data) {
  return sendRequest({
    url: '/shop/wechat/user/upPhone',
    method: 'post',
    data: data,
  });
}

export function fetchUserOrderCount() {
  return sendRequest({
    url: '/shop/wechat/user/orderCount',
    method: 'get',
  });
}
