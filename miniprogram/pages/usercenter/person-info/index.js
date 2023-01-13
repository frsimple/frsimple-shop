import {
  isLogin,
  getUserInfo,
  getToken,
  setUserInfo,
} from '../../../utils/util';
import Toast, { hideToast } from 'tdesign-miniprogram/toast/index';
import { baseUrl } from '../../../config/index';
import Message, { hideMessage } from 'tdesign-miniprogram/message/index';
import { fetchUserPhone } from '../../../services/usercenter/fetchUsercenter';

Page({
  data: {
    personInfo: {
      avatarUrl: '',
      nickName: '',
      phoneNumber: '',
    },
    showUnbindConfirm: false,
    typeVisible: false,
    sessionId: '',
    defaultAvatarUrl:
      'https://cdn-we-retail.ym.tencent.com/miniapp/usercenter/icon-user-center-avatar@2x.png',
  },
  onShow() {
    this.init();
  },
  init() {
    if (!isLogin()) {
      wx.switchTab({ url: '/pages/usercenter/index' });
    } else {
      this.fetchData();
    }
  },
  async getphonenumber(e) {
    const _that = this;
    if (e.detail && e.detail.code) {
      Toast({
        context: _that,
        selector: '#t-toast',
        message: '正在设置手机号...',
        theme: 'loading',
        direction: 'column',
        duration: 0,
      });
      try {
        //let logInfo = await wx.login();
        //console.log(logInfo);
        let res = await fetchUserPhone({
          sessionKey: _that.data.sessionId,
          encryptedData: e.detail.encryptedData,
          ivStr: e.detail.iv,
        });
        if (res.code == 0) {
          _that.refWxSession();
          Message.success({
            context: _that,
            offset: ['20rpx', '32rpx'],
            duration: 3000,
            icon: 'check-circle',
            content: '手机号设置成功',
          });
          let user = getUserInfo();
          setUserInfo({
            avatarUrl: user.avatarUrl,
            nickName: user.nickName,
            phoneNumber: res.data,
          });
          _that.setData({
            'personInfo.phoneNumber': res.data,
          });
        } else {
          Message.error({
            context: _that,
            offset: ['20rpx', '32rpx'],
            duration: 3000,
            content: '手机号设置失败：' + res.msg,
          });
        }
      } catch (er) {
        Message.error({
          context: _that,
          offset: ['20rpx', '32rpx'],
          duration: 3000,
          content: '手机号设置失败：' + er,
        });
      } finally {
        hideToast({ context: _that, selector: '#t-toast' });
      }
    }
  },
  fetchData() {
    const _that = this;
    wx.login({
      timeout: 10000,
      success: (result) => {
        _that.setData({
          sessionId: result.code,
        });
      },
      fail: () => {},
      complete: () => {},
    });
    let user = getUserInfo();
    this.setData({
      personInfo: {
        avatarUrl: user.avatarUrl,
        nickName: user.nickName,
        phoneNumber: user.phoneNumber ? user.phoneNumber : '',
      },
    });
  },
  refWxSession() {
    wx.login({
      timeout: 10000,
      success: (result) => {
        this.setData({
          sessionId: result.code,
        });
      },
      fail: () => {},
      complete: () => {},
    });
  },
  onChooseAvatar(e) {
    const { avatarUrl } = e.detail;
    const _that = this;
    Toast({
      context: _that,
      selector: '#t-toast',
      message: '上传中...',
      theme: 'loading',
      direction: 'column',
      duration: 0,
    });
    wx.uploadFile({
      url: baseUrl + '/shop/wechat/user/upAvatar', //仅为示例，非真实的接口地址
      filePath: avatarUrl,
      name: 'file',
      header: {
        Authorization: 'Bearer ' + getToken(),
      },
      success(res) {
        Message.success({
          context: _that,
          offset: ['20rpx', '32rpx'],
          duration: 3000,
          icon: 'check-circle',
          content: '头像设置成功',
        });
        let user = getUserInfo();
        setUserInfo({
          avatarUrl: JSON.parse(res.data).msg,
          nickName: user.nickName,
          phoneNumber: user.phoneNumber,
        });
        _that.setData({
          'personInfo.avatarUrl': JSON.parse(res.data).msg,
        });
        //do something
      },
      fail(er) {
        Message.error({
          context: _that,
          offset: ['80rpx', '32rpx'],
          duration: 3000,
          content: '头像设置失败:' + er,
        });
      },
      complete() {
        hideToast({ context: _that, selector: '#t-toast' });
      },
    });
  },
  onClickCell({ currentTarget }) {
    const { dataset } = currentTarget;
    const { nickName } = this.data.personInfo;

    switch (dataset.type) {
      case 'gender':
        this.setData({
          typeVisible: true,
        });
        break;
      case 'name':
        wx.navigateTo({
          url: `/pages/usercenter/name-edit/index?name=${
            nickName ? nickName : ''
          }`,
        });
        break;
      case 'avatarUrl':
        this.toModifyAvatar();
        break;
      default: {
        break;
      }
    }
  },
  onClose() {
    this.setData({
      typeVisible: false,
    });
  },
  onConfirm(e) {
    const { value } = e.detail;
    this.setData(
      {
        typeVisible: false,
        'personInfo.gender': value,
      },
      () => {
        Toast({
          context: this,
          selector: '#t-toast',
          message: '设置成功',
          theme: 'success',
        });
      },
    );
  },
  async toModifyAvatar() {
    try {
      const tempFilePath = await new Promise((resolve, reject) => {
        wx.chooseImage({
          count: 1,
          sizeType: ['compressed'],
          sourceType: ['album', 'camera'],
          success: (res) => {
            const { path, size } = res.tempFiles[0];
            if (size <= 10485760) {
              resolve(path);
            } else {
              reject({ errMsg: '图片大小超出限制，请重新上传' });
            }
          },
          fail: (err) => reject(err),
        });
      });
      const tempUrlArr = tempFilePath.split('/');
      const tempFileName = tempUrlArr[tempUrlArr.length - 1];
      Toast({
        context: this,
        selector: '#t-toast',
        message: `已选择图片-${tempFileName}`,
        theme: 'success',
      });
    } catch (error) {
      if (error.errMsg === 'chooseImage:fail cancel') return;
      Toast({
        context: this,
        selector: '#t-toast',
        message: error.errMsg || error.msg || '修改头像出错了',
        theme: 'fail',
      });
    }
  },
});
