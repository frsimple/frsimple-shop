import { getCategoryList } from '../../../services/good/fetchCategoryList';
Page({
  data: {
    list: [],
  },
  async init() {
    try {
      const res = await getCategoryList();
      let result = res.data;
      this.setData({
        list: result,
      });
    } catch (error) {
      console.error('err:', error);
    }
  },

  onShow() {
    this.getTabBar().init();
  },
  onChange(obj) {
    wx.navigateTo({
      url: `/pages/goods/result/index?searchValue=${obj.detail.item.name}`,
    });
  },
  onLoad() {
    this.init(true);
  },
});
