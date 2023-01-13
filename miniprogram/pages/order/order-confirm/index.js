import Toast, { hideToast } from 'tdesign-miniprogram/toast/index';
import {
  queryDefaultRevAddress,
  dispatchCommitPay,
} from '../../../services/order/orderConfirm';
const stripeImg = `https://cdn-we-retail.ym.tencent.com/miniapp/order/stripe.png`;

Page({
  data: {
    placeholder: '备注信息',
    stripeImg,
    loading: true,
    settleDetailData: {
      storeGoodsList: [], //正常下单商品列表
      outOfStockGoodsList: [], //库存不足商品
      abnormalDeliveryGoodsList: [], // 不能正常配送商品
      inValidGoodsList: [], // 失效或者库存不足
      limitGoodsList: [], //限购商品
      couponList: [], //门店优惠券信息
      priceCount: 0,
      rateCount: 0,
      settleType: false,
    }, // 获取结算页详情 data
    orderCardList: [], // 仅用于商品卡片展示
    couponsShow: false, // 显示优惠券的弹框
    goodsRequestList: [],
    userAddressReq: null,
    popupShow: false, // 不在配送范围 失效 库存不足 商品展示弹框
    notesPosition: 'center',
    storeInfoList: [],
    storeNoteIndex: 0, //当前填写备注门店index
    promotionGoodsList: [], //当前门店商品列表(优惠券)
    couponList: [], //当前门店所选优惠券
    submitCouponList: [], //所有门店所选优惠券
    currentStoreId: null, //当前优惠券storeId
    userAddress: null,
    remark: '',
    orderId: '',
    payJson: null,
  },

  payLock: false,
  noteInfo: [],
  tempNoteInfo: [],
  onLoad(options) {
    //this.handleOptionsParams(options);
  },
  onShow() {
    Toast({
      context: this,
      selector: '#t-toast',
      message: '加载中...',
      duration: 0,
      theme: 'loading',
      direction: 'column',
    });
    this.handleOptionsParams();
  },

  //计算一下金额
  compAmont(obj, type, params1, params2) {
    let rate = 0;
    let val = type == 'aj' ? params1 : params2;
    if (val <= obj.param1) {
      rate = obj.param2;
    } else {
      let other = val - obj.param1;
      rate = Math.ceil(other / obj.param3) * obj.param4;
    }
    return rate;
  },
  // 处理不同情况下跳转到结算页时需要的参数
  async handleOptionsParams(options, couponList) {
    let goodsRequestList = wx.getStorageSync('order.goodsRequestList'); // 商品列表
    let addressId = '';
    if (wx.getStorageSync('address.id')) {
      addressId = wx.getStorageSync('address.id');
    }
    let res = await queryDefaultRevAddress(addressId);
    if (res.data) {
      let address = res.data;
      this.setData({
        userAddress: address,
      });
      //计算运费和商品总价
      let rateC = 0;
      let priceC = 0;
      goodsRequestList = goodsRequestList.map((row) => {
        row['isCanGo'] = true;
        let currentRateC = 0;
        let buyNum = row.buyNum;
        let weight = row.weight;
        let currentPrice = row.buyNum * row.price;
        let rateInfo = row.rateTemInfo;
        console.log(row);
        if (rateInfo.nosend.value) {
          let nosend = rateInfo.nosend.value;
          if (
            nosend.indexOf(address.cityCode) > 0 ||
            nosend.indexOf(address.districtCode) > 0 ||
            nosend.indexOf(address.provinceCode) > 0
          ) {
            row['isCanGo'] = false;
          }
        }
        //自定义邮费
        if (rateInfo.type == '01') {
          let isGo = false;
          if (rateInfo.payjson.length != 0) {
            rateInfo.payjson.forEach((item) => {
              if (
                typeof item.param0 == 'object' &&
                (item.param0.join(',').indexOf(address.cityCode) > 0 ||
                  item.param0.join(',').indexOf(address.districtCode) > 0 ||
                  item.param0.join(',').indexOf(address.provinceCode) > 0) &&
                !isGo
              ) {
                isGo = true;
                currentRateC = this.compAmont(
                  item,
                  rateInfo.paytype,
                  buyNum,
                  weight,
                );
              }
            });
          }
          if (!isGo) {
            currentRateC = this.compAmont(
              rateInfo.payjson[0],
              rateInfo.paytype,
              buyNum,
              weight,
            );
          }
          if (rateInfo.conjson.length != 0) {
            rateInfo.conjson.forEach((item) => {
              if (
                (item.param0.join(',').indexOf(address.cityCode) > 0 ||
                  item.param0.join(',').indexOf(address.districtCode) > 0 ||
                  item.param0.join(',').indexOf(address.provinceCode) > 0) &&
                (buyNum >= item.param2 || currentPrice >= item.param1)
              ) {
                currentRateC = 0;
              }
            });
          }
        }
        rateC += currentRateC;
        priceC += currentPrice;
        return {
          ...row,
        };
      });
      console.log(rateC, priceC);
      this.setData({
        'settleDetailData.rateCount': rateC,
        'settleDetailData.priceCount': priceC + rateC,
        'settleDetailData.settleType': true,
      });
    } else {
      this.setData({
        'settleDetailData.settleType': false,
        'settleDetailData.rateCount': '--',
        'settleDetailData.priceCount': '--',
        userAddress: null,
      });
    }
    this.setData({
      'settleDetailData.storeGoodsList': goodsRequestList,
      loading: false,
    });

    hideToast({ context: this, selector: '#t-toast' });
  },

  isInvalidOrder(data) {
    // 失效 不在配送范围 限购的商品 提示弹窗
    if (
      (data.limitGoodsList && data.limitGoodsList.length > 0) ||
      (data.abnormalDeliveryGoodsList &&
        data.abnormalDeliveryGoodsList.length > 0) ||
      (data.inValidGoodsList && data.inValidGoodsList.length > 0)
    ) {
      this.setData({ popupShow: true });
      return true;
    }
    this.setData({ popupShow: false });
    if (data.settleType === 0) {
      return true;
    }
    return false;
  },

  handleError() {
    Toast({
      context: this,
      selector: '#t-toast',
      message: '结算异常, 请稍后重试',
      duration: 2000,
      icon: '',
    });

    setTimeout(() => {
      wx.navigateBack();
    }, 1500);
    this.setData({
      loading: false,
    });
  },
  getRequestGoodsList(storeGoodsList) {
    const filterStoreGoodsList = [];
    storeGoodsList &&
      storeGoodsList.forEach((store) => {
        const { storeName } = store;
        store.skuDetailVos &&
          store.skuDetailVos.forEach((goods) => {
            const data = goods;
            data.storeName = storeName;
            filterStoreGoodsList.push(data);
          });
      });
    return filterStoreGoodsList;
  },
  handleGoodsRequest(goods, isOutStock = false) {
    const {
      reminderStock,
      quantity,
      storeId,
      uid,
      saasId,
      spuId,
      goodsName,
      skuId,
      storeName,
      roomId,
    } = goods;
    const resQuantity = isOutStock ? reminderStock : quantity;
    return {
      quantity: resQuantity,
      storeId,
      uid,
      saasId,
      spuId,
      goodsName,
      skuId,
      storeName,
      roomId,
    };
  },
  onGotoAddress() {
    if (this.data.userAddress) {
      wx.navigateTo({
        url: `/pages/usercenter/address/list/index?selectMode=1&isOrderSure=1&id=${this.data.userAddress.id}`,
      });
    } else {
      wx.navigateTo({
        url: `/pages/usercenter/address/list/index?selectMode=1&isOrderSure=1`,
      });
    }
  },
  onNotes(e) {
    const { storenoteindex: storeNoteIndex } = e.currentTarget.dataset;
    // 添加备注信息
    this.setData({
      dialogShow: true,
      storeNoteIndex,
    });
  },
  onInput(e) {
    this.setData({
      remark: e.detail.value,
    });
  },
  onNoteConfirm() {
    this.setData({
      dialogShow: false,
    });
  },
  onNoteCancel() {
    this.setData({
      dialogShow: false,
    });
  },

  onSureCommit() {
    // 商品库存不足继续结算
    const { settleDetailData } = this.data;
    const { outOfStockGoodsList, storeGoodsList, inValidGoodsList } =
      settleDetailData;
    if (
      (outOfStockGoodsList && outOfStockGoodsList.length > 0) ||
      (inValidGoodsList && storeGoodsList)
    ) {
      // 合并正常商品 和 库存 不足商品继续支付
      // 过滤不必要的参数
      const filterOutGoodsList = [];
      outOfStockGoodsList &&
        outOfStockGoodsList.forEach((outOfStockGoods) => {
          const { storeName } = outOfStockGoods;
          outOfStockGoods.unSettlementGoods.forEach((ele) => {
            const data = ele;
            data.quantity = ele.reminderStock;
            data.storeName = storeName;
            filterOutGoodsList.push(data);
          });
        });
      const filterStoreGoodsList = this.getRequestGoodsList(storeGoodsList);
      const goodsRequestList = filterOutGoodsList.concat(filterStoreGoodsList);
      this.handleOptionsParams({ goodsRequestList });
    }
  },
  //支付成功
  paySuccess(payOrderInfo) {
    // 支付成功
    Toast({
      context: this,
      selector: '#t-toast',
      message: '支付成功',
      duration: 2000,
      icon: 'check-circle',
    });
    const paramsStr = Object.keys(payOrderInfo)
      .map((k) => `${k}=${payOrderInfo[k]}`)
      .join('&');
    // 跳转支付结果页面
    wx.redirectTo({ url: `/pages/order/pay-result/index?${paramsStr}` });
  },
  // 提交订单
  async submitOrder() {
    const { settleDetailData, userAddress, remark, orderId, payJson } =
      this.data;
    const _that = this;
    if (orderId) {
      Toast({
        context: this,
        selector: '#t-toast-pay',
        message: '正在支付...',
        duration: 0,
        theme: 'loading',
        direction: 'column',
      });
      wx.requestPayment({
        ...payJson,
        success: (result) => {
          _that.paySuccess({
            totalPaid: settleDetailData.priceCount,
            orderNo: this.data.orderId,
          });
        },
        fail: () => {
          Toast({
            context: this,
            selector: '#t-toast',
            message: '订单支付失败',
            duration: 2000,
            icon: 'close-circle',
          });
        },
        complete: () => {
          hideToast({ context: this, selector: '#t-toast-pay' });
        },
      });
    } else {
      const submitForm = {
        paytype: 'wx',
        price: settleDetailData.priceCount,
        //rprice:priceCount,
        freight: settleDetailData.rateCount,
        takeInfo: userAddress,
        goods: settleDetailData.storeGoodsList,
        datasource: 'wx',
        remark: remark,
      };
      try {
        Toast({
          context: this,
          selector: '#t-toast',
          message: '订单提交中...',
          duration: 0,
          theme: 'loading',
          direction: 'column',
        });
        let res = await dispatchCommitPay(submitForm);
        hideToast({ context: this, selector: '#t-toast' });
        if (res.code == 0) {
          this.setData({
            orderId: res.data.orderId,
            payJson: res.data.payJson,
          });
          Toast({
            context: this,
            selector: '#t-toast-pay',
            message: '正在支付...',
            duration: 0,
            theme: 'loading',
            direction: 'column',
          });
          wx.requestPayment({
            ...res.data.payJson,
            success: (result) => {
              _that.paySuccess({
                totalPaid: settleDetailData.priceCount,
                orderNo: this.data.orderId,
              });
            },
            fail: () => {
              Toast({
                context: this,
                selector: '#t-toast',
                message: '取消订单支付',
                duration: 2000,
                icon: 'check-circle',
              });
            },
            complete: () => {
              hideToast({ context: this, selector: '#t-toast-pay' });
            },
          });
        } else {
          Toast({
            context: this,
            selector: '#t-toast',
            message: res.msg,
            duration: 5000,
          });
        }
      } catch (er) {
        hideToast({ context: this, selector: '#t-toast' });
        Toast({
          context: this,
          selector: '#t-toast',
          message: res.msg,
          duration: 5000,
        });
      } finally {
        //hideToast({ context: this, selector: '#t-toast' });
      }
    }
  },
  hide() {
    // 隐藏 popup
    this.setData({
      'settleDetailData.abnormalDeliveryGoodsList': [],
    });
  },

  onGoodsNumChange(e) {
    const {
      detail: { value },
      currentTarget: {
        dataset: { goods },
      },
    } = e;
    const index = this.goodsRequestList.findIndex(
      ({ storeId, spuId, skuId }) =>
        goods.storeId === storeId &&
        goods.spuId === spuId &&
        goods.skuId === skuId,
    );
    if (index >= 0) {
      // eslint-disable-next-line no-confusing-arrow
      const goodsRequestList = this.goodsRequestList.map((item, i) =>
        i === index ? { ...item, quantity: value } : item,
      );
      this.handleOptionsParams({ goodsRequestList });
    }
  },

  onPopupChange() {
    this.setData({
      popupShow: !this.data.popupShow,
    });
  },
});
