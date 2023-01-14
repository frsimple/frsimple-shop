<template>
  <t-config-provider :global-config="globalConfig">
    <t-watermark
      :watermark-content="{
        text: 'frsimple 商城',
      }"
      :width="120"
      :height="60"
      :y="120"
      :x="80"
      :z-index="1000"
    >
      <router-view :class="[mode]" />
    </t-watermark>
  </t-config-provider>
</template>
<script setup lang="ts">
import { computed, onMounted } from 'vue';
import config from '@/config/style';
import { useSettingStore } from '@/store';
import merge from 'lodash/merge';
import enConfig from 'tdesign-vue-next/es/locale/zh_CN';

const store = useSettingStore();

const mode = computed(() => {
  return store.displayMode;
});

const globalConfig = merge(enConfig, {
  table: {},
});

onMounted(() => {
  store.updateConfig({ ...config });
});
</script>
<style lang="less" scoped>
@import '@/style/variables.less';

#nprogress .bar {
  background: var(--tdvns-brand-color) !important;
}
</style>
