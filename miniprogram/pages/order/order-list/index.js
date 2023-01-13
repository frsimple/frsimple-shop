import { OrderStatus } from '../config';
import {
  fetchOrders,
  fetchOrdersCount,
  fetchDict,
} from '../../../services/order/orderList';
import { buttonArray } from '../../../services/order/orderDetail';
import { cosThumb } from '../../../utils/util';
import Toast, { hideToast } from 'tdesign-miniprogram/toast/index';

Page({
  page: {
    size: 5,
    num: 1,
  },

  data: {
    tabs: [
      { key: '0', text: '全部' },
      { key: OrderStatus.PENDING_PAYMENT, text: '待付款', info: '' },
      { key: OrderStatus.PENDING_DELIVERY, text: '待发货', info: '' },
      { key: OrderStatus.PENDING_RECEIPT, text: '待收货', info: '' },
      { key: OrderStatus.COMPLETE, text: '待评价', info: '' },
      { key: OrderStatus.OVER, text: '已完成', info: '' },
      { key: OrderStatus.PAYMENT_TIMEOUT, text: '已取消', info: '' },
    ],
    curTab: '0',
    orderList: [],
    listLoading: 0,
    pullDownRefreshing: false,
    emptyImg:
      'https://cdn-we-retail.ym.tencent.com/miniapp/order/empty-order-list.png',
    backRefresh: false,
    status: '0',
    isShowPopup: false,
    orderStatus: {},
    goodChecks: [],
    readCheckGoods: [],
    readOrder: {},
    isCanSubmit: false,
  },

  async onLoad(query) {
    let status = query.status;
    status = this.data.tabs.map((t) => t.key).includes(status) ? status : '0';
    //初始化订单状态字典
    let res = await fetchDict('SP_SHOP_ORDERSTATUS');
    let statusMap = {};
    res.data.forEach((row) => {
      statusMap[row.value] = row.label;
    });
    this.setData({
      orderStatus: statusMap,
    });
    await this.init(status);
    this.pullDownRefresh = this.selectComponent('#wr-pull-down-refresh');
  },

  onShow() {
    if (
      wx.getStorageSync('isReload') &&
      wx.getStorageSync('isReload') === '1'
    ) {
      this.refreshList(this.data.curTab);
      wx.setStorageSync('isReload', '0');
    }
    if (!this.data.backRefresh) return;
    this.onRefresh();
    this.setData({ backRefresh: false });
  },

  onReachBottom1() {
    if (this.data.listLoading === 0) {
      this.getOrderList(this.data.curTab);
    }
  },

  onPageScroll(e) {
    this.pullDownRefresh && this.pullDownRefresh.onPageScroll(e);
  },

  async onPullDownRefresh_(e) {
    //const { callback } = e.detail;
    this.setData({ pullDownRefreshing: true });
    await this.refreshList(this.data.curTab);
    this.setData({ pullDownRefreshing: false });
  },

  init(status) {
    status = status !== undefined ? status : this.data.curTab;
    this.setData({
      status,
    });
    this.refreshList(status);
  },

  getOrderList(statusCode = '0', reset = false) {
    const { orderStatus } = this.data;
    if (reset) {
      this.setData({
        orderList: [],
      });
    }
    const params = {
      status: statusCode,
      current: this.page.num,
      size: this.page.size,
    };
    if (statusCode == '0') params.status = '';
    this.setData({ listLoading: 1 });
    fetchOrders(params)
      .then((res) => {
        this.page.num++;
        let orderList = [];
        if (res && res.data && res.data.records) {
          orderList = (res.data.records || []).map((order) => {
            //根据订单状态，生成按钮
            return {
              id: order.id,
              orderNo: order.id,
              //parentOrderNo: order.parentOrderNo,
              //storeId: order.storeId,
              //storeName: order.storeName,
              status: order.status,
              statusDesc: orderStatus[order.status],
              amount: order.nprice ? order.nprice : order.price,
              totalAmount: order.nprice ? order.nprice : order.price,
              //logisticsNo: order.logisticsVO.logisticsNo,
              createTime: order.createTime,
              goodsList: (order.goods || []).map((goods) => ({
                id: goods.id,
                thumb: cosThumb(goods.primaryImage, 70),
                title: goods.goodsName,
                skuId: goods.skuId,
                spuId: goods.spuId,
                specs: goods.selectedSkuInfo ? goods.selectedSkuInfo.rule : '',
                price: goods.price,
                num: goods.buyNum,
                //titlePrefixTags: goods.tagText ? [{ text: goods.tagText }] : [],
              })),
              buttons: buttonArray[order.status],
              //groupInfoVo: order.groupInfoVo,
              freightFee: order.freight,
              applyNo: order.applyNo,
            };
          });
        }
        this.setData({
          orderList: this.data.orderList.concat(orderList),
          listLoading: orderList.length > 0 ? 0 : 2,
        });
      })
      .catch((err) => {
        this.setData({ listLoading: 3 });
        return Promise.reject(err);
      });
  },

  onReTryLoad() {
    this.getOrderList(this.data.curTab);
  },

  onTabChange(e) {
    const { value } = e.detail;
    console.log(value);
    this.setData({
      status: value,
    });
    this.refreshList(value);
  },

  getOrdersCount() {
    fetchOrdersCount().then((res) => {
      const tabsCount = res.data || {};
      this.setData({
        'tabs[1].info': tabsCount.count0,
        'tabs[2].info': tabsCount.count1,
        'tabs[3].info': tabsCount.count2,
        'tabs[4].info': tabsCount.count3,
      });
    });
  },

  refreshList(status = '0') {
    this.page = {
      size: this.page.size,
      num: 1,
    };
    this.setData({ curTab: status, orderList: [] });
    this.getOrderList(status, true);
    this.getOrdersCount();
  },

  onRefresh() {
    this.refreshList(this.data.curTab);
  },
  onShowToast(e) {
    Toast({
      context: this,
      selector: '#t-toast',
      message: e.detail,
      //direction: 'column',
      duration: 3000,
      preventScrollThrough: true,
      icon: 'close-circle',
    });
  },

  onShowPayToast(e) {
    Toast({
      context: this,
      selector: '#t-toast-pay',
      message: '正在支付...',
      duration: 0,
      theme: 'loading',
      direction: 'column',
    });
  },
  onClosePayToast(e) {
    hideToast({ context: this, selector: '#t-toast-pay' });
  },
  onShowGoodPopup(e) {
    let { order } = e.detail;
    if (order.applyNo) {
      wx.navigateTo({
        url: `/pages/order/after-service-detail/index?rightsNo=${order.applyNo}`,
      });
      return;
    }
    let goodsList = order.goodsList;
    goodsList = goodsList.map((row) => {
      row.checked = false;
      row.rnum = row.num;
      return {
        ...row,
      };
    });
    console.log(goodsList);
    this.setData({
      isShowPopup: true,
      readCheckGoods: goodsList,
      readOrder: order,
    });
  },
  onChange(e) {
    let { readCheckGoods } = this.data;
    readCheckGoods[e.detail.value[0]].checked =
      !readCheckGoods[e.detail.value[0]].checked;
    this.setData({
      readCheckGoods: readCheckGoods,
    });
    //isCanSubmit
    let isCanSubmit = false;
    readCheckGoods.forEach((row) => {
      if (row.checked) {
        isCanSubmit = true;
      }
    });
    this.setData({ isCanSubmit });
  },
  handleChangeNum(e) {
    const { index } = e.currentTarget.dataset;
    let { value } = e.detail;
    let { readCheckGoods } = this.data;
    readCheckGoods[index].rnum = value;
    this.setData({
      readCheckGoods: readCheckGoods,
    });
  },
  handlePopupBtn(e) {
    this.setData({
      isShowPopup: false,
    });
  },
  handlePopupNextBtn(e) {
    let { readCheckGoods, readOrder } = this.data;
    let maxPrice = 0.0;
    let goods = [];
    readCheckGoods.forEach((row) => {
      if (row.checked) {
        maxPrice += row.price * row.rnum;
        goods.push(row);
      }
    });

    //计算最大可退款金额，如果有特殊改价则以特殊改价金额为准
    if (maxPrice > parseFloat(readOrder.totalAmount)) {
      readOrder['maxPrice'] = parseFloat(readOrder.totalAmount);
    } else {
      readOrder['maxPrice'] = maxPrice;
    }
    let orderInfo = {
      goods: goods,
      order: readOrder,
    };
    this.setData({
      isShowPopup: false,
    });
    wx.setStorageSync('frsimple_applyservice', orderInfo);
    wx.redirectTo({ url: `/pages/order/apply-service/index` });
  },
  onOrderCardTap(e) {
    const { order } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/order/order-detail/index?orderNo=${order.orderNo}`,
    });
  },
});
