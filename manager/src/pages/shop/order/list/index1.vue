<template>
  <div>
    <div class="sp-role-left">
      <t-card :bordered="false">
        <div class="sp-role-left-header">
          <t-row :gutter="10">
            <t-col :span="9" :offset="3">
              <t-row :gutter="10" justify="end">
                <t-col :span="3">
                  <t-input
                    placeholder="订单号"
                    type="search"
                    clearable
                    v-model="params.orderId"
                    @clear="clearName(0)"
                    @enter="firstFetch"
                  ></t-input>
                </t-col>
                <t-col :span="3">
                  <t-input
                    placeholder="下单人手机号"
                    type="search"
                    clearable
                    v-model="params.phone"
                    @clear="clearName(1)"
                    @enter="firstFetch"
                  ></t-input>
                </t-col>
                <t-col :span="5">
                  <t-date-range-picker
                    @clear="clearName(2)"
                    placeholder="交易日期"
                    allow-input
                    clearable
                    @pick="onPick"
                    @change="onChange"
                  />
                </t-col>
                <t-col :span="1">
                  <t-button theme="default" variant="outline" @click="firstFetch">查询</t-button>
                </t-col>
              </t-row>
            </t-col>
          </t-row>
        </div>
        <t-table
          row-key="id"
          :data="data"
          :max-height="'64vh'"
          :columns="columns"
          :table-layout="'fixed'"
          :pagination="pagination"
          @page-change="onPageChange"
          :loading="dataLoading"
          :loadingProps="{ size: '23px', text: '加载中...' }"
          class="_tableList"
        >
          <template #id="{ row }">
            {{ row.id }}<br />
            <span class="_row1">{{ payType[row.paytype] }}支付</span>
          </template>
          <template #userid="{ row }">
            <t-space align="center">
              <span>
                <t-avatar
                  v-if="row.userAvatar"
                  :image="row.userAvatar"
                  size="45px"
                  shape="round"
                  :hide-on-load-failed="false"
                />
                <t-avatar v-else icon="user" size="45px" shape="round" :hide-on-load-failed="false" />
              </span>
              <span>
                <t-space direction="vertical" size="small">
                  <span class="_row1"> {{ row.userName ? row.userName : '<未设置昵称>' }}</span>
                  <span class="_row2"> {{ row.userPhone }}</span>
                </t-space>
              </span>
            </t-space>
          </template>
          <template #price="{ row }">
            <span class="je">￥{{ row.nprice ? row.nprice : row.price }}</span
            ><span class="_row1">/运费￥{{ row.freight }}</span>
          </template>
          <template #status="{ row }">
            {{ prodStatus[row.status] }}
          </template>
          <template #goods="{ row }">
            <t-row v-for="(item, index) in row.goods" :key="index" :gutter="10" class="_goodRow">
              <t-col :span="2">
                <t-image :style="{ width: '60px', height: '60px' }" :src="item.primaryImage"></t-image>
              </t-col>
              <t-col :span="10">
                <t-row>
                  <t-col :span="12">
                    <span class="_goodsName">{{ item.goodsName }}</span>
                  </t-col>
                </t-row>
                <t-row>
                  <t-col :span="2">
                    <span class="_skuName">{{ item.selectedSkuInfo ? item.selectedSkuInfo.rule : '' }}</span>
                  </t-col>
                  <t-col :span="1" :offset="1">
                    <span class="_skuName">x{{ item.buyNum }}</span>
                  </t-col>
                </t-row>
              </t-col>
            </t-row>
          </template>
          <template #operation="{ row }">
            <t-space>
              <t-button size="small" theme="primary" @click="showDia(row)">发货</t-button>
              <t-button size="small" theme="default">订单详情</t-button>
            </t-space>
          </template>
        </t-table>
      </t-card>
    </div>
    <t-dialog
      v-model:visible="showJeDialog"
      header="填写运单信息"
      :confirm-btn="confirmBtn"
      :closeOnOverlayClick="false"
      :on-confirm="onConfirmAnother"
    >
      <template #body>
        <t-form ref="form" :data="jeForm" :rules="rules">
          <t-form-item label="订单号" name="id">
            <t-input v-model="jeForm.id" :disabled="true" />
          </t-form-item>
          <t-form-item label="快递公司" name="company">
            <t-select v-model="jeForm.company" placeholder="请选择快递公司">
              <t-option
                v-for="item in expressList"
                :key="item.value"
                :value="item.value"
                :label="item.label"
              ></t-option>
            </t-select>
          </t-form-item>
          <t-form-item label="快递单号" name="expressNo">
            <t-input v-model="jeForm.expressNo" placeholder="请输入快递单号" />
          </t-form-item>
        </t-form>
      </template>
    </t-dialog>
  </div>
</template>

