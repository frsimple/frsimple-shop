<template>
  <t-drawer
    v-model:visible="addShopVisible"
    destroyOnClose
    :closeOnOverlayClick="false"
    :closeOnEscKeydown="false"
    header="订单详情"
    size="60%"
    :confirmBtn="null"
  >
    <t-loading size="23px" text="加载中..." :loading="dataLoad" show-overlay>
      <t-space style="width: 100%; margin-bottom: 8px">
        <t-alert theme="info">
          <template #message>
            <span style="font-weight: 700">当前订单状态：{{ prodStatus[selectedRow.status] }}</span>
          </template>
        </t-alert>
      </t-space>
      <div id="container" class="anchor-demo anchor-container-demo">
        <t-anchor id="default" container="#anchor-container" @click="handleClick">
          <t-anchor-item href="#content-1" title="基本信息" ref="anchor1" />
          <t-anchor-item href="#content-2" title="商品信息" ref="anchor2" />
          <t-anchor-item href="#content-3" title="收货人信息" ref="anchor3" />
          <t-anchor-item href="#content-4" title="快递信息" ref="anchor4" />
          <t-anchor-item href="#content-5" title="操作流水" ref="anchor5" />
          <t-anchor-item href="#content-6" title="评价" ref="anchor6" />
        </t-anchor>
        <t-space id="anchor-container" direction="vertical" :size="0">
          <div id="content-1" class="anchor-content-1">
            <t-card class="pd" :bordered="false">
              <template #title>
                <span class="title">基本信息</span>
              </template>
              <template #content>
                <div style="padding-right: 60px">
                  <t-row :gutter="[16, 24]">
                    <t-col :span="3">
                      <div class="_title">订单来源</div>
                    </t-col>
                    <t-col :span="9">
                      <div class="_content">{{ orderData[selectedRow.datasource] }}</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_title">订单号</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_content">{{ selectedRow.id }}</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_title">下单时间</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_content">{{ selectedRow.createtime }}</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_title">下单用户</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_content">
                        <t-space align="center">
                          <t-avatar
                            v-if="selectedRow.userAvatar"
                            shape="round"
                            :image="selectedRow.userAvatar"
                          ></t-avatar>
                          <span>{{ selectedRow.userName ? selectedRow.userName : '<未设置>' }}</span>
                        </t-space>
                      </div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_title">联系电话</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_content">{{ selectedRow.userPhone }}</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_title">订单金额</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_content-price">
                        {{ selectedRow.nprice ? selectedRow.nprice : selectedRow.price }}元
                      </div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_title">订单运费</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_content-price">{{ selectedRow.freight }}元</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_title">支付方式</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_content">{{ payType[selectedRow.paytype] }}</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_title">支付时间</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_content">{{ selectedRow.paytime }}</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_title">订单备注</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_content">{{ selectedRow.remark }}</div>
                    </t-col>
                  </t-row>
                </div>
              </template>
            </t-card>
          </div>
          <div id="content-2" class="anchor-content-2">
            <t-card class="pd" :bordered="false">
              <template #title>
                <span class="title">商品信息</span>
              </template>
              <template #content>
                <t-table
                  row-key="goodsName"
                  :data="selectedRow.goods"
                  :columns="[
                    { colKey: 'primaryImage', title: '商品图片', width: '50' },
                    { colKey: 'goodsName', title: '商品名称', width: '120', align: 'center' },
                    { colKey: 'buyNum', title: '购买数量', width: '100', align: 'center' },
                    { colKey: 'price', title: '商品单价', width: '100', align: 'center' },
                  ]"
                  :table-layout="'auto'"
                  bordered
                >
                  <template #primaryImage="{ row }">
                    <t-image :src="row.primaryImage"></t-image>
                  </template>
                  <template #goodsName="{ row }">
                    {{ row.selectedSkuInfo ? row.goodsName + '/' + row.selectedSkuInfo.rule : row.goodsName }}
                  </template>
                  <template #price="{ row }"> {{ row.price }}元 </template>
                </t-table>
              </template>
            </t-card>
          </div>
          <div id="content-3" class="anchor-content-3">
            <t-card class="pd" :bordered="false">
              <template #title>
                <span class="title">收货人信息</span>
              </template>
              <template #content>
                <div style="padding-right: 60px">
                  <t-row :gutter="[16, 24]">
                    <t-col :span="3">
                      <div class="_title">收货人</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_content">{{ selectedRow.takeInfo.rname }}</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_title">联系电话</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_content">{{ selectedRow.takeInfo.rphonedec }}</div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_title">收货地址</div>
                    </t-col>
                    <t-col :span="9">
                      <div class="_content">
                        {{
                          selectedRow.takeInfo.rarea.provinceName +
                          '/' +
                          selectedRow.takeInfo.rarea.cityName +
                          '/' +
                          selectedRow.takeInfo.rarea.districtName
                        }}
                      </div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_title">详细地址</div>
                    </t-col>
                    <t-col :span="9">
                      <div class="_content">
                        {{ selectedRow.takeInfo.raddress }}
                      </div>
                    </t-col>
                  </t-row>
                </div>
              </template>
            </t-card>
          </div>

          <div id="content-4" class="anchor-content-4">
            <t-card class="pd" :bordered="false">
              <template #title>
                <span class="title">快递信息</span>
              </template>
              <template #content>
                <div style="padding-right: 60px">
                  <t-row :gutter="[16, 24]">
                    <t-col :span="3">
                      <div class="_title">快递公司</div>
                    </t-col>
                    <t-col :span="9">
                      <div class="_content">
                        {{ selectedRow.express?.company ? expressMap[selectedRow.express?.company] : '' }}
                      </div>
                    </t-col>
                    <t-col :span="3">
                      <div class="_title">快递单号</div>
                    </t-col>
                    <t-col :span="9">
                      <div class="_content">
                        {{ selectedRow.express?.expressNo ? selectedRow.express.expressNo : '' }}
                      </div>
                    </t-col>
                  </t-row>
                </div>
              </template>
            </t-card>
          </div>
          <div id="content-5" class="anchor-content-5">
            <t-card class="pd" :bordered="false">
              <template #title>
                <span class="title">操作流水</span>
              </template>
              <template #content>
                <t-timeline :layout="'vertical'" mode="same">
                  <t-timeline-item
                    v-for="(item, index) in orderDetails"
                    :key="index"
                    :label="item.createTime"
                    dot-color="primary"
                  >
                    {{ item.remark }}
                  </t-timeline-item>
                </t-timeline>
              </template>
            </t-card>
          </div>
          <div id="content-6" class="anchor-content-6">
            <t-card class="pd" :bordered="false">
              <template #title>
                <span class="title">评价</span>
              </template>
              <template #content>
                <span>暂未评价</span>
                <t-comment
                  v-if="commentList && commentList.length != 0"
                  v-for="(item, index) in commentList"
                  :key="index"
                  :avatar="item.avatar"
                  :author="item.nickName"
                  :datetime="item.createTime"
                  :content="item.content"
                  class="comment-reply"
                >
                  <template v-if="item.markList && item.markList.length != 0" #reply>
                    <t-comment
                      v-for="(row, index1) in item.markList"
                      :key="index1"
                      avatar=""
                      :datetime="row.createTime"
                      :content="row.content"
                    >
                      <template #author>
                        <span>店家回复</span>
                      </template>
                    </t-comment>
                  </template>
                </t-comment>
              </template>
            </t-card>
          </div>
        </t-space>
      </div></t-loading
    >
  </t-drawer>
