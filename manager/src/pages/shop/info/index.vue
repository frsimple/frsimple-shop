<template>
  <div>
    <t-tabs :value="value" :theme="theme" @change="handlerChange">
      <t-tab-panel value="all">
        <template #label> 全部商品({{ productCount.all }}) </template>
        <p style="padding: 5px">
          <AllProduct @reloadCount="initData" />
        </p>
      </t-tab-panel>
      <t-tab-panel value="del">
        <template #label> 已删除商品({{ productCount.del }}) </template>
        <p style="padding: 5px">
          <DelProduct @reloadCount="initData" />
        </p>
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
import AllProduct from './all.vue';
import DelProduct from './del.vue';
import { infoCount } from '@/api/shop/good/info';
const value = ref('all');
const theme = ref('normal');

const productCount = ref({
  all: '-',
  del: '-',
});
const initData = async () => {
  let res = await infoCount();
  productCount.value.all = res.data.all;
  productCount.value.del = res.data.del;
};

const handlerChange = (newValue) => {
  value.value = newValue;
};

onMounted(() => {
  initData();
});
</script>
<style lang="less" scoped>
@import '@/style/variables';
</style>
