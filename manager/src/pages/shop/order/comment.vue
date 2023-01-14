<template>
  <div>
    <div class="sp-role-left">
      <t-card :bordered="false">
        <div class="sp-role-left-header">
          <t-row :gutter="10">
            <t-col :span="9" :offset="3">
              <t-row :gutter="10" justify="end">
                <t-col :span="3" :offset="6">
                  <t-input
                    placeholder="订单号"
                    type="search"
                    clearable
                    v-model="params.orderid"
                    @clear="clearName(0)"
                    @enter="firstFetch"
                  ></t-input>
                </t-col>
                <t-col :span="2">
                  <t-select
                    placeholder="是否已经回复"
                    v-model="params.isReply"
                    clearable
                    @clear="clearName(1)"
                    @change="firstFetch"
                  >
                    <t-option key="1" label="是" value="1" />
                    <t-option key="0" label="否" value="0"></t-option>
                  </t-select>
                </t-col>
                <!-- <t-col :span="5">
                  <t-date-range-picker
                    @clear="clearName(2)"
                    placeholder="交易日期"
                    allow-input
                    clearable
                    @pick="onPick"
                    @change="onChange"
                  />
                </t-col> -->
                <t-col :span="1">
                  <t-button theme="default" variant="outline" @click="firstFetch">查询</t-button>
                </t-col>
              </t-row>
            </t-col>
          </t-row>
        </div>
        <div class="tdesign-demo-block-column" style="width: 100%">
          <t-table
            row-key="id"
            :data="data"
            :max-height="'64vh'"
            :columns="columns"
            :table-layout="'fixed'"
            :pagination="pagination"
            @page-change="onPageChange"
            :loading="dataLoading"
            :loadingProps="{ size: '23px', text: '加载中...' }"
            class="_tableList"
          >
            <template #user="{ row }">
              <t-row :gutter="5">
                <t-col :span="2">
                  <t-avatar
                    v-if="row.user.avatar"
                    :image="row.user.avatar"
                    size="40px"
                    shape="round"
                    :hide-on-load-failed="false"
                  />
                  <t-avatar v-else size="40px" shape="round">
                    <template #icon>
                      <t-icon name="user" size="30px"></t-icon>
                    </template>
                  </t-avatar>
                </t-col>
                <t-col :span="10">
                  <t-row>
                    <t-col :span="12"
                      ><span class="_row1"> {{ row.user.name ? row.user.name : '<未设置昵称>' }}</span></t-col
                    >
                  </t-row>
                  <t-row>
                    <t-col :span="12"
                      ><span class="_row2"> {{ row.user.phone }}</span></t-col
                    >
                  </t-row>
                </t-col>
              </t-row>
            </template>
            <template #content="{ row }">
              <t-space direction="vertical">
                <span
                  ><t-space
                    ><span class="_star">是否匿名</span><b>{{ row.isShowname === '1' ? '是' : '否' }}</b></t-space
                  ></span
                >
                <span
                  ><t-space
                    ><span class="_star">评价时间</span><b>{{ row.createTime }}</b></t-space
                  ></span
                >
                <span
                  ><t-space
                    ><span class="_star">评价星级</span
                    ><span
                      ><t-rate
                        :texts="['差评', '中差评', '中评', '好评', '满意']"
                        disabled
                        show-text
                        :default-value="parseInt(row.mark)"
                      >
                        <template #icon="iconProps"> <star-icon v-bind="iconProps" /> </template></t-rate
                    ></span> </t-space
                ></span>
                <span
                  ><t-space
                    ><span class="_star">评价内容</span><b>{{ row.content }}</b></t-space
                  ></span
                >
                <t-swiper v-if="row.imgs" :duration="300" :interval="2000">
                  <t-swiper-item v-for="(item, index) in row.imgs.split(',')" :key="index">
                    <t-image-viewer :images="row.imgs.split(',')">
                      <template #trigger="{ open }">
                        <t-image :src="item" :style="{ height: '200px' }" mode="fill" overlay-trigger="hover">
                          <template #overlayContent>
                            <div
                              @click="open"
                              :style="{
                                background: 'rgba(0,0,0,.4)',
                                color: '#fff',
                                height: '100%',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                              }"
                            >
                              预览
                            </div>
                          </template>
                        </t-image>
                      </template>
                    </t-image-viewer>
                  </t-swiper-item>
                </t-swiper>
              </t-space>
            </template>
            <template #isReply="{ row }">
              <t-tag v-if="row.isReply === '1'" theme="success" variant="outline" shape="round">已回复</t-tag>
              <t-tag v-else theme="warning" shape="round" variant="outline">未回复</t-tag>
            </template>
            <template #good="{ row }">
              <t-row :gutter="10" class="_goodRow">
                <t-col :span="2">
                  <t-image :style="{ width: '40px', height: '40px' }" :src="row.good.primaryImage"></t-image>
                </t-col>
                <t-col :span="10">
                  <t-row>
                    <t-col :span="12">
                      <span class="_goodsName">{{ row.good.goodsName }}</span>
                    </t-col>
                  </t-row>
                  <t-row>
                    <t-col :span="2">
                      <span class="_skuName">{{ row.good.specs }}</span>
                    </t-col>
                    <t-col :span="1" :offset="1">
                      <span class="_skuName">x{{ row.good.buyNum }}</span>
                    </t-col>
                  </t-row>
                </t-col>
              </t-row>
            </template>
            <template #operation="{ row }">
              <t-space direction="vertical">
                <t-button size="small" theme="primary" @click="showReplyContentDialog(row)">添加回复</t-button>
                <t-popconfirm theme="danger" content="是否确认删除评论?" @confirm="deleteMark(row, null)">
                  <t-button size="small" theme="primary">删除评价</t-button>
                </t-popconfirm>
                <t-button v-if="row.markList.length !== 0" size="small" theme="default" @click="showReplyList(row)"
                  >回复列表</t-button
                >
              </t-space>
            </template>
          </t-table>
        </div>
      </t-card>
    </div>
    <t-dialog v-model:visible="showRely" :confirmBtn="null">
      <t-timeline :reverse="false" mode="same">
        <t-timeline-item v-for="(item, index) in replyList" :key="index" :label="item.createTime" dot-color="primary">
          {{ item.content }}
          <t-popconfirm theme="danger" content="是否确认删除评论?" @confirm="deleteMark(item, index)">
            <t-button size="small" theme="primary">删除评价</t-button>
          </t-popconfirm>
        </t-timeline-item>
      </t-timeline>
    </t-dialog>
    <t-dialog
      v-model:visible="showReplyContent"
      :header="'添加回复（订单号：' + selectedRow.orderid + '）'"
      destroy-on-close
      :show-in-attached-element="true"
      placement="center"
      width="600"
      :confirmBtn="confirmBtn"
      :onConfirm="saveMark"
    >
      <div slot="body">
        <div>
          <t-space
            ><label>是否匿名</label> <b>{{ selectedRow.isShowname === '1' ? '是' : '否' }}</b></t-space
          >
        </div>
        <div>
          <t-space
            ><label>评价时间</label> <b>{{ selectedRow.createTime }}</b></t-space
          >
        </div>
        <div>
          <t-space
            ><label>评价星级</label>
            <b></b>
            <t-rate
              :texts="['差评', '中差评', '中评', '好评', '满意']"
              disabled
              show-text
              :default-value="parseInt(selectedRow.mark)"
            ></t-rate>
          </t-space>
        </div>
        <div>
          <t-space
            ><label>评价内容</label> <b>{{ selectedRow.content }}</b></t-space
          >
        </div>
        <t-space direction="vertical">
          <t-swiper v-if="selectedRow.imgs" :duration="300" :interval="2000">
            <t-swiper-item v-for="(item, index) in selectedRow.imgs.split(',')" :key="index">
              <t-image-viewer :images="selectedRow.imgs.split(',')">
                <template #trigger="{ open }">
                  <t-image :src="item" :style="{ height: '200px' }" mode="fill" overlay-trigger="hover">
                    <template #overlayContent>
                      <div
                        @click="open"
                        :style="{
                          background: 'rgba(0,0,0,.4)',
                          color: '#fff',
                          height: '100%',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                        }"
                      >
                        预览
                      </div>
                    </template>
                  </t-image>
                </template>
              </t-image-viewer>
            </t-swiper-item>
          </t-swiper>
          <t-textarea
            :style="{ width: '500px !important', height: '300px' }"
            v-model="replyContent"
            placeholder="请输入回复内容"
            :maxcharacter="200"
            :autosize="{ minRows: 5 }"
            autofocus
          ></t-textarea>
        </t-space>
      </div>
    </t-dialog>
  </div>
