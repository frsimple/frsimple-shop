<template>
  <t-row :gutter="10" class="row-container">
    <t-col :xs="12" :xl="8">
      <t-card :style="{ height: '260px' }" :bordered="false" class="dashboard-chart-card" :loading="dataLoad">
        <template #title>
          <span :style="{ fontSize: `19px`, fontWeight: 700 }">待办处理</span>
        </template>
        <template #actions>
          <t-button shape="square" theme="primary" @click="initData">
            <template #icon><t-icon name="refresh"></t-icon></template>
          </t-button>
        </template>
        <template #content>
          <t-row>
            <t-col :span="4" v-for="(item, index) in PANE_LIST" :key="item.title">
              <div style="text-align: center">
                <t-space direction="vertical" align="center">
                  <t-button theme="default" variant="text" @click="toPage(index)"
                    >{{ item.title }}
                    <template #suffix>
                      <t-icon name="arrow-right"></t-icon>
                    </template>
                  </t-button>
                  <div class="dashboard-item-top">
                    <span :style="{ fontSize: `${1 * 20}px` }">{{ item.number }}</span>
                  </div>
                </t-space>
              </div>
            </t-col>
          </t-row>
        </template>
      </t-card>
    </t-col>
    <t-col :xs="12" :xl="4">
      <t-card :style="{ height: '260px' }" :bordered="false" class="dashboard-chart-card" :loading="dataLoad1">
        <template #title>
          <span :style="{ fontSize: `19px`, fontWeight: 700 }">商品管理</span>
        </template>
        <template #actions>
          <t-button shape="square" theme="primary" @click="initData1">
            <template #icon><t-icon name="refresh"></t-icon></template>
          </t-button>
        </template>
        <t-row>
          <t-col :span="6">
            <div style="text-align: center">
              <t-space direction="vertical" align="center">
                <span>销售中</span>
                <div class="dashboard-item-top">
                  <span :style="{ fontSize: `${1 * 20}px` }">{{ prodCount.on }}</span>
                </div>
              </t-space>
            </div>
          </t-col>
          <t-col :span="6">
            <div style="text-align: center">
              <t-space direction="vertical" align="center">
                <span>待上架</span>
                <div class="dashboard-item-top">
                  <span :style="{ fontSize: `${1 * 20}px` }">{{ prodCount.close }}</span>
                </div>
              </t-space>
            </div>
          </t-col>
        </t-row>
      </t-card>
    </t-col>
  </t-row>
</template>
<script lang="ts">
export default {
  name: 'DashboardBase',
};
</script>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { queryDataCount } from '@/api/shop/order/list';
import { infoCount } from '@/api/shop/good/info';
import router from '@/router';
const PANE_LIST = ref([
  {
    title: '待支付',
    number: 0,
  },
  {
    title: '待发货',
    number: 0,
  },
  {
    title: '售后',
    number: 0,
  },
]);

const dataLoad = ref(false);
const dataLoad1 = ref(false);
const initData = async () => {
  dataLoad.value = true;
  let res = await queryDataCount();
  PANE_LIST.value[0].number = res.data.count0;
  PANE_LIST.value[1].number = res.data.count1;
  PANE_LIST.value[2].number = res.data.count2;
  dataLoad.value = false;
};
const prodCount = ref({
  all: null,
  on: null,
  close: null,
});
const initData1 = async () => {
  dataLoad1.value = true;
  let res = await infoCount();
  prodCount.value = res.data;
  dataLoad1.value = false;
};

const toPage = (index) => {
  if (index == 2) {
    router.push({
      path: '/orders/orderservice',
    });
  } else {
    router.push({
      path: '/orders/list',
      query: {
        index: index,
      },
    });
  }
};
onMounted(() => {
  initData();
  initData1();
});
</script>

<style lang="less" scoped>
.dashboard-chart-card {
  padding: 8px;

  :deep(.t-card__header) {
    padding-bottom: 24px;
  }

  :deep(.t-card__title) {
    font-size: 20px;
    font-weight: 500;
  }
}
</style>
