<template>
  <div>
    <div class="sp-role-left">
      <t-card :bordered="false">
        <div class="sp-role-left-header">
          <t-row :gutter="10">
            <t-col :span="9" :offset="3">
              <t-row :gutter="10" justify="end">
                <t-col :span="3" :offset="3">
                  <t-input
                    placeholder="订单号"
                    type="search"
                    clearable
                    v-model="params.orderId"
                    @clear="clearName(0)"
                    @enter="firstFetch"
                  ></t-input>
                </t-col>
                <!-- <t-col :span="3">
                  <t-input
                    placeholder="下单人手机号"
                    type="search"
                    clearable
                    v-model="params.phone"
                    @clear="clearName(1)"
                    @enter="firstFetch"
                  ></t-input>
                </t-col> -->
                <t-col :span="3">
                  <t-input
                    placeholder="售后单号"
                    type="search"
                    clearable
                    v-model="params.id"
                    @clear="clearName(2)"
                    @enter="firstFetch"
                  ></t-input>
                </t-col>
                <t-col :span="2">
                  <t-select
                    placeholder="售后进度"
                    v-model="params.status"
                    clearable
                    @clear="clearName(3)"
                    @change="firstFetch"
                  >
                    <t-option
                      v-for="(item, index) in applyStatusList"
                      :key="index"
                      :label="item.label"
                      :value="item.value"
                    />
                  </t-select>
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
          :table-layout="'auto'"
          tableContentWidth="2150px"
          :pagination="pagination"
          @page-change="onPageChange"
          :loading="dataLoading"
          :loadingProps="{ size: '23px', text: '加载中...' }"
          class="_tableList"
        >
          <template #imgs="{ row }">
            <t-swiper v-if="row.imgs" :duration="300" :interval="2000">
              <t-swiper-item v-for="(item, index) in row.imgs" :key="index">
                <t-image-viewer :images="row.imgs">
                  <template #trigger="{ open }">
                    <t-image :src="item" :style="{ height: '200px' }" mode="fill" overlay-trigger="hover">
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
          </template>
          <template #type="{ row }">
            <t-space direction="vertical">
              <t-row>
                <t-col :span="4"><span class="_star">售后单号</span></t-col>
                <t-col :span="8"
                  ><b>{{ row.id }}</b></t-col
                >
              </t-row>
              <t-row>
                <t-col :span="4"><span class="_star">售后类型</span></t-col>
                <t-col :span="8"
                  ><b
                    >{{ applyType[row.type] }}
                    <t-tooltip v-if="row.type == 'REFUND_GOODS' && row.express" theme="primary">
                      <template #content>
                        快递公司：{{ companyMap[row.express.company] }} <br />
                        快递单号：{{ row.express.expressNo }}
                      </template>
                      <t-button theme="primary" size="small">快递信息</t-button>
                    </t-tooltip></b
                  ></t-col
                >
              </t-row>
              <t-row>
                <t-col :span="4"><span class="_star">退款金额</span></t-col>
                <t-col :span="8"
                  ><b>￥{{ row.price }}</b></t-col
                >
              </t-row>
              <t-row>
                <t-col :span="4"><span class="_star">是否收到货</span></t-col>
                <t-col :span="8"
                  ><b>{{ row.isGet == '1' ? '是' : '否' }}</b></t-col
                >
              </t-row>
              <t-row>
                <t-col :span="4"><span class="_star">退款原因</span></t-col>
                <t-col :span="8"
                  ><b>{{ row.reason }}</b></t-col
                >
              </t-row>
              <t-row>
                <t-col :span="4"><span class="_star">备注说明</span></t-col>
                <t-col :span="8"
                  ><b>{{ row.remark }}</b></t-col
                >
              </t-row>
            </t-space>
          </template>
          <template #orderId="{ row }">
            <t-space direction="vertical">
              <div>{{ row.order.id }}</div>
              <div>
                <span class="je">￥{{ row.order.nprice ? row.order.nprice : row.order.price }}</span
                ><span class="_row1">/运费￥{{ row.order.freight }}</span>
              </div>
            </t-space>
          </template>
          <template #userid="{ row }">
            <t-space align="center">
              <span>
                <t-avatar
                  v-if="row.user.avatar"
                  :image="row.user.avatar"
                  size="45px"
                  shape="round"
                  :hide-on-load-failed="false"
                />
                <t-avatar v-else icon="user" size="45px" shape="round" :hide-on-load-failed="false" />
              </span>
              <span>
                <t-space direction="vertical" size="small">
                  <span class="_row1"> {{ row.user.name ? row.user.name : '<未设置昵称>' }}</span>
                  <span class="_row2"> {{ row.user.phone }}</span>
                </t-space>
              </span>
            </t-space>
          </template>
          <template #price="{ row }">
            <span class="je">￥{{ row.price }}</span>
          </template>
          <template #status="{ row }">
            <t-space direction="vertical" size="small">
              <b>{{ applyStatus[row.status] }}</b>
              <t-tag variant="light-outline" v-if="row.status === '99'" shape="round" theme="primary">{{
                row.result === '1' ? '同意申请' : '拒绝申请'
              }}</t-tag>
              <span v-if="row.status === '99'" class="_row1">{{ row.resultMsg }}</span>
            </t-space>
          </template>
          <template #goods="{ row }">
            <t-row v-for="(item, index) in row.goods" :key="index" :gutter="10" class="_goodRow">
              <t-col :span="2">
                <t-image :src="item.thumb"></t-image>
              </t-col>
              <t-col :span="10">
                <t-row>
                  <t-col :span="12">
                    <span class="_goodsName">{{ item.title }}</span>
                  </t-col>
                </t-row>
                <t-row>
                  <t-col :span="2">
                    <span class="_skuName">{{ item.specs ? item.specs : '' }}</span>
                  </t-col>
                  <t-col :span="1" :offset="1">
                    <span class="_skuName">x{{ item.rnum }}</span>
                  </t-col>
                </t-row>
              </t-col>
            </t-row>
          </template>
          <template #operation="{ row }">
            <t-space direction="vertical">
              <t-popconfirm
                v-if="row.status == '0'"
                theme="danger"
                content="该售后申请是否审批通过？"
                confirmBtn="通过"
                cancelBtn="拒绝"
                @confirm="checkApply(row, '1', '同意')"
                @cancel="checkApplyCancel(row)"
              >
                <t-button size="small" theme="primary">售后审批</t-button>
              </t-popconfirm>
              <t-button size="small" theme="default" v-if="row.type == 'REFUND_GOODS'" @click="editExpress(row)"
                >{{ row.express ? '修改' : '填写' }}运单号</t-button
              >
              <t-popconfirm
                v-if="row.status == '0'"
                theme="danger"
                content="是否确认撤销售后申请？"
                @confirm="delApply(row)"
              >
                <t-button size="small" theme="default">撤销申请</t-button>
              </t-popconfirm>
            </t-space>
          </template>
        </t-table>
      </t-card>
    </div>
    <t-dialog
      v-model:visible="showDivDialog"
      :header="'修改运单号（售后单号：' + divFrom.id + '）'"
      :show-in-attached-element="true"
      placement="center"
      width="450"
      :confirmBtn="confirmBtn"
      :onConfirm="saveDivInfo"
    >
      <div slot="body">
        <t-form ref="formRef" :data="divFrom" :rules="divFromRule">
          <t-form-item label="快递公司" name="company">
            <t-select v-model="divFrom.company" placeholder="请选择快递公司">
              <t-option v-for="(item, index) in companyList" :key="index" :label="item.label" :value="item.value" />
            </t-select>
          </t-form-item>
          <t-form-item label="快递单号" name="expressNo">
            <t-input placeholder="请输入快递单号" v-model="divFrom.expressNo" />
          </t-form-item>
        </t-form>
      </div>
    </t-dialog>

    <t-dialog
      v-model:visible="showDivDialog1"
      :header="'填写审批拒绝原因（售后单号：' + selectRow.id + '）'"
      :show-in-attached-element="true"
      placement="center"
      width="450"
      :confirmBtn="confirmBtn1"
      :onConfirm="saveRefApply"
    >
      <div slot="body">
        <t-textarea
          :maxlength="100"
          v-model="selectRow.content"
          placeholder="请输入拒绝原因"
          :autosize="{ minRows: 3, maxRows: 5 }"
        />
      </div>
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
import { queryAftersalesList, editAftersales, delAftersales, checkAftersales } from '@/api/shop/order/list';
import { dicVals } from '@/api/common';
import { MessagePlugin } from 'tdesign-vue-next';
const emit = defineEmits(['reloadCount']);

