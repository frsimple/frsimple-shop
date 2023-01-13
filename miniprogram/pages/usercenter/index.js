import {
  fetchUserCenter,
  loginByWechat,
  fetchUserOrderCount,
} from '../../services/usercenter/fetchUsercenter';
import Toast, { hideToast } from 'tdesign-miniprogram/toast/index';
import {
  isLogin,
  setToken,
  getUserInfo,
  setUserInfo,
  setSessionId,
} from '../../utils/util';
import Message, { hideMessage } from 'tdesign-miniprogram/message/index';

const menuData = [
  [
    {
      title: '收货地址',
      tit: '',
      url: '',
      type: 'address',
    },
    {
      title: '优惠券',
      tit: '',
      url: '',
      type: 'coupon',
    },
    {
      title: '积分',
      tit: '',
      url: '',
      type: 'point',
    },
  ],
  // [
  //   {
  //     title: '帮助中心',
  //     tit: '',
  //     url: '',
  //     type: 'help-center',
  //   },
  //   {
  //     title: '客服热线',
  //     tit: '',
  //     url: '',
  //     type: 'service',
  //     icon: 'service',
  //   },
  // ],
];

const orderTagInfos = [
  {
    title: '待付款',
    iconName: 'wallet',
    orderNum: 0,
    tabType: '00',
    status: 1,
  },
  {
    title: '待发货',
    iconName: 'deliver',
    orderNum: 0,
    tabType: '01',
    status: 1,
  },
  {
    title: '待收货',
    iconName: 'package',
    orderNum: 0,
    tabType: '02',
    status: 1,
  },
  {
    title: '待评价',
    iconName: 'comment',
    orderNum: 0,
    tabType: '03',
    status: 1,
  },
  {
    title: '退款/售后',
    iconName: 'exchang',
    orderNum: 0,
    tabType: 0,
    status: 1,
  },
];

const getDefaultData = () => ({
  showMakePhone: false,
  userInfo: {
    avatarUrl: '',
    nickName: '正在登录...',
    phoneNumber: '',
  },
  menuData,
  orderTagInfos,
  customerServiceInfo: {},
  currAuthStep: 1,
  showKefu: true,
  versionNo: '',
  isShow: false,
});

