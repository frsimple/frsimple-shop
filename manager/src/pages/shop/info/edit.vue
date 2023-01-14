<template>
  <t-drawer
    v-model:visible="addShopVisible"
    destroyOnClose
    :closeOnOverlayClick="false"
    :closeOnEscKeydown="false"
    header="修改商品"
    size="60%"
    :confirmBtn="saveBtn"
    :onConfirm="saveData"
  >
    <t-loading size="23px" text="加载中..." :loading="dataLoad" show-overlay>
      <div id="container" class="anchor-demo anchor-container-demo">
        <t-anchor id="default" container="#anchor-container" @click="handleClick">
          <t-anchor-item href="#content-1" title="基本信息" ref="anchor1" />
          <t-anchor-item href="#content-2" title="图文信息" ref="anchor2" />
          <t-anchor-item href="#content-3" title="价格库存" ref="anchor3" />
          <t-anchor-item href="#content-4" title="服务履约" ref="anchor4" />
          <t-anchor-item href="#content-5" title="营销信息" ref="anchor5" />
          <t-anchor-item href="#content-6" title="商品详情" ref="anchor6" />
        </t-anchor>
        <t-space id="anchor-container" direction="vertical" :size="0">
          <div id="content-1" class="anchor-content-1">
            <t-card class="pd" :bordered="false">
              <template #title>
                <span class="title">基本信息</span>
              </template>
              <template #content>
                <t-form ref="tform" :data="form" :rules="rules" label-width="100px">
                  <t-form-item required label="商品分类" name="cate">
                    <t-cascader
                      v-model="form.cate"
                      :keys="{ value: 'id', label: 'name' }"
                      :options="classTree"
                      placeholder="请选择所属分类"
                    ></t-cascader>
                  </t-form-item>
                  <t-form-item label="商品名称" name="name">
                    <t-input
                      v-model="form.name"
                      show-limit-number
                      :maxlength="50"
                      placeholder="请输入商品名称"
                    ></t-input>
                  </t-form-item>
                  <t-form-item label="商品标签" name="tabs">
                    <t-tagInput v-model="form.tabs" :max="5" placeholder="请输入商品标签，按下回车确定"></t-tagInput>
                  </t-form-item>
                  <t-form-item label="商品简介" name="des">
                    <t-textarea v-model="form.des" :maxlength="120" placeholder="请输入商品描述"></t-textarea>
                  </t-form-item>
                </t-form>
              </template>
            </t-card>
          </div>
          <div id="content-2" class="anchor-content-2">
            <t-card class="pd" :bordered="false">
              <template #title>
                <span class="title">图文信息</span>
              </template>
              <t-form ref="tform1" :data="form1" :rules="rules1" label-width="100px">
                <t-form-item required label="商品封面" name="file1">
                  <t-upload
                    v-model="form1.file1"
                    theme="image"
                    tips="请选择单张图片文件上传，最大不超过5M"
                    :request-method="requestMethod"
                    :sizeLimit="1024 * 5"
                    accept="image/*"
                    :onRemove="removeFile"
                    :locale="{
                      triggerUploadText: {
                        image: '请选择图片',
                      },
                    }"
                  ></t-upload>
                </t-form-item>
                <t-form-item label="商品轮播图" name="file2">
                  <t-upload
                    v-model="form1.file2"
                    theme="image"
                    :request-method="requestMethod1"
                    :onRemove="removeFile1"
                    :sizeLimit="1024 * 5"
                    tips="允许选择最多只能上传 5 张图片，单张最大不超过5M"
                    accept="image/*"
                    multiple
                    :max="5"
                  ></t-upload>
                </t-form-item>
              </t-form>
            </t-card>
          </div>
          <div id="content-3" class="anchor-content-3">
            <t-card class="pd" :bordered="false">
              <template #title>
                <span class="title">价格库存</span>
              </template>
              <template #content>
                <t-form ref="tform2" :data="form2" :rules="rules2" layout="vertical" label-width="100px">
                  <t-form-item required label="是否单规格" name="isSingle">
                    <t-radio-group v-model="form2.isSingle">
                      <t-radio value="1">是</t-radio>
                      <t-radio value="0">否</t-radio>
                    </t-radio-group>
                  </t-form-item>
                  <t-form-item v-if="form2.isSingle === '1'" required label="是否上架" name="status">
                    <t-radio-group v-model="form2.status">
                      <t-radio value="01">是</t-radio>
                      <t-radio value="00">否</t-radio>
                    </t-radio-group>
                  </t-form-item>
                  <t-form-item v-if="form2.isSingle === '1'" required label="价格" name="price">
                    <t-input-number
                      v-model="form2.price"
                      placeholder="请输入商品价格"
                      theme="normal"
                      align="left"
                      :style="{ width: '200px' }"
                    >
                      <template #suffix><span>元</span></template>
                    </t-input-number>
                  </t-form-item>
                  <t-form-item v-if="form2.isSingle === '1'" required label="划线价格" name="nprice">
                    <t-input-number
                      v-model="form2.nprice"
                      placeholder="请输入商品划线价格"
                      theme="normal"
                      align="left"
                      :style="{ width: '200px' }"
                    >
                      <template #suffix><span>元</span></template>
                    </t-input-number>
                  </t-form-item>

                  <t-form-item v-if="form2.isSingle === '1'" required label="重量" name="weight">
                    <t-input-number
                      v-model="form2.weight"
                      placeholder="请输入商品重量"
                      theme="normal"
                      align="left"
                      :style="{ width: '200px' }"
                    >
                      <template #suffix><span>kg</span></template>
                    </t-input-number>
                  </t-form-item>

                  <t-form-item v-if="form2.isSingle === '1'" required label="库存" name="inventory">
                    <t-input-number
                      v-model="form2.inventory"
                      placeholder="请输入商品库存"
                      theme="normal"
                      align="left"
                      :style="{ width: '200px' }"
                    >
                      <template #suffix><span>件</span></template>
                    </t-input-number>
                  </t-form-item>

                  <t-form-item v-if="form2.isSingle === '0'" required label="商品规格" name="skuTem">
                    <t-select
                      v-model="form2.skuTem"
                      @change="changeSku"
                      placeholder="请选择规格模板"
                      :style="{ width: '210px' }"
                    >
                      <t-option
                        v-for="item in skuTemData"
                        :key="item.id"
                        :value="item.id"
                        :label="item.title"
                      ></t-option
                    ></t-select>
                  </t-form-item>
                  <t-form-item v-if="form2.isSingle === '0' && form2.skuTem" required label="规格配图" name="skuTem1">
                    <t-select
                      v-model="form2.skuTem1"
                      @change="changeSku1"
                      :style="{ width: '210px' }"
                      placeholder="请选择配图规格"
                    >
                      <t-option
                        v-for="item in selectSkuTem"
                        :key="item.id"
                        :value="item.id"
                        :label="item.value"
                      ></t-option
                    ></t-select>
                  </t-form-item>
                </t-form>
                <div v-if="form2.isSingle === '0' && form2.skuTem && form2.skuTem1" style="padding: 15px">
                  <t-row :gutter="[5, 15]">
                    <t-col :span="3" v-for="(item, index) in selectSkuTem1" :key="index">
                      <t-space size="0px" direction="vertical" align="center">
                        <t-upload
                          v-model="item.url"
                          theme="image"
                          :request-method="requestMethod2"
                          :sizeLimit="1024 * 5"
                          accept="image/*"
                          :onRemove="removeFile2"
                          @click="imgClick(item)"
                          :locale="{
                            triggerUploadText: {
                              image: '点击上传',
                            },
                          }"
                        ></t-upload>
                        <span class="s-color">{{ item.value }}</span>
                      </t-space>
                    </t-col>
                  </t-row>
                </div>
                <div v-if="form2.isSingle === '0' && form2.skuTem" style="padding: 15px">
                  <t-table
                    ref="tableRef"
                    row-key="id"
                    :columns="columns"
                    :table-layout="'auto'"
                    :data="skuData"
                    vertical-align="top"
                    bordered
                  >
                    <template #price="{ row }">
                      <t-input-number v-model="row.price" theme="normal" align="left">
                        <template #suffix><span>元</span></template>
                      </t-input-number>
                    </template>
                    <template #nprice="{ row }">
                      <t-input-number v-model="row.nprice" theme="normal" align="left">
                        <template #suffix><span>元</span></template>
                      </t-input-number>
                    </template>
                    <template #inventory="{ row }">
                      <t-input-number v-model="row.inventory" theme="normal" align="left">
                        <template #suffix><span>件</span></template>
                      </t-input-number>
                    </template>
                    <template #weight="{ row }">
                      <t-input-number v-model="row.weight" theme="normal" align="left">
                        <template #suffix><span>kg</span></template>
                      </t-input-number>
                    </template>
                    <template #status="{ row }">
                      <t-switch v-model="row.status" size="large">
                        <template #label="slotProps">{{ slotProps.value ? '是' : '否' }}</template>
                      </t-switch>
                    </template>
                  </t-table>
                </div>
              </template>
            </t-card>
          </div>

          <div id="content-4" class="anchor-content-4">
            <t-card class="pd" :bordered="false">
              <template #title>
                <span class="title">服务履约</span>
              </template>
              <template #content>
                <t-form ref="tform3" :data="form3" :rules="rules3" label-width="100px">
                  <t-form-item required label="运费模版" name="rateTem">
                    <t-select
                      v-model="form3.rateTem"
                      :options="temList"
                      :keys="{ value: 'id', label: 'name' }"
                    ></t-select>
                  </t-form-item>
                  <t-form-item label="售后服务" name="security">
                    <t-select
                      v-model="form3.security"
                      :options="tabsList"
                      :keys="{ value: 'id', label: 'title' }"
                    ></t-select>
                  </t-form-item>
                </t-form>
              </template>
            </t-card>
          </div>
          <div id="content-5" class="anchor-content-5">
            <t-card class="pd" :bordered="false">
              <template #title>
                <span class="title">营销信息</span>
              </template>
              <template #content>
                <t-form ref="tform4" :data="form4" :rules="rules4" label-width="100px">
                  <t-form-item required label="是否新品首发" name="isnew">
                    <t-radio-group v-model="form4.isnew">
                      <t-radio value="1">是</t-radio>
                      <t-radio value="0">否</t-radio>
                    </t-radio-group>
                  </t-form-item>
                  <t-form-item label="是否推荐" name="isgood">
                    <t-radio-group v-model="form4.isgood">
                      <t-radio value="1">是</t-radio>
                      <t-radio value="0">否</t-radio>
                    </t-radio-group>
                  </t-form-item>
                </t-form>
              </template>
            </t-card>
          </div>
          <div id="content-6" class="anchor-content-6">
            <t-card class="pd" :bordered="false">
              <template #title>
                <span class="title">商品详情</span>
              </template>
              <template #content>
                <div style="border: 1px solid #ccc">
                  <Toolbar
                    style="border-bottom: 1px solid #ccc"
                    :editor="editorRef"
                    :defaultConfig="toolbarConfig"
                    :mode="mode"
                  />
                  <Editor
                    style="height: 500px; overflow-y: hidden"
                    v-model="valueHtml"
                    :defaultConfig="editorConfig"
                    :mode="mode"
                    @onCreated="handleCreated"
                  />
                </div>
              </template>
            </t-card>
          </div>
        </t-space></div
    ></t-loading>
  </t-drawer>
