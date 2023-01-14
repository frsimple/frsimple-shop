<template>
  <div>
    <div class="sp-role-left">
      <t-card :bordered="false">
        <div class="sp-role-left-header">
          <t-row>
            <t-col :span="6">
              <t-button v-if="authAdd" @click="addRow">新增</t-button>
            </t-col>
            <t-col :span="6">
              <t-row :gutter="10">
                <t-col :flex="1" :span="6" :offset="10">
                  <t-button theme="default" variant="outline" @click="firstFetch">
                    <template #icon>
                      <t-icon name="refresh"></t-icon>
                    </template>
                    刷新</t-button
                  >
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
          :loadingProps="{ size: '23px', text: '加载中...' }"
          :loading="dataLoading"
        >
          <template #status="{ row }">
            <t-tag v-if="row.status === '1'" :theme="'primary'" shape="round">{{ statusDic[row.status] }}</t-tag>
            <t-tag v-if="row.status === '0'" shape="round">{{ statusDic[row.status] }}</t-tag>
          </template>
          <template #operation="{ row }">
            <t-popconfirm
              :content="row.status === '1' ? '是否确定停用该消息?' : '启用后其他消息状态将被自动停用，是否启用该消息?'"
              @confirm="openOrCloseMsg(row)"
            >
              <t-button v-if="authDel" size="small" variant="outline" theme="primary">
                {{ row.status === '1' ? '停用' : '启用' }}
              </t-button>
            </t-popconfirm>
            <t-button v-if="authEdit" size="small" variant="outline" theme="default" @click="editRow(row)"
              >修改</t-button
            >
            <t-popconfirm theme="danger" content="是否确认删除？" @confirm="delRow(row)">
              <t-button v-if="authDel" size="small" variant="base" theme="default">删除</t-button>
            </t-popconfirm>
          </template>
        </t-table>
      </t-card>
    </div>
    <!-- 新增/修改角色 -->
    <t-dialog
      v-model:visible="visibleModal"
      width="500"
      :closeOnOverlayClick="false"
      :header="opt === 'add' ? '新增消息' : '修改消息'"
      mode="modal"
      draggable
      :confirm-btn="saveBtn"
      :on-confirm="onSubmit"
    >
      <template #body>
        <t-form ref="form" :label-align="'right'" :data="msgForm" :layout="'inline'" :rules="rules">
          <t-form-item label="消息内容" name="content">
            <t-textarea
              v-model="msgForm.content"
              :style="{ width: '300px' }"
              :autosize="{ minRows: 6, maxRows: 10 }"
              placeholder="请输入消息内容"
            />
          </t-form-item>
          <t-form-item label="是否启用" name="status">
            <t-radio-group v-model="msgForm.status">
              <t-radio-button v-for="item in statusLis" :key="item.value" :value="item.value">{{
                item.label
              }}</t-radio-button>
            </t-radio-group>
          </t-form-item>
        </t-form>
      </template>
    </t-dialog>
  </div>
</template>

<script lang="ts">
export default {
  name: 'ListTenant',
};
</script>

<script setup lang="ts">
import { ref, onMounted, computed, reactive, getCurrentInstance } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import {
  queryMessageList,
  addMessage,
  editMessage,
  delMessage,
  openMessage,
  closeMessage,
} from '@/api/shop/manager/message';

import { useUserStore } from '@/store';
import { dicVals } from '@/api/common';

//字典初始化
const statusDic = ref({});
const statusLis = ref([]);
const initDicts = async () => {
  let res = await dicVals('SP_ISUSE');
  statusLis.value = res.data;
  res.data.forEach((row) => {
    statusDic.value[row.value] = row.label;
  });
};

//获取table全局高度
const tableHeight = getCurrentInstance().appContext.config.globalProperties.$tableHeight;

//权限控制
const userStore = useUserStore();
const authAdd = computed(() => userStore.roles.includes('shop:message:add'));
const authEdit = computed(() => userStore.roles.includes('shop:message:edit'));
const authDel = computed(() => userStore.roles.includes('shop:message:del'));

const firstFetch = async () => {
  pagination.value.current = 1;
  await fetchData();
};

