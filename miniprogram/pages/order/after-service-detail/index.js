import Toast from 'tdesign-miniprogram/toast/index';
import { ServiceType } from '../config';
import { getRightsDetail, addOrEditExpress } from './api';
import { fetchDict } from '../../../services/order/orderList';

const TitleConfig = {
  [ServiceType.ORDER_CANCEL]: '退款详情',
  [ServiceType.ONLY_REFUND]: '退款详情',
  [ServiceType.RETURN_GOODS]: '退货退款详情',
};

Page({
  data: {
    pageLoading: true,
    serviceRaw: {},
    service: {},
    deliveryButton: {},
    gallery: {
      current: 0,
      show: false,
      proofs: [],
    },
    showProofs: false,
    backRefresh: false,
    applyStatus: {},
    applyType: {},
    companyMap: {},
    companyList: [],
    expressDia: {
      title: '修改运单号',
      visible: false,
      company: '',
      expressNo: '',
    },
  },

  async onLoad(query) {
    let res = await fetchDict('SP_SHOP_APPLYSTATUS');
    let applyStatus = {};
    res.data.forEach((row) => {
      applyStatus[row.value] = row.label;
    });
    let res1 = await fetchDict('SP_SHOP_APPLYTYPE');
    let applyType = {};
    res1.data.forEach((row) => {
      applyType[row.value] = row.label;
    });

    let res2 = await fetchDict('SP_SHOP_EXPRESSCOMPANY');
    let companyMap = {};
    let companyList = res2.data;
    res2.data.forEach((row) => {
      companyMap[row.value] = row.label;
    });
    this.setData({
      applyStatus,
      applyType,
      companyMap,
      companyList,
    });
    this.rightsNo = query.rightsNo;
    this.inputDialog = this.selectComponent('#input-dialog');
    this.init();
  },

  onShow() {
    // 当从其他页面返回，并且 backRefresh 被置为 true 时，刷新数据
    if (!this.data.backRefresh) return;
    this.init();
    this.setData({ backRefresh: false });
  },

  // 页面刷新，展示下拉刷新
  onPullDownRefresh_(e) {
    const { callback } = e.detail;
    return this.getService().then(() => callback && callback());
  },

  init() {
    this.setData({ pageLoading: true });
    this.getService().then(() => {
      this.setData({ pageLoading: false });
    });
  },

  getService() {
    return getRightsDetail(this.rightsNo).then((res) => {
      const serviceRaw = res.data;
      // 滤掉填写运单号、修改运单号按钮，这两个按钮特殊处理，不在底部按钮栏展示
      if (!serviceRaw.buttonVOs) serviceRaw.buttonVOs = [];
      const deliveryButton = {};
      let statusDesc = '';
      if (serviceRaw.status == '0') {
        statusDesc = '请耐心等待，商家将在1个工作日内进行审核';
      } else if (serviceRaw.status == '-1') {
        statusDesc = '售后单已撤销';
      } else if (serviceRaw.status == '99') {
        if (serviceRaw.result === '1') {
          statusDesc = '商家同意申请，退款金额在三个工作日内到账';
        } else {
          statusDesc = '商家拒绝申请。拒绝原因：' + serviceRaw.resultMsg;
        }
      }
      const service = {
        id: serviceRaw.id,
        serviceNo: serviceRaw.id,
        //storeName: serviceRaw.rights.storeName,
        type: serviceRaw.type,
        typeDesc: this.data.applyType[serviceRaw.type],
        status: serviceRaw.status,
        //statusIcon: this.genStatusIcon(serviceRaw.rights),
        statusName: this.data.applyStatus[serviceRaw.status],
        statusDesc: statusDesc,
        amount: serviceRaw.price,
        goodsList: (serviceRaw.goods || []).map((item, i) => ({
          ...item,
          stockQuantity: 0,
        })),
        orderNo: serviceRaw.orderId, // 订单编号
        rightsNo: serviceRaw.id, // 售后服务单号
        rightsReasonDesc: serviceRaw.reason, // 申请售后原因
        // isRefunded:
        //   serviceRaw.rights.userRightsStatus === ServiceStatus.REFUNDED, // 是否已退款
        // refundMethodList: (serviceRaw.refundMethodList || []).map((m) => ({
        //   name: m.refundMethodName,
        //   amount: m.refundMethodAmount,
        // })), // 退款明细
        refundRequestAmount: serviceRaw.price, // 申请退款金额
        //payTraceNo: serviceRaw.rightsRefund.traceNo, // 交易流水号
        createTime: serviceRaw.createTime, // 申请时间
        logisticsNo: serviceRaw.express?.expressNo
          ? serviceRaw.express.expressNo
          : '', // 退货物流单号
        logisticsCompanyCode: serviceRaw.express?.company
          ? serviceRaw.express.company
          : '', // 退货物流公司
        logisticsCompanyName: serviceRaw.express?.company
          ? this.data.companyMap[serviceRaw.express.company]
          : '', // 退货物流公司
        remark: serviceRaw.remark, // 退货备注
        // receiverName: serviceRaw.logisticsVO.receiverName, // 收货人
        // receiverPhone: serviceRaw.logisticsVO.receiverPhone, // 收货人电话
        // receiverAddress: this.composeAddress(serviceRaw), // 收货人地址
        applyRemark: serviceRaw.reason, // 申请退款时的填写的说明
        //buttons: serviceRaw.buttonVOs || [],
        //logistics: serviceRaw.logisticsVO,
      };
      const proofs = serviceRaw.imgs || [];
      this.setData({
        serviceRaw,
        service,
        deliveryButton,
        'gallery.proofs': proofs,
        showProofs: true,
      });
      wx.setNavigationBarTitle({
        title: TitleConfig[service.type],
      });
    });
  },

  composeAddress(service) {
    return [
      service.logisticsVO.receiverProvince,
      service.logisticsVO.receiverCity,
      service.logisticsVO.receiverCountry,
      service.logisticsVO.receiverArea,
      service.logisticsVO.receiverAddress,
    ]
      .filter((item) => !!item)
      .join(' ');
  },

  onRefresh() {
    this.init();
  },

  editLogistices() {
    this.setData({
      inputDialogVisible: true,
    });
    this.inputDialog.setData({
      cancelBtn: '取消',
      confirmBtn: '确定',
    });
    this.inputDialog._onComfirm = () => {
      Toast({
        message: '确定填写物流单号',
      });
    };
  },

  onProofTap(e) {
    if (this.data.gallery.show) {
      this.setData({
        'gallery.show': false,
      });
      return;
    }
    const { index } = e.currentTarget.dataset;
    this.setData({
      'gallery.show': true,
      'gallery.current': index,
    });
  },

  onGoodsCardTap(e) {
    const { index } = e.currentTarget.dataset;
    const goods = this.data.serviceRaw.rightsItem[index];
    wx.navigateTo({ url: `/pages/goods/details/index?skuId=${goods.skuId}` });
  },

  onServiceNoCopy() {
    wx.setClipboardData({
      data: this.data.service.serviceNo,
    });
  },

  onAddressCopy() {
    wx.setClipboardData({
      data: `${this.data.service.receiverName}  ${this.data.service.receiverPhone}\n${this.data.service.receiverAddress}`,
    });
  },

  async confirmDialog(e) {
    if (!this.data.expressDia.expressNo || !this.data.expressDia.company) {
      Toast({
        context: this,
        selector: '#t-toast',
        message: '请填写运单号和快递公司',
      });
      return;
    }
    let res = await addOrEditExpress({
      id: this.data.serviceRaw.id,
      express: {
        expressNo: this.data.expressDia.expressNo,
        company: this.data.expressDia.company,
      },
    });
    if (res.code == 0) {
      this.setData({
        'expressDia.visible': false,
        'service.logisticsNo': this.data.expressDia.expressNo,
        'service.logisticsCompanyCode': this.data.expressDia.company,
        'service.logisticsCompanyName':
          this.data.companyMap[this.data.expressDia.company],
      });

      Toast({
        context: this,
        selector: '#t-toast',
        message: '修改成功',
      });
    } else {
      Toast({
        context: this,
        selector: '#t-toast',
        message: '修改失败',
      });
    }
  },

  closeDialog() {
    const { expressDia } = this.data;
    this.setData({ 'expressDia.visible': false });
  },
  onExpressChange(e) {
    this.setData({
      'expressDia.company': e.detail.value,
    });
  },
  onNoInput(e) {
    this.setData({
      'expressDia.expressNo': e.detail.value,
    });
  },
  onAddExpress() {
    this.setData({
      'expressDia.visible': true,
      'expressDia.title': '填写运单号',
      'expressDia.company': '',
      'expressDia.expressNo': '',
    });
  },
  onEditExpress() {
    console.log(this.data.serviceRaw.express.company);
    this.setData({
      'expressDia.visible': true,
      'expressDia.title': '修改运单号',
      'expressDia.expressNo': this.data.serviceRaw.express.expressNo,
      'expressDia.company': this.data.serviceRaw.express.company,
    });
  },
});
