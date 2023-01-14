<template>
  <div>
    <t-card
      :loading="loading"
      title="热词搜索设置"
      description="请输入设置的热词，回车确认，若热词重复则系统自动去重"
      :hover-shadow="false"
      :bordered="false"
    >
      <t-tag-input v-model="tags1" :style="{ width: '400px' }" clearable placeholder="请输入热词" />
      <template #footer>
        <t-button :style="{ 'margin-right': '8px' }" :loading="loading" :disabled="loading" @click="handleSave">
          保存
        </t-button>
      </template>
    </t-card>
  </div>
</template>

<script lang="ts">
export default {
  name: 'MainList',
};
</script>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getHotSearch, setHotSearch } from '@/api/shop/manager/main';
import { MessagePlugin } from 'tdesign-vue-next';
const loading = ref(true);
const tags1 = ref([]);

const handleSave = async () => {
  if (tags1.value && tags1.value.length != 0) {
    loading.value = true;
    const arr = [...new Set(tags1.value)];
    let res = await setHotSearch({
      content: arr.join(','),
    });
    loading.value = false;
    if (res.code == 0) {
      tags1.value = arr;
      MessagePlugin.success('保存成功');
    } else {
      MessagePlugin.error('保存失败：' + res.msg);
    }
  } else {
    MessagePlugin.warning('请输入热词');
  }
};

const initData = async () => {
  let res = await getHotSearch();
  loading.value = false;
  if (res.data) {
    tags1.value = res.data.split(',');
  }
};
onMounted(() => {
  initData();
});
</script>

<style lang="less" scoped>
@import '@/style/variables';
</style>
