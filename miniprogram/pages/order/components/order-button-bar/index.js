import Toast, { hideToast } from 'tdesign-miniprogram/toast/index';
import Dialog from 'tdesign-miniprogram/dialog/index';
import { OrderButtonTypes } from '../../config';
import {
  fetchQueryPayJson,
  fetchConfirmOrder,
  fetchCanlOrder,
  fetchCanlOrder1,
  fetchOrderPay,
} from '../../../../services/order/orderDetail';

Component({
  options: {
    addGlobalClass: true,
  },
  properties: {
    order: {
      type: Object,
      observer(order) {
        // 判定有传goodsIndex ，则认为是商品button bar, 仅显示申请售后按钮
        if (this.properties?.goodsIndex !== null) {
          const goods = order.goodsList[Number(this.properties.goodsIndex)];
          this.setData({
            buttons: {
              left: [],
              right: (goods.buttons || []).filter(
                (b) => b.type == OrderButtonTypes.APPLY_REFUND,
              ),
            },
          });
          return;
        }
        // 订单的button bar 不显示申请售后按钮
        const buttonsRight = (order.buttons || [])
          // .filter((b) => b.type !== OrderButtonTypes.APPLY_REFUND)
          .map((button) => {
            //邀请好友拼团按钮
            if (
              button.type === OrderButtonTypes.INVITE_GROUPON &&
              order.groupInfoVo
            ) {
              const {
                groupInfoVo: { groupId, promotionId, remainMember, groupPrice },
                goodsList,
              } = order;
              const goodsImg = goodsList[0] && goodsList[0].imgUrl;
              const goodsName = goodsList[0] && goodsList[0].name;
              return {
                ...button,
                openType: 'share',
                dataShare: {
                  goodsImg,
                  goodsName,
                  groupId,
                  promotionId,
                  remainMember,
                  groupPrice,
                  storeId: order.storeId,
                },
              };
            }
            return button;
          });
        // 删除订单按钮单独挪到左侧
        const deleteBtnIndex = buttonsRight.findIndex(
          (b) => b.type === OrderButtonTypes.DELETE,
        );
        let buttonsLeft = [];
        if (deleteBtnIndex > -1) {
          buttonsLeft = buttonsRight.splice(deleteBtnIndex, 1);
        }
        this.setData({
          buttons: {
            left: buttonsLeft,
            right: buttonsRight,
          },
        });
      },
    },
    goodsIndex: {
      type: Number,
      value: null,
    },
    isBtnMax: {
      type: Boolean,
      value: false,
    },
  },

  data: {
    order: {},
    buttons: {
      left: [],
      right: [],
    },
    showConfigDiaLog: false,
  },

  methods: {
    // 点击【订单操作】按钮，根据按钮类型分发
    onOrderBtnTap(e) {
      const { type } = e.currentTarget.dataset;
      switch (type) {
        case OrderButtonTypes.DELETE:
          this.onDelete(this.data.order);
          break;
        case OrderButtonTypes.CANCEL: //订单取消
          this.onCancel(this.data.order);
          break;
        case OrderButtonTypes.CONFIRM: //确认收货
          this.onConfirm(this.data.order);
          break;
        case OrderButtonTypes.PAY: //支付
          this.onPay(this.data.order);
          break;
        case OrderButtonTypes.APPLY_REFUND: //申请售后
          this.onApplyRefund(this.data.order);
          break;
        case OrderButtonTypes.VIEW_REFUND:
          this.onViewRefund(this.data.order);
          break;
        case OrderButtonTypes.COMMENT: //订单评价
          this.onAddComent(this.data.order);
          break;
        case OrderButtonTypes.INVITE_GROUPON:
          //分享邀请好友拼团
          break;
        case OrderButtonTypes.REBUY: //再次购买
          this.onBuyAgain(this.data.order);
      }
    },

    onCancel(order) {
      const _that = this;
      if (order.status == '01') {
        Dialog.confirm({
          title: '是否确定取消订单？',
          content: '',
          confirmBtn: { content: '确定', variant: 'base' },
          cancelBtn: '在考虑一下 ',
        })
          .then(() => {
            fetchCanlOrder1(order.id)
              .then((res) => {
                Dialog.close();
                if (res.code == 0) {
                  _that.triggerEvent('refresh');
                  _that.triggerEvent('showtoast', '订单取消成功');
                } else {
                  _that.triggerEvent('showtoast', '订单取消失败:' + res.msg);
                }
              })
              .catch((er) => {
                _that.triggerEvent('showtoast', '订单取消失败:' + er);
              });
          })
          .catch(() => {});
      } else if (order.status == '00') {
        Dialog.confirm({
          title: '是否确定取消订单？',
          content: '',
          confirmBtn: { content: '确定', variant: 'base' },
          cancelBtn: '在考虑一下 ',
        })
          .then(() => {
            fetchCanlOrder(order.id)
              .then((res) => {
                Dialog.close();
                if (res.code == 0) {
                  _that.triggerEvent('refresh');
                  _that.triggerEvent('showtoast', '订单取消成功');
                } else {
                  _that.triggerEvent('showtoast', '订单取消失败:' + res.msg);
                }
              })
              .catch((er) => {
                _that.triggerEvent('showtoast', '订单取消失败:' + er);
              });
          })
          .catch(() => {});
      }
    },

    onConfirm(order) {
      const _that = this;
      Dialog.confirm({
        title: '确认是否已经收到货？',
        content: '',
        confirmBtn: { content: '确定', variant: 'base' },
        cancelBtn: '取消',
      })
        .then(() => {
          fetchConfirmOrder(order.id)
            .then((res) => {
              if (res.code == 0) {
                _that.triggerEvent('refresh');
              } else {
                _that.triggerEvent('showtoast', '确认收货失败:' + res.msg);
              }
            })
            .catch((er) => {
              _that.triggerEvent('showtoast', '确认收货失败:' + er);
            });
        })
        .catch(() => {});
    },

    async onPay(order) {
      //this.triggerEvent('refresh');
      const _that = this;
      let res = await fetchQueryPayJson(order.id);
      _that.triggerEvent('showpaytoast');
      wx.requestPayment({
        ...res.data,
        success: (result) => {
          this.paySuccess({
            totalPaid: order.amount,
            orderNo: order.id,
          });
        },
        fail: () => {
          _that.triggerEvent('showtoast', '订单支付失败');
        },
        complete: () => {
          _that.triggerEvent('closepaytoast');
        },
      });
    },
    //支付成功
    paySuccess(payOrderInfo) {
      const paramsStr = Object.keys(payOrderInfo)
        .map((k) => `${k}=${payOrderInfo[k]}`)
        .join('&');
      // 跳转支付结果页面
      wx.redirectTo({ url: `/pages/order/pay-result/index?${paramsStr}` });
    },
    onBuyAgain(order) {
      if (order.goodsList[0].skuId) {
        wx.navigateTo({
          url: `/pages/goods/details/index?spuId=${order.goodsList[0].spuId}&skuId=${order.goodsList[0].skuId}`,
        });
      } else {
        wx.navigateTo({
          url: `/pages/goods/details/index?spuId=${order.goodsList[0].spuId}`,
        });
      }
    },

    onApplyRefund(order) {
      this.triggerEvent('showgoodpopup', {
        order: order,
      });
      // const goods = order.goodsList[this.properties.goodsIndex];
      // const params = {
      //   orderNo: order.orderNo,
      //   skuId: goods?.skuId ?? '19384938948343',
      //   spuId: goods?.spuId ?? '28373847384343',
      //   orderStatus: order.status,
      //   logisticsNo: order.logisticsNo,
      //   price: goods?.price ?? 89,
      //   num: goods?.num ?? 89,
      //   createTime: order.createTime,
      //   orderAmt: order.totalAmount,
      //   payAmt: order.amount,
      //   canApplyReturn: true,
      // };
      // const paramsStr = Object.keys(params)
      //   .map((k) => `${k}=${params[k]}`)
      //   .join('&');
      // wx.navigateTo({ url: `/pages/order/apply-service/index?${paramsStr}` });
    },

    onViewRefund() {
      Toast({
        context: this,
        selector: '#t-toast',
        message: '你点击了查看退款',
        icon: '',
      });
    },

    /** 添加订单评论 */
    onAddComent(order) {
      let goodsList = order.goodsList;
      goodsList = goodsList.map((row) => {
        row.rate = 5;
        row.remark = '';
        row.imgs = [];
        return {
          ...row,
        };
      });
      wx.setStorageSync('_goodsList', goodsList);
      wx.navigateTo({
        url: `/pages/goods/comments/create/index?orderNo=${order?.orderNo}`,
      });
    },
  },
});
