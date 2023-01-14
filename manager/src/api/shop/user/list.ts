import request from '@/utils/request';

//查询用户信息列表
export function queryUserList(params) {
  return request({
    url: '/shop/user/list',
    method: 'get',
    params: params,
  });
}
