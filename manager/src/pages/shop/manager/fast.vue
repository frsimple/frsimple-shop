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
                <t-col :flex="1" :span="6" :offset="4">
                  <t-input
                    placeholder="请输入标题"
                    type="search"
                    clearable
                    v-model="params.title"
                    @clear="clearName"
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
          <template #operation="{ row }">
            <t-button v-if="authEdit" size="small" variant="outline" theme="default" @click="editRow(row)"
              >修改</t-button
            >
            <t-popconfirm theme="danger" content="是否确认删除？" @confirm="delRow(row)">
              <t-button v-if="authDel" size="small" variant="outline" theme="danger">删除</t-button>
            </t-popconfirm>
          </template>
          <template #status="{ row }">
            <t-tag theme="primary" shape="round">{{ statusDic[row.status] }}</t-tag>
          </template>
          <template #type="{ row }">
            <t-tag theme="primary" shape="round">{{ typeDic[row.type] }}</t-tag>
          </template>
          <template #img="{ row }">
            <t-image-viewer :images="[row.img]">
              <template #trigger="{ open }">
                <div class="tdesign-demo-image-viewer__ui-image">
                  <img alt="test" :src="row.img" class="tdesign-demo-image-viewer__ui-image--img" mode="aspectFill" />
                  <div class="tdesign-demo-image-viewer__ui-image--hover" @click="open">
                    <span><BrowseIcon size="1.4em" /> 预览</span>
                  </div>
                </div>
              </template>
            </t-image-viewer>
          </template>
        </t-table>
      </t-card>
    </div>
    <!-- 新增/修改角色 -->
    <t-dialog
      v-model:visible="visibleModal"
      width="500"
      :closeOnOverlayClick="false"
      :header="opt === 'add' ? '新增' : '修改'"
      mode="modal"
      draggable
      :confirm-btn="saveBtn"
      :on-confirm="onSubmit"
    >
      <template #body>
        <t-form ref="form" :label-align="'right'" :data="imgForm" :layout="'inline'" :rules="rules">
          <t-form-item label="排序" name="sort">
            <t-input-number
              v-model="imgForm.sort"
              placeholder="请选择排序"
              :style="{ width: '200px' }"
            ></t-input-number>
          </t-form-item>
          <t-form-item label="标题" name="title">
            <t-input v-model="imgForm.title" :style="{ width: '400px' }" placeholder="请输入标题"></t-input>
          </t-form-item>
          <t-form-item label="图标" name="img" :style="{ width: '800px' }">
            <t-upload
              v-model="imgForm.file"
              :tips="'上传文件大小在 5M 以内'"
              theme="image"
              accept="image/*"
              :locale="{
                triggerUploadText: {
                  image: '请选择图片',
                },
              }"
              :before-upload="beforeUpload"
              :request-method="requestMethod"
            >
            </t-upload>
          </t-form-item>
          <t-form-item label="类型" name="type" :style="{ width: '400px' }">
            <t-radio-group v-model="imgForm.type">
              <t-radio-button v-for="item in typeLis" :key="item.value" :value="item.value">{{
                item.label
              }}</t-radio-button>
            </t-radio-group>
          </t-form-item>
          <t-form-item v-if="imgForm.type !== '00'" label="地址" name="url">
            <t-input v-model="imgForm.url" :style="{ width: '400px' }" placeholder="请输入链接地址"></t-input>
          </t-form-item>
          <t-form-item label="是否启用" name="status">
            <t-radio-group v-model="imgForm.status">
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
  name: 'MainList',
};
</script>

<script setup lang="ts">
import { ref, onMounted, computed, reactive, getCurrentInstance } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import { BrowseIcon } from 'tdesign-icons-vue-next';
import { queryMainList, addFast, ediFast, delFast } from '@/api/shop/manager/fast';
import { useUserStore } from '@/store';
import { dicVals, commonUploadImg } from '@/api/common';
//获取table全局高度
const visible = ref(false);
const tableHeight = getCurrentInstance().appContext.config.globalProperties.$tableHeight;

const statusDic = ref({});
const typeDic = ref({});
const statusLis = ref([]);
const typeLis = ref([]);
const initDicts = async () => {
  let res = await dicVals('SP_ISUSE');
  statusLis.value = res.data;
  res.data.forEach((row) => {
    statusDic.value[row.value] = row.label;
  });
  let res1 = await dicVals('SP_FASTTYPE');
  typeLis.value = res1.data;
  res1.data.forEach((row) => {
    typeDic.value[row.value] = row.label;
  });
};

//图片上传

const requestMethod = async (file) => {
  let form = new FormData();
  form.append('file', file.raw);
  try {
    let res = await commonUploadImg(form);
    if (res.code === 0) {
      imgForm.img = res.msg;
      return new Promise((resolve) => {
        resolve({ status: 'success', response: { url: res.msg } });
      });
    } else {
      MessagePlugin.error('上传失败:' + res.msg);
      return new Promise((resolve) => {
        resolve({ status: 'fail', error: '上传失败' });
      });
    }
  } catch (er) {
    MessagePlugin.error('上传失败:' + er);
    return new Promise((resolve) => {
      // resolve 参数为关键代码
      resolve({ status: 'fail', error: '上传失败' });
    });
  }
};
const beforeUpload = (file) => {
  if (file.size > 5 * 1024 * 1024) {
    MessagePlugin.warning('上传的图片不能大于5M');
    return false;
  }
  return true;
};