Page({
  data: getDefaultData(),

  onLoad() {
    this.getVersionInfo();
  },

  onShow() {
    this.getTabBar().init();
    this.init();
  },
  onPullDownRefresh() {
    wx.stopPullDownRefresh();
    this.init();
  },

  init() {
    if (isLogin()) {
      this.queryOrderCount();
      if (getUserInfo()) {
        let user = getUserInfo();
        this.setData({
          userInfo: {
            avatarUrl: user.avatarUrl,
            nickName: user.nickName,
            phoneNumber: user.phoneNumber,
          },
          currAuthStep: 2,
        });
      } else {
        this.fetUseriInfoHandle();
      }
    }
  },

  queryOrderCount() {
    fetchUserOrderCount()
      .then((res) => {
        if (res.code == 0) {
          let resData = res.data;
          this.setData({
            'orderTagInfos[0].orderNum': resData.count0,
            'orderTagInfos[1].orderNum': resData.count1,
            'orderTagInfos[2].orderNum': resData.count2,
            'orderTagInfos[3].orderNum': resData.count3,
            'orderTagInfos[4].orderNum': resData.count4,
          });
        }
      })
      .catch((er) => {})
      .finally(() => {});
  },
  async fetUseriInfoHandle() {
    try {
      let userRes = await fetchUserCenter();
      console.log(userRes);
      Message.success({
        context: this,
        offset: ['80rpx', '32rpx'],
        duration: 3000,
        icon: 'check-circle',
        content: '登录成功',
      });
      setUserInfo({
        avatarUrl: userRes.data.avatar,
        nickName: userRes.data.name,
        phoneNumber: userRes.data.phone,
      });
      console.log('头像：', wx.getStorageSync('userInfo_avatarUrl'));
      this.setData({
        userInfo: {
          avatarUrl: userRes.data.avatar,
          nickName: userRes.data.name,
          phoneNumber: userRes.data.phone,
        },
        currAuthStep: 2,
      });
    } catch (er) {
      this.setData({
        currAuthStep: 1,
      });
    } finally {
      hideToast({ context: this, selector: '#t-toast' });
    }
    this.queryOrderCount();
  },

  onClickCell({ currentTarget }) {
    if (!isLogin()) {
      Toast({
        context: this,
        selector: '#t-toast',
        message: '请先登录',
        icon: '',
        duration: 1000,
      });
      return;
    }
    const { type } = currentTarget.dataset;

    switch (type) {
      case 'address': {
        wx.navigateTo({ url: '/pages/usercenter/address/list/index' });
        break;
      }
      case 'service': {
        this.openMakePhone();
        break;
      }
      case 'help-center': {
        Toast({
          context: this,
          selector: '#t-toast',
          message: '你点击了帮助中心',
          icon: '',
          duration: 1000,
        });
        break;
      }
      case 'point': {
        Toast({
          context: this,
          selector: '#t-toast',
          message: '敬请期待',
          icon: '',
          duration: 1000,
        });
        break;
      }
      case 'coupon': {
        Toast({
          context: this,
          selector: '#t-toast',
          message: '敬请期待',
          icon: '',
          duration: 1000,
        });
        // wx.navigateTo({ url: '/pages/coupon/coupon-list/index' });
        break;
      }
      default: {
        Toast({
          context: this,
          selector: '#t-toast',
          message: '未知跳转',
          icon: '',
          duration: 1000,
        });
        break;
      }
    }
  },

  jumpNav(e) {
    const status = e.detail.tabType;
    if (!isLogin()) {
      Toast({
        context: this,
        selector: '#t-toast',
        message: '请先登录',
        icon: '',
        duration: 1000,
      });
      return;
    }
    if (status === 0) {
      wx.navigateTo({ url: '/pages/order/after-service-list/index' });
    } else {
      wx.navigateTo({ url: `/pages/order/order-list/index?status=${status}` });
    }
  },

  jumpAllOrder() {
    if (!isLogin()) {
      Toast({
        context: this,
        selector: '#t-toast',
        message: '请先登录',
        icon: '',
        duration: 1000,
      });
      return;
    }
    wx.navigateTo({ url: '/pages/order/order-list/index' });
  },

  openMakePhone() {
    this.setData({ showMakePhone: true });
  },

  closeMakePhone() {
    this.setData({ showMakePhone: false });
  },

  call() {
    wx.makePhoneCall({
      phoneNumber: this.data.customerServiceInfo.servicePhone,
    });
  },

  gotoUserEditPage() {
    const { currAuthStep } = this.data;
    const _that = this;
    if (currAuthStep === 2) {
      wx.navigateTo({ url: '/pages/usercenter/person-info/index' });
    } else {
      this.setData({
        currAuthStep: 3,
      });
      Toast({
        context: this,
        selector: '#t-toast',
        message: '登录中...',
        theme: 'loading',
        direction: 'column',
        duration: 0,
      });
      //未登录状态开始登录
      wx.login({
        timeout: 10000,
        success: (result) => {
          setSessionId(result.code);
          loginByWechat({
            avatar: '',
            username: '',
            authCode: result.code,
            type: '03',
          })
            .then((res) => {
              setToken(res.access_token);
              _that.fetUseriInfoHandle();
            })
            .catch((er) => {
              hideToast({ context: _that, selector: '#t-toast' });
              Message.error({
                context: _that,
                offset: ['80rpx', '32rpx'],
                duration: 3000,
                content: '登录失败：' + er.msg,
              });
              _that.setData({
                currAuthStep: 1,
              });
            });
        },
        fail: (error) => {
          hideToast({ context: _that, selector: '#t-toast' });
          _that.setData({
            currAuthStep: 1,
          });
          Message.error({
            context: _that,
            offset: ['80rpx', '32rpx'],
            duration: 3000,
            icon: 'error-circle',
            content: '微信登录失败：' + error,
          });
        },
        complete: function () {
          //hideToast({ context: _that, selector: '#t-toast' });
        },
      });
    }
  },

  getVersionInfo() {
    const versionInfo = wx.getAccountInfoSync();
    const { version, envVersion = __wxConfig } = versionInfo.miniProgram;
    this.setData({
      versionNo: envVersion === 'release' ? version : envVersion,
    });
  },
});
