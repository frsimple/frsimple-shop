import { getRightsList, delApplyService } from './api';
import { AfterServiceStatus } from '../config';
import { fetchDict } from '../../../services/order/orderList';
import Dialog from 'tdesign-miniprogram/dialog/index';
Page({
  page: {
    size: 10,
    num: 1,
  },

  data: {
    tabs: [
      {
        key: -1,
        text: '全部',
      },
      {
        key: AfterServiceStatus.TO_AUDIT,
        text: '待审核',
      },
      {
        key: AfterServiceStatus.THE_APPROVED,
        text: '已审核',
      },
      {
        key: AfterServiceStatus.COMPLETE,
        text: '已完成',
      },
      {
        key: AfterServiceStatus.CLOSED,
        text: '已关闭',
      },
    ],
    curTab: -1,
    dataList: [],
    listLoading: 0, // 0-未加载，1-加载中，2-已全部加载
    pullDownRefreshing: false, // 下拉刷新时不显示load-more
    emptyImg:
      'https://cdn-we-retail.ym.tencent.com/miniapp/order/empty-order-list.png',
    backRefresh: false,
    applyStatus: {},
    applyType: {},
  },

  async onLoad(query) {
    // let status = parseInt(query.status);
    // status = this.data.tabs.map((t) => t.key).includes(status) ? status : -1;
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
    this.setData({
      applyStatus,
      applyType,
    });
    this.init();
    this.pullDownRefresh = this.selectComponent('#wr-pull-down-refresh');
  },

  onShow() {
    // 当从其他页面返回，并且 backRefresh 被置为 true 时，刷新数据
    if (!this.data.backRefresh) return;
    this.onRefresh();
    this.setData({
      backRefresh: false,
    });
  },

  onReachBottom() {
    if (this.data.listLoading === 0) {
      this.getAfterServiceList(this.data.curTab);
    }
  },

  onPageScroll(e) {
    this.pullDownRefresh && this.pullDownRefresh.onPageScroll(e);
  },

  onPullDownRefresh_(e) {
    //const { callback } = e.detail;
    this.setData({
      pullDownRefreshing: true,
    }); // 下拉刷新时不显示load-more
    this.refreshList(this.data.curTab)
      .then(() => {
        this.setData({
          pullDownRefreshing: false,
        });
        ///callback && callback();
      })
      .catch((err) => {
        this.setData({
          pullDownRefreshing: false,
        });
        //Promise.reject(err);
      });
  },

  init(status) {
    status = status !== undefined ? status : this.data.curTab;
    this.refreshList(status);
  },

  getAfterServiceList(statusCode = -1, reset = false) {
    const params = {
      parameter: {
        size: this.page.size,
        current: this.page.num,
      },
    };
    //if (statusCode !== -1) params.parameter.afterServiceStatus = statusCode;
    this.setData({
      listLoading: 1,
    });
    return getRightsList(params)
      .then((res) => {
        this.page.num++;
        let dataList = [];
        let { tabs } = this.data;
        if (res.code == 0) {
          dataList = (res.data.records || []).map((_data) => {
            return {
              id: _data.id,
              serviceNo: _data.id,
              //storeName: _data.rights.storeName,
              type: _data.type,
              typeDesc: this.data.applyType[_data.type],
              typeDescIcon:
                _data.type === 'REFUND_MONEY'
                  ? 'money-circle'
                  : 'return-goods-1',
              status: _data.status,
              statusName: this.data.applyStatus[_data.status],
              statusDesc: '',
              amount: _data.price,
              goodsList: _data.goods.map((row) => {
                row['stockQuantity'] = 0;
                return { ...row };
              }),
              buttons: [],
              remark: _data.remark, // 退货备注
              reason: _data.reason,
            };
          });
        }
        return new Promise((resolve) => {
          if (reset) {
            this.setData(
              {
                dataList: [],
              },
              () => resolve(),
            );
          } else resolve();
        }).then(() => {
          this.setData({
            tabs,
            dataList: this.data.dataList.concat(dataList),
            listLoading: dataList.length > 0 ? 0 : 2,
          });
        });
      })
      .catch((err) => {
        this.setData({
          listLoading: 3,
        });
        return Promise.reject(err);
      });
  },

  onReTryLoad() {
    this.getAfterServiceList(this.data.curTab);
  },

  onTabChange(e) {
    const { value } = e.detail;
    const tab = this.data.tabs.find((v) => v.key === value);
    if (!tab) return;
    this.refreshList(value);
  },

  refreshList(status = -1) {
    this.page = {
      size: 10,
      num: 1,
    };
    this.setData({
      curTab: status,
      dataList: [],
    });
    return this.getAfterServiceList(status, true);
  },

  onRefresh() {
    this.refreshList(this.data.curTab);
  },

  // 点击订单卡片
  onAfterServiceCardTap(e) {
    wx.navigateTo({
      url: `/pages/order/after-service-detail/index?rightsNo=${e.currentTarget.dataset.order.id}`,
    });
  },
  onDelApplyService(e) {
    const _that = this;
    const dialogConfig = {
      context: this,
      title: '撤销申请确认',
      content: '是否确认撤销申请？',
      confirmBtn: { content: '确定', variant: 'base' },
      cancelBtn: '取消',
    };
    Dialog.confirm(dialogConfig)
      .then(async () => {
        let res = await delApplyService(e.currentTarget.dataset.apply);
        if (res.code == 0) {
          this.refreshList(this.data.curTab);
          Toast({
            context: _that,
            selector: '#t-toast',
            message: '撤销成功',
            duration: 2000,
            icon: 'check-circle',
          });
        } else {
          Toast({
            context: _that,
            selector: '#t-toast',
            message: '撤销失败',
            duration: 2000,
            icon: 'close-circle',
          });
        }
      })
      .catch(() => console.log('点击了取消'))
      .finally(() => Dialog.close());
  },
});