</template>

<script lang="ts">
export default {
  name: 'allProcuct',
};
</script>

<script setup lang="ts">
import { ref, onMounted, reactive, getCurrentInstance, computed } from 'vue';
import { queryCommentList, addMark, delMark } from '@/api/shop/order/comment';
import { dicVals } from '@/api/common';
import { BrowseIcon } from 'tdesign-icons-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import { StarIcon } from 'tdesign-icons-vue-next';

//加载回复列表弹框
const showRely = ref(false);
const replyList = ref([]);
const showReplyList = (row) => {
  replyList.value = row.markList;
  showRely.value = true;
};
//end

//删除评价
const deleteMark = async (row, index) => {
  let msg = MessagePlugin.loading('正在删除中...');
  let res = await delMark(row.id);
  MessagePlugin.close(msg);
  if (res.code === 0) {
    MessagePlugin.success('删除成功');
    if (index || index === 0) {
      if (replyList.value.length == 1) {
        showRely.value = false;
      } else {
        replyList.value.splice(index, 1);
      }
    }
    await fetchData();
  } else {
    MessagePlugin.error('删除失败:' + res.msg);
  }
};
//end

//添加回复
const showReplyContent = ref(false);
const selectedRow = ref({
  id: '',
  orderid: '',
  createTime: '',
  content: '',
  mark: '',
  parentid: '',
  imgs: '',
  isShowname: '',
});
const confirmBtn = ref({
  content: '确认回复',
  theme: 'primary',
  loading: false,
});
const replyContent = ref('');
const showReplyContentDialog = (row) => {
  replyContent.value = '';
  selectedRow.value = row;
  showReplyContent.value = true;
};
const saveMark = async () => {
  if (!replyContent.value) {
    MessagePlugin.warning('请输入回复内容');
    return;
  }
  confirmBtn.value = {
    content: '保存中...',
    theme: 'primary',
    loading: true,
  };
  let res = await addMark({
    parentid: selectedRow.value.id,
    orderid: selectedRow.value.orderid,
    content: replyContent.value,
  });

  if (res.code === 0) {
    showReplyContent.value = false;
    MessagePlugin.success('回复成功!');
    fetchData();
  } else {
    MessagePlugin.error('回复失败:' + res.msg);
  }
  confirmBtn.value = {
    content: '确认回复',
    theme: 'primary',
    loading: false,
  };
};
//end
//加载字典数据
const prodStatus = ref({});
const prodStatusList = ref([]);
const payType = ref({});
const payTypeList = ref([]);
const initDicts = async () => {
  let res = await dicVals('SP_SHOP_ORDERSTATUS');
  prodStatusList.value = res.data;
  res.data.forEach((row) => {
    prodStatus.value[row.value] = row.label;
  });

  let res1 = await dicVals('SP_PAYTYPE');
  payTypeList.value = res1.data;
  res1.data.forEach((row) => {
    payType.value[row.value] = row.label;
  });
};
//获取table全局高度
const tableHeight = getCurrentInstance().appContext.config.globalProperties.$tableHeight;

