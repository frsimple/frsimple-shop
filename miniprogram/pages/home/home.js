import {
  fetchHome,
  fetchMsg,
  fetchFast,
  fetchGoodList,
} from '../../services/home/home';
import Toast from 'tdesign-miniprogram/toast/index';

Page({
  data: {
    imgSrcs: [],
    tabList: [],
    goodsList: [],
    goodsListLoadStatus: 0,
    pageLoading: false,
    current: 1,
    autoplay: true,
    duration: 500,
    interval: 5000,
    navigation: { type: 'dots' },
    marquee2: {
      speed: 60,
      loop: -1,
      delay: 0,
    },
    message: '',
    fastList: [],
  },
  goodListPagination: {
    index: 1,
    num: 10,
  },

  privateData: {
    tabIndex: 0,
  },

  onShow() {
    this.getTabBar().init();
  },

  onLoad() {
    this.init();
  },
  onReachBottom() {
    this.setData({
      showBackTop: true,
    });
    if (this.data.goodsListLoadStatus === 0) {
      this.loadGoodsList();
    }
  },

  async onPullDownRefresh() {
    await this.init();
    wx.stopPullDownRefresh();
  },

  init() {
    this.loadHomePage();
    this.loadGoodsList(true);
  },

  loadHomePage() {
    fetchHome()
      .then((res) => {
        let mainImg = res.data;
        this.setData({
          imgSrcs: mainImg,
        });
      })
      .catch((er) => {});
    fetchMsg()
      .then((res) => {
        let msg = res.data;
        if (msg) {
          this.setData({
            message: msg[0].content,
          });
        }
      })
      .catch((er) => {});
    fetchFast()
      .then((res) => {
        let fast = res.data;
        this.setData({
          fastList: fast,
        });
      })
      .catch((er) => {});
    this.setData({
      'baseRefresh.value': false,
    });
  },

  clickGrid(e) {
    let url = e.target.dataset.url;
    let type = e.target.dataset.type;
    if (type == '02') {
      wx.navigateTo({
        url: '/pages/other/other?url=' + url,
      });
    } else if (type == '01') {
      wx.navigateTo({
        url: url,
      });
    } else if (type == '03') {
      wx.switchTab({
        url: url,
      });
    }
  },

  onReTry() {
    this.loadGoodsList();
  },

  async loadGoodsList(fresh = false) {
    if (fresh) {
      wx.pageScrollTo({
        scrollTop: 0,
      });
    }

    this.setData({ goodsListLoadStatus: 1 });

    const pageSize = this.goodListPagination.num;
    let pageIndex = this.goodListPagination.index + 1;
    if (fresh) {
      pageIndex = 1;
    }

    try {
      let res = await fetchGoodList({
        current: pageIndex,
        size: pageSize,
      });
      let nextList = [];
      const goodsData = res.data.records;
      if (goodsData.length == 0) {
        this.setData({
          goodsListLoadStatus: 2,
        });
        return;
      }
      goodsData.forEach((item) => {
        nextList.push({
          spuId: item.id,
          thumb: item.mainimg,
          title: item.name,
          price:
            item.price.indexOf('-') > 0 ? item.price.split('-')[0] : item.price,
          originPrice:
            item.nprice.indexOf('-') > 0
              ? item.nprice.split('-')[0]
              : item.nprice,
          tags: '',
        });
      });
      this.setData({
        goodsList: fresh ? nextList : this.data.goodsList.concat(nextList),
        goodsListLoadStatus: 0,
      });

      this.goodListPagination.index = pageIndex;
      this.goodListPagination.num = pageSize;
    } catch (err) {
      this.setData({ goodsListLoadStatus: 3 });
    }
  },

  goodListClickHandle(e) {
    const { goods } = e.detail;
    console.log('选择商品：', goods);
    wx.navigateTo({
      url: `/pages/goods/details/index?spuId=${goods.spuId}`,
    });
  },

  navToSearchPage() {
    wx.navigateTo({ url: '/pages/goods/search/index' });
  },

  navToActivityDetail(e) {
    let url = e.target.dataset.url;
    let type = e.target.dataset.type;
    if (type == '01') {
      wx.navigateTo({
        url: '/pages/other/other?url=' + url,
      });
    } else if (type == '02') {
      wx.navigateTo({
        url: url,
      });
    } else if (type == '03') {
      wx.switchTab({
        url: url,
      });
    }
  },
});
