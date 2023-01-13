import Dialog from 'tdesign-miniprogram/dialog/index';
import Toast from 'tdesign-miniprogram/toast/index';
import { priceFormat } from '../../../utils/util';
import { ServiceReceiptStatus } from '../config';
import reasonSheet from '../components/reason-sheet/reasonSheet';
import { baseUrl } from '../../../config/index';
import {
  fetchApplyReasonList,
  dispatchApplyService,
} from '../../../services/order/applyService';

Page({
  query: {},
  orderInfo: {},
  data: {
    rightsReasonList1: [
      { id: '9', desc: '空包裹' },
      { id: '10', desc: '快递/物流一直未送到' },
      { id: '11', desc: '货物破损已拒签' },
      { id: '12', desc: '不喜欢' },
    ],
    rightsReasonList: [
      { id: '1', desc: '实际商品与描述不符' },
      { id: '2', desc: '质量问题' },
      { id: '3', desc: '少件/漏发' },
      { id: '4', desc: '包装/商品/污迹/裂痕/变形' },
      { id: '5', desc: '发货太慢' },
      { id: '6', desc: '物流配送太慢' },
      { id: '7', desc: '商家发错货' },
      { id: '8', desc: '不喜欢' },
    ],
    uploading: false, // 凭证上传状态
    canApplyReturn: true, // 是否可退货
    goodsInfo: [],
    receiptStatusList: [
      { desc: '否', status: ServiceReceiptStatus.NOT_RECEIPTED },
      { desc: '是', status: ServiceReceiptStatus.RECEIPTED },
    ],
    applyReasons: [],
    serviceType: null, // 20-仅退款，10-退货退款
    serviceFrom: {
      returnNum: 1,
      receiptStatus: {
        desc: '否',
        status: ServiceReceiptStatus.NOT_RECEIPTED,
      },
      applyReason: { desc: '请选择', id: null },
      // max-填写上限(单位分)，current-当前值(单位分)，temp输入框中的值(单位元)
      amount: { max: 0, current: 0, temp: 0, focus: false },
      remark: '',
      rightsImageUrls: [],
    },
    maxApplyNum: 2, // 最大可申请售后的商品数
    amountTip: '',
    showReceiptStatusDialog: false,
    validateRes: {
      valid: false,
      msg: '',
    },
    submitting: false,
    inputDialogVisible: false,
    uploadGridConfig: {
      column: 3,
      width: 212,
      height: 212,
    },
    serviceRequireType: '',
  },

  setWatcher(key, callback) {
    let lastData = this.data;
    const keys = key.split('.');
    keys.slice(0, -1).forEach((k) => {
      lastData = lastData[k];
    });
    const lastKey = keys[keys.length - 1];
    this.observe(lastData, lastKey, callback);
  },

  observe(data, k, callback) {
    let val = data[k];
    Object.defineProperty(data, k, {
      configurable: true,
      enumerable: true,
      set: (value) => {
        val = value;
        callback();
      },
      get: () => {
        return val;
      },
    });
  },

  validate() {
    console.log(this.data.serviceFrom);
    let valid = true;
    let msg = '';
    // 检查必填项
    if (!this.data.serviceFrom.applyReason.id) {
      valid = false;
      msg = '请填写退款原因';
    } else if (!this.data.serviceFrom.amount.current) {
      valid = false;
      msg = '请填写退款金额';
    }
    if (this.data.serviceFrom.amount.current <= 0) {
      valid = false;
      msg = '退款金额必须大于0';
    }
    this.setData({ validateRes: { valid, msg } });
  },

  onLoad(query) {
    this.orderInfo = wx.getStorageSync('frsimple_applyservice');
    this.query = query;
    //if (!this.checkQuery()) return;
    this.setData({
      canApplyReturn: query.canApplyReturn === 'true',
    });
    this.init();
    this.inputDialog = this.selectComponent('#input-dialog');
    this.setWatcher('serviceFrom.returnNum', this.validate.bind(this));
    this.setWatcher('serviceFrom.applyReason', this.validate.bind(this));
    this.setWatcher('serviceFrom.amount', this.validate.bind(this));
    this.setWatcher('serviceFrom.rightsImageUrls', this.validate.bind(this));
  },

  async init() {
    try {
      await this.refresh();
    } catch (e) {}
  },

  checkQuery() {
    const { orderNo, skuId } = this.query;
    if (!orderNo) {
      Dialog.alert({
        content: '请先选择订单',
      }).then(() => {
        wx.redirectTo({ url: 'pages/order/order-list/index' });
      });
      return false;
    }
    if (!skuId) {
      Dialog.alert({
        content: '请先选择商品',
      }).then(() => {
        wx.redirectTo(`pages/order/order-detail/index?orderNo=${orderNo}`);
      });
      return false;
    }
    return true;
  },

  async refresh() {
    wx.showLoading({ title: 'loading' });
    try {
      const { order, goods } = this.orderInfo;
      console.log(goods);
      this.setData({
        goodsInfo: goods,
        'serviceFrom.amount': {
          max: order.maxPrice,
          current: order.maxPrice,
        },
        //'serviceFrom.returnNum': res.data.numOfSku,
        amountTip: `最多可申请退款¥ ${priceFormat(
          order.maxPrice * 100,
          0,
        )}，含发货运费¥ ${priceFormat(order.freightFee * 100, 0)}`,
        //maxApplyNum: res.data.numOfSkuAvailable,
      });
      wx.hideLoading();
    } catch (err) {
      wx.hideLoading();
      throw err;
    }
  },

  provStep() {
    wx.setNavigationBarTitle({ title: '选择售后类型' });
    this.setData({ serviceRequireType: '' });
    this.switchReceiptStatus(-1);
  },
  onApplyOnlyRefund() {
    wx.setNavigationBarTitle({ title: '申请退款' });
    this.setData({ serviceRequireType: 'REFUND_MONEY' });
    this.switchReceiptStatus(0);
  },

  onApplyReturnGoods() {
    wx.setNavigationBarTitle({ title: '申请退货退款' });
    this.setData({ serviceRequireType: 'REFUND_GOODS' });
    this.switchReceiptStatus(1);
  },

  onApplyReturnGoodsStatus() {
    reasonSheet({
      show: true,
      title: '选择退款原因',
      options: this.data.applyReasons.map((r) => ({
        title: r.desc,
      })),
      showConfirmButton: true,
      showCancelButton: true,
      emptyTip: '请选择退款原因',
    }).then((indexes) => {
      console.log(this.data.applyReasons[indexes[0]]);
      this.setData({
        'serviceFrom.applyReason': this.data.applyReasons[indexes[0]],
      });
    });
  },

  onChangeReturnNum(e) {
    const { value } = e.detail;
    this.setData(
      {
        'serviceFrom.returnNum': value,
      },
      () => {
        this.refresh();
      },
    );
  },

  handleReasonChange(e) {
    console.log(e.detail);
    const { value } = e.detail;
    this.setData({
      'serviceFrom.receiptStatus.desc': value ? '是' : '否',
      'serviceFrom.receiptStatus.status': value,
    });
  },
  onApplyGoodsStatus() {
    reasonSheet({
      show: true,
      title: '请选择收货状态',
      options: this.data.receiptStatusList.map((r) => ({
        title: r.desc,
      })),
      showConfirmButton: true,
      emptyTip: '请选择收货状态',
    }).then((indexes) => {
      this.setData({
        'serviceFrom.receiptStatus': this.data.receiptStatusList[indexes[0]],
      });
    });
  },

  switchReceiptStatus(index) {
    const statusItem = this.data.receiptStatusList[index];
    const { rightsReasonList1, rightsReasonList } = this.data;
    // 没有找到对应的状态，则清空/初始化
    if (!statusItem) {
      this.setData({
        showReceiptStatusDialog: false,
        'serviceFrom.receiptStatus': {
          desc: '否',
          status: ServiceReceiptStatus.NOT_RECEIPTED,
        },
        'serviceFrom.applyReason': { desc: '请选择', id: null }, // 收货状态改变时，初始化申请原因
        applyReasons: [],
      });
      return;
    } else {
      this.setData({
        showReceiptStatusDialog: false,
        'serviceFrom.receiptStatus': {
          desc: statusItem.desc,
          status: statusItem.status,
        },
        'serviceFrom.applyReason': { desc: '请选择', id: null }, // 收货状态改变时，初始化申请原因
        applyReasons: index == 0 ? rightsReasonList1 : rightsReasonList,
      });
    }
  },

  getApplyReasons(receiptStatus) {
    const params = { rightsReasonType: receiptStatus };
    return fetchApplyReasonList(params)
      .then((res) => {
        return res.data.rightsReasonList.map((reason) => ({
          type: reason.id,
          desc: reason.desc,
        }));
      })
      .catch(() => {
        return [];
      });
  },

  onReceiptStatusDialogConfirm(e) {
    const { index } = e.currentTarget.dataset;
    this.switchReceiptStatus(index);
  },

  onAmountTap() {
    this.setData({
      'serviceFrom.amount.temp': priceFormat(
        this.data.serviceFrom.amount.current * 100,
      ),
      'serviceFrom.amount.focus': true,
      inputDialogVisible: true,
    });
    this.inputDialog.setData({
      cancelBtn: '取消',
      confirmBtn: '确定',
    });
    this.inputDialog._onComfirm = () => {
      this.setData({
        'serviceFrom.amount.current': this.data.serviceFrom.amount.temp,
      });
    };
    this.inputDialog._onCancel = () => {};
  },

  // 对输入的值进行过滤
  onAmountInput(e) {
    let { value } = e.detail;
    const regRes = value.match(/\d+(\.?\d*)?/); // 输入中，允许末尾为小数点
    value = regRes ? regRes[0] : '';
    this.setData({ 'serviceFrom.amount.temp': value });
  },

  // 失去焦点时，更严格的过滤并转化为float
  onAmountBlur(e) {
    let { value } = e.detail;
    console.log(value);
    const regRes = value.match(/\d+(\.?\d+)?/); // 失去焦点时，不允许末尾为小数点
    value = regRes ? regRes[0] : '0';
    value = parseFloat(value);
    if (value > this.data.serviceFrom.amount.max) {
      value = this.data.serviceFrom.amount.max;
    }
    this.setData({
      'serviceFrom.amount.temp': priceFormat(value * 100),
      'serviceFrom.amount.focus': false,
    });
  },

  onAmountFocus() {
    this.setData({ 'serviceFrom.amount.focus': true });
  },

  onRemarkChange(e) {
    const { value } = e.detail;
    this.setData({
      'serviceFrom.remark': value,
    });
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
  // 发起申请售后请求
  onSubmit() {
    this.submitCheck().then(async () => {
      this.setData({ submitting: true });
      const { rightsImageUrls } = this.data.sessionFrom;
      //先上传图片
      let imgs = [];
      for (let f = 0; f < rightsImageUrls.length; f++) {
        let res = await this.upLoadImg(rightsImageUrls[f].url);
        imgs.push(res.msg);
      }
      const params = {
        type: this.data.serviceRequireType,
        orderId: this.orderInfo.order.id,
        goods: this.data.goodsInfo,
        remark: this.data.serviceFrom.remark,
        reason: this.data.serviceFrom.applyReason.desc,
        imgs: imgs,
        price: this.data.serviceFrom.amount.current,
        isGet: serviceFrom.receiptStatus.status ? '1' : '0',
      };
      try {
        let res = await dispatchApplyService(params);
        if (res.code === 0) {
          Toast({
            context: this,
            selector: '#t-toast',
            message: '申请成功',
            icon: '',
          });
          wx.redirectTo({
            url: `/pages/order/after-service-detail/index?rightsNo=${res.data.id}`,
          });
        } else {
          Toast({
            context: this,
            selector: '#t-toast',
            message: '申请失败',
            icon: '',
          });
        }
      } catch (er) {
        Toast({
          context: this,
          selector: '#t-toast',
          message: '申请失败',
          icon: '',
        });
      } finally {
        this.setData({ submitting: false });
      }
    });
  },

  submitCheck() {
    return new Promise((resolve) => {
      const { msg, valid } = this.data.validateRes;
      if (!valid) {
        Toast({
          context: this,
          selector: '#t-toast',
          message: msg,
          icon: '',
        });
        return;
      }
      resolve();
    });
  },

  handleSuccess(e) {
    const { files } = e.detail;
    this.setData({
      'sessionFrom.rightsImageUrls': files,
    });
  },

  handleRemove(e) {
    const { index } = e.detail;
    const {
      sessionFrom: { rightsImageUrls },
    } = this.data;
    rightsImageUrls.splice(index, 1);
    this.setData({
      'sessionFrom.rightsImageUrls': rightsImageUrls,
    });
  },

  handleComplete() {
    this.setData({
      uploading: false,
    });
  },

  handleSelectChange() {
    this.setData({
      uploading: true,
    });
  },
});