</template>

<script lang="ts">
export default {
  name: 'addInfo',
};
</script>

<script setup lang="ts">
import { computed, shallowRef, onMounted, onBeforeUnmount, ref, reactive, nextTick } from 'vue';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
import { MessagePlugin, DialogPlugin } from 'tdesign-vue-next';
import Sortable from 'sortablejs';
import { uploadImgs, editInfo } from '@/api/shop/good/info';
import { queryList as querySkuList } from '@/api/shop/good/sku';
import { queryTreeList as queryClassTree } from '@/api/shop/good/class';
import { queryList as queryTem } from '@/api/shop/good/ratetem';
import { queryList as queryTabs } from '@/api/shop/good/tabs';
const props = defineProps(['addShopVisible']);
const emit = defineEmits(['reloadList']);

//初始化加载
const skuTemData = ref([]);
const classTree = ref([]);
const temList = ref([]);
const tabsList = ref([]);
const initData = async () => {
  let skuRes = await querySkuList({
    size: 1000,
    current: 1,
  });
  skuTemData.value = skuRes.data.records;
  let res = await queryClassTree();
  classTree.value = res.data;
  let res1 = await queryTem({
    size: 1000,
    current: 1,
  });
  temList.value = res1.data;
  let res2 = await queryTabs({
    type: '01',
    size: 1000,
    current: 1,
  });
  tabsList.value = res2.data.records;
};

