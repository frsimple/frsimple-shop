import request from '@/utils/request';

//新增字典
export function queryMainList(params) {
  return request({
    url: '/shop/mainimg/list',
    method: 'get',
    params: params,
  });
}

//新增字典
export function addMain(data) {
  return request({
    url: '/shop/mainimg/addMain',
    method: 'post',
    data: data,
  });
}

//修改字典
export function editMain(data) {
  return request({
    url: '/shop/mainimg/editMain',
    method: 'post',
    data: data,
  });
}

//删除字典
export function delMain(id) {
  return request({
    url: '/shop/mainimg/delMain/' + id,
    method: 'get',
  });
}

//查询热词
export function getHotSearch() {
  return request({
    url: '/shop/mainimg/get/hotSearch',
    method: 'post',
  });
}

//设置热词
export function setHotSearch(data) {
  return request({
    url: '/shop/mainimg/set/hotSearch',
    method: 'post',
    data: data,
  });
}
