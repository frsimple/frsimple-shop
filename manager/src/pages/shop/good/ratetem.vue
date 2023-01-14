<template>
  <div class="main">
    <div class="top">
      <t-row>
        <t-col :span="12">
          <div>
            <t-space>
              <t-button @click="addRow">新增运费模板</t-button>
              <t-button theme="default" variant="outline" shape="circle" @click="refData">
                <template #icon> <t-icon name="refresh"></t-icon></template>
              </t-button>
            </t-space>
          </div>
        </t-col>
      </t-row>
    </div>
    <t-loading text="加载中..." size="23px" v-if="dataLoading"></t-loading>
    <div v-if="!dataLoading" style="padding-left: 5px; padding-right: 5px">
      <t-row :gutter="[15, 15]">
        <t-col :span="3" v-for="(item, index) in dataList" :key="index">
          <t-card :title="item.name" :bordered="false" :style="{ height: '320px' }">
            <template #actions>
              <t-space>
                <t-button theme="primary" variant="outline" size="small" @click="editRow(item)">修改</t-button>
                <t-popconfirm theme="danger" content="是否确认删除？" @confirm="delRow(item)">
                  <t-button v-if="authDel" theme="default" share="circle" size="small">删除</t-button>
                </t-popconfirm>
              </t-space>
            </template>
            <template #content>
              <div class="info-block">
                <div class="info-item">
                  <h1>发货地区</h1>
                  <span :class="{ true: 'inProgress' }"> {{ item.sendRegName }} </span>
                </div>
                <div class="info-item">
                  <h1>发货时效</h1>
                  <span :class="{ true: 'inProgress' }"> {{ sendTimeDic[item.sendtime] }} </span>
                </div>
                <div class="info-item">
                  <h1>邮费类型</h1>
                  <span :class="{ true: 'inProgress' }">
                    {{
                      item.type === '01' ? (item.paytype === 'az' ? '自定义邮费|按重量' : '自定义邮费|按件数') : '包邮'
                    }}
                  </span>
                </div>
                <div class="info-item">
                  <h1>暂不发货地区</h1>
                  <t-tooltip :content="!item.nosend['value'] ? '无' : item.nosend['label']">
                    <span :class="{ true: 'inProgress' }">
                      {{ !item.nosend['value'] ? '无' : item.nosend['label'] }}
                    </span>
                  </t-tooltip>
                </div>
              </div>
            </template>
          </t-card>
        </t-col>
      </t-row>
    </div>

    <!-- 新增修改模板 -->
    <t-dialog
      v-model:visible="visibleModal"
      width="860"
      :closeOnOverlayClick="false"
      :header="opt === 'add' ? '新增' : '修改'"
      mode="modal"
      draggable
      :top="50"
      :confirm-btn="saveBtn"
      :on-confirm="onSubmit"
    >
      <template #body>
        <div style="max-height: 700px; overflow-y: scroll; padding-bottom: 30px">
          <t-form ref="form" :label-align="'right'" :data="temForm" :layout="'vertical'" :rules="rules">
            <t-form-item label="模板标题" name="name">
              <t-input
                v-model="temForm.name"
                :maxlength="10"
                show-limit-number
                :style="{ width: '400px' }"
                placeholder="请输入模板标题"
                clearable
              >
              </t-input>
            </t-form-item>
            <t-form-item label="发货地区" name="sendregion" :style="{ width: '400px' }">
              <t-cascader
                v-model="temForm.sendregion"
                :options="treeData"
                valueType="full"
                placeholder="请选择发货地区"
                clearable
              />
            </t-form-item>
            <t-form-item label="发货时效" name="sendtime" :style="{ width: '250px' }">
              <t-select v-model="temForm.sendtime" placeholder="请选择发货时效" clearable>
                <t-option v-for="obj in sendTimeLis" :key="obj.value" :label="obj.label" :value="obj.value" />
              </t-select>
            </t-form-item>
            <t-form-item label="邮费类型" name="type">
              <t-radio-group
                v-model="temForm.type"
                :options="[
                  { value: '00', label: '包邮' },
                  { value: '01', label: '自定义邮费' },
                ]"
                @change="postageChange"
              ></t-radio-group>
            </t-form-item>
            <t-form-item v-if="temForm.type === '01'" label="计价方式" name="paytype">
              <t-radio-group
                v-model="temForm.paytype"
                :options="[
                  { value: 'az', label: '按重量' },
                  { value: 'aj', label: '按件数' },
                ]"
              ></t-radio-group>
            </t-form-item>
            <t-form-item v-if="temForm.type === '01'" label="默认运费">
              <t-space size="10">
                <t-input-number
                  theme="column"
                  :min="0"
                  style="width: 80px"
                  placeholder=""
                  v-model="temForm.defaultJson.param1"
                >
                </t-input-number>
                <span :style="{ lineHeight: '32px' }"
                  >&nbsp;{{ temForm.paytype === 'aj' ? '件内' : 'kg内' }}&nbsp;</span
                >
                <t-input-number
                  v-model="temForm.defaultJson.param2"
                  theme="column"
                  align="center"
                  :min="0"
                  style="width: 100px"
                  placeholder=""
                ></t-input-number
                ><span :style="{ lineHeight: '32px' }">&nbsp;元，每增加&nbsp;</span>
                <t-input-number
                  v-model="temForm.defaultJson.param3"
                  theme="column"
                  align="center"
                  :min="0"
                  style="width: 80px"
                  placeholder=""
                ></t-input-number
                ><span :style="{ lineHeight: '32px' }"
                  >&nbsp;{{ temForm.paytype === 'aj' ? '件，增加' : 'kg，增加' }}&nbsp;</span
                >
                <t-input-number
                  v-model="temForm.defaultJson.param4"
                  theme="column"
                  align="center"
                  :min="0"
                  style="width: 120px"
                  placeholder=""
                ></t-input-number
                ><span :style="{ lineHeight: '32px' }">&nbsp;元&nbsp;</span>
              </t-space>
            </t-form-item>
            <t-card v-if="temForm.type === '01'" class="pd" :bordered="false">
              <template #title>
                <span class="title">指定地区运费</span>
              </template>
              <template #actions>
                <t-space>
                  <t-button theme="primary" variant="outline" @click="addZdJson">添加</t-button>
                </t-space></template
              >
              <template #content>
                <t-list :split="true" style="max-height: 160px">
                  <t-list-item v-for="item in temForm.zdJson" :key="item.id">
                    <t-list-item-meta>
                      <template #title>
                        <t-cascader
                          v-model="item.param0"
                          multiple
                          value-mode="parentFirst"
                          :options="treeData"
                          placeholder="请选择地区"
                        />
                      </template>
                      <template #description>
                        <t-space size="10">
                          <t-input-number
                            v-model="item.param1"
                            theme="column"
                            :min="0"
                            style="width: 80px"
                            placeholder=""
                          >
                          </t-input-number>
                          <span :style="{ lineHeight: '32px' }"
                            >&nbsp;{{ temForm.paytype === 'aj' ? '件内' : 'kg内' }}&nbsp;</span
                          >
                          <t-input-number
                            v-model="item.param2"
                            theme="column"
                            align="center"
                            :min="0"
                            style="width: 100px"
                            placeholder=""
                          ></t-input-number
                          ><span :style="{ lineHeight: '32px' }">&nbsp;元，每增加&nbsp;</span>
                          <t-input-number
                            v-model="item.param3"
                            theme="column"
                            align="center"
                            :min="0"
                            style="width: 80px"
                            placeholder=""
                          ></t-input-number
                          ><span :style="{ lineHeight: '32px' }"
                            >&nbsp;{{ temForm.paytype === 'aj' ? '件，增加' : 'kg，增加' }}&nbsp;</span
                          >
                          <t-input-number
                            v-model="item.param4"
                            theme="column"
                            align="center"
                            :min="0"
                            style="width: 120px"
                            placeholder=""
                          ></t-input-number
                          ><span :style="{ lineHeight: '32px' }">&nbsp;元&nbsp;</span>
                        </t-space>
                      </template>
                    </t-list-item-meta>
                    <template #action>
                      <t-button variant="text" theme="primary" @click="delJsonRow(0, item.id)"> 删除 </t-button>
                    </template>
                  </t-list-item></t-list
                >
              </template>
            </t-card>
            <t-card
              v-if="temForm.type === '01'"
              class="pd"
              :bordered="false"
              :subtitle="'（金额/件数满足任一条件即可包邮）'"
            >
              <template #title>
                <span class="title">满足条件包邮</span>
              </template>
              <template #actions>
                <t-space>
                  <t-button theme="primary" variant="outline" @click="addConJson">添加</t-button>
                </t-space></template
              >
              <template #content>
                <t-list :split="true" style="max-height: 160px">
                  <t-list-item v-for="item in temForm.conJson" :key="item.id">
                    <t-space>
                      <div style="width: 260px">
                        <t-cascader
                          v-model="item.param0"
                          multiple
                          value-mode="parentFirst"
                          :options="treeData"
                          placeholder="请选择地区"
                        ></t-cascader>
                      </div>

                      <t-input-number
                        v-model="item.param1"
                        theme="column"
                        align="center"
                        :min="0"
                        style="width: 150px"
                        placeholder="请输入金额"
                      >
                      </t-input-number>
                      <t-input-number
                        v-model="item.param2"
                        theme="column"
                        align="center"
                        :min="0"
                        style="width: 150px"
                        placeholder="请输入件数"
                      ></t-input-number>
                    </t-space>
                    <template #action>
                      <t-button variant="text" theme="primary" @click="delJsonRow(1, item.id)"> 删除 </t-button>
                    </template>
                  </t-list-item></t-list
                >
              </template>
            </t-card>
            <t-form-item label="暂不发货地区" name="nosend">
              <t-cascader
                ref="nosendRef"
                v-model="temForm.nosend"
                multiple
                value-mode="parentFirst"
                :options="treeData"
                placeholder="请选择暂不发货地区"
                @change="changeNoSend"
              />
            </t-form-item>
          </t-form>
        </div>
      </template>
    </t-dialog>
  </div>
