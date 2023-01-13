import { formatTime } from '../../../utils/util';
import { LogisticsIconMap } from '../config';
import { fetchOrderDetail } from '../../../services/order/orderDetail';
import { fetchDict } from '../../../services/order/orderList';
import Toast from 'tdesign-miniprogram/toast/index';
import { getAddressPromise } from '../../usercenter/address/list/util';

Page({
  data: {
    pageLoading: true,
    order: {}, // 后台返回的原始数据
    _order: {}, // 内部使用和提供给 order-card 的数据
    storeDetail: {},
    countDownTime: null,
    addressEditable: false,
    backRefresh: false, // 用于接收其他页面back时的状态
    formatCreateTime: '', //格式化订单创建时间
    logisticsNodes: [],
    /** 订单评论状态 */
    orderHasCommented: true,
    orderStatus: {},
    expressMap: {},
  },

  async onLoad(query) {
    //初始化订单状态字典
    let res = await fetchDict('SP_SHOP_ORDERSTATUS');
    let statusMap = {};
    res.data.forEach((row) => {
      statusMap[row.value] = row.label;
    });

    let res1 = await fetchDict('SP_SHOP_EXPRESSCOMPANY');
    let expressMap = {};
    res1.data.forEach((row) => {
      expressMap[row.value] = row.label;
    });
    this.setData({
      orderStatus: statusMap,
      expressMap,
    });
    this.orderNo = query.orderNo;
    this.init();
    this.navbar = this.selectComponent('#navbar');
    this.pullDownRefresh = this.selectComponent('#wr-pull-down-refresh');
  },

  onShow() {
    // 当从其他页面返回，并且 backRefresh 被置为 true 时，刷新数据
    if (!this.data.backRefresh) return;
    this.onRefresh();
    this.setData({ backRefresh: false });
  },

  onPageScroll(e) {
    this.pullDownRefresh && this.pullDownRefresh.onPageScroll(e);
  },

  onImgError(e) {
    if (e.detail) {
      console.error('img 加载失败');
    }
  },

  // 页面初始化，会展示pageLoading
  init() {
    this.setData({ pageLoading: true });
    //this.getStoreDetail();
    this.getDetail()
      .then(() => {
        this.setData({ pageLoading: false });
      })
      .catch((e) => {
        console.error(e);
      });
  },

  // 页面刷新，展示下拉刷新
  onRefresh() {
    this.init();
    // 如果上一页为订单列表，通知其刷新数据
    const pages = getCurrentPages();
    const lastPage = pages[pages.length - 2];
    if (lastPage) {
      lastPage.data.backRefresh = true;
    }
  },

  // 页面刷新，展示下拉刷新
  onPullDownRefresh_(e) {
    const { callback } = e.detail;
    return this.getDetail().then(() => callback && callback());
  },

  getDetail() {
    const { orderStatus } = this.data;
    return fetchOrderDetail(this.orderNo).then((res) => {
      const order = res.data;
      const _order = {
        id: order.id,
        orderNo: order.id,
        status: order.status,
        statusDesc: orderStatus[order.status],
        amount: order.nprice ? order.nprice : order.price,
        totalAmount: order.nprice ? order.nprice : order.price,
        freightFee: order.freight,
        goodsList: (order.goods || []).map((goods) =>
          Object.assign({}, goods, {
            id: goods.id,
            thumb: goods.primaryImage,
            title: goods.goodsName,
            skuId: goods.skuId,
            spuId: goods.spuId,
            specs: goods.selectedSkuInfo ? goods.selectedSkuInfo.rule : '',
            price: goods.price, // 商品销售单价, 优先取限时活动价
            num: goods.buyNum,
            buttons: [],
          }),
        ),
        buttons: [],
        createTime: order.createtime,
        receiverAddress: this.composeAddress(order),
      };
      this.setData({
        order,
        _order,
        formatCreateTime: order.createtime, // 格式化订单创建时间
        countDownTime: this.computeCountDownTime(order.createtime),
        addressEditable:
          order.status != '-1' &&
          (order.status == '00' || order.status == '01'), // 订单正在取消审核时不允许修改地址（但是返回的状态码与待发货一致）
        isPaid: order.status != '-1' && order.status != '00',
      });
    });
  },

  // 展开物流节点
  flattenNodes(nodes) {
    return (nodes || []).reduce((res, node) => {
      return (node.nodes || []).reduce((res1, subNode, index) => {
        res1.push({
          title: index === 0 ? node.title : '', // 子节点中仅第一个显示title
          desc: subNode.status,
          date: formatTime(+subNode.timestamp, 'YYYY-MM-DD HH:mm:ss'),
          icon: index === 0 ? LogisticsIconMap[node.code] || '' : '', // 子节点中仅第一个显示icon
        });
        return res1;
      }, res);
    }, []);
  },

  datermineInvoiceStatus(order) {
    // 1-已开票
    // 2-未开票（可补开）
    // 3-未开票
    // 4-门店不支持开票
    return order.invoiceStatus;
  },

  // 拼接省市区
  composeAddress(order) {
    return [
      order.takeInfo.rarea.provinceName,
      order.takeInfo.rarea.cityName,
      //order.takeInfo.rarea.receiverCountry,
      order.takeInfo.rarea.districtName,
      order.takeInfo.raddress,
    ]
      .filter((s) => !!s)
      .join(' ');
  },

  // 仅对待支付状态计算付款倒计时
  // 返回时间若是大于2020.01.01，说明返回的是关闭时间，否则说明返回的直接就是剩余时间
  computeCountDownTime(createtime) {
    let orderCreate = new Date(createtime);
    orderCreate.setMinutes(orderCreate.getMinutes() + 15);
    console.log(orderCreate - Date.now());
    return orderCreate - Date.now();
  },

  onCountDownFinish() {
    //this.setData({ countDownTime: -1 });
    const { countDownTime, order } = this.data;
    if (
      countDownTime > 0 ||
      (order && order.groupInfoVo && order.groupInfoVo.residueTime > 0)
    ) {
      this.onRefresh();
    }
  },

  onGoodsCardTap(e) {
    const { goods } = e.currentTarget.dataset;
    if (goods.skuId) {
      wx.navigateTo({
        url: `/pages/goods/details/index?spuId=${goods.spuId}&skuId=${goods.skuId}`,
      });
    } else {
      wx.navigateTo({
        url: `/pages/goods/details/index?spuId=${goods.spuId}`,
      });
    }
  },

  onEditAddressTap() {
    getAddressPromise()
      .then((address) => {
        this.setData({
          'order.logisticsVO.receiverName': address.name,
          'order.logisticsVO.receiverPhone': address.phone,
          '_order.receiverAddress': address.address,
        });
      })
      .catch(() => {});

    wx.navigateTo({
      url: `/pages/usercenter/address/list/index?selectMode=1`,
    });
  },

  onOrderNumCopy() {
    wx.setClipboardData({
      data: this.data.order.orderNo,
    });
  },

  onDeliveryNumCopy() {
    wx.setClipboardData({
      data: this.data.order.logisticsVO.logisticsNo,
    });
  },

  onToInvoice() {
    wx.navigateTo({
      url: `/pages/order/invoice/index?orderNo=${this.data._order.orderNo}`,
    });
  },

  onSuppleMentInvoice() {
    wx.navigateTo({
      url: `/pages/order/receipt/index?orderNo=${this.data._order.orderNo}`,
    });
  },

  onDeliveryClick() {
    const logisticsData = {
      nodes: this.data.order.orderDetailsList,
      express: this.data.order.express,
      expressMap: this.data.expressMap,
    };
    wx.navigateTo({
      url: `/pages/order/delivery-detail/index?data=${encodeURIComponent(
        JSON.stringify(logisticsData),
      )}`,
    });
  },

  /** 跳转订单评价 */
  navToCommentCreate() {
    wx.navigateTo({
      url: `/pages/order/createComment/index?orderNo=${this.orderNo}`,
    });
  },

  /** 跳转拼团详情/分享页*/
  toGrouponDetail() {
    wx.showToast({ title: '点击了拼团' });
  },

  clickService() {
    Toast({
      context: this,
      selector: '#t-toast',
      message: '您点击了联系客服',
    });
  },

  onOrderInvoiceView() {
    wx.navigateTo({
      url: `/pages/order/invoice/index?orderNo=${this.orderNo}`,
    });
  },
});