//填写运单号
const formRef = ref(null);
const editExpress = (row) => {
  console.log(row);
  showDivDialog.value = true;
  formRef.value.reset();
  divFrom.value.id = row.id;
  divFrom.value.company = row.express?.company;
  divFrom.value.expressNo = row.express?.expressNo;
};
const divFrom = ref({
  id: '',
  expressNo: '',
  company: '',
});
const divFromRule = {
  expressNo: [{ required: true, message: '请选择快递公司', type: 'error' }],
  company: [{ required: true, message: '请输入快递单号', type: 'error' }],
};
const showDivDialog = ref(false);
const confirmBtn = ref({
  content: '保存',
  theme: 'primary',
  loading: false,
});
const saveDivInfo = async () => {
  let result = await formRef.value.validate();
  if (typeof result !== 'object' && result) {
    confirmBtn.value.content = '保存中...';
    confirmBtn.value.loading = true;
    let res = await editAftersales({
      id: divFrom.value.id,
      express: {
        expressNo: divFrom.value.expressNo,
        company: divFrom.value.company,
      },
    });
    confirmBtn.value.content = '保存';
    confirmBtn.value.loading = false;
    if (res.code === 0) {
      MessagePlugin.success('修改成功');
      showDivDialog.value = false;
      fetchData();
    } else {
      MessagePlugin.error('修改失败');
    }
  }
};