const tform = ref(null);
const tform1 = ref(null);
const tform2 = ref(null);
const tform3 = ref(null);
const tform4 = ref(null);
//loading对象
const dataLoad = ref(true);
const addShopVisible = ref(false);
const initAddData = async (row) => {
  addShopVisible.value = true;
  dataLoad.value = true;
  saveBtn.content = '初始化...';
  saveBtn.loading = true;
  await initData();
  //重置表单
  tform.value.reset();
  tform1.value.reset();
  tform2.value.reset();
  tform3.value.reset();
  tform4.value.reset();
  skuImgs.value = [];
  selectSkuTem.value = [];
  selectSkuTem1.value = [];
  skuData.value = [];
  form1.file1 = [];
  form1.file2 = [];
  form1.mainimg = '';
  form1.imgs = [];
  //初始化表单数据
  form.id = row.id;
  form.cate = row.cate;
  form.des = row.des;
  form.name = row.name;
  form.tabs = row.tabs.split(',');
  form1.file1.push({
    url: row.mainimg,
  });
  form1.mainimg = row.mainimg;
  row.imgs.split(',').forEach((img) => {
    form1.file2.push({
      url: img,
    });
  });
  form1.imgs = row.imgs.split(',');
  form2.isSingle = row.isSingle;
  form2.status = row.status;
  if (form2.isSingle === '1') {
    form2.inventory = row.inventory;
    form2.nprice = row.nprice;
    form2.price = row.price;
    form2.weight = row.weight;
  } else {
    console.log(row);
    skuData.value = row.skulist.filter((row1) => {
      row1.status = row1.status === '00' ? false : true;
      return row1;
    });
    form2.skuTem = row.sku.skutem;
    form2.skuTem1 = row.sku.skuimg;
    skuImgs.value = row.sku.img;
    selectSkuTem.value = row.sku.sku;
    row.sku.img.forEach((obj) => {
      selectSkuTem1.value.push({
        id: obj.id,
        url: [
          {
            url: obj.url,
            id: obj.id,
          },
        ],
        value: obj.value,
      });
    });
  }
  form3.rateTem = row.rateTem;
  form3.security = row.security;
  form4.isgood = row.isgood;
  form4.isnew = row.isnew;
  nextTick(() => {
    setTimeout(() => {
      valueHtml.value = '';
      editorRef.value.clear();
      editorRef.value.setHtml(row.content);
    }, 1000);
  });
  dataLoad.value = false;
  saveBtn.content = '保存';
  saveBtn.loading = false;
};
//定义form对象
const form = reactive({
  id: null,
  cate: '',
  name: '',
  tabs: [],
  des: '',
});
const rules = {
  cate: [{ required: true, message: '请选择所属分类', type: 'error' }],
  name: [{ required: true, message: '请输商品名称', type: 'error' }],
  tabs: [{ required: true, message: '请输入商品标签', type: 'error' }],
  des: [{ required: true, message: '请输商品描述', type: 'error' }],
};
//图文照片
const form1 = reactive({
  file1: [],
  file2: [],
  mainimg: '',
  imgs: [],
});
const rules1 = {
  file1: [{ required: true, message: '请上传商品封面', type: 'error' }],
  file2: [{ required: true, message: '请上传商品轮播图', type: 'error' }],
};