const firstFetch = async () => {
  pagination.value.current = 1;
  await fetchData();
};

//左侧角色菜单列表数据start
const data = ref([]);
const columns = [
  {
    width: 340,
    colKey: 'content',
    title: '评论信息',
    ellipsis: false,
    fixed: 'left',
  },
  {
    width: 220,
    colKey: 'orderid',
    title: '订单号',
    ellipsis: false,
  },
  {
    width: 350,
    colKey: 'good',
    title: '商品信息',
    ellipsis: false,
  },
  // {
  //   width: 80,
  //   colKey: 'price',
  //   title: '订单总金额/运费',
  //   ellipsis: false,
  // },
  {
    width: 280,
    colKey: 'user',
    title: '用户信息',
    align: 'left',
  },
  {
    width: 120,
    colKey: 'isReply',
    title: '是否已回复',
    ellipsis: false,
    align: 'center',
  },
  // {
  //   width: 90,
  //   colKey: 'createtime',
  //   title: '评价时间',
  //   align: 'center',
  // },
  {
    colKey: 'operation',
    title: '操作',
    width: 100,
    cell: 'operation',
    fixed: 'right',
    align: 'center',
  },
];
const dataLoading = ref(false);
const pagination = ref({
  pageSize: 20,
  total: 0,
  current: 1,
});
const params = reactive({
  isReply: '',
  orderid: '',
});
const onPageChange = async (pageInfo) => {
  pagination.value.current = pageInfo.current;
  pagination.value.pageSize = pageInfo.pageSize;
  await fetchData();
};
const clearName = async (index) => {
  if (index === 0) {
    params.orderid = '';
  } else if (index === 1) {
    params.isReply = '';
  }
  pagination.value.current = 1;
  await fetchData();
};
const fetchData = async () => {
  data.value = [];
  dataLoading.value = true;
  try {
    let res = await queryCommentList({
      size: pagination.value.pageSize,
      current: pagination.value.current,
      orderid: params.orderid,
      isReply: params.isReply,
      parentid: '0',
    });
    if (res.code === 0) {
      data.value = res.data.records;
      pagination.value.total = res.data.total;
    }
  } catch (er) {
  } finally {
    dataLoading.value = false;
  }
};
//左侧角色菜单列表数据end

