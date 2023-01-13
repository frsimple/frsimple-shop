import Dialog from 'tdesign-miniprogram/dialog/index';
import Toast from 'tdesign-miniprogram/toast/index';
import { fetchCartGroupData, fetchDelCart } from '../../services/cart/cart';
import { isLogin } from '../../utils/util';
Page({
  data: {
    cartGroupData: null,
    isAllSelected: false,
    totalAmount: 0,
    selectedGoodsCount: 0,
    loading: true,
  },

  // 调用自定义tabbar的init函数，使页面与tabbar激活状态保持一致
  onShow() {
    this.getTabBar().init();
    if (!isLogin()) {
      wx.switchTab({
        url: '/pages/usercenter/index',
      });
      return;
    }
    this.refreshData();
  },

  onLoad() {},

  async refreshData() {
    let res = await fetchCartGroupData();
    if (res.code == 0) {
      this.setData({
        cartGroupData: res.data,
      });
    }
    this.setData({
      loading: false,
    });
    // this.getCartGroupData().then((res) => {
    //   let isEmpty = true;
    //   const cartGroupData = res.data;
    //   // 一些组件中需要的字段可能接口并没有返回，或者返回的数据结构与预期不一致，需要在此先对数据做一些处理
    //   // 统计门店下加购的商品是否全选、是否存在缺货/无货
    //   for (const store of cartGroupData.storeGoods) {
    //     store.isSelected = true; // 该门店已加购商品是否全选
    //     store.storeStockShortage = false; // 该门店已加购商品是否存在库存不足
    //     if (!store.shortageGoodsList) {
    //       store.shortageGoodsList = []; // 该门店已加购商品如果库存为0需单独分组
    //     }
    //     for (const activity of store.promotionGoodsList) {
    //       activity.goodsPromotionList = activity.goodsPromotionList.filter(
    //         (goods) => {
    //           goods.originPrice = undefined;

    //           // 统计是否有加购数大于库存数的商品
    //           if (goods.quantity > goods.stockQuantity) {
    //             store.storeStockShortage = true;
    //           }
    //           // 统计是否全选
    //           if (!goods.isSelected) {
    //             store.isSelected = false;
    //           }
    //           // 库存为0（无货）的商品单独分组
    //           if (goods.stockQuantity > 0) {
    //             return true;
    //           }
    //           store.shortageGoodsList.push(goods);
    //           return false;
    //         },
    //       );

    //       if (activity.goodsPromotionList.length > 0) {
    //         isEmpty = false;
    //       }
    //     }
    //     if (store.shortageGoodsList.length > 0) {
    //       isEmpty = false;
    //     }
    //   }
    //   cartGroupData.invalidGoodItems = cartGroupData.invalidGoodItems.map(
    //     (goods) => {
    //       goods.originPrice = undefined;
    //       return goods;
    //     },
    //   );
    //   cartGroupData.isNotEmpty = !isEmpty;
    //   this.setData({ cartGroupData });
    // });
  },

  findGoods(spuId, skuId) {
    let currentStore;
    let currentActivity;
    let currentGoods;
    const { storeGoods } = this.data.cartGroupData;
    for (const store of storeGoods) {
      for (const activity of store.promotionGoodsList) {
        for (const goods of activity.goodsPromotionList) {
          if (goods.spuId === spuId && goods.skuId === skuId) {
            currentStore = store;
            currentActivity = currentActivity;
            currentGoods = goods;
            return {
              currentStore,
              currentActivity,
              currentGoods,
            };
          }
        }
      }
    }
    return {
      currentStore,
      currentActivity,
      currentGoods,
    };
  },

  // 注：实际场景时应该调用接口获取购物车数据
  // getCartGroupData() {
  //   const { cartGroupData } = this.data;
  //   if (!cartGroupData) {
  //     return fetchCartGroupData();
  //   }
  //   return Promise.resolve({ data: cartGroupData });
  // },

  // 选择单个商品
  // 注：实际场景时应该调用接口更改选中状态
  selectGoodsService({ spuId, skuId, isSelected }) {
    this.findGoods(spuId, skuId).currentGoods.isSelected = isSelected;
    return Promise.resolve();
  },

  // 全选门店
  // 注：实际场景时应该调用接口更改选中状态
  selectStoreService({ storeId, isSelected }) {
    const currentStore = this.data.cartGroupData.storeGoods.find(
      (s) => s.storeId === storeId,
    );
    currentStore.isSelected = isSelected;
    currentStore.promotionGoodsList.forEach((activity) => {
      activity.goodsPromotionList.forEach((goods) => {
        goods.isSelected = isSelected;
      });
    });
    return Promise.resolve();
  },

  // 加购数量变更
  // 注：实际场景时应该调用接口
  changeQuantityService({ spuId, skuId, quantity }) {
    this.findGoods(spuId, skuId).currentGoods.quantity = quantity;
    return Promise.resolve();
  },

  onBtnClick() {
    console.log('点击了吗');
    wx.switchTab({
      url: '/pages/home/home',
    });
  },
  // 删除加购商品
  // 注：实际场景时应该调用接口
  deleteGoodsService(goods) {
    fetchDelCart({
      ids: goods.ids,
    })
      .then((res) => {
        if (res.code == 0) {
          this.refreshData();
        } else {
          Toast({
            context: this,
            selector: '#t-toast',
            message: '删除失败,请重试',
          });
        }
      })
      .catch((er) => {
        Toast({
          context: this,
          selector: '#t-toast',
          message: '删除失败,请重试',
        });
      });
  },

  // 清空失效商品
  // 注：实际场景时应该调用接口
  // clearInvalidGoodsService() {
  //   this.data.cartGroupData.invalidGoodItems = [];
  //   return Promise.resolve();
  // },

  onGoodsSelect(e) {
    const { goods, isSelected } = e.detail;
    console.log(goods, isSelected);
    if (goods.stock < goods.buyNum) {
      Toast({
        context: this,
        selector: '#t-toast',
        message: '库存不足-[' + goods.goodsName + ']',
      });
      return;
    }
    let { totalAmount, selectedGoodsCount, cartGroupData } = this.data;
    if (isSelected) {
      this.setData({
        totalAmount: totalAmount + goods.price * goods.buyNum,
        selectedGoodsCount: selectedGoodsCount + goods.buyNum,
      });
    } else {
      this.setData({
        totalAmount: totalAmount - goods.price * goods.buyNum,
        selectedGoodsCount: selectedGoodsCount - goods.buyNum,
      });
    }
    cartGroupData = cartGroupData.map((item) => {
      if (item.spuId == goods.spuId) {
        if (goods.skuId && item.skuId == goods.skuId) {
          item.isSelected = isSelected;
        }
        if (!goods.skuId) {
          item.isSelected = isSelected;
        }
      }
      return {
        ...item,
      };
    });
    let isSelectAll = true;
    cartGroupData.forEach((item) => {
      if (item.isCanBuy) {
        if (!item.isSelected) {
          isSelectAll = false;
        }
      }
    });
    this.setData({
      isAllSelected: isSelectAll,
      cartGroupData: cartGroupData,
    });
    // const { currentGoods } = this.findGoods(spuId, skuId);
    // Toast({
    //   context: this,
    //   selector: '#t-toast',
    //   message: `${isSelected ? '选择' : '取消'}"${
    //     currentGoods.title.length > 5
    //       ? `${currentGoods.title.slice(0, 5)}...`
    //       : currentGoods.title
    //   }"`,
    //   icon: '',
    // });
    // this.selectGoodsService({ spuId, skuId, isSelected }).then(() =>
    //   this.refreshData(),
    // );
  },

  // onStoreSelect(e) {
  //   const {
  //     store: { storeId },
  //     isSelected,
  //   } = e.detail;
  //   this.selectStoreService({ storeId, isSelected }).then(() =>
  //     this.refreshData(),
  //   );
  // },

  onQuantityChange(e) {
    const { goods, buyNum } = e.detail;
    let goodList = this.data.cartGroupData;
    goodList = goodList.map((item) => {
      if (item.spuId == goods.spuId) {
        if (goods.skuId && item.skuId == goods.skuId) {
          item.buyNum = buyNum;
          if (goods.isSelected) {
            this.setData({
              totalAmount:
                this.data.totalAmount -
                goods.price * goods.buyNum +
                goods.price * buyNum,
              selectedGoodsCount:
                this.data.selectedGoodsCount - goods.buyNum + buyNum,
            });
          }
        } else if (!goods.skuId) {
          item.buyNum = buyNum;
          if (goods.isSelected) {
            this.setData({
              totalAmount:
                this.data.totalAmount -
                goods.price * goods.buyNum +
                goods.price * buyNum,
              selectedGoodsCount:
                this.data.selectedGoodsCount - goods.buyNum + buyNum,
            });
          }
        }
      }
      return {
        ...item,
      };
    });
    this.setData({
      cartGroupData: goodList,
    });
  },

  // goCollect() {
  //   /** 活动肯定有一个活动ID，用来获取活动banner，活动商品列表等 */
  //   const promotionID = '123';
  //   wx.navigateTo({
  //     url: `/pages/promotion-detail/index?promotion_id=${promotionID}`,
  //   });
  // },

  goGoodsDetail(e) {
    const { spuId, skuId } = e.detail.goods;
    wx.navigateTo({
      url: `/pages/goods/details/index?spuId=${spuId}&skuId=${skuId}`,
    });
  },

  // clearInvalidGoods() {
  //   // 实际场景时应该调用接口清空失效商品
  //   this.clearInvalidGoodsService().then(() => this.refreshData());
  // },

  onGoodsDelete(e) {
    const { goods } = e.detail;
    Dialog.confirm({
      content: '确认删除该商品吗?',
      confirmBtn: '确定',
      cancelBtn: '取消',
    }).then(() => {
      this.deleteGoodsService(goods);
    });
  },

  onSelectAll(event) {
    const { isAllSelected } = event?.detail ?? {};
    console.log(isAllSelected);
    let { cartGroupData } = this.data;
    if (!isAllSelected) {
      let isCanGo = true;
      cartGroupData.forEach((item) => {
        if (item.isCanBuy && item.stock < item.buyNum) {
          Toast({
            context: this,
            selector: '#t-toast',
            message: '库存不足-[' + item.goodsName + ']',
          });
          isCanGo = false;
        }
      });
      if (!isCanGo) {
        this.setData({
          isAllSelected: isAllSelected,
        });
        return;
      }
    }
    // 调用接口改变全选
    let count = 0;
    let price = 0;
    cartGroupData = cartGroupData.map((item) => {
      if (item.isCanBuy) {
        if (!isAllSelected) {
          item.isSelected = true;
          count += item.buyNum;
          price += item.price * item.buyNum;
        } else {
          item.isSelected = false;
        }
      }
      return {
        ...item,
      };
    });
    this.setData({
      isAllSelected: !isAllSelected,
      cartGroupData: cartGroupData,
      totalAmount: price,
      selectedGoodsCount: count,
    });
  },

  onToSettle() {
    // const goodsRequestList = [];
    // this.data.cartGroupData.storeGoods.forEach((store) => {
    //   store.promotionGoodsList.forEach((promotion) => {
    //     promotion.goodsPromotionList.forEach((m) => {
    //       if (m.isSelected == 1) {
    //         goodsRequestList.push(m);
    //       }
    //     });
    //   });
    // });
    // wx.setStorageSync(
    //   'order.goodsRequestList',
    //   JSON.stringify(goodsRequestList),
    // );
    const goodsRequestList = [];
    let { cartGroupData } = this.data;
    cartGroupData.forEach((item) => {
      if (item.isSelected) {
        goodsRequestList.push({
          buyNum: item.buyNum,
          spuId: item.spuId,
          goodsName: item.goodsName,
          skuId: item.skuId,
          primaryImage: item.primaryImage,
          selectedSkuInfo: item.selectedSkuInfo,
          price: item.price,
          rateTemInfo: item.rateTemInfo,
          weight: item.selectedSkuInfo
            ? item.buyNum * parseFloat(item.selectedSkuInfo.weight)
            : item.buyNum * parseFloat(item.weight),
        });
      }
    });
    wx.setStorageSync('order.goodsRequestList', goodsRequestList);
    wx.navigateTo({ url: '/pages/order/order-confirm/index' });
  },
  onGotoHome() {
    wx.switchTab({ url: '/pages/home/home' });
  },
});