//价格库存
const form2 = reactive({
  skuTem: '',
  skuTem1: '',
  defaultSku: '',
  skuList: [],
  isSingle: '',
  price: '', //价格
  nprice: '', //划线价格
  status: '', //是否上架
  inventory: '', //库存
  weight: 0, //重量
});
const rules2 = {
  isSingle: [{ required: true, message: '请选择规则模板', type: 'error' }],
  price: [{ required: true, message: '请输入商品价格', type: 'error' }],
  nprice: [{ required: true, message: '请输入商品划线价格', type: 'error' }],
  status: [{ required: true, message: '请选择是否上架', type: 'error' }],
  inventory: [{ required: true, message: '请输入商品库存', type: 'error' }],
  weight: [{ required: true, message: '请输入商品重量', type: 'error' }],
  skuTem: [{ required: true, message: '请选择规则模板', type: 'error' }],
  skuTem1: [{ required: true, message: '请选择规格配图', type: 'error' }],
};
const removeFile = async (context) => {
  form1.mainimg = '';
};
const removeFile1 = async (context) => {
  form1.imgs = form1.imgs.filter((row) => {
    if (row !== context.file.url) {
      return row;
    }
  });
};
const requestMethod = async (file) => {
  let form = new FormData();
  form.append('file', file.raw);
  try {
    let res = await uploadImgs(form);
    if (res.code === 0) {
      form1.mainimg = res.msg;
      return new Promise((resolve) => {
        resolve({ status: 'success', response: { url: res.msg } });
      });
    } else {
      MessagePlugin.success('上传失败:' + res.msg);
      return new Promise((resolve) => {
        resolve({ status: 'fail', error: '上传失败' });
      });
    }
  } catch (er) {
    MessagePlugin.success('上传失败:' + er);
    return new Promise((resolve) => {
      // resolve 参数为关键代码
      resolve({ status: 'fail', error: '上传失败' });
    });
  }
};
const requestMethod1 = async (files) => {
  let form = new FormData();
  form.append('file', files[0].raw);
  try {
    let res = await uploadImgs(form);
    if (res.code === 0) {
      form1.imgs.push(res.msg);
      return new Promise((resolve) => {
        resolve({ status: 'success', response: { url: res.msg } });
      });
    } else {
      MessagePlugin.success('更换失败:' + res.msg);
      return new Promise((resolve) => {
        resolve({ status: 'fail', error: '上传失败' });
      });
    }
  } catch (er) {
    MessagePlugin.success('更换失败:' + er);
    return new Promise((resolve) => {
      // resolve 参数为关键代码
      resolve({ status: 'fail', error: '上传失败' });
    });
  }
};
//end

