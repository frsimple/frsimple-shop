import {
  getSearchHistory,
  getSearchPopular,
} from '../../../services/good/fetchSearchHistory';

Page({
  data: {
    historyWords: [],
    popularWords: [],
    searchValue: '',
    dialog: {
      title: '确认删除当前历史记录',
      showCancelButton: true,
      message: '',
    },
    dialogShow: false,
  },

  deleteType: 0,
  deleteIndex: '',

  onShow() {
    this.queryPopular();
  },

  async queryPopular() {
    try {
      const res = await getSearchPopular();
      if (res.code == 0) {
        if (res.data) {
          this.setData({
            popularWords: res.data.split(','),
          });
        }
      }
    } catch (error) {
      console.error(error);
    }
  },

  confirm() {
    const { historyWords } = this.data;
    const { deleteType, deleteIndex } = this;
    historyWords.splice(deleteIndex, 1);
    if (deleteType === 0) {
      this.setData({
        historyWords,
        dialogShow: false,
      });
    } else {
      this.setData({ historyWords: [], dialogShow: false });
    }
  },

  close() {
    this.setData({ dialogShow: false });
  },

  deleteCurr(e) {
    const { index } = e.currentTarget.dataset;
    const { dialog } = this.data;
    this.deleteIndex = index;
    this.setData({
      dialog: {
        ...dialog,
        message: '确认删除当前历史记录',
        deleteType: 0,
      },
      dialogShow: true,
    });
  },

  handleHistoryTap(e) {
    const { value } = e.currentTarget.dataset;
    const _searchValue = value;
    if (_searchValue) {
      this.setData({
        searchValue: _searchValue,
      });
      wx.navigateTo({
        url: `/pages/goods/result/index?searchValue=${_searchValue}`,
      });
    }
  },

  handleSubmit(e) {
    console.log(e);
    const { value } = e.detail;
    if (value) {
      this.setData({
        searchValue: value,
      });
      wx.navigateTo({
        url: `/pages/goods/result/index?searchValue=${value}`,
      });
    }
  },
});
