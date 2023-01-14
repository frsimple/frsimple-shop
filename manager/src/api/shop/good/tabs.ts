import request from '@/utils/request';

//查询标签模板列表
export function queryList(params) {
  return request({
    url: '/shop/tabs/list',
    method: 'get',
    params: params,
  });
}

//新增标签模板
export function addTabs(data) {
  return request({
    url: '/shop/tabs/add',
    method: 'post',
    data: data,
  });
}

//修改标签模板
export function editTabs(data) {
  return request({
    url: '/shop/tabs/edit',
    method: 'post',
    data: data,
  });
}

//删除标签模板
export function delTabs(id) {
  return request({
    url: '/shop/tabs/del/' + id,
    method: 'delete',
  });
}
