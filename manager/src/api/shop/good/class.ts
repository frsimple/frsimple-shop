import request from '@/utils/request';

//查询树形分类
export function queryTreeList() {
  return request({
    url: '/shop/class/listTree',
    method: 'get',
  });
}

//新增分类
export function addClass(data) {
  return request({
    url: '/shop/class/add',
    method: 'post',
    data: data,
  });
}

//修改分类
export function editClass(data) {
  return request({
    url: '/shop/class/edit',
    method: 'post',
    data: data,
  });
}

//删除分类
export function delClass(id) {
  return request({
    url: '/shop/class/del/' + id,
    method: 'delete',
  });
}