//服务履约
const form3 = reactive({
  rateTem: '',
  security: '',
});
const rules3 = {
  rateTem: [{ required: true, message: '请选择运费模板', type: 'error' }],
  security: [{ required: true, message: '请选择服务保障', type: 'error' }],
};
const selectSkuTem = ref([]);
const changeSku = async (value) => {
  form2.skuTem1 = '';
  selectSkuTem.value = [];
  selectSkuTem1.value = [];
  skuTemData.value.forEach((row) => {
    if (value == row.id) {
      selectSkuTem.value = row.content;
    }
  });
  //组装库存数据
  let tableData = [];
  selectSkuTem.value.forEach((row) => {
    let rowData = [];
    row.label.forEach((row1) => {
      rowData.push(row1.value);
    });
    tableData.push(rowData);
  });
  let returnData = cartesianProductOf(tableData);
  skuData.value = [];
  editableRowKeys.value = [];
  returnData.forEach((row) => {
    let rowKey = Math.random().toString(16).substring(2);
    skuData.value.push({
      id: rowKey,
      rule: row.join('/'),
      price: null,
      nprice: null,
      inventory: null,
      weight: 0,
      status: true,
    });
  });
  //end
};
const selectSkuTem1 = ref([]);
const skuImgs = ref([]);
const changeSku1 = async (value) => {
  selectSkuTem1.value = [];
  skuImgs.value = [];
  selectSkuTem.value.forEach((row) => {
    if (value === row.id) {
      row.label.forEach((row) => {
        let curRow = JSON.parse(JSON.stringify(row));
        curRow['url'] = [];
        selectSkuTem1.value.push({
          ...curRow,
        });
      });
    }
  });
  selectSkuTem1.value.forEach((row) => {
    skuImgs.value.push({
      id: row.id,
      value: row.value,
      url: '',
    });
  });
};
function cartesianProductOf(data) {
  return Array.prototype.reduce.call(
    data,
    function (a, b) {
      let sku = [];
      a.forEach(function (a) {
        b.forEach(function (b) {
          sku.push(a.concat([b]));
        });
      });
      return sku;
    },
    [[]],
  );
}
const selectImg = ref(null);
const imgClick = (item) => {
  selectImg.value = item;
};
const requestMethod2 = async (file) => {
  let form = new FormData();
  form.append('file', file.raw);
  try {
    let res = await uploadImgs(form);
    if (res.code === 0) {
      skuImgs.value = skuImgs.value.filter((row) => {
        if (row.id === selectImg.value.id) {
          row.url = res.msg;
        }
        return row;
      });

      return new Promise((resolve) => {
        resolve({ status: 'success', response: { id: selectImg.value.id, url: res.msg } });
      });
    } else {
      MessagePlugin.success('更换失败:' + res.msg);
      return new Promise((resolve) => {
        resolve({ status: 'fail', error: '上传失败' });
      });
    }
  } catch (er) {
    MessagePlugin.success('更换失败:' + er);
    return new Promise((resolve) => {
      // resolve 参数为关键代码
      resolve({ status: 'fail', error: '上传失败' });
    });
  }
};
const removeFile2 = async (context) => {
  console.log(skuImgs.value, context.file);
  skuImgs.value = skuImgs.value.filter((row) => {
    if (row.id === context.file.id) {
      row.url = '';
    }
    return row;
  });
};
//end

