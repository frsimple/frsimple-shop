<template>
  <div>
    <div class="sp-role-left">
      <t-card :bordered="false">
        <div class="sp-role-left-header">
          <t-row>
            <t-col :span="2">
              <t-button v-if="authAdd" @click="addRow(null)">新增一级分类</t-button>
            </t-col>
            <t-col :flex="1" :span="1" :offset="9">
              <t-button theme="default" variant="outline" @click="firstFetch"
                ><template #icon>
                  <t-icon name="refresh"></t-icon>
                </template>
                刷新</t-button
              >
            </t-col>
          </t-row>
        </div>
        <t-space style="width: 60%; margin-bottom: 8px">
          <t-alert theme="info">
            <template #message>
              <span style="font-weight: 700">最多支持三级分类</span>
            </template>
          </t-alert>
        </t-space>
        <t-enhanced-table
          row-key="id"
          :data="data"
          :max-height="'calc(98vh - 235px)'"
          :columns="columns"
          :tree="{
            childrenKey: 'children',
            treeNodeColumnIndex: 0,
          }"
          :loadingProps="{ size: '23px', text: '加载中...' }"
          :table-layout="'fixed'"
          :loading="dataLoading"
        >
          <template #operation="{ row }">
            <t-button
              v-if="authAdd && row.level < 3"
              size="small"
              variant="outline"
              theme="primary"
              @click="addRow(row)"
              >新增下级分类</t-button
            >
            <t-button v-if="authEdit" size="small" variant="outline" theme="default" @click="editRow(row)"
              >修改</t-button
            >
            <t-popconfirm theme="danger" content="是否确认删除？" @confirm="delRow(row)">
              <t-button
                v-if="authDel"
                :disabled="row.parentId === '0' || (row.children && row.children.length != 0)"
                size="small"
                variant="outline"
                theme="danger"
                >删除</t-button
              >
            </t-popconfirm>
          </template>
          <template #url="{ row }">
            <t-image-viewer :images="[row.url]">
              <template #trigger="{ open }">
                <div class="tdesign-demo-image-viewer__ui-image">
                  <img alt="test" :src="row.url" class="tdesign-demo-image-viewer__ui-image--img" mode="aspectFill" />
                  <div class="tdesign-demo-image-viewer__ui-image--hover" @click="open">
                    <span><BrowseIcon size="1.4em" /> 预览</span>
                  </div>
                </div>
              </template>
            </t-image-viewer>
          </template>
        </t-enhanced-table>
      </t-card>
    </div>
    <!-- 新增/修改角色 -->
    <t-dialog
      v-model:visible="visibleModal"
      width="600"
      :closeOnOverlayClick="false"
      :header="opt === 'add' ? '新增' : '修改'"
      mode="modal"
      draggable
      :confirm-btn="saveBtn"
      :on-confirm="onSubmit"
    >
      <template #body>
        <t-form ref="form" :label-align="'top'" :data="classForm" :layout="'inline'" :rules="rules">
          <t-form-item v-if="opt === 'edit'" label="分类编号" name="id">
            <t-input v-model="classForm.id" :style="{ width: '400px' }" :disabled="true"></t-input>
          </t-form-item>
          <t-form-item label="上级分类" name="parentname">
            <t-input v-model="classForm.parentname" :style="{ width: '400px' }" :disabled="true"></t-input>
          </t-form-item>
          <t-form-item label="分类图片" name="url" :style="{ width: '400px' }">
            <t-upload
              v-model="classForm.file"
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
          <t-form-item label="分类名称" name="name">
            <t-input v-model="classForm.name" :style="{ width: '400px' }" placeholder="请输入组织名称"></t-input>
          </t-form-item>
          <t-form-item label="排序" name="sort">
            <t-input v-model="classForm.sort" :style="{ width: '400px' }" placeholder="请输入排序"></t-input>
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
import { ref, onMounted, computed, reactive } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import { queryTreeList, addClass, editClass, delClass } from '@/api/shop/good/class';
import { BrowseIcon } from 'tdesign-icons-vue-next';
import { useUserStore } from '@/store';
import { commonUploadImg } from '@/api/common';
//权限控制
const userStore = useUserStore();
const authAdd = computed(() => userStore.roles.includes('shop:class:add'));
const authEdit = computed(() => userStore.roles.includes('shop:class:edit'));
const authDel = computed(() => userStore.roles.includes('shop:class:del'));

