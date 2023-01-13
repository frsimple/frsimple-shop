/* eslint-disable no-param-reassign */
import { getSearchResult } from '../../../services/good/featchSearchResult';
import Toast from 'tdesign-miniprogram/toast/index';

const initFilters = {
  overall: 1,
  sorts: '',
};

Page({
  data: {
    goodsList: [],
    sorts: '',
    overall: 1,
    overall1: 0,
    show: false,
    minVal: '',
    maxVal: '',
    minSalePriceFocus: false,
    maxSalePriceFocus: false,
    filter: initFilters,
    hasLoaded: false,
    keywords: '',
    loadMoreStatus: 0,
    loading: true,
  },

  total: 0,
  pageNum: 1,
  pageSize: 20,

  onLoad(options) {
    const { searchValue = '' } = options || {};
    this.setData(
      {
        keywords: searchValue,
      },
      () => {
        this.init(true);
      },
    );
  },

  generalQueryData(reset = false) {
    const { filter, keywords } = this.data;
    const { pageNum, pageSize } = this;
    const { sorts, overall1 } = filter;
    console.log(keywords);
    const params = {
      //sort: 0, // 0 综合，1 价格
      current: 1,
      size: 20,
      name: keywords,
    };
    if (sorts) {
      if (sorts == 'desc') {
        params.desc = 'minprice';
      } else {
        params.asc = 'minprice';
      }
    } else {
      if (overall1) {
        params.desc = 'sales';
      } else {
        params.desc = '';
        params.asc = '';
      }
    }
    if (reset) return params;
    return {
      ...params,
      current: pageNum + 1,
      size: pageSize,
    };
  },

  async init(reset = true) {
    const { loadMoreStatus, goodsList = [] } = this.data;
    const params = this.generalQueryData(reset);
    if (loadMoreStatus !== 0) return;
    this.setData({
      loadMoreStatus: 1,
      loading: true,
    });
    try {
      const result = await getSearchResult(params);
      const data = result.data;
      if (result.code == 0) {
        const { records, total } = data;
        if (total === 0 && reset) {
          this.total = total;
          this.setData({
            emptyInfo: {
              tip: '抱歉，未找到相关商品',
            },
            hasLoaded: true,
            loadMoreStatus: 0,
            loading: false,
            goodsList: [],
          });
          return;
        }
        let addRow = [];
        records.forEach((item) => {
          addRow.push({
            spuId: item.id,
            thumb: item.mainimg,
            title: item.name,
            price: item.minprice,
            originPrice:
              item.nprice.indexOf('-') > 0
                ? item.nprice.split('-')[0]
                : item.nprice,
            tags: '',
          });
        });
        const _goodsList = reset ? addRow : goodsList.concat(addRow);
        const _loadMoreStatus = _goodsList.length === total ? 2 : 0;
        this.pageNum = params.current || 1;
        this.total = total;
        this.setData({
          goodsList: _goodsList,
          loadMoreStatus: _loadMoreStatus,
        });
      } else {
        this.setData({
          loading: false,
        });
        wx.showToast({
          title: '查询失败，请稍候重试',
        });
      }
    } catch (error) {
      this.setData({
        loading: false,
      });
    }
    this.setData({
      hasLoaded: true,
      loading: false,
    });
  },

  handleCartTap() {
    wx.switchTab({
      url: '/pages/cart/index',
    });
  },

  handleSubmit(e) {
    this.setData({
      keywords: e.detail.value,
    });
    this.setData(
      {
        goodsList: [],
        loadMoreStatus: 0,
      },
      () => {
        this.init(true);
      },
    );
  },

  onReachBottom() {
    const { goodsList } = this.data;
    const { total = 0 } = this;
    if (goodsList.length === total) {
      this.setData({
        loadMoreStatus: 2,
      });
      return;
    }
    this.init(false);
  },

  gotoGoodsDetail(e) {
    const { index } = e.detail;
    const { spuId } = this.data.goodsList[index];
    wx.navigateTo({
      url: `/pages/goods/details/index?spuId=${spuId}`,
    });
  },

  handleFilterChange(e) {
    const { overall, sorts } = e.detail;
    if (sorts) {
      const { total } = this;
      const _filter = {
        sorts,
        overall: 0,
        overall1: 0,
      };
      this.setData({
        filter: _filter,
        sorts,
        overall: 0,
        overall1: 0,
      });

      this.pageNum = 1;
      this.setData(
        {
          goodsList: [],
          loadMoreStatus: 0,
        },
        () => {
          total && this.init(true);
        },
      );
    } else {
      const { total } = this;
      const _filter = {
        sorts,
        overall,
        overall1: overall == 1 ? 0 : 1,
      };
      this.setData({
        filter: _filter,
        sorts,
        overall,
        overall1: overall == 1 ? 0 : 1,
      });

      this.pageNum = 1;
      this.setData(
        {
          goodsList: [],
          loadMoreStatus: 0,
        },
        () => {
          total && this.init(true);
        },
      );
    }
  },
  handleFilterChange1(e) {
    const { overall1, sorts } = e.detail;
    const { total } = this;
    const _filter = {
      sorts: '',
      overall1,
      overall: overall1 == 1 ? 0 : 1,
    };
    this.setData({
      filter: _filter,
      sorts: '',
      overall1,
      overall: overall1 == 1 ? 0 : 1,
    });

    this.pageNum = 1;
    this.setData(
      {
        goodsList: [],
        loadMoreStatus: 0,
      },
      () => {
        total && this.init(true);
      },
    );
  },

  clearVal() {
    this.setData(
      {
        goodsList: [],
        loadMoreStatus: 0,
        keywords: '',
      },
      () => {
        this.init(true);
      },
    );
  },
});