//
const form4 = reactive({
  isnew: '',
  isgood: '',
});
const rules4 = {
  isnew: [{ required: true, message: '请选择是否新品', type: 'error' }],
  isgood: [{ required: true, message: '请选择是否推荐', type: 'error' }],
};
//商品规格表格 -- start
const editableRowKeys = ref([]);
const skuData = ref([]);
const tableCols = ref([
  {
    title: '价格',
    colKey: 'price',
    width: 120,
  },
  {
    title: '划线价格',
    colKey: 'nprice',
    width: 120,
  },
  {
    title: '库存',
    colKey: 'inventory',
    width: 120,
  },
  {
    title: '重量',
    colKey: 'weight',
    width: 100,
  },
  {
    title: '是否上架',
    colKey: 'status',
    width: 60,
    fixed: 'right',
  },
  { title: '规格', colKey: 'rule', fixed: 'right', width: 150 },
]);
const columns = computed(() => {
  return tableCols.value;
});

//商品规格表格 -- end

//富文本编辑框
const mode = ref('simple');
const editorRef = shallowRef();
const valueHtml = ref('');
onMounted(() => {
  //initData();
  setTimeout(() => {
    valueHtml.value = '';
  }, 1500);
});
const toolbarConfig = {};
type insPric = (url, alt, href) => {};
const editorConfig = {
  placeholder: '请填写商品详情',
  MENU_CONF: {
    uploadImage: {
      async customUpload(file: File, insertFn: insPric) {
        try {
          let form = new FormData();
          form.append('file', file);
          let res = await uploadImgs(form);
          if (res.code === 0) {
            insertFn(res.msg, '', '');
          } else {
            MessagePlugin.success('上传失败:' + res.msg);
          }
        } catch (er) {
          MessagePlugin.success('上传失败:' + er);
        }
      },
    },
  },
};
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
});
const handleCreated = (editor) => {
  editorRef.value = editor; // 记录 editor 实例，重要！
};
//富文本编辑框 -- end

