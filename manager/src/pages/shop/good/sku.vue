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
                    placeholder="请输入规格模板名称"
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
          <template #content="{ row }">
            <div class="info-block">
              <div class="info-item" v-for="(item, index) in row.content" :key="index">
                <h1>{{ item.value }}</h1>
                <t-tag theme="primary" shape="round"
                  ><template #content>
                    <span style="margin-left: 0px !important" v-for="(item1, index1) in item.label" :key="index1">{{
                      index1 === item.label.length - 1 ? item1.value : item1.value + '/'
                    }}</span>
                  </template>
                </t-tag>
              </div>
            </div>
          </template>
          <template #operation="{ row }">
            <t-button v-if="authEdit" size="small" variant="outline" theme="default" @click="editRow(row)"
              >修改</t-button
            >
            <t-popconfirm theme="danger" content="是否确认删除？" @confirm="delRow(row)">
              <t-button v-if="authDel" size="small" variant="outline" theme="danger">删除</t-button>
            </t-popconfirm>
          </template>
        </t-table>
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
      destroyOnClose
    >
      <template #body>
        <t-form ref="form" :label-align="'right'" :data="skuForm" :layout="'inline'" :rules="rules">
          <t-form-item label="规模模板标题" name="title">
            <t-input v-model="skuForm.title" :style="{ width: '400px' }" placeholder="请输入规模模板标题"></t-input>
          </t-form-item>
          <t-form-item label="规模模板内容" name="content" :style="{ width: '400px' }">
            <t-space>
              <t-input placeholder="请输入规格名称" v-model="skuVal" /><t-button @click="addSkuRow">新增规格</t-button>
            </t-space>
          </t-form-item>
          <t-form-item v-for="(item, index) in skuForm.content" :key="index">
            <t-card class="pd" :bordered="false" :style="{ width: '400px' }">
              <template #title>
                <span>{{ item.value }}</span>
              </template>
              <template #actions>
                <t-space>
                  <t-button theme="primary" variant="outline" shape="circle" @click="addIpt(item.id)">
                    <template #icon> <t-icon name="add"></t-icon></template>
                  </t-button>
                  <t-button theme="default" shape="circle" @click="delSkuRow(item.id)">
                    <template #icon> <t-icon name="delete"></t-icon></template>
                  </t-button> </t-space
              ></template>
              <template #content>
                <div :id="item.id">
                  <t-space
                    v-for="(item1, index1) in item.label"
                    :key="index1"
                    class="list-group-item"
                    style="margin-bottom: 5px"
                    :data-id="item1.id"
                  >
                    <t-input placeholder="请输入规格值" v-model="item1.value" /><t-button
                      theme="default"
                      shape="circle"
                      @click="delIpt(item.id, item1.id)"
                    >
                      <template #icon> <t-icon name="delete"></t-icon></template> </t-button
                    ><t-button theme="default" class="move"
                      ><template #icon><t-icon name="bulletpoint"></t-icon> </template>拖拽排序</t-button
                    ></t-space
                  >
                </div>
              </template>
            </t-card>
          </t-form-item>
        </t-form>
      </template>
    </t-dialog>
  </div>
</template>

<script lang="ts">
export default {
  name: 'SkuList',
};
</script>

<script setup lang="ts">
import { ref, onMounted, computed, reactive, getCurrentInstance, nextTick } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import { queryList, addSku, editSku, delSku } from '@/api/shop/good/sku';
import { useUserStore } from '@/store';
import Sortable from 'sortablejs';

// 创建sortable实例
const changeList = ref({});
const initSortable = (name) => {
  // 创建拖拽实例
  var sortList = new Sortable(document.getElementById(name), {
    group: 'shared',
    animation: 150, //动画
    handle: '.move', //指定拖拽目标，点击此目标才可拖拽元素(此例中设置操作按钮拖拽)
    dataIdAttr: 'data-id',
    onStart: function () {},
    // 结束拖动事件
    onEnd: function (evt) {
      if (evt.newIndex !== evt.oldIndex) {
        changeList.value[evt.from.id] = sortList.toArray();
      }
    },
  });
};

//获取table全局高度
const visible = ref(false);
const tableHeight = getCurrentInstance().appContext.config.globalProperties.$tableHeight;

//权限控制
const userStore = useUserStore();
const authAdd = computed(() => userStore.roles.includes('shop:sku:add'));
const authEdit = computed(() => userStore.roles.includes('shop:sku:edit'));
const authDel = computed(() => userStore.roles.includes('shop:sku:del'));

const firstFetch = async () => {
  pagination.value.current = 1;
  await fetchData();
};

