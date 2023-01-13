Page({
  data: {
    logisticsData: {
      nodes: [],
      express: {},
      expressMap: {},
    },
    active: 0,
  },

  onLoad(query) {
    let data;
    try {
      data = JSON.parse(decodeURIComponent(query.data || '{}'));
    } catch (e) {
      console.warn('物流节点数据解析失败', e);
    }
    this.setData({ logisticsData: data });
  },

  onLogisticsNoCopy() {
    wx.setClipboardData({ data: this.data.logisticsData.logisticsNo });
  },

  onCall() {
    const { phoneNumber } = this.data.logisticsData;
    wx.makePhoneCall({
      phoneNumber,
    });
  },
});
