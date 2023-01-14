<template>
  <div>
    <t-tabs :value="value" :theme="theme" @change="handlerChange">
      <t-tab-panel value="tab">
        <template #label> 全部 </template>
        <All @reloadCount="initData" />
      </t-tab-panel>
      <t-tab-panel value="tab1">
        <template #label> 待付款({{ dataCount.count0 }}) </template>
        <Index0 @reloadCount="initData" />
      </t-tab-panel>
      <t-tab-panel value="tab2">
        <template #label> 待发货({{ dataCount.count1 }}) </template>
        <Index1 @reloadCount="initData" />
      </t-tab-panel>
      <t-tab-panel value="tab3">
        <template #label> 待收货 </template>
        <Index2 @reloadCount="initData" />
      </t-tab-panel>
      <t-tab-panel value="tab4">
        <template #label> 待评价 </template>
        <Index3 @reloadCount="initData" />
      </t-tab-panel>
      <t-tab-panel value="tab6">
        <template #label> 已取消 </template>
        <Index4 @reloadCount="initData" />
      </t-tab-panel>
      <t-tab-panel value="tab5">
        <template #label> 交易完成 </template>
        <Index5 @reloadCount="initData" />
      </t-tab-panel>
      <t-tab-panel value="tab7">
        <template #label> 售后({{ dataCount.count2 }}) </template>
        <Index6 @reloadCount="initData" />
      </t-tab-panel>
    </t-tabs>
  </div>
</template>

<script lang="ts">
export default {
  name: 'ListBase',
};
</script>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import All from './list/all.vue';
import Index0 from './list/index0.vue';
import Index1 from './list/index1.vue';
import Index2 from './list/index2.vue';
import Index3 from './list/index3.vue';
import Index4 from './list/index4.vue';
import Index5 from './list/index5.vue';
import Index6 from './list/index6.vue';
import { queryDataCount } from '@/api/shop/order/list';
import router from '@/router';
const value = ref('tab');
const theme = ref('normal');

const dataCount = ref({
  count0: '-',
  count1: '-',
  count2: '-',
});

const initData = async () => {
  let res = await queryDataCount();
  dataCount.value.count0 = res.data.count0;
  dataCount.value.count1 = res.data.count1;
  dataCount.value.count2 = res.data.count2;
};
const handlerChange = (newValue) => {
  value.value = newValue;
};
onMounted(() => {
  console.log(router.currentRoute.value.query);
  if (router.currentRoute.value.query && router.currentRoute.value.query.index) {
    let tabIndex = router.currentRoute.value.query.index;
    if (tabIndex == '0') {
      value.value = 'tab1';
    } else if (tabIndex == '1') {
      value.value = 'tab2';
    }
  }
  initData();
});
</script>
<style lang="less" scoped>
@import '@/style/variables';
</style>