//新增/修改弹窗start
const visibleModal = ref(false);
const msgForm = reactive({
  id: '',
  content: '',
  status: '',
});
const form = ref(null);
const saveBtn = reactive({
  content: '保存',
  loading: false,
});

const rules = {
  content: [{ required: true, message: '请输入消息内容', type: 'error' }],
  status: [{ required: true, message: '请选择是否启用', type: 'error' }],
};
const onSubmit = async () => {
  let result = await form.value.validate();
  if (typeof result !== 'object' && result) {
    saveBtn.content = '保存中...';
    saveBtn.loading = true;
    let submitForm = {
      content: msgForm.content,
      status: msgForm.status,
      id: null,
    };
    if (opt.value === 'add') {
      try {
        let result1 = await addMessage(submitForm);
        if (result1.code === 0) {
          visibleModal.value = false;
          await fetchData();
          MessagePlugin.success('保存成功');
        } else {
          MessagePlugin.error('保存失败：' + result1.msg);
        }
      } catch (error) {
        MessagePlugin.error('保存失败');
      } finally {
        saveBtn.content = '保存';
        saveBtn.loading = false;
      }
    } else {
      submitForm.id = msgForm.id;
      try {
        let result1 = await editMessage(submitForm);
        if (result1.code === 0) {
          visibleModal.value = false;
          fetchData();
          MessagePlugin.success('保存成功');
        } else {
          MessagePlugin.error('保存失败：' + result1.msg);
        }
      } catch (error) {
        MessagePlugin.error('保存失败');
      } finally {
        saveBtn.content = '保存';
        saveBtn.loading = false;
      }
    }
  }
};
//新增/修改弹窗end

//左侧角色菜单列表数据start
const data = ref([]);
const columns = [
  {
    width: 400,
    colKey: 'content',
    title: '消息内容',
    align: 'left',
  },
  {
    width: 100,
    colKey: 'status',
    title: '是否启用',
    ellipsis: true,
    align: 'center',
  },
  {
    width: 100,
    colKey: 'createtime',
    title: '创建时间',
  },
  {
    colKey: 'operation',
    title: '操作',
    width: 100,
    cell: 'operation',
    align: 'center',
    fixed: 'right',
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
});
const onPageChange = async (pageInfo) => {
  pagination.value.current = pageInfo.current;
  pagination.value.pageSize = pageInfo.pageSize;
  await fetchData();
};
const clearName = async () => {
  params.name = '';
  pagination.value.current = 1;
  await fetchData();
};
const opt = ref('add');
const addRow = async () => {
  opt.value = 'add';
  form.value.reset();
  msgForm.id = '';
  msgForm.status = '0';
  visibleModal.value = true;
};
const editRow = async (row) => {
  opt.value = 'edit';
  form.value.reset();
  msgForm.id = row.id;
  msgForm.content = row.content;
  msgForm.status = row.status;
  visibleModal.value = true;
};
const delRow = async (row) => {
  delMessage(row.id)
    .then((res) => {
      if (res.code === 0) {
        fetchData();
        MessagePlugin.success('删除成功');
      } else {
        MessagePlugin.error('删除失败：' + res.msg);
      }
    })
    .catch((error) => {
      MessagePlugin.error('删除失败');
    });
};

const openOrCloseMsg = async (row) => {
  if (row.status === '1') {
    closeMessage(row.id)
      .then((res) => {
        if (res.code === 0) {
          fetchData();
          MessagePlugin.success('停用成功');
        } else {
          MessagePlugin.error('停用失败：' + res.msg);
        }
      })
      .catch((er) => {
        MessagePlugin.error('停用失败');
      });
  } else {
    openMessage(row.id)
      .then((res) => {
        if (res.code === 0) {
          fetchData();
          MessagePlugin.success('启用成功');
        } else {
          MessagePlugin.error('启用失败：' + res.msg);
        }
      })
      .catch((er) => {
        MessagePlugin.error('启用失败');
      });
  }
};

const fetchData = async () => {
  data.value = [];
  dataLoading.value = true;
  try {
    let res = await queryMessageList({
      size: pagination.value.pageSize,
      current: pagination.value.current,
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
onMounted(() => {
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
