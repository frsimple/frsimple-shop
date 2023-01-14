<template>
  <result title="敬请期待" tip="功能研发中..." type="maintenance">
    <t-button theme="default" @click="() => $router.push('/')">返回首页</t-button>
  </result>
</template>

<script lang="ts">
export default {
  name: 'ResultMaintenance',
};
</script>
<script setup lang="ts">
import Result from '@/components/result/index.vue';
</script>
