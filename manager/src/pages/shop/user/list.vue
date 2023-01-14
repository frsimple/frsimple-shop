<template>
  <div>
    <div class="sp-role-left">
      <t-card :bordered="false">
        <div class="sp-role-left-header">
          <t-row>
            <t-col :span="6" :offset="6">
              <t-row :gutter="10">
                <t-col :flex="1" :span="4" :offset="2">
                  <t-input
                    placeholder="请输入用户昵称"
                    type="search"
                    clearable
                    v-model="params.name"
                    @clear="clearName(0)"
                    @enter="firstFetch"
                  ></t-input>
                </t-col>
                <t-col :flex="1" :span="4">
                  <t-input
                    placeholder="请输入手机号"
                    type="search"
                    clearable
                    v-model="params.phone"
                    @clear="clearName(1)"
                    @enter="firstFetch"
                  ></t-input>
                </t-col>
                <t-col :flex="1" :span="1">
                  <t-button theme="default" variant="outline" @click="firstFetch">查询</t-button>
                </t-col>
              </t-row>
            </t-col>
          </t-row>
        </div>
        <t-table
          row-key="id"
          :data="data"
          :max-height="tableHeight"
          :columns="columns"
          :table-layout="'fixed'"
          :pagination="pagination"
          @page-change="onPageChange"
          :loading="dataLoading"
          :loadingProps="{ size: '23px', text: '加载中...' }"
        >
          <template #avatar="{ row }">
            <t-avatar v-if="row.avatar" :image="row.avatar" size="40px" shape="round" :hide-on-load-failed="false" />
            <t-avatar v-else icon="user" size="40px" shape="round" :hide-on-load-failed="false" />
          </template>
          <template #datasource="{ row }">
            {{ dataSource[row.datasource] }}
          </template>
          <!-- <template #operation="{ row }">
            <t-button v-if="authEdit" size="small" variant="outline" theme="default" @click="editRow(row)"
              >修改</t-button
            >
            <t-button v-if="authDel" size="small" variant="outline" theme="danger" @click="delRow(row)">删除</t-button>
          </template> -->
        </t-table>
      </t-card>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: 'ListTenant',
};
</script>

<script setup lang="ts">
import { ref, onMounted, reactive, getCurrentInstance } from 'vue';
import { queryUserList } from '@/api/shop/user/list';
import { dicVals } from '@/api/common';
import { init } from 'echarts';

//加载字典数据
const dataSource = ref({});
const dataSourceL = ref([]);
const initDicts = async () => {
  let res = await dicVals('SP_SHOP_DATASOURCE');
  dataSourceL.value = res.data;
  res.data.forEach((row) => {
    dataSource.value[row.value] = row.label;
  });
};

//获取table全局高度
const tableHeight = getCurrentInstance().appContext.config.globalProperties.$tableHeight;

const firstFetch = async () => {
  pagination.value.current = 1;
  await fetchData();
};

const data = ref([]);
const columns = [
  {
    width: 120,
    colKey: 'openid',
    title: 'openId',
  },
  {
    width: 120,
    colKey: 'name',
    title: '昵称',
    ellipsis: true,
  },
  {
    width: 100,
    colKey: 'avatar',
    title: '头像',
    align: 'center',
  },
  {
    width: 100,
    colKey: 'phone',
    title: '手机号',
    align: 'center',
  },
  {
    width: 100,
    colKey: 'createtime',
    title: '注册时间',
    ellipsis: true,
    align: 'center',
  },
  {
    width: 100,
    colKey: 'datasource',
    title: '数据来源',
    align: 'center',
  },
  // {
  //   colKey: 'operation',
  //   title: '操作',
  //   width: 200,
  //   cell: 'operation',
  //   fixed: 'right',
  // },
];
const dataLoading = ref(false);
const pagination = ref({
  pageSize: 20,
  total: 0,
  current: 1,
});
const params = reactive({
  name: '',
  phone: '',
});
const onPageChange = async (pageInfo) => {
  pagination.value.current = pageInfo.current;
  pagination.value.pageSize = pageInfo.pageSize;
  await fetchData();
};
const clearName = async (index) => {
  if (index == 0) {
    params.name = '';
  } else if (index == 1) {
    params.phone = '';
  }
  pagination.value.current = 1;
  await fetchData();
};
const fetchData = async () => {
  data.value = [];
  dataLoading.value = true;
  try {
    let res = await queryUserList({
      size: pagination.value.pageSize,
      current: pagination.value.current,
      name: params.name,
      phone: params.phone,
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

//vue的api
onMounted(async () => {
  initDicts();
  fetchData();
});
</script>

<style lang="less" scoped>
@import '@/style/variables';
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
    padding-bottom: 30px;
  }
}
</style>
