import { sendRequest } from '../../../utils/request';

export function getRightsList(parameter) {
  return sendRequest({
    url: '/shop/wechat/order/queryApplyService',
    method: 'get',
    data: parameter,
  });
}

export function delApplyService(id) {
  return sendRequest({
    url: '/shop/wechat/order/delApplyService/' + id,
    method: 'get',
  });
}
