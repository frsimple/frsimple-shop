import request from '@/utils/request';

//查询快捷菜单列表
export function queryMainList(params) {
  return request({
    url: '/shop/fast/list',
    method: 'get',
    params: params,
  });
}

//新增快捷菜单
export function addFast(data) {
  return request({
    url: '/shop/fast/add',
    method: 'post',
    data: data,
  });
}

//修改快捷菜单
export function ediFast(data) {
  return request({
    url: '/shop/fast/edit',
    method: 'post',
    data: data,
  });
}

//删除快捷菜单
export function delFast(id) {
  return request({
    url: '/shop/fast/del/' + id,
    method: 'get',
  });
}