//vue的api
onMounted(async () => {
  initDicts();
  fetchData();
});
</script>
<style lang="less" scoped>
@import '@/style/variables';

._tableList {
  ._row {
    font-weight: 700;
    font-size: 15px;
  }
  ._row1 {
    color: var(--td-text-color-placeholder);
    font-size: 12px;
  }
  ._row2 {
    color: var(--td-text-color-primary);
    font-size: 12px;
  }
  .je {
    color: var(--td-brand-color);
    font-size: 16px;
    font-weight: 700;
  }
  ._goodsName {
    color: var(--td-text-color-primary);
    font-size: 14px;
    font-weight: 700;
  }
  ._skuName {
    color: var(--td-text-color-placeholder);
    font-size: 12px;
  }
  ._goodRow {
    margin-bottom: 4px;
  }
  ._star {
    color: var(--td-text-color-placeholder);
  }
}

.menu-active {
  color: var(--td-brand-color) !important;
}
.menu-unactive {
  color: var(--tdvns-text-color-primary) !important;
}
.menu-text {
  vertical-align: middle;
}
.sp-role-left {
  border-radius: 8px;
  .sp-role-left-header {
    padding-bottom: 15px;
  }
}
</style>

<style scoped>
.tdesign-demo-image-viewer__ui-image {
  width: 200px;
  height: 200px;
  display: inline-flex;
  position: relative;
  justify-content: center;
  align-items: center;
  border-radius: var(--td-radius-small);
  overflow: hidden;
}

.tdesign-demo-image-viewer__ui-image--hover {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  left: 0;
  top: 0;
  opacity: 0;
  background-color: rgba(0, 0, 0, 0.6);
  color: var(--td-text-color-anti);
  line-height: 22px;
  transition: 0.2s;
}

.tdesign-demo-image-viewer__ui-image:hover .tdesign-demo-image-viewer__ui-image--hover {
  opacity: 1;
  cursor: pointer;
}

.tdesign-demo-image-viewer__ui-image--img {
  width: 200px;
  height: 200px;
  cursor: pointer;
  position: absolute;
}

.tdesign-demo-image-viewer__ui-image--footer {
  padding: 0 16px;
  height: 56px;
  width: 100%;
  line-height: 56px;
  font-size: 16px;
  position: absolute;
  bottom: 0;
  color: var(--td-text-color-anti);
  background-image: linear-gradient(0deg, rgba(0, 0, 0, 0.4) 0%, rgba(0, 0, 0, 0) 100%);
  display: flex;
  box-sizing: border-box;
}

.tdesign-demo-image-viewer__ui-image--title {
  flex: 1;
}

.tdesign-demo-popup__reference {
  margin-left: 16px;
}

.tdesign-demo-image-viewer__ui-image--icons .tdesign-demo-icon {
  cursor: pointer;
}

.tdesign-demo-image-viewer__base {
  width: 200px;
  height: 200px;
  margin: 10px;
  border: 4px solid var(--td-bg-color-secondarycontainer);
  border-radius: var(--td-radius-medium);
}
</style>
