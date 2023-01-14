<template>
  <div>
    <div class="sp-role-left">
      <t-card :bordered="false">
        <div class="sp-role-left-header">
          <t-row :gutter="10">
            <t-col :span="6" :offset="6">
              <t-row :gutter="10" justify="end">
                <t-col :span="5">
                  <t-input
                    placeholder="请输入商品标题"
                    type="search"
                    clearable
                    v-model="params.name"
                    @clear="clearName(0)"
                    @enter="firstFetch"
                  ></t-input>
                </t-col>
                <t-col :span="2">
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
          class="_tableList"
        >
          <template #id="{ row }">
            <t-row>
              <t-col :span="3">
                <t-image :fit="'fill'" :src="row.mainimg" :style="{ width: '80px', height: '80px' }"></t-image>
              </t-col>
              <t-col :span="9">
                <t-row>
                  <t-col :span="12">
                    <span class="_row1">商品编号：{{ row.id }}</span>
                  </t-col>
                </t-row>
                <t-row>
                  <t-col :span="12">
                    <span class="_row">{{ row.name }}</span>
                  </t-col>
                </t-row>
              </t-col>
            </t-row>
          </template>
          <template #price="{ row }">
            <t-row>
              <t-col :span="12">
                <span class="je">￥{{ row.price }}</span>
              </t-col>
            </t-row>
            <t-row>
              <t-col :span="12">
                <span class="_row1" style="text-decoration: line-through">￥ {{ row.nprice }}</span>
              </t-col>
            </t-row>
          </template>
          <template #operation="{ row }">
            <!-- <t-button size="small" variant="outline" theme="default">修改</t-button> -->
            <t-popconfirm
              theme="danger"
              content="恢复之后商品将变为<待上架>状态，是否确认恢复商品?"
              @confirm="upProduct(row)"
            >
              <t-button size="small" variant="outline" theme="danger">恢复</t-button>
            </t-popconfirm>
          </template>
        </t-table>
      </t-card>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: 'allProcuct',
};
</script>

<script setup lang="ts">
import { ref, onMounted, reactive, getCurrentInstance } from 'vue';
import { queryDelList } from '@/api/shop/good/info';
import { dicVals } from '@/api/common';
import { removeDel } from '@/api/shop/good/info';
import { MessagePlugin } from 'tdesign-vue-next';
const emit = defineEmits(['reloadCount']);

//恢复商品待上架
const upProduct = async (item) => {
  let delMsg = MessagePlugin.loading('正在处理中...');
  try {
    let res = await removeDel(item.id);
    MessagePlugin.close(delMsg);
    if (res.code === 0) {
      fetchData();
      emit('reloadCount');
      MessagePlugin.success('操作成功');
    } else {
      MessagePlugin.error('操作失败：' + res.msg);
    }
  } catch (error) {
    MessagePlugin.error('操作失败：' + error);
  }
};
//加载字典数据
const prodStatus = ref({});
const prodStatusList = ref([]);
const initDicts = async () => {
  let res = await dicVals('SP_PRODUCTSTATUS');
  prodStatusList.value = res.data;
  res.data.forEach((row) => {
    prodStatus.value[row.value] = row.label;
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
    width: 200,
    colKey: 'id',
    title: '商品',
    ellipsis: false,
  },
  {
    width: 120,
    colKey: 'price',
    title: '价格',
    ellipsis: false,
    align: 'center',
  },
  // {
  //   width: 60,
  //   colKey: 'inventory',
  //   title: '库存',
  //   ellipsis: false,
  // },
  {
    width: 100,
    colKey: 'sales',
    title: '销量',
    align: 'center',
  },
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
  name: '',
  status: '',
});
const onPageChange = async (pageInfo) => {
  pagination.value.current = pageInfo.current;
  pagination.value.pageSize = pageInfo.pageSize;
  await fetchData();
};
const clearName = async (index) => {
  if (index === 0) {
    params.name = '';
  } else if (index === 3) {
    params.status = '';
    return;
  }
  pagination.value.current = 1;
  await fetchData();
};
const fetchData = async () => {
  data.value = [];
  dataLoading.value = true;
  try {
    let res = await queryDelList({
      size: pagination.value.pageSize,
      current: pagination.value.current,
      name: params.name,
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
  .je {
    color: var(--td-brand-color);
    font-size: 15px;
    font-weight: 700;
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