</template>

<script lang="ts">
export default {
  name: 'viewInfo',
};
</script>

<script setup lang="ts">
import { onMounted, ref, Ref } from 'vue';
import { queryOrderDetails } from '@/api/shop/order/list';
import { dicVals } from '@/api/common';
import { queryMarkList } from '@/api/shop/order/list';
const dataLoad = ref(true);
const addShopVisible = ref(false);

//加载字典数据
const prodStatus = ref({});
const prodStatusList = ref([]);
const payType = ref({});
const payTypeList = ref([]);
const orderData = ref({});
const orderDataList = ref([]);
const expressMap = ref({});
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

  let res2 = await dicVals('SP_SHOP_ORDERDATASOURCE');
  orderDataList.value = res2.data;
  res2.data.forEach((row) => {
    orderData.value[row.value] = row.label;
  });

  let res3 = await dicVals('SP_SHOP_EXPRESSCOMPANY');
  res3.data.forEach((row) => {
    expressMap.value[row.value] = row.label;
  });
};
//快捷菜单 -- start
const handleClick = ({ e, href, title }) => {
  e.preventDefault();
};
//快捷菜单 -- end
const selectedRow = ref<Record<string, any>>({
  id: '',
  status: '',
  paytype: '',
  createtime: '',
  price: '',
  nprice: '',
  remark: '',
  goods: [],
  takeInfo: {
    rname: '',
    rphonedec: '',
    raddress: '',
    rarea: {
      cityCode: '',
      cityName: '',
      districtCode: '',
      districtName: '',
      provinceCode: '',
      provinceName: '',
    },
  },
  express: {
    //company: '',
    //expressNo: '',
  },
  freight: '',
  paytime: '',
  datasource: '',
  markList: [],
  userAvatar: '',
  userName: '',
  userPhone: '',
});
const commentList = ref([]);
const orderDetails = ref([]);
const initData = async (row: Record<string, any>) => {
  selectedRow.value = row;
  addShopVisible.value = true;
  dataLoad.value = true;
  let res = await queryOrderDetails(row.id);
  orderDetails.value = res.data;
  let res1 = await queryMarkList(row.id);
  commentList.value = res1.data;
  dataLoad.value = false;
};
defineExpose({
  initData,
});
onMounted(async () => {
  initDicts();
});
</script>

<style lang="less" scoped>
.t-anchor {
  width: 256px !important;
}
.anchor-container-demo {
  display: flex;
}
#anchor-container {
  flex-grow: 1;
  height: calc(100vh - 215px);
  overflow: auto;
  .anchor-content-1 {
    padding-bottom: 20px;
  }
  .anchor-content-2 {
    padding-bottom: 20px;
  }
  .anchor-content-3 {
    padding-bottom: 20px;
  }
  .anchor-content-4 {
    padding-bottom: 20px;
  }
  .anchor-content-5 {
    padding-bottom: 20px;
  }
  .anchor-content-6 {
    //padding-bottom: 20px;
  }
  ._title {
    color: var(--td-text-color-secondary);
    text-align: right;
  }
  ._content {
    color: var(--td-text-color-primary);
    font-weight: 700;
    text-align: left;
  }
  ._content-price {
    color: var(--td-brand-color);
    font-size: 16px;
    font-weight: 700;
  }
  .title {
    color: var(--td-text-color-secondary);
    font-size: 18px;
    font-weight: 700;
  }
  .pd {
    background: var(--td-bg-color-page);
    //height: 100%;
    border-radius: 8px;
    //width: 900px;
    .s-color {
      color: var(--td-text-color-placeholder);
    }
  }
}
</style>
