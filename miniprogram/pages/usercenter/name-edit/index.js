import { fetchUserName } from '../../../services/usercenter/fetchUsercenter';
import Message, { hideMessage } from 'tdesign-miniprogram/message/index';
import { getUserInfo, setUserInfo } from '../../../utils/util';

Page({
  data: {
    nameValue: '',
    defaultValue: '',
    saveBtn: {
      text: '保存',
      load: false,
    },
  },
  onLoad(options) {
    const { name } = options;
    this.setData({
      nameValue: name,
      defaultValue: name,
    });
  },
  changeNick(e) {
    //console.log(e);
    if (e.detail.value) {
      this.setData({
        nameValue: e.detail.value,
      });
    }
  },
  async onSubmit(event) {
    console.log(event.detail.value);
    if (!this.data.nameValue) {
      Message.error({
        context: this,
        offset: ['20rpx', '32rpx'],
        duration: 3000,
        content: '请输入昵称',
      });
      return false;
    }
    this.setData({
      saveBtn: {
        text: '保存中...',
        load: true,
      },
    });
    let res = await fetchUserName({
      name: this.data.nameValue,
    });
    this.setData({
      saveBtn: {
        text: '保存',
        load: false,
      },
    });
    if (res.code == 0) {
      //刷新缓存
      let user = getUserInfo();
      setUserInfo({
        avatarUrl: user.avatarUrl,
        nickName: this.data.nameValue,
        phoneNumber: user.phoneNumber,
      });
      Message.success({
        context: this,
        offset: ['20rpx', '32rpx'],
        duration: 1500,
        icon: 'check-circle',
        content: '昵称设置成功',
      });
      //wx.navigateBack({ backRefresh: true });
    } else {
      Message.error({
        context: this,
        offset: ['20rpx', '32rpx'],
        duration: 3000,
        content: '昵称设置失败：' + res.msg,
      });
    }
  },
  clearContent() {
    this.setData({
      nameValue: '',
    });
  },
});
