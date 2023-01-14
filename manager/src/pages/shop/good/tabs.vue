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
          <template #tabs="{ row }">
            <t-space class="tag-demo light">
              <t-tag theme="primary" variant="light" v-for="item in row.tabs.split(',')" :key="item">{{ item }}</t-tag>
            </t-space>
          </template>
          <template #operation="{ row }">
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
      height="600"
      :closeOnOverlayClick="false"
      :header="opt === 'add' ? '新增' : '修改'"
      mode="modal"
      draggable
      :confirm-btn="saveBtn"
      :on-confirm="onSubmit"
    >
      <template #body>
        <t-form ref="form" :label-align="'right'" :data="tagsForm" :layout="'inline'" :rules="rules">
          <t-form-item label="模板标题" name="title">
            <t-input v-model="tagsForm.title" :style="{ width: '300px' }" placeholder="请输入模板标题"> </t-input>
          </t-form-item>
          <t-form-item label="标签内容" name="tabs">
            <t-tag-input
              v-model="tagsForm.tabs"
              :style="{ width: '300px' }"
              clearable
              placeholder="请输入消息内容,点击回车确认"
            />
          </t-form-item>
        </t-form>
      </template>
    </t-dialog>
  </div>
</template>

<script lang="ts">
export default {
  name: 'tabsList',
};
</script>

<script setup lang="ts">
import { ref, onMounted, computed, reactive, getCurrentInstance } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import { queryList, addTabs, editTabs, delTabs } from '@/api/shop/good/tabs';

import { useUserStore } from '@/store';

//获取table全局高度
const tableHeight = getCurrentInstance().appContext.config.globalProperties.$tableHeight;

//权限控制
const userStore = useUserStore();
const authAdd = computed(() => userStore.roles.includes('shop:tabs:add'));
const authEdit = computed(() => userStore.roles.includes('shop:tabs:edit'));
const authDel = computed(() => userStore.roles.includes('shop:tabs:del'));

const firstFetch = async () => {
  pagination.value.current = 1;
  await fetchData();
};

//新增/修改弹窗start
const visibleModal = ref(false);
const tagsForm = reactive({
  id: '',
  tabs: [],
  title: '',
});
const form = ref(null);
const saveBtn = reactive({
  content: '保存',
  loading: false,
});

const rules = {
  tabs: [{ required: true, message: '请输入标签内容', type: 'error' }],
  title: [{ required: true, message: '请输入模板标题', type: 'error' }],
};
const onSubmit = async () => {
  let result = await form.value.validate();
  if (typeof result !== 'object' && result) {
    saveBtn.content = '保存中...';
    saveBtn.loading = true;
    let submitForm = {
      tabs: tagsForm.tabs.join(','),
      title: tagsForm.title,
      id: null,
      type: null,
    };
    if (opt.value === 'add') {
      submitForm.type = '01';
      try {
        let result1 = await addTabs(submitForm);
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
      submitForm.id = tagsForm.id;
      try {
        let result1 = await editTabs(submitForm);
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
    width: 100,
    colKey: 'title',
    title: '模板标题',
    ellipsis: true,
    align: 'center',
  },
  {
    width: 400,
    colKey: 'tabs',
    title: '标签内容',
    align: 'left',
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
  tagsForm.id = '';
  visibleModal.value = true;
};
const editRow = async (row) => {
  opt.value = 'edit';
  form.value.reset();
  tagsForm.id = row.id;
  tagsForm.tabs = row.tabs.split(',');
  tagsForm.title = row.title;
  visibleModal.value = true;
};
const delRow = async (row) => {
  delTabs(row.id)
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

const fetchData = async () => {
  data.value = [];
  dataLoading.value = true;
  try {
    let res = await queryList({
      type: '01',
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
  await fetchData();
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
