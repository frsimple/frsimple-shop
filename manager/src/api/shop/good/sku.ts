import request from '@/utils/request';

//查询sku模板信息
export function queryList(params) {
  return request({
    url: '/shop/sku/list',
    method: 'get',
    params: params,
  });
}

//新增sku模板
export function addSku(data) {
  return request({
    url: '/shop/sku/add',
    method: 'post',
    data: data,
  });
}

//修改sku模板
export function editSku(data) {
  return request({
    url: '/shop/sku/edit',
    method: 'post',
    data: data,
  });
}

//删除sku模板
export function delSku(id) {
  return request({
    url: '/shop/sku/del/' + id,
    method: 'delete',
  });
}
