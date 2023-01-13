import { fetchAddComment } from '../../../../services/good/fetchGood';
import Toast from 'tdesign-miniprogram/toast/index';
import Dialog from 'tdesign-miniprogram/dialog/index';
import { baseUrl } from '../../../../config/index';
Page({
  data: {
    serviceRateValue: 1,
    goodRateValue: 1,
    conveyRateValue: 1,
    isAnonymous: false,
    uploadFiles: [],
    gridConfig: {
      width: 218,
      height: 218,
      column: 3,
    },
    isAllowedSubmit: false,
    imageProps: {
      mode: 'aspectFit',
    },
    orderNo: '',
    goodList: [],
  },

  onLoad(options) {
    const goodList = wx.getStorageSync('_goodsList');
    console.log(goodList);
    this.setData({
      orderNo: options.orderNo,
      goodList: goodList,
    });
  },

  onRateChange(e) {
    const { value } = e?.detail;
    const { index } = e.currentTarget.dataset;
    const { goodList } = this.data;
    goodList[index].rate = value;
    this.setData({ goodList }, () => {
      this.updateButtonStatus();
    });
  },

  onAnonymousChange(e) {
    const status = !!e?.detail?.checked;
    this.setData({ isAnonymous: status });
  },

  handleSuccess(e) {
    const { files } = e.detail;
    console.log(files);
    const { good, index } = e.currentTarget.dataset;
    const { goodList } = this.data;
    const _that = this;
    good['files'] = files;
    goodList[index] = good;
    _that.setData({
      goodList: goodList,
    });
  },

  handleRemove(e) {
    console.log(e);
    const fileIndex = e.detail.index;
    const { good, index } = e.currentTarget.dataset;
    const { goodList } = this.data;
    good.files.splice(fileIndex, 1);
    goodList[index] = good;
    this.setData({
      goodList: goodList,
    });
  },

  onTextAreaChange(e) {
    const value = e?.detail?.value;
    const { index } = e.currentTarget.dataset;
    const { goodList } = this.data;
    goodList[index]['remark'] = value;
    this.setData({ goodList }, () => {
      this.updateButtonStatus();
    });
  },

  updateButtonStatus() {
    const { goodList } = this.data;
    let isCanSave = true;
    goodList.forEach((row) => {
      if (!row.rate || !row.remark) {
        isCanSave = false;
      }
    });
    this.setData({ isAllowedSubmit: isCanSave });
  },

  upLoadImg(filePath) {
    return new Promise((resolve, reject) => {
      wx.uploadFile({
        url: baseUrl + '/shop/wechat/order/uploadImg', //仅为示例，非真实的接口地址
        filePath: filePath,
        name: 'file',
        formData: {},
        header: {
          Authorization: 'Bearer ' + wx.getStorageSync('token'),
        },
        success(res) {
          const data = JSON.parse(res.data);
          resolve(data);
        },
        fial(er) {
          reject(er);
        },
      });
    });
  },
  async onSubmitBtnClick() {
    const { isAllowedSubmit } = this.data;
    if (!isAllowedSubmit) return;
    let { goodList, orderNo, isAnonymous } = this.data;

    //先上传图片
    for (let i = 0; i < goodList.length; i++) {
      let row = goodList[i];
      row.imgs = [];
      for (let f = 0; f < row.files.length; f++) {
        let res = await this.upLoadImg(row.files[f].url);
        row.imgs.push(res.msg);
      }
    }
    let res1 = await fetchAddComment({
      orderid: orderNo,
      goodList: goodList,
      isShowname: isAnonymous ? '1' : '0',
    });
    if (res1.code === 0) {
      Dialog.confirm({
        title: '评价成功,谢谢参与',
        content: '',
        confirmBtn: { content: '确定', variant: 'base' },
      })
        .then(() => {
          Dialog.close();
          wx.setStorageSync('isReload', '1');
          wx.navigateBack();
        })
        .catch(() => {});
    } else {
      Toast({
        context: this,
        selector: '#t-toast',
        message: '评价失败:' + res1.msg,
        icon: 'close-circle',
        duration: 2000,
      });
    }

    // wx.navigateBack();
  },
});
