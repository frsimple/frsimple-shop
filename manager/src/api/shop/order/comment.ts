import request from '@/utils/request';

//查询订单信息列表
export function queryCommentList(params) {
  return request({
    url: '/shop/mark/list',
    method: 'get',
    params: params,
  });
}

//添加回复
export function addMark(data) {
  return request({
    url: '/shop/mark/add',
    method: 'post',
    data: data,
  });
}

//删除评价
export function delMark(id) {
  return request({
    url: '/shop/mark/del/' + id,
    method: 'delete',
  });
}