//图片上传

const requestMethod = async (file) => {
  let form = new FormData();
  form.append('file', file.raw);
  try {
    let res = await commonUploadImg(form);
    if (res.code === 0) {
      classForm.url = res.msg;
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

const firstFetch = async () => {
  pagination.value.current = 1;
  await fetchData();
};

//新增/修改弹窗start
const visibleModal = ref(false);
const classForm = reactive({
  id: '',
  name: '',
  parentid: '',
  parentname: '',
  sort: '',
  tenantid: '',
  level: null,
  file: [],
  url: '',
});
const form = ref(null);
const saveBtn = reactive({
  content: '保存',
  loading: false,
});
const rules = {
  name: [{ required: true, message: '请输入分类名称', type: 'error' }],
  sort: [{ required: true, message: '请输入排序', type: 'error' }],
  url: [{ required: true, message: '请上传分类图片', type: 'error' }],
};
const onSubmit = async () => {
  let result = await form.value.validate();
  if (typeof result !== 'object' && result) {
    saveBtn.content = '保存中...';
    saveBtn.loading = true;
    let submitForm = {
      name: classForm.name,
      parentid: classForm.parentid,
      sort: classForm.sort,
      url: classForm.url,
      id: null,
      level: null,
    };
    if (opt.value === 'add') {
      submitForm.level = classForm.level;
      try {
        let result1 = await addClass(submitForm);
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
      submitForm.id = classForm.id;
      try {
        let result1 = await editClass(submitForm);
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
    width: 220,
    colKey: 'name',
    title: '分类名称',
    ellipsis: true,
  },
  // {
  //   width: 120,
  //   colKey: 'id',
  //   title: '分类编号',
  //   align: 'center',
  // },
  {
    width: 120,
    colKey: 'url',
    title: '分类图片',
    align: 'center',
  },
  {
    width: 100,
    colKey: 'sort',
    title: '排序',
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
const addRow = async (row) => {
  opt.value = 'add';
  form.value.reset();
  if (null == row) {
    classForm.id = '';
    classForm.parentid = '0';
    classForm.tenantid = '';
    classForm.parentname = '顶级';
    classForm.level = 1;
  } else {
    classForm.id = '';
    classForm.parentid = row.id;
    classForm.parentname = row.name;
    classForm.level = row.level + 1;
  }
  classForm.file = [];
  classForm.url = '';
  visibleModal.value = true;
};
const editRow = async (row) => {
  opt.value = 'edit';
  form.value.reset();
  classForm.id = row.id;
  classForm.name = row.name;
  classForm.parentid = row.parentid;
  classForm.sort = row.sort;
  classForm.parentname = row.parentname;
  classForm.file = [{ name: '', url: row.url }];
  classForm.url = row.url;
  visibleModal.value = true;
};
const delRow = async (row) => {
  delClass(row.id)
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
    let res = await queryTreeList();
    if (res.code === 0) {
      data.value = res.data;
      //pagination.value.total = res.data.total;
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

.t-alert {
  padding: 6px 12px !important;
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
    padding-bottom: 30px;
  }
}
</style>

<style scoped>
.tdesign-demo-image-viewer__ui-image {
  width: 80px;
  height: 80px;
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
  width: 80px;
  height: 80px;
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
  width: 80px;
  height: 80px;
  margin: 10px;
  border: 4px solid var(--td-bg-color-secondarycontainer);
  border-radius: var(--td-radius-medium);
}
</style>
