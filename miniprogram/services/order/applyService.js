import { config } from '../../config/index';
import { sendRequest } from '../../utils/request';

/** 获取售后单mock数据 */
function mockFetchRightsPreview(params) {
  const { delay } = require('../_utils/delay');
  const { genRightsPreview } = require('../../model/order/applyService');

  return delay().then(() => genRightsPreview(params));
}

/** 获取售后单数据 */
export function fetchRightsPreview(params) {
  if (config.useMock) {
    return mockFetchRightsPreview(params);
  }

  return new Promise((resolve) => {
    resolve('real api');
  });
}

/** 确认收货 */
export function dispatchConfirmReceived() {
  if (config.useMock) {
    const { delay } = require('../_utils/delay');
    return delay();
  }

  return new Promise((resolve) => {
    resolve('real api');
  });
}

/** 获取可选的mock售后原因列表 */
function mockFetchApplyReasonList(params) {
  const { delay } = require('../_utils/delay');
  const { genApplyReasonList } = require('../../model/order/applyService');

  return delay().then(() => genApplyReasonList(params));
}

/** 获取可选的售后原因列表 */
export function fetchApplyReasonList(params) {
  if (config.useMock) {
    return mockFetchApplyReasonList(params);
  }

  return new Promise((resolve) => {
    resolve('real api');
  });
}

/** 发起售后申请 */
export function dispatchApplyService(data) {
  return sendRequest({
    url: '/shop/wechat/order/applyService',
    method: 'post',
    data: data,
  });
}