//滚动到指定位置
const goContent = (id) => {
  document.querySelector('#anchor-container').scrollTo({
    top: document.getElementById(id).offsetTop, //推荐使用，getBoundingClientRect 相对于当前视口的位置
    behavior: 'smooth', // 平滑滚动
  });
};
//数据提交
const saveBtn = {
  content: '保存',
  loading: false,
};
const saveData = async () => {
  //先判断几个form是否有必填
  let result = await tform.value.validate();
  if (!result || typeof result === 'object') {
    goContent('content-1');
    return false;
  }
  let result1 = await tform1.value.validate();
  if (!result1 || typeof result1 === 'object') {
    goContent('content-2');
    return false;
  }
  let result2 = await tform2.value.validate();
  if (!result2 || typeof result2 === 'object') {
    goContent('content-3');
    return false;
  }
  //如果选择多规格需要校验是否上传照片和多规格价格
  if (form2.isSingle === '0') {
    let isUpImg = true;
    skuImgs.value.forEach((row) => {
      if (!row.url) {
        isUpImg = false;
      }
    });
    if (!isUpImg) {
      goContent('content-3');
      MessagePlugin.warning('请上传规格图片');
      return false;
    }
    // if (selectSkuTem1.value.length !== skuImgs.value.length) {
    //   goContent('content-3');
    //   MessagePlugin.warning('请上传规格图片');
    //   return false;
    // }
    let isCanGo = true;
    skuData.value.forEach((row) => {
      if (row.price == undefined || row.nprice == undefined || row.inventory == undefined || row.weight == undefined) {
        isCanGo = false;
      }
    });
    if (!isCanGo) {
      goContent('content-3');
      MessagePlugin.warning('请填写完整的规格信息');
      return false;
    }
  }
  let result3 = await tform3.value.validate();
  if (!result3 || typeof result3 === 'object') {
    goContent('content-4');
    return false;
  }
  let result4 = await tform4.value.validate();
  if (!result4 || typeof result4 === 'object') {
    goContent('content-5');
    return false;
  }
  if (editorRef.value.isEmpty()) {
    goContent('content-6');
    MessagePlugin.warning('请填写商品详情信息');
    return false;
  }

  const confirmDia = DialogPlugin({
    header: '提醒',
    body: '是否确定保存商品数据?',
    confirmBtn: '确定',
    //cancelBtn: '暂不',
    onConfirm: async ({ e }) => {
      confirmDia.hide();
      dataLoad.value = true;
      saveBtn.loading = true;
      saveBtn.content = '保存中...';
      //开始组装请求数据
      let formData = {
        id: form.id,
        name: form.name,
        des: form.des,
        cate: form.cate,
        tabs: form.tabs.join(','),
        mainimg: form1.mainimg,
        imgs: form1.imgs.join(','),
        isSingle: form2.isSingle,
        price: null,
        nprice: null,
        inventory: null,
        weight: null,
        defaultSku: null,
        sku: null,
        skulist: null,
        status: form2.status,
        rateTem: form3.rateTem,
        security: form3.security,
        isnew: form4.isnew,
        isgood: form4.isgood,
        content: valueHtml.value,
        minprice: null,
        maxprice: null,
      };
      //设置sku数据封装
      if (form2.isSingle === '1') {
        formData.minprice = parseFloat(form2.price).toFixed(2);
        formData.maxprice = parseFloat(form2.price).toFixed(2);
        formData.price = parseFloat(form2.price).toFixed(2);
        formData.nprice = parseFloat(form2.nprice).toFixed(2);
        formData.inventory = form2.inventory;
        formData.weight = form2.weight;
      } else {
        formData.sku = {
          sku: selectSkuTem.value,
          img: skuImgs.value,
          skuimg: form2.skuTem1,
          skutem: form2.skuTem,
        };
        formData.skulist = [];
        let priceArray = new Array();
        let npriceArray = new Array();
        skuData.value.forEach((row) => {
          priceArray.push(row.price);
          npriceArray.push(row.nprice);
          formData.skulist.push({
            id: row.id,
            rule: row.rule,
            price: row.price,
            nprice: row.nprice,
            inventory: row.inventory,
            weight: row.weight,
            status: row.status ? '01' : '00',
          });
        });
        let MaxPrice = Math.max(...priceArray);
        let MinPrice = Math.min(...priceArray);
        let MaxnPrice = Math.max(...npriceArray);
        let MinnPrice = Math.min(...npriceArray);
        formData.minprice = MinPrice.toFixed(2);
        formData.maxprice = MaxPrice.toFixed(2);
        formData.price = MinPrice.toFixed(2) + '-' + MaxPrice.toFixed(2);
        formData.nprice = MinnPrice.toFixed(2) + '-' + MaxnPrice.toFixed(2);
        let proStatus = '00';
        // skuData.value.forEach((row) => {
        //   if (MinPrice == row.price) {
        //     formData.defaultSku = row.id;
        //     formData.inventory = row.inventory;
        //     formData.weight = row.weight;
        //   }
        //   if (row.status) {
        //     proStatus = '01';
        //   }
        // });
        //formData.status = proStatus;
      }
      //开始提交
      try {
        let res = await editInfo(formData);
        if (res.code === 0) {
          addShopVisible.value = false;
          emit('reloadList');
          MessagePlugin.success('保存成功');
        } else {
          MessagePlugin.error('保存失败：' + res.msg);
        }
      } catch (error) {
        MessagePlugin.error('保存失败：' + error);
      } finally {
        dataLoad.value = false;
        saveBtn.loading = false;
        saveBtn.content = '保存';
      }
    },
    onClose: ({ e, trigger }) => {
      confirmDia.hide();
    },
  });
};
//edn
//快捷菜单 -- start
const handleClick = ({ e, href, title }) => {
  e.preventDefault();
};
defineExpose({
  initAddData,
});
//快捷菜单 -- end
</script>

<style>
@import '@wangeditor/editor/dist/css/style.css';
</style>
<style lang="less" scoped>
.anchor-container-demo {
  display: flex;
}
#anchor-container {
  flex-grow: 1;
  height: calc(100vh - 150px);
  overflow: auto;
  .anchor-content-1 {
    padding-bottom: 20px;
  }
  .anchor-content-2 {
    padding-bottom: 20px;
  }
  .anchor-content-3 {
    padding-bottom: 20px;
  }
  .anchor-content-4 {
    padding-bottom: 20px;
  }
  .anchor-content-5 {
    padding-bottom: 20px;
  }
  .anchor-content-6 {
    //padding-bottom: 20px;
  }
  .pd {
    background: var(--td-bg-color-page);
    //height: 100%;
    border-radius: 8px;
    //width: 900px;
    .s-color {
      color: var(--td-text-color-placeholder);
    }
  }
  .title {
    color: #252931;
    font-size: 18px;
    font-weight: 700;
  }
}
.t-anchor__item {
  width: 150px !important;
}
</style>
