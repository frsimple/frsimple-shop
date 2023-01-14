import request from '@/utils/request';

//查询消息
export function queryMessageList(params) {
  return request({
    url: '/shop/message/list',
    method: 'get',
    params: params,
  });
}

//新增消息
export function addMessage(data) {
  return request({
    url: '/shop/message/addMessage',
    method: 'post',
    data: data,
  });
}

//修改消息
export function editMessage(data) {
  return request({
    url: '/shop/message/editMessage',
    method: 'post',
    data: data,
  });
}

//删除消息
export function delMessage(id) {
  return request({
    url: '/shop/message/delMessage/' + id,
    method: 'get',
  });
}

//启用消息
export function openMessage(id) {
  return request({
    url: '/shop/message/open/' + id,
    method: 'get',
  });
}

//停用消息
export function closeMessage(id) {
  return request({
    url: '/shop/message/close/' + id,
    method: 'get',
  });
}
