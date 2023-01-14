import request from '@/utils/request';

//查询运费模板列表
export function queryList(params) {
  return request({
    url: '/shop/ratetem/list',
    method: 'get',
    params: params,
  });
}

//新增运费模板
export function addTem(data) {
  return request({
    url: '/shop/ratetem/add',
    method: 'post',
    data: data,
  });
}

//修改运费模板
export function editTem(data) {
  return request({
    url: '/shop/ratetem/edit',
    method: 'post',
    data: data,
  });
}

//删除运费模板
export function delTem(id) {
  return request({
    url: '/shop/ratetem/del/' + id,
    method: 'delete',
  });
}
