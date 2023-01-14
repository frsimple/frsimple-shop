import request from '@/utils/request';

//查询全部产品列表
export function queryList(params) {
  return request({
    url: '/shop/info/list',
    method: 'get',
    params: params,
  });
}

//查询已删除产品列表
export function queryDelList(params) {
  return request({
    url: '/shop/info/delList',
    method: 'get',
    params: params,
  });
}

//查询商品总量
export function infoCount() {
  return request({
    url: '/shop/info/infoCount',
    method: 'get',
  });
}

//新增商品信息
export function addInfo(data) {
  return request({
    url: '/shop/info/add',
    method: 'post',
    data: data,
  });
}

//修改商品信息
export function editInfo(data) {
  return request({
    url: '/shop/info/edit',
    method: 'post',
    data: data,
  });
}

//新增商品信息
export function uploadImgs(data) {
  return request({
    url: '/shop/info/uploadImgs',
    method: 'post',
    data: data,
  });
}

//恢复产品状态到待上架
export function removeDel(id) {
  return request({
    url: '/shop/info/upProduct/' + id,
    method: 'get',
  });
}

//删除商品
export function removeProd(id) {
  return request({
    url: '/shop/info/delProduct/' + id,
    method: 'get',
  });
}

//上架商品
export function upProduct1(id) {
  return request({
    url: '/shop/info/upProduct1/' + id,
    method: 'get',
  });
}

//下架商品
export function downProduct(id) {
  return request({
    url: '/shop/info/downProduct/' + id,
    method: 'get',
  });
}
