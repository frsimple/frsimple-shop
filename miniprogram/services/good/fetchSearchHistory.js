import { config } from '../../config/index';
import { sendRequest } from '../../utils/request';

/** 获取搜索历史 */
function mockSearchHistory() {
  const { delay } = require('../_utils/delay');
  const { getSearchHistory } = require('../../model/search');
  return delay().then(() => getSearchHistory());
}

/** 获取搜索历史 */
export function getSearchHistory() {
  if (config.useMock) {
    return mockSearchHistory();
  }
  return new Promise((resolve) => {
    resolve('real api');
  });
}

/** 获取搜索历史 */
export function getSearchPopular() {
  return sendRequest({
    url: '/shop/wechat/home/api/hotSearch',
    method: 'get',
    headers: {
      isAuth: true,
    },
  });
}