//新增规格
const skuVal = ref('');
const cloneSku = ref([]);
const addSkuRow = async () => {
  if (skuVal.value.trim() === '') {
    MessagePlugin.warning('请输入规格名称');
    return false;
  }
  let id = Math.random().toString(16).substring(2);
  skuForm.content.push({
    id: id,
    value: skuVal.value,
    label: [
      {
        id: Math.random().toString(16).substring(2),
        value: '',
      },
    ],
  });
  nextTick(() => {
    initSortable(id);
  });
  skuVal.value = '';
};
const delSkuRow = async (id) => {
  skuForm.content = skuForm.content.filter((row) => {
    if (row.id !== id) {
      return row;
    }
  });
};
const addIpt = (id) => {
  skuForm.content = skuForm.content.filter((row) => {
    if (row.id == id) {
      row.label.push({
        id: Math.random().toString(16).substring(2),
        value: '',
      });
      return row;
    } else {
      return row;
    }
  });
  nextTick(() => {
    initSortable(id);
  });
};
const delIpt = (fromId, id) => {
  skuForm.content.filter((row) => {
    if (fromId == row.id) {
      row.label = row.label.filter((row1) => {
        if (row1.id != id) {
          return row1;
        }
      });
    }
    return row;
  });
};

//新增/修改弹窗start
const visibleModal = ref(false);
const skuForm = reactive({
  id: '',
  title: '',
  content: [],
});
const form = ref(null);
const saveBtn = reactive({
  content: '保存',
  loading: false,
});

const rules = {
  title: [{ required: true, message: '请输入规模模板标题', type: 'error' }],
};
const onSubmit = async () => {
  let result = await form.value.validate();
  if (typeof result !== 'object' && result) {
    if (skuForm.content.length == 0) {
      MessagePlugin.warning('请输入完整的规格信息');
    }
    try {
      skuForm.content.forEach((row) => {
        if (row.label.length == 0) {
          throw Error('请输入完整的规格信息');
        } else {
          row.label.forEach((row1) => {
            if (row1.value.trim() === '') {
              throw Error('请输入完整的规格信息');
            }
          });
        }
      });
    } catch (error) {
      MessagePlugin.warning('请输入完整的规格信息');
      return;
    }
    saveBtn.content = '保存中...';
    saveBtn.loading = true;
    let submitForm = {
      id: null,
      title: skuForm.title,
      content: [],
    };
    //开始处理排序内容
    skuForm.content.forEach((row) => {
      if (changeList.value[row.id]) {
        let contentObj = {
          id: row.id,
          value: row.value,
          label: [],
        };
        let labelArray = new Array();
        changeList.value[row.id].forEach((row1) => {
          row.label.forEach((row2) => {
            if (row2.id == row1) {
              labelArray.push(JSON.parse(JSON.stringify(row2)));
            }
          });
        });
        contentObj.label = labelArray;
        submitForm.content.push(contentObj);
      } else {
        submitForm.content.push(JSON.parse(JSON.stringify(row)));
      }
    });
    console.log(submitForm);
    if (opt.value === 'add') {
      try {
        let result1 = await addSku(submitForm);
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
      submitForm.id = skuForm.id;
      try {
        let result1 = await editSku(submitForm);
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
    width: 120,
    colKey: 'id',
    title: '模板ID',
    align: 'left',
  },
  {
    width: 180,
    colKey: 'title',
    title: '规模模板标题',
    align: 'left',
  },
  {
    width: 200,
    colKey: 'content',
    title: '规格模板内容',
    align: 'left',
  },
  {
    width: 80,
    colKey: 'createtime',
    title: '创建时间',
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
  skuForm.id = '';
  skuForm.content = [];
  changeList.value = {};
  visibleModal.value = true;
  await nextTick(() => {
    form.value.reset();
  });
};
const editRow = async (row) => {
  console.log(row);
  opt.value = 'edit';
  changeList.value = [];
  skuForm.id = row.id;
  skuForm.title = row.title;
  skuForm.content = [];
  skuForm.content = row.content;
  visibleModal.value = true;
  await nextTick(async () => {
    //await form.value.reset();
    row.content.forEach((row1) => {
      initSortable(row1.id);
    });
  });
};
const delRow = async (row) => {
  delSku(row.id)
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
      size: pagination.value.pageSize,
      current: pagination.value.current,
      title: params.title,
      desc: 'createtime',
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

.info-block .info-item h1 {
  width: 40px !important;
}
.info-block .info-item span {
  width: auto !important;
}
.pd {
  background: var(--td-bg-color-page);
  border-radius: 8px;
  //margin: 20px;
}
</style>
