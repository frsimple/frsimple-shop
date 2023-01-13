import { resp } from '../after-service-list/api';
import dayjs from 'dayjs';
import { mockIp, mockReqId } from '../../../utils/mock';
import { sendRequest } from '../../../utils/request';

export const formatTime = (date, template) => dayjs(date).format(template);

export function getRightsDetail(id) {
  return sendRequest({
    url: '/shop/wechat/order/getApplyService/' + id,
    method: 'get',
  });
}

export function addOrEditExpress(data) {
  return sendRequest({
    url: '/shop/wechat/order/editApplyExpress',
    method: 'post',
    data: data,
  });
}

export function cancelRights() {
  const _resq = {
    data: {},
    code: 'Success',
    msg: null,
    requestId: mockReqId(),
    clientIp: mockIp(),
    rt: 79,
    success: true,
  };
  return Promise.resolve(_resq);
}
