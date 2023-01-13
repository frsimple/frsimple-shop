import { baseUrl } from '../config/index';
import { getToken, logOut } from '../utils/util';

const sendRequest = (params) => {
  var headers = params.headers || {};
  if (!headers.isAuth) {
    if (!getToken()) {
      return Promise.reject({ code: 401, msg: '请先登录' });
    } else {
      headers['Authorization'] = 'Bearer ' + getToken();
    }
  }
  return new Promise((resolve, reject) => {
    wx.request({
      url: baseUrl + '' + params.url,
      method: params.method,
      data: params.data,
      header: headers,
      success: (res) => {
        if (res.statusCode < 200 || res.statusCode > 300) {
          reject('系统错误');
        } else if (res.statusCode === 403 || res.statusCode === 401) {
          logOut();
          reject('登录失效/无权操作');
        } else {
          resolve(res.data);
        }
      },
      fail: (err) => {
        console.log(err);
        if (err.response && err.response.status === 401) {
          if (params.url !== '/auth/oauth/token') {
            wx.showToast({
              title: '请先登录在进行操作',
              icon: 'none',
              image: '',
              duration: 1500,
              mask: false,
              success: (result) => {},
              fail: () => {},
              complete: () => {},
            });
          }
          reject(err.response);
        } else if (err.response && err.response.status === 403) {
          logOut();
          wx.showToast({
            title: '无权操作',
            icon: 'none',
            image: '',
            duration: 1500,
            mask: false,
            success: (result) => {},
            fail: () => {},
            complete: () => {},
          });
          reject(err.response);
        } else if (err.response && err.response.status === 500) {
          wx.showToast({
            title: '系统错误',
            icon: 'none',
            image: '',
            duration: 1500,
            mask: false,
            success: (result) => {},
            fail: () => {},
            complete: () => {},
          });
          reject(err.response);
        } else if (err.response && err.response.status === 404) {
          wx.showToast({
            title: '链接找不到了',
            icon: 'none',
            image: '',
            duration: 1500,
            mask: false,
            success: (result) => {},
            fail: () => {},
            complete: () => {},
          });
          reject(err.response);
        } else {
          reject(err);
        }
      },
    });
  });
};

module.exports = {
  sendRequest,
};