//权限控制
const userStore = useUserStore();
const authAdd = computed(() => userStore.roles.includes('shop:fast:add'));
const authEdit = computed(() => userStore.roles.includes('shop:fast:edit'));
const authDel = computed(() => userStore.roles.includes('shop:fast:del'));

const firstFetch = async () => {
  pagination.value.current = 1;
  await fetchData();
};

//新增/修改弹窗start
const visibleModal = ref(false);
const imgForm = reactive({
  id: '',
  title: '',
  img: '',
  file: [],
  type: '',
  url: '',
  status: '',
  sort: 0,
});
const form = ref(null);
const saveBtn = reactive({
  content: '保存',
  loading: false,
});

const rules = {
  title: [{ required: true, message: '请输入标题', type: 'error' }],
  img: [{ required: true, message: '请上传图片', type: 'error' }],
  type: [{ required: true, message: '请选择类型', type: 'error' }],
  url: [{ required: true, message: '请输入链接地址', type: 'error' }],
  status: [{ required: true, message: '请选择是否启用', type: 'error' }],
  sort: [{ required: true, message: '请选择排序', type: 'error' }],
};
const onSubmit = async () => {
  let result = await form.value.validate();
  if (typeof result !== 'object' && result) {
    saveBtn.content = '保存中...';
    saveBtn.loading = true;
    let submitForm = {
      id: null,
      title: imgForm.title,
      img: imgForm.img,
      url: imgForm.url,
      type: imgForm.type,
      status: imgForm.status,
      sort: imgForm.sort,
    };
    if (opt.value === 'add') {
      try {
        let result1 = await addFast(submitForm);
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
      submitForm.id = imgForm.id;
      try {
        let result1 = await ediFast(submitForm);
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
    width: 60,
    colKey: 'sort',
    title: '排序',
    align: 'center',
  },
  {
    width: 60,
    colKey: 'title',
    title: '标题',
    align: 'left',
  },
  {
    width: 100,
    colKey: 'img',
    title: '图标',
    ellipsis: true,
    align: 'center',
  },
  {
    width: 60,
    colKey: 'type',
    title: '类型',
    align: 'center',
  },
  {
    width: 220,
    colKey: 'url',
    title: '链接地址',
    align: 'center',
  },
  {
    width: 60,
    colKey: 'status',
    title: '是否启用',
    ellipsis: true,
    align: 'center',
  },
  {
    colKey: 'operation',
    title: '操作',
    width: 200,
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
  title: '',
});
const onPageChange = async (pageInfo) => {
  pagination.value.current = pageInfo.current;
  pagination.value.pageSize = pageInfo.pageSize;
  await fetchData();
};
const clearName = async () => {
  params.title = '';
  pagination.value.current = 1;
  await fetchData();
};
const opt = ref('add');
const addRow = async () => {
  opt.value = 'add';
  form.value.reset();
  imgForm.id = '';
  imgForm.status = '1';
  imgForm.type = '01';
  imgForm.file = [];
  imgForm.img = '';
  imgForm.sort = 0;
  imgForm.url = '';
  visibleModal.value = true;
};
const editRow = async (row) => {
  opt.value = 'edit';
  form.value.reset();
  imgForm.id = row.id;
  imgForm.title = row.title;
  imgForm.url = row.url;
  imgForm.type = row.type;
  imgForm.status = row.status;
  imgForm.file = [{ name: '', url: row.img }];
  imgForm.img = row.img;
  imgForm.sort = row.sort;
  visibleModal.value = true;
};
const delRow = async (row) => {
  delFast(row.id)
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
    let res = await queryMainList({
      size: pagination.value.pageSize,
      current: pagination.value.current,
      title: params.title,
      asc: 'sort',
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

<style scoped>
.tdesign-demo-image-viewer__ui-image {
  width: 60px;
  height: 60px;
  display: inline-flex;
  position: relative;
  justify-content: center;
  align-items: center;
  border-radius: var(--td-radius-small);
  overflow: hidden;
}

.tdesign-demo-image-viewer__ui-image--hover {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  left: 0;
  top: 0;
  opacity: 0;
  background-color: rgba(0, 0, 0, 0.6);
  color: var(--td-text-color-anti);
  line-height: 22px;
  transition: 0.2s;
}

.tdesign-demo-image-viewer__ui-image:hover .tdesign-demo-image-viewer__ui-image--hover {
  opacity: 1;
  cursor: pointer;
}

.tdesign-demo-image-viewer__ui-image--img {
  width: 60px;
  height: 60px;
  cursor: pointer;
  position: absolute;
}

.tdesign-demo-image-viewer__ui-image--footer {
  padding: 0 16px;
  height: 56px;
  width: 100%;
  line-height: 56px;
  font-size: 16px;
  position: absolute;
  bottom: 0;
  color: var(--td-text-color-anti);
  background-image: linear-gradient(0deg, rgba(0, 0, 0, 0.4) 0%, rgba(0, 0, 0, 0) 100%);
  display: flex;
  box-sizing: border-box;
}

.tdesign-demo-image-viewer__ui-image--title {
  flex: 1;
}

.tdesign-demo-popup__reference {
  margin-left: 16px;
}

.tdesign-demo-image-viewer__ui-image--icons .tdesign-demo-icon {
  cursor: pointer;
}

.tdesign-demo-image-viewer__base {
  width: 60px;
  height: 60px;
  margin: 10px;
  border: 4px solid var(--td-bg-color-secondarycontainer);
  border-radius: var(--td-radius-medium);
}
</style>