//撤销售后申请
const delApply = async (row) => {
  let res = await delAftersales(row.id);
  if (res.code === 0) {
    MessagePlugin.success('撤销成功');
    fetchData();
  } else {
    MessagePlugin.error('撤销失败');
  }
};

//售后审批
const checkApply = async (row, result, resultMsg) => {
  let res = await checkAftersales({
    id: row.id,
    result: result,
    resultMsg: resultMsg,
  });
  confirmBtn1.value.content = '确定';
  confirmBtn1.value.loading = false;
  if (res.code === 0) {
    showDivDialog1.value = false;
    MessagePlugin.success('审批成功');
    fetchData();
  } else {
    MessagePlugin.error('审批失败');
  }
};
const selectRow = ref({
  id: '',
  content: '',
});
const showDivDialog1 = ref(false);
const checkApplyCancel = (row) => {
  selectRow.value.id = row.id;
  selectRow.value.content = '';
  showDivDialog1.value = true;
};
const confirmBtn1 = ref({
  content: '确定',
  theme: 'primary',
  loading: false,
});
const saveRefApply = () => {
  if (!selectRow.value.content) {
    MessagePlugin.warning('请填写拒绝原因');
    return;
  }
  confirmBtn1.value.content = '提交中...';
  confirmBtn1.value.loading = true;
  checkApply(
    {
      id: selectRow.value.id,
    },
    '0',
    selectRow.value.content,
  );
};

//end
//加载字典数据
const applyStatus = ref({});
const applyStatusList = ref([]);
const applyType = ref({});
const applyTypeList = ref([]);
const companyMap = ref({});
const companyList = ref([]);
const initDicts = async () => {
  let res = await dicVals('SP_SHOP_APPLYSTATUS');
  applyStatusList.value = res.data;
  res.data.forEach((row) => {
    applyStatus.value[row.value] = row.label;
  });

  let res1 = await dicVals('SP_SHOP_APPLYTYPE');
  applyTypeList.value = res1.data;
  res1.data.forEach((row) => {
    applyType.value[row.value] = row.label;
  });

  let res2 = await dicVals('SP_SHOP_EXPRESSCOMPANY');
  companyList.value = res2.data;
  res2.data.forEach((row) => {
    companyMap.value[row.value] = row.label;
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
    colKey: 'type',
    title: '售后申请信息',
    align: 'left',
    fixed: 'left',
  },
  {
    width: 150,
    colKey: 'imgs',
    title: '图片说明',
    ellipsis: false,
  },
  {
    width: 180,
    colKey: 'goods',
    title: '售后商品',
    ellipsis: false,
  },
  {
    width: 100,
    colKey: 'orderId',
    title: '关联订单',
  },
  // {
  //   width: 80,
  //   colKey: 'price',
  //   title: '退款金额',
  //   align: 'center',
  // },
  {
    width: 120,
    colKey: 'userid',
    title: '用户信息',
    align: 'left',
  },
  {
    width: 80,
    colKey: 'status',
    title: '售后进度',
    align: 'center',
  },
  {
    width: 120,
    colKey: 'createTime',
    title: '申请时间',
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
  //phone: '',
  orderId: '',
  status: '',
  id: '',
});
const onPageChange = async (pageInfo) => {
  pagination.value.current = pageInfo.current;
  pagination.value.pageSize = pageInfo.pageSize;
  await fetchData();
};
const clearName = async (index) => {
  if (index === 0) {
    params.orderId = '';
  } else if (index === 2) {
    params.id = '';
  } else if (index === 3) {
    params.status = '';
  }
  pagination.value.current = 1;
  await fetchData();
};
const fetchData = async () => {
  data.value = [];
  dataLoading.value = true;
  try {
    let res = await queryAftersalesList({
      size: pagination.value.pageSize,
      current: pagination.value.current,
      orderId: params.orderId,
      id: params.id,
      status: params.status,
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
  ._star {
    color: var(--td-text-color-placeholder);
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
