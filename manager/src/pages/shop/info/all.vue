<template>
  <div>
    <div class="sp-role-left">
      <t-card :bordered="false">
        <div class="sp-role-left-header">
          <t-row :gutter="10">
            <t-col :span="4">
              <t-button @click="clickAddShop">新增商品</t-button>
            </t-col>
            <t-col :span="6" :offset="2">
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
                <t-col :span="3">
                  <t-select
                    v-model="params.status"
                    :options="prodStatusList"
                    clearable
                    @change="statusChange"
                    @clear="clearName(3)"
                    placeholder="请选择商品状态"
                  />
                </t-col>
                <t-col :span="2">
                  <t-button theme="default" variant="outline" @click="firstFetch">查询</t-button>
                </t-col>
              </t-row>
            </t-col>
          </t-row>
        </div>
        <t-space style="width: 60%; margin-bottom: 8px">
          <t-alert theme="info">
            <template #message>
              <span style="font-weight: 700"
                >查询到{{ prodCount.all }}个商品，其中{{ prodCount.on }}个已上架，{{ prodCount.close }}个未上架</span
              >
            </template>
          </t-alert>
        </t-space>
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
            <t-row :gutter="10">
              <t-col :span="4">
                <t-swiper :duration="300" :interval="2000" :navigation="{ showSlideBtn: 'never', size: 'small' }">
                  <t-swiper-item v-for="(item, index) in row.imgs.split(',')" :key="index">
                    <t-image-viewer :images="row.imgs.split(',')">
                      <template #trigger="{ open }">
                        <t-image :src="item" :style="{ height: '140px' }" mode="fill" overlay-trigger="hover">
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
                <!-- <t-image :fit="'fill'" :src="row.mainimg" :style="{ width: '80px', height: '80px' }"></t-image> -->
              </t-col>
              <t-col :span="8">
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
          <template #status="{ row }">
            <t-row>
              <t-col :span="12">
                {{ prodStatus[row.status] }}
              </t-col>
            </t-row>
            <t-row>
              <t-col :span="12">
                <span class="_row1">最后修改时间：{{ row.updatetime }}</span>
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
            <t-popconfirm v-if="row.status === '00'" theme="danger" content="是否确认上架该商品?" @confirm="upPro(row)">
              <t-button size="small" variant="outline" theme="default">上架</t-button>
            </t-popconfirm>
            <t-popconfirm
              v-if="row.status === '01'"
              theme="danger"
              content="是否确认下架该商品?"
              @confirm="downPro(row)"
            >
              <t-button size="small" variant="outline" theme="default">下架</t-button>
            </t-popconfirm>
            <t-button
              v-if="row.status === '00'"
              size="small"
              variant="outline"
              theme="default"
              @click="clickEditShop(row)"
              >修改</t-button
            >
            <t-popconfirm v-if="row.status === '00'" theme="danger" content="是否确认删除?" @confirm="delProduct(row)">
              <t-button size="small" variant="outline" theme="danger">删除</t-button>
            </t-popconfirm>
          </template>
        </t-table>
      </t-card>
    </div>

    <!--  新增商品 -->
    <add-shop ref="addShopWin" @reloadList="reloadList"></add-shop>
    <!--  修改商品 -->
    <edit-shop ref="editShopWin" @reloadList="reloadList"></edit-shop>
  </div>
</template>

<script lang="ts">
export default {
  name: 'allProcuct',
};
</script>

<script setup lang="ts">
import { ref, onMounted, reactive, getCurrentInstance } from 'vue';
import { queryList, infoCount, removeProd, upProduct1, downProduct } from '@/api/shop/good/info';
import AddShop from './add.vue';
import EditShop from './edit.vue';
import { dicVals } from '@/api/common';
import { MessagePlugin } from 'tdesign-vue-next';
import { width } from 'dom7';

const emit = defineEmits(['reloadCount']);

const reloadList = () => {
  fetchData();
  initData();
  emit('reloadCount');
};
//上架商品
const upPro = async (row) => {
  let upMsg = MessagePlugin.loading('正在上架中...');
  try {
    let res = await upProduct1(row.id);
    MessagePlugin.close(upMsg);
    if (res.code === 0) {
      MessagePlugin.success('操作成功');
      reloadList();
    } else {
      MessagePlugin.error('操作失败:' + res.msg);
    }
  } catch (error) {
    MessagePlugin.error('操作失败:' + error);
  }
};

//下架商品
const downPro = async (row) => {
  let downMsg = MessagePlugin.loading('正在下架中...');
  try {
    let res = await downProduct(row.id);
    MessagePlugin.close(downMsg);
    if (res.code === 0) {
      MessagePlugin.success('操作成功');
      reloadList();
    } else {
      MessagePlugin.error('操作失败:' + res.msg);
    }
  } catch (error) {
    MessagePlugin.error('操作失败:' + error);
  }
};

//删除商品
const delProduct = async (row) => {
  let delMsg = MessagePlugin.loading('正在删除中...');
  try {
    let res = await removeProd(row.id);
    MessagePlugin.close(delMsg);
    if (res.code === 0) {
      MessagePlugin.success('操作成功');
      reloadList();
    } else {
      MessagePlugin.error('操作失败:' + res.msg);
    }
  } catch (error) {
    MessagePlugin.error('操作失败:' + error);
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
type typeProductCount = {
  all: number;
  on: number;
  close: number;
};
const prodCount = ref({
  all: null,
  on: null,
  close: null,
});
const initData = async () => {
  let res = await infoCount();
  prodCount.value = res.data;
};
//新增商品的抽屉
const addShopWin = ref(null);
const clickAddShop = async () => {
  addShopWin.value.initAddData();
};

//修改商品的抽屉
const editShopWin = ref(null);
const clickEditShop = (row) => {
  editShopWin.value.initAddData(row);
};

//获取table全局高度
const tableHeight = getCurrentInstance().appContext.config.globalProperties.$tableHeight;

const firstFetch = () => {
  pagination.value.current = 1;
  fetchData();
};

//左侧角色菜单列表数据start
const data = ref([]);
const columns = [
  {
    width: 220,
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
    width: 100,
    colKey: 'status',
    title: '商品状态',
    align: 'left',
    ellipsis: false,
  },
  {
    colKey: 'operation',
    title: '操作',
    width: 120,
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
const onPageChange = (pageInfo) => {
  pagination.value.current = pageInfo.current;
  pagination.value.pageSize = pageInfo.pageSize;
  fetchData();
};
const statusChange = (value) => {
  params.status = value;
  pagination.value.current = 1;
  fetchData();
};
const clearName = async (index) => {
  if (index === 0) {
    params.name = '';
  } else if (index === 3) {
    params.status = '';
    return;
  }
  pagination.value.current = 1;
  fetchData();
};
const fetchData = async () => {
  data.value = [];
  dataLoading.value = true;
  try {
    let res = await queryList({
      size: pagination.value.pageSize,
      current: pagination.value.current,
      name: params.name,
      status: params.status,
    });
    if (res.code === 0) {
      data.value = res.data.records.filter((row) => {
        //row.imgs = row.mainimg + ',' + row.imgs;
        return {
          ...row,
        };
      });
      pagination.value.total = res.data.total;
    }
  } catch (er) {
  } finally {
    dataLoading.value = false;
  }
};
//左侧角色菜单列表数据end

//vue的api
onMounted(() => {
  initDicts();
  initData();
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

.t-alert {
  padding: 6px 12px !important;
}
.t-tag {
  cursor: pointer;
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