</template>

<script lang="ts">
export default {
  name: 'rateTem',
};
</script>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import { queryList, addTem, editTem, delTem } from '@/api/shop/good/ratetem';
import { regTree, dicVals } from '@/api/common';
import { ResData } from '@/interface';
import { useUserStore } from '@/store';

//权限控制
const userStore = useUserStore();
const authAdd = computed(() => userStore.roles.includes('shop:ratetem:add'));
const authEdit = computed(() => userStore.roles.includes('shop:ratetem:edit'));
const authDel = computed(() => userStore.roles.includes('shop:ratetem:del'));

//删除数据
const delRow = async (row) => {
  delTem(row.id)
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

//加载字典数据
const sendTimeDic = ref({});
const sendTimeLis = ref([]);
const initDicts = async () => {
  let res: ResData = await dicVals('SP_SHOP_SENDTIME');
  sendTimeLis.value = res.data;
  res.data.forEach((row) => {
    sendTimeDic.value[row.value] = row.label;
  });
};

//修改暂不发货地区
const nosendRef = ref(null);
const changeNoSend = (value, context) => {
  console.log(context);
};
//新增/修改运费模板
const addZdJson = async () => {
  temForm.zdJson.push({
    id: Math.random(),
    param0: '',
    param1: '',
    param2: '',
    param3: '',
    param4: '',
  });
};
const addConJson = async () => {
  temForm.conJson.push({
    id: Math.random(),
    param0: '',
    param1: '',
    param2: '',
  });
};
const delJsonRow = async (type: number, id) => {
  if (type === 0) {
    temForm.zdJson = temForm.zdJson.filter((row) => {
      if (row.id !== id) {
        return row;
      }
    });
  } else if (type === 1) {
    temForm.conJson = temForm.conJson.filter((row) => {
      if (row.id !== id) {
        return row;
      }
    });
  }
};
const postageChange = async () => {
  if (temForm.type === '00') {
    temForm.conJson = [];
    temForm.zdJson = [];
  } else if (temForm.type === '01') {
  }
};
const visibleModal = ref(false);
const saveBtn = reactive({
  content: '保存',
  loading: false,
});
const opt = ref('add');

//修改数据
const editRow = async (item) => {
  opt.value = 'edit';
  form.value.reset();
  visibleModal.value = true;
  temForm.id = item.id;
  temForm.name = item.name;
  temForm.sendregion = item.sendregion.split(',');
  temForm.sendtime = item.sendtime;
  temForm.type = item.type;
  temForm.paytype = item.paytype;
  temForm.nosend = !item.nosend['value'] ? [] : item.nosend['value'].split(',');
  temForm.zdJson = [];
  temForm.conJson = [];
  if (temForm.type === '01') {
    if (item.payjson.length !== 0) {
      item.payjson.forEach((row) => {
        if (typeof row.param0 == 'string') {
          temForm.defaultJson.param1 = row.param1;
          temForm.defaultJson.param2 = row.param2;
          temForm.defaultJson.param3 = row.param3;
          temForm.defaultJson.param4 = row.param4;
        } else {
          temForm.zdJson.push({
            param0: row.param0,
            param1: row.param1,
            param2: row.param2,
            param3: row.param3,
            param4: row.param4,
          });
        }
      });
    }
    if (item.conjson.length !== 0) {
      item.conjson.forEach((row) => {
        temForm.conJson.push({
          param0: row.param0,
          param1: row.param1,
          param2: row.param2,
        });
      });
    }
  }
};
const onSubmit = async () => {
  let result = await form.value.validate();
  if (typeof result !== 'object' && result) {
    if (temForm.type === '01') {
      if (
        !temForm.defaultJson.param1 ||
        temForm.defaultJson.param1 <= 0 ||
        !temForm.defaultJson.param2 ||
        temForm.defaultJson.param2 <= 0 ||
        !temForm.defaultJson.param3 ||
        temForm.defaultJson.param3 <= 0 ||
        !temForm.defaultJson.param4 ||
        temForm.defaultJson.param4 <= 0
      ) {
        MessagePlugin.error('请输入默认运费');
        return false;
      }
      if (temForm.zdJson.length != 0) {
        let isReturn = true;
        temForm.zdJson.forEach((row) => {
          if (
            !row.param0 ||
            !row.param1 ||
            row.param1 <= 0 ||
            !row.param2 ||
            row.param2 <= 0 ||
            !row.param3 ||
            row.param3 <= 0 ||
            !row.param4 ||
            row.param4 <= 0
          ) {
            MessagePlugin.error('请输入指定地区运费');
            isReturn = false;
          }
        });
        if (!isReturn) {
          return isReturn;
        }
      }
      if (temForm.conJson.length != 0) {
        let isReturn = true;
        temForm.conJson.forEach((row) => {
          if (!row.param0 || !row.param1 || row.param1 <= 0 || !row.param2 || row.param2 <= 0) {
            MessagePlugin.error('请输入满足包邮条件地区/件数/金额');
            isReturn = false;
          }
        });
        if (!isReturn) {
          return isReturn;
        }
      }
    }
    saveBtn.content = '保存中...';
    saveBtn.loading = true;
    //开始组装模板信息进行
    let subForm = {
      id: null,
      name: temForm.name,
      type: temForm.type,
      paytype: temForm.paytype,
      sendtime: temForm.sendtime,
      sendregion: temForm.sendregion.join(','),
      payjson: [],
      conjson: [],
      nosendArray: temForm.nosend.join(','),
    };
    if (temForm.type === '01') {
      //默认运费
      subForm.payjson.push({
        param0: 'default',
        param1: temForm.defaultJson.param1,
        param2: temForm.defaultJson.param2,
        param3: temForm.defaultJson.param3,
        param4: temForm.defaultJson.param4,
      });
      //循环放入指定地区运费
      temForm.zdJson.forEach((row) => {
        subForm.payjson.push({
          param0: row.param0,
          param1: row.param1,
          param2: row.param2,
          param3: row.param3,
          param4: row.param4,
        });
      });
      //循环放入满足条件包邮数据
      temForm.conJson.forEach((row) => {
        subForm.conjson.push({
          param0: row.param0,
          param1: row.param1,
          param2: row.param2,
        });
      });
    } else {
      subForm.paytype = null;
      subForm.payjson = [];
      subForm.conjson = [];
    }
    try {
      let result;
      if (opt.value === 'add') {
        result = await addTem(subForm);
      } else {
        subForm.id = temForm.id;
        result = await editTem(subForm);
      }
      if (result.code === 0) {
        visibleModal.value = false;
        await fetchData();
        MessagePlugin.success('保存成功');
      } else {
        MessagePlugin.error('保存失败：' + result.msg);
      }
    } catch (error) {
      MessagePlugin.error('保存失败');
    } finally {
      saveBtn.content = '保存';
      saveBtn.loading = false;
    }
  }
};
const form = ref(null);
const temForm = reactive({
  id: '',
  name: '',
  sendregion: [],
  sendtime: '',
  type: '',
  nosend: [],
  paytype: '',
  zdJson: [],
  conJson: [],
  defaultJson: {
    param1: null,
    param2: null,
    param3: null,
    param4: null,
  },
});
const rules = {
  name: [{ required: true, message: '请输入运费模板标题', type: 'error' }],
  sendregion: [{ required: true, message: '请选择发货地区', type: 'error' }],
  sendtime: [{ required: true, message: '请选择发货时效', type: 'error' }],
  type: [{ required: true, message: '请选择邮费类型', type: 'error' }],
  paytype: [{ required: true, message: '请选择计价方式', type: 'error' }],
};

//新增模板
const addRow = async () => {
  opt.value = 'add';
  form.value.reset();
  temForm.id = '';
  temForm.conJson = [];
  temForm.zdJson = [];
  temForm.defaultJson = {
    param1: null,
    param2: null,
    param3: null,
    param4: null,
  };
  visibleModal.value = true;
};

//刷新数据列表
const refData = async () => {
  await fetchData();
};

//查询模板列表信息
const dataList = ref([]);
const dataLoading = ref(false);
const fetchData = async () => {
  dataList.value = [];
  dataLoading.value = true;
  try {
    let res = await queryList({});
    if (res.code === 0) {
      dataList.value = res.data;
    }
  } catch (er) {
  } finally {
    dataLoading.value = false;
  }
};

//加载地区树形
const treeData = ref([]);
const loadTreeData = async () => {
  let res: ResData = await regTree();
  treeData.value = res.data;
};

//首次加载调用
onMounted(async () => {
  initDicts();
  await fetchData();
  await loadTreeData();
});
</script>

<style lang="less" scoped>
@import '@/style/variables.less';
.main {
  .top {
    padding: 15px;
  }
  .pd {
    background: var(--td-bg-color-page);
    border-radius: 8px;
    margin: 20px;
  }
}
</style>