<script lang="ts">
export default {
  name: 'allProcuct',
};
</script>

<script setup lang="ts">
import { ref, onMounted, reactive, getCurrentInstance } from 'vue';
import { queryOrderList, orderSend } from '@/api/shop/order/list';
import { dicVals } from '@/api/common';
import { MessagePlugin } from 'tdesign-vue-next';
const emit = defineEmits(['reloadCount']);

//修改订单金额
const showJeDialog = ref(false);
const selectedOrder = ref({});
const form = ref(null);
const jeForm = ref({
  id: '',
  expressNo: '',
  company: '',
});
const rules = {
  expressNo: [{ required: true, message: '请输入运单号', type: 'error' }],
  company: [{ required: true, message: '请选择快递公司', type: 'error' }],
};
const confirmBtn = ref({
  content: '确定发货',
  theme: 'primary',
  loading: false,
});
const showDia = async (order) => {
  selectedOrder.value = order;
  form.value.reset();
  jeForm.value.id = order.id;
  jeForm.value.expressNo = '';
  jeForm.value.company = '';
  showJeDialog.value = true;
};
const onConfirmAnother = async () => {
  let result = await form.value.validate();
  if (typeof result !== 'object' && result) {
    confirmBtn.value = {
      content: '保存中...',
      theme: 'primary',
      loading: true,
    };
    try {
      let res = await orderSend({
        id: jeForm.value.id,
        express: {
          expressNo: jeForm.value.expressNo,
          company: jeForm.value.company,
        },
      });
      if (res.code == 0) {
        MessagePlugin.success('发货成功');
        fetchData();
        emit('reloadCount');
        showJeDialog.value = false;
      } else {
        MessagePlugin.error('发货失败:' + res.msg);
      }
    } catch (er) {
      MessagePlugin.error('发货失败:' + er);
    } finally {
      confirmBtn.value = {
        content: '确定发货',
        theme: 'primary',
        loading: false,
      };
    }
  }
};
//end

//日期选择器
const onPick = (value, context) => console.log('onPick:', value, context);
const onChange = (value, context) => {
  let timeArray = context.dayjsValue.map((d) => d.format('YYYY-MM-DD'));
  params.startTime = timeArray[0];
  params.endTime = timeArray[1];
  firstFetch();
};

//加载字典数据
const prodStatus = ref({});
const prodStatusList = ref([]);
const payType = ref({});
const payTypeList = ref([]);
const expressMap = ref({});
const expressList = ref([]);
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

  let res2 = await dicVals('SP_SHOP_EXPRESSCOMPANY');
  expressList.value = res2.data;
  res2.data.forEach((row) => {
    expressMap.value[row.value] = row.label;
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
    width: 110,
    colKey: 'id',
    title: '订单号',
    ellipsis: false,
  },
  {
    width: 240,
    colKey: 'goods',
    title: '商品信息',
    ellipsis: false,
  },
  {
    width: 80,
    colKey: 'price',
    title: '订单总金额/运费',
    ellipsis: false,
  },
  {
    width: 100,
    colKey: 'userid',
    title: '用户信息',
    align: 'left',
  },
  //   {
  //     width: 60,
  //     colKey: 'status',
  //     title: '订单状态',
  //     ellipsis: false,
  //   },
  {
    width: 90,
    colKey: 'createtime',
    title: '下单时间',
    align: 'center',
  },
  {
    colKey: 'operation',
    title: '操作',
    width: 80,
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
  phone: '',
  orderId: '',
  status: '',
  startTime: '',
  endTime: '',
});
const onPageChange = async (pageInfo) => {
  pagination.value.current = pageInfo.current;
  pagination.value.pageSize = pageInfo.pageSize;
  await fetchData();
};
const clearName = async (index) => {
  if (index === 0) {
    params.orderId = '';
  } else if (index === 1) {
    params.phone = '';
  } else if (index === 2) {
    params.startTime = '';
    params.endTime = '';
  }
  pagination.value.current = 1;
  await fetchData();
};
const fetchData = async () => {
  data.value = [];
  dataLoading.value = true;
  try {
    let res = await queryOrderList({
      size: pagination.value.pageSize,
      current: pagination.value.current,
      orderId: params.orderId,
      phone: params.phone,
      startTime: params.startTime,
      endTime: params.endTime,
      status: '01',
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
  ._row2 {
    color: var(--td-text-color-primary);
    font-size: 12px;
  }
  .je {
    color: var(--td-brand-color);
    font-size: 16px;
    font-weight: 700;
  }
  ._goodsName {
    color: var(--td-text-color-primary);
    font-size: 14px;
    font-weight: 700;
  }
  ._skuName {
    color: var(--td-text-color-placeholder);
    font-size: 12px;
  }
  ._goodRow {
    margin-bottom: 4px;
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
