import Toast from 'tdesign-miniprogram/toast/index';
import {
  fetchGood,
  fetchGoodMark,
  fetchAddCart,
} from '../../../services/good/fetchGood';
import { isLogin } from '../../../utils/util';
import { cdnBase } from '../../../config/index';

const imgPrefix = `${cdnBase}/`;

const recLeftImg = `${imgPrefix}common/rec-left.png`;
const recRightImg = `${imgPrefix}common/rec-right.png`;
const obj2Params = (obj = {}, encode = false) => {
  const result = [];
  Object.keys(obj).forEach((key) =>
    result.push(`${key}=${encode ? encodeURIComponent(obj[key]) : obj[key]}`),
  );

  return result.join('&');
};

Page({
  data: {
    isLoad: true,
    isNull: false,
    isShowPromotionPop: false,
    activityList: [],
    recLeftImg,
    recRightImg,
    details: {},
    jumpArray: [
      {
        title: '首页',
        url: '/pages/home/home',
        iconName: 'home',
      },
      {
        title: '购物车',
        url: '/pages/cart/index',
        iconName: 'cart',
        showCartNum: true,
      },
    ],
    isSingleStock: true,
    isStock: true,
    cartNum: 0,
    soldout: false,
    buttonType: 1,
    buyNum: 1,
    selectedAttrStr: '',
    skuArray: [],
    primaryImage: '',
    specImg: '',
    isSpuSelectPopupShow: false,
    isAllSelectedSku: false,
    buyType: 0,
    outOperateStatus: false, // 是否外层加入购物车
    operateType: 0,
    selectSkuSellsPrice: 0,
    selectSkuSellsnPrice: 0,
    maxLinePrice: 0,
    minSalePrice: 0,
    maxSalePrice: 0,
    list: [],
    spuId: '',
    navigation: { type: 'fraction' },
    current: 0,
    autoplay: true,
    duration: 500,
    interval: 5000,
    soldNum: 0, // 已售数量
    commontObj: {},
    currentStock: null,
    selectedSku: [],
    selectedSkuId: null,
    selectedSkuInfo: null,
  },

  handlePopupHide() {
    this.setData({
      isSpuSelectPopupShow: false,
    });
  },

  showSkuSelectPopup(type) {
    this.setData({
      buyType: type || 0,
      outOperateStatus: type == 1,
      isSpuSelectPopupShow: true,
    });
  },

  buyItNow() {
    if (this.data.details.isSingle == '0') {
      this.showSkuSelectPopup(1);
    } else {
      this.gotoBuy();
    }
  },

  toAddCart() {
    if (this.data.details.isSingle == '0') {
      this.showSkuSelectPopup(2);
    } else {
      if (!isLogin()) {
        Toast({
          context: this,
          selector: '#t-toast',
          message: '请先登录',
          icon: '',
          duration: 1000,
        });
        return;
      }
      //单一规格直接把商品添加到购物车
      fetchAddCart({
        prodId: this.data.details.id,
        num: this.data.buyNum,
      })
        .then((res) => {
          if (res.code == 0) {
            Toast({
              context: this,
              selector: '#t-toast',
              message: '已添加至购物车',
              icon: 'chevron-down-circle',
              duration: 2000,
            });
          } else {
            Toast({
              context: this,
              selector: '#t-toast',
              message: '购物车添加失败',
              icon: 'close-circle',
              duration: 2000,
            });
          }
        })
        .catch((er) => {
          Toast({
            context: this,
            selector: '#t-toast',
            message: '购物车添加失败',
            icon: 'close-circle',
            duration: 2000,
          });
        });
    }
  },

  toNav(e) {
    const { url } = e.detail;
    wx.switchTab({
      url: url,
    });
  },

  showCurImg(e) {
    const { index } = e.detail;
    const { images } = this.data.details;
    wx.previewImage({
      current: images[index],
      urls: images, // 需要预览的图片http链接列表
    });
  },

  chooseSpecItem(e) {
    const _that = this;
    let { details } = this.data;
    const { selectedSpecId, selectedValueId, selectedValue } = e.detail;
    //计算选中的内容
    details.sku.sku = details.sku.sku.map((row) => {
      if (row.id == selectedSpecId) {
        row.label = row.label.map((item) => {
          let nItem = { ...item };
          if (item.id == selectedValueId) {
            nItem.isChoosed = true;
          } else {
            nItem.isChoosed = false;
          }
          return {
            ...nItem,
          };
        });
        return {
          ...row,
        };
      } else {
        return {
          ...row,
        };
      }
    });
    this.setData({
      details: details,
    });
    //计算选中图片
    const { sku, mainimg } = this.data.details;
    sku.img.forEach((el) => {
      if (el.id == selectedValueId) {
        this.setData({
          specImg: el.url,
        });
      }
    });
    if (!this.data.specImg) {
      this.setData({
        specImg: mainimg,
      });
    }
    //计算选中的sku
    let { selectedSku } = this.data;
    selectedSku = selectedSku.map((item) => {
      if (selectedSpecId == item.id) {
        item.value = selectedValue;
      }
      return {
        ...item,
      };
    });
    this.setData({
      selectedSku,
    });
    //先判断是否全部选中，如果sku选择全，在计算价格和库存
    let isSelectAll = true;
    selectedSku.forEach((item) => {
      if (!item.value) {
        isSelectAll = false;
      }
    });
    this.setData({
      isAllSelectedSku: isSelectAll,
    });
    if (isSelectAll) {
      //先获取选中的sku对象
      let selectedSkuValues = [];
      selectedSku.forEach((item) => {
        selectedSkuValues.push(item.value);
      });
      details.skulist.forEach((item) => {
        if (
          item.rule.split('/').sort().toString() ==
          selectedSkuValues.sort().toString()
        ) {
          _that.setData({
            selectedSkuId: item.id,
            selectSkuSellsPrice: item.price,
            selectSkuSellsnPrice: item.nprice,
            currentStock: '库存' + item.inventory,
            isStock: parseInt(item.inventory) > 0 ? true : false,
            selectedSkuInfo: item,
          });
        }
      });
      this.setData({
        selectedAttrStr: selectedSkuValues.join('/'),
      });
    } else {
      _that.setData({
        selectSkuSellsPrice: details.price,
        selectSkuSellsnPrice: details.nprice,
        currentStock: null,
        isStock: true,
        selectedAttrStr: null,
        selectedSkuId: null,
        selectedSkuInfo: null,
      });
    }
    console.log(this.data.selectedSkuInfo);
    //this.getSkuItem(selectedSku);
  },

  // getSkuItem(specList, selectedSku) {
  //   const { skulist } = this.data.details;
  //   const { skuArray, primaryImage } = this.data;
  //   const selectedSkuValues = this.getSelectedSkuValues(specList, selectedSku);
  //   let selectedAttrStr = ` 件  `;
  //   selectedSkuValues.forEach((item) => {
  //     selectedAttrStr += `，${item.specValue}  `;
  //   });
  //   // eslint-disable-next-line array-callback-return
  //   const skuItem = skuArray.filter((item) => {
  //     let status = true;
  //     (item.specInfo || []).forEach((subItem) => {
  //       if (
  //         !selectedSku[subItem.specId] ||
  //         selectedSku[subItem.specId] !== subItem.specValueId
  //       ) {
  //         status = false;
  //       }
  //     });
  //     if (status) return item;
  //   });
  //   this.selectSpecsName(selectedSkuValues.length > 0 ? selectedAttrStr : '');
  //   if (skuItem) {
  //     this.setData({
  //       selectItem: skuItem,
  //       selectSkuSellsPrice: skuItem.price || 0,
  //     });
  //   } else {
  //     this.setData({
  //       selectItem: null,
  //       selectSkuSellsPrice: 0,
  //     });
  //   }
  //   this.setData({
  //     specImg: skuItem && skuItem.skuImage ? skuItem.skuImage : primaryImage,
  //   });
  // },

  // 获取已选择的sku名称
  // getSelectedSkuValues(skuTree, selectedSku) {
  //   const normalizedTree = this.normalizeSkuTree(skuTree);
  //   return Object.keys(selectedSku).reduce((selectedValues, skuKeyStr) => {
  //     const skuValues = normalizedTree[skuKeyStr];
  //     const skuValueId = selectedSku[skuKeyStr];
  //     if (skuValueId !== '') {
  //       const skuValue = skuValues.filter((value) => {
  //         return value.specValueId === skuValueId;
  //       })[0];
  //       skuValue && selectedValues.push(skuValue);
  //     }
  //     return selectedValues;
  //   }, []);
  // },

  // normalizeSkuTree(skuTree) {
  //   const normalizedTree = {};
  //   skuTree.forEach((treeItem) => {
  //     normalizedTree[treeItem.specId] = treeItem.specValueList;
  //   });
  //   return normalizedTree;
  // },

  // selectSpecsName(selectSpecsName) {
  //   if (selectSpecsName) {
  //     this.setData({
  //       selectedAttrStr: selectSpecsName,
  //     });
  //   } else {
  //     this.setData({
  //       selectedAttrStr: '',
  //     });
  //   }
  // },

  addCart() {
    if (!isLogin()) {
      Toast({
        context: this,
        selector: '#t-toast',
        message: '请先登录',
        icon: '',
        duration: 1000,
      });
      return;
    }
    const { isAllSelectedSku } = this.data;
    if (!isAllSelectedSku) {
      Toast({
        context: this,
        selector: '#t-toast',
        message: '请选择规格',
        icon: '',
        duration: 1000,
      });
      return;
    }
    fetchAddCart({
      prodId: this.data.details.id,
      skuId: this.data.selectedSkuId,
      primaryImage:
        this.data.details.isSingle === '0'
          ? this.data.specImg
          : this.data.details.mainimg,
      num: this.data.buyNum,
    })
      .then((res) => {
        if (res.code == 0) {
          this.handlePopupHide();
          Toast({
            context: this,
            selector: '#t-toast',
            message: '已添加至购物车',
            icon: 'chevron-down-circle',
            duration: 2000,
          });
        } else {
          Toast({
            context: this,
            selector: '#t-toast',
            message: '购物车添加失败',
            icon: 'close-circle',
            duration: 2000,
          });
        }
      })
      .catch((er) => {
        Toast({
          context: this,
          selector: '#t-toast',
          message: '购物车添加失败',
          icon: 'close-circle',
          duration: 2000,
        });
      });
  },

  gotoBuy() {
    if (!isLogin()) {
      Toast({
        context: this,
        selector: '#t-toast',
        message: '请先登录',
        icon: '',
        duration: 1000,
      });
      return;
    }
    const { isAllSelectedSku, buyNum } = this.data;
    console.log(isAllSelectedSku);
    if (!isAllSelectedSku) {
      Toast({
        context: this,
        selector: '#t-toast',
        message: '请选择规格',
        icon: '',
        duration: 1000,
      });
      return;
    }
    this.handlePopupHide();
    const query = {
      //quantity: buyNum,
      // storeId: '1',
      buyNum: buyNum,
      spuId: this.data.spuId,
      goodsName: this.data.details.name,
      skuId: this.data.selectedSkuId,
      primaryImage:
        this.data.details.isSingle === '1'
          ? this.data.details.mainimg
          : this.data.specImg,
      selectedSkuInfo: this.data.selectedSkuInfo,
      price: this.data.selectSkuSellsPrice,
      rateTemInfo: this.data.details.rateTemInfo,
      weight:
        this.data.details.isSingle == '0'
          ? buyNum * parseFloat(this.data.selectedSkuInfo.weight)
          : buyNum * parseFloat(this.data.details.weight),
      //  type === 1 ? this.data.skuList[0].skuId : this.data.selectItem.skuId,
      // available: this.data.details.available,
      // price: this.data.details.minSalePrice,
      // specInfo: this.data.details.specList?.map((item) => ({
      //   specTitle: item.title,
      //   specValue: item.specValueList[0].specValue,
      // })),
      // primaryImage: this.data.details.primaryImage,
      // spuId: this.data.details.spuId,
      // thumb: this.data.details.primaryImage,
      // title: this.data.details.title,
    };
    // let urlQueryStr = obj2Params({
    //   goodsRequestList: JSON.stringify([query]),
    // });
    // urlQueryStr = urlQueryStr ? `?${urlQueryStr}` : '';
    console.log([query]);
    wx.setStorageSync('order.goodsRequestList', [query]);
    const path = `/pages/order/order-confirm/index`;
    wx.navigateTo({
      url: path,
    });
  },

  specsConfirm() {
    const { buyType } = this.data;
    if (buyType === 1) {
      this.gotoBuy();
    } else {
      this.addCart();
    }
    // this.handlePopupHide();
  },

  changeNum(e) {
    this.setData({
      buyNum: e.detail.buyNum,
    });
  },

  closePromotionPopup() {
    this.setData({
      isShowPromotionPop: false,
    });
  },

  promotionChange(e) {
    const { index } = e.detail;
    wx.navigateTo({
      url: `/pages/promotion-detail/index?promotion_id=${index}`,
    });
  },

  showPromotionPopup() {
    this.setData({
      isShowPromotionPop: true,
    });
  },

  getDetail(spuId, skuId) {
    const _that = this;
    fetchGood(spuId)
      .then((res) => {
        if (res.code == 0) {
          let previewImages = res.data.imgs.split(',');
          previewImages.unshift(res.data.mainimg);
          let goodDetails = {
            ...res.data,
            images: previewImages,
            index: 0,
          };
          //多规格初始化选择的sku内容
          let selectedSkuList = [];
          if (goodDetails.isSingle == '0') {
            goodDetails.sku.sku.forEach((item) => {
              selectedSkuList.push({
                id: item.id,
                value: '',
              });
            });
            //如果传入了skuid则需要默认选中
            if (skuId) {
              let skuArray = [];
              goodDetails.skulist.forEach((item) => {
                if (item.id == skuId) {
                  skuArray = item.rule.split('/');
                  _that.setData({
                    isAllSelectedSku: true,
                    selectedSkuId: skuId,
                    selectSkuSellsPrice: item.price,
                    selectSkuSellsnPrice: item.nprice,
                    currentStock: '库存' + item.inventory,
                    isStock: parseInt(item.inventory) > 0 ? true : false,
                    selectedAttrStr: item.rule,
                    selectedSkuInfo: item,
                  });
                }
              });
              //循环判断默认选中图片
              goodDetails.sku.img.forEach((item) => {
                if (this.data.selectedAttrStr.indexOf(item.value) >= 0) {
                  this.setData({
                    specImg: item.url,
                  });
                }
              });
              console.log(goodDetails.sku.sku);
              //循环判断默认选中规格值
              goodDetails.sku.sku = goodDetails.sku.sku.map((item, index) => {
                item.label = item.label.map((row) => {
                  if (row.value == skuArray[index]) {
                    row.isChoosed = true;
                    selectedSkuList[index].value = row.value;
                  } else {
                    row.isChoosed = false;
                  }
                  return {
                    ...row,
                  };
                });
                return {
                  ...item,
                };
              });
            } else {
              _that.setData({
                specImg: res.data.mainimg,
                selectSkuSellsPrice: goodDetails.price,
                selectSkuSellsnPrice: goodDetails.nprice,
                isAllSelectedSku: false,
              });
            }
          } else {
            if (goodDetails.inventory == '0') {
              this.setData({
                isSingleStock: false,
              });
            }
            _that.setData({
              isAllSelectedSku: true,
              specImg: res.data.mainimg,
              selectSkuSellsPrice: goodDetails.price,
              selectSkuSellsnPrice: goodDetails.nprice,
            });
          }
          this.setData({
            details: goodDetails,
            isLoad: false,
            isNull: false,
            selectedSku: selectedSkuList,
          });
        } else {
          this.setData({
            isLoad: false,
            isNull: true,
          });
        }
        // this.setData({
        //   details,
        //   activityList,
        //   isStock: details.spuStockQuantity > 0,
        //   maxSalePrice: maxSalePrice ? parseInt(maxSalePrice) : 0,
        //   maxLinePrice: maxLinePrice ? parseInt(maxLinePrice) : 0,
        //   minSalePrice: minSalePrice ? parseInt(minSalePrice) : 0,
        //   list: promotionArray,
        //   skuArray: skuArray,
        //   primaryImage,
        //   soldout: isPutOnSale === 0,
        //   soldNum,
        // });
      })
      .catch((er) => {});
    // Promise.all([fetchGood(spuId), fetchActivityList()]).then((res) => {
    //   const [details, activityList] = res;
    //   const skuArray = [];
    //   const {
    //     skuList,
    //     primaryImage,
    //     isPutOnSale,
    //     minSalePrice,
    //     maxSalePrice,
    //     maxLinePrice,
    //     soldNum,
    //   } = details;
    //   skuList.forEach((item) => {
    //     skuArray.push({
    //       skuId: item.skuId,
    //       quantity: item.stockInfo ? item.stockInfo.stockQuantity : 0,
    //       specInfo: item.specInfo,
    //     });
    //   });
    //   const promotionArray = [];
    //   activityList.forEach((item) => {
    //     promotionArray.push({
    //       tag: item.promotionSubCode === 'MYJ' ? '满减' : '满折',
    //       label: '满100元减99.9元',
    //     });
    //   });
    //   this.setData({
    //     details,
    //     activityList,
    //     isStock: details.spuStockQuantity > 0,
    //     maxSalePrice: maxSalePrice ? parseInt(maxSalePrice) : 0,
    //     maxLinePrice: maxLinePrice ? parseInt(maxLinePrice) : 0,
    //     minSalePrice: minSalePrice ? parseInt(minSalePrice) : 0,
    //     list: promotionArray,
    //     skuArray: skuArray,
    //     primaryImage,
    //     soldout: isPutOnSale === 0,
    //     soldNum,
    //   });
    // });
  },

  async getCommentsList(spuId) {
    // try {
    //   const code = 'Success';
    //   const data = await getGoodsDetailsCommentList();
    //   const { homePageComments } = data;
    //   if (code.toUpperCase() === 'SUCCESS') {
    //     const nextState = {
    //       commentsList: homePageComments.map((item) => {
    //         return {
    //           goodsSpu: item.spuId,
    //           userName: item.userName || '',
    //           commentScore: item.commentScore,
    //           commentContent: item.commentContent || '用户未填写评价',
    //           userHeadUrl: item.isAnonymity
    //             ? this.anonymityAvatar
    //             : item.userHeadUrl || this.anonymityAvatar,
    //         };
    //       }),
    //     };
    //     this.setData(nextState);
    //   }
    // } catch (error) {
    //   console.error('comments error:', error);
    // }
    try {
      let res = await fetchGoodMark(spuId);
      this.setData({
        commontObj: res.data,
      });
    } catch (error) {}
  },

  onShareAppMessage() {
    // 自定义的返回信息
    // const { selectedAttrStr } = this.data;
    // let shareSubTitle = '';
    // if (selectedAttrStr.indexOf('件') > -1) {
    //   const count = selectedAttrStr.indexOf('件');
    //   shareSubTitle = selectedAttrStr.slice(count + 1, selectedAttrStr.length);
    // }
    const customInfo = {
      imageUrl: this.data.details.mainimg,
      title: this.data.details.name,
      path: `/pages/goods/details/index?spuId=${this.data.spuId}`,
    };
    return customInfo;
  },

  // /** 获取评价统计 */
  // async getCommentsStatistics() {
  //   try {
  //     const code = 'Success';
  //     const data = await getGoodsDetailsCommentsCount();
  //     if (code.toUpperCase() === 'SUCCESS') {
  //       const {
  //         badCount,
  //         commentCount,
  //         goodCount,
  //         goodRate,
  //         hasImageCount,
  //         middleCount,
  //       } = data;
  //       const nextState = {
  //         commentsStatistics: {
  //           badCount: parseInt(`${badCount}`),
  //           commentCount: parseInt(`${commentCount}`),
  //           goodCount: parseInt(`${goodCount}`),
  //           /** 后端返回百分比后数据但没有限制位数 */
  //           goodRate: Math.floor(goodRate * 10) / 10,
  //           hasImageCount: parseInt(`${hasImageCount}`),
  //           middleCount: parseInt(`${middleCount}`),
  //         },
  //       };
  //       this.setData(nextState);
  //     }
  //   } catch (error) {
  //     console.error('comments statiistics error:', error);
  //   }
  // },

  /** 跳转到评价列表 */
  navToCommentsListPage() {
    wx.navigateTo({
      url: `/pages/goods/comments/index?spuId=${this.data.spuId}`,
    });
  },

  onLoad(query) {
    const { spuId, skuId } = query;
    this.setData({
      spuId: spuId,
    });
    this.getDetail(spuId, skuId);
    this.getCommentsList(spuId);
    //this.getCommentsStatistics(spuId);
  },
});
