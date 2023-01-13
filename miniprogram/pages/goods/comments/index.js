import {
  fetchComments,
  fetchCommentsCount,
} from '../../../services/comments/fetchComments';
const layoutMap = {
  0: 'vertical',
};
Page({
  data: {
    pageLoading: false,
    commentList: [],
    pageNum: 1,
    myPageNum: 1,
    pageSize: 10,
    total: 0,
    myTotal: 0,
    hasLoaded: false,
    layoutText: layoutMap[0],
    loadMoreStatus: 0,
    myLoadStatus: 0,
    spuId: '1060004',
    commentLevel: 0,
    hasImage: '',
    commentType: 0,
    totalCount: 0,
    countObj: {
      badCount: '0',
      commentCount: '0',
      goodCount: '0',
      middleCount: '0',
      hasImageCount: '0',
      uidCount: '0',
    },
  },
  onLoad(options) {
    console.log(this.data.commentType);
    this.getCount(options);
    this.getComments(options);
  },
  async getCount(options) {
    try {
      const result = await fetchCommentsCount(options.spuId);
      this.setData({
        'countObj.goodCount': result.data.good,
        'countObj.middleCount': result.data.mid,
        'countObj.badCount': result.data.low,
        'countObj.commentCount': result.data.all,
      });
      // const { data, code = '' } = result;
      // if (code.toUpperCase() === 'SUCCESS') {
      //     wx.setNavigationBarTitle({
      //     title: `全部评价(${data.commentCount})`,
      //     });
      //     this.setData({
      //     countObj: data,
      //     });
      // } else {
      //     wx.showToast({
      //     title: '查询失败，请稍候重试',
      //     });
      // }
    } catch (error) {}
  },
  generalQueryData(reset) {
    const { hasImage, pageNum, pageSize, spuId, commentLevel } = this.data;
    const params = {
      current: 1,
      size: 10,
      spuId: spuId,
    };
    if (commentLevel > 0) {
      params.mark = Number(commentLevel);
    }
    // if (hasImage && hasImage === '1') {
    //   params.queryParameter.hasImage = true;
    // } else {
    //   delete params.queryParameter.hasImage;
    // }
    // 重置请求
    if (reset) return params;

    return {
      ...params,
      current: pageNum + 1,
      size,
    };
  },
  async init(reset = true) {
    console.log('开始查询数据');
    const { loadMoreStatus, commentList = [] } = this.data;
    const params = this.generalQueryData(reset);
    console.log('获取请求参数', params);
    // 在加载中或者无更多数据，直接返回
    if (loadMoreStatus !== 0) return;

    this.setData({
      loadMoreStatus: 1,
    });

    try {
      const res = await fetchComments(params.spuId, {
        ...params,
      });
      const data = res.data;
      if (res.code === 0) {
        let { records: pageList, total: totalCount = 0 } = data;

        pageList.forEach((row) => {
          row.imgs = row.imgs.split(',');
        });
        if (Number(totalCount) === 0 && reset) {
          this.setData({
            commentList: [],
            hasLoaded: true,
            total: totalCount,
            loadMoreStatus: 2,
          });
          return;
        }
        const _commentList = reset ? pageList : commentList.concat(pageList);
        const _loadMoreStatus =
          _commentList.length === Number(totalCount) ? 2 : 0;
        this.setData({
          commentList: _commentList,
          pageNum: params.current || 1,
          totalCount: Number(totalCount),
          loadMoreStatus: _loadMoreStatus,
        });
      } else {
        wx.showToast({
          title: '查询失败，请稍候重试',
        });
      }
    } catch (error) {}
    this.setData({
      hasLoaded: true,
    });
  },
  getScoreArray(score) {
    var array = [];
    for (let i = 0; i < 5; i++) {
      if (i < score) {
        array.push(2);
      } else {
        array.push(0);
      }
    }
    return array;
  },
  getComments(options) {
    const { commentLevel = 0, spuId, hasImage = '' } = options;
    if (commentLevel !== 0) {
      this.setData({
        commentLevel: commentLevel,
        commentType: commentLevel,
      });
    }
    this.setData({
      hasImage: hasImage,
      spuId: spuId,
    });
    this.init(true);
  },
  changeTag(e) {
    var { commenttype } = e.currentTarget.dataset;
    var { commentType } = this.data;
    if (commentType === Number(commenttype)) return;
    this.setData({
      loadMoreStatus: 0,
      commentList: [],
      total: 0,
      myTotal: 0,
      myPageNum: 1,
      pageNum: 1,
    });
    if (commenttype === '0') {
      this.setData({
        hasImage: '',
        commentLevel: 0,
      });
    }
    //  else if (commenttype === '4') {
    //   this.setData({
    //     hasImage: '1',
    //     commentLevel: '',
    //   });
    // }
    else {
      this.setData({
        hasImage: '',
        commentLevel: Number(commenttype),
      });
    }
    // if (commenttype === '5') {
    //   this.setData({
    //     myLoadStatus: 1,
    //     commentType: commenttype,
    //   });
    //   this.getMyCommentsList();
    // } else {
    //   this.setData({
    //     myLoadStatus: 0,
    //     commentType: commenttype,
    //   });
    //   this.init(true);
    // }
    this.setData({
      myLoadStatus: 0,
      commentType: Number(commenttype),
    });
    this.init(true);
  },
  onReachBottom() {
    const { total = 0, commentList } = this.data;
    if (commentList.length === total) {
      this.setData({
        loadMoreStatus: 2,
      });
      return;
    }

    this.init(false);
  },
});
