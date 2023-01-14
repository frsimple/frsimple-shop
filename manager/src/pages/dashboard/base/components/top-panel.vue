<template>
  <t-row :gutter="[10, 10]">
    <t-col :xs="12" :xl="12">
      <t-card :style="{ height: '231px' }" :bordered="false" :class="{ 'dashboard-item': true }" :loading="loadData">
        <template #title>
          <span :style="{ fontSize: `19px`, fontWeight: 700 }">数据概览</span>
        </template>
        <template #actions>
          <t-button shape="square" theme="primary" @click="initData">
            <template #icon><t-icon name="refresh"></t-icon></template>
          </t-button>
        </template>
        <t-row>
          <t-col :span="3" v-for="(item, index) in PANE_LIST" :key="item.title">
            <div style="text-align: center">
              <t-space direction="vertical" align="center">
                <span>{{ item.title }}</span>
                <div class="dashboard-item-top">
                  <span :style="{ fontSize: `${1 * 20}px` }">{{ index === 0 ? '￥' + item.number : item.number }}</span>
                </div>
                <div v-if="index != 3" class="dashboard-item-bottom">
                  <div class="dashboard-item-block">
                    较昨日 {{ index === 0 ? '￥' + item.tnumber : item.tnumber }}
                    <trend
                      class="dashboard-item-trend"
                      :type="item.trend >= 0 ? 'up' : 'down'"
                      :describe="item.trend"
                    />
                  </div>
                </div>
              </t-space>
            </div>
          </t-col>
        </t-row>
      </t-card>
    </t-col>
  </t-row>
</template>

<script lang="ts">
export default {
  name: 'DashboardBase',
};
</script>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { homeTop } from '@/api/home';

// 导入样式
import Trend from '@/components/trend/index.vue';
const loadData = ref(false);
const PANE_LIST = ref([
  {
    title: '今日支付金额(元)',
    number: 0,
    tnumber: 0,
    trend: 0,
    leftType: 'echarts-bar',
  },
  {
    title: '今日订单数量(个)',
    number: 0,
    tnumber: 0,
    trend: 0,
    leftType: 'echarts-bar',
  },
  {
    title: '今日新增用户(个)',
    number: 0,
    tnumber: 0,
    trend: 0,
    leftType: 'icon-file-paste',
  },
  {
    title: '昨日访客量(个)',
    number: 0,
    tnumber: 0,
    trend: 0,
    leftType: 'icon-usergroup',
  },
]);

const initData = async () => {
  loadData.value = true;
  let res = await homeTop();
  PANE_LIST.value[0].number = res.data.todayPayMoney;
  PANE_LIST.value[0].tnumber = res.data.yesterDayPayMoeny;
  PANE_LIST.value[0].trend = res.data.payMoeny;

  PANE_LIST.value[1].number = res.data.todayOrderCount;
  PANE_LIST.value[1].tnumber = res.data.yesterdayOrderCount;
  PANE_LIST.value[1].trend = res.data.orderCount;

  PANE_LIST.value[2].number = res.data.todayAddUserCount;
  PANE_LIST.value[2].tnumber = res.data.yesterDayAddUserCount;
  PANE_LIST.value[2].trend = res.data.addUserCount;

  PANE_LIST.value[3].number = res.data.yesterDayUserCount;
  PANE_LIST.value[3].tnumber = res.data.yesterDayUserCount;
  PANE_LIST.value[3].trend = res.data.userCount;
  loadData.value = false;
};
onMounted(() => {
  initData();
});
</script>

<style lang="less" scoped>
@import '@/style/variables.less';

.dashboard-item {
  padding: 8px;

  :deep(.t-card__footer) {
    padding-top: 0;
  }

  :deep(.t-card__title) {
    font-size: 14px;
    font-weight: 500;
  }

  :deep(.t-card__body) {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    flex: 1;
    position: relative;
  }

  &:hover {
    cursor: pointer;
  }

  &-top {
    display: flex;
    flex-direction: row;
    align-items: flex-start;

    > span {
      display: inline-block;
      color: var(--tdvns-text-color-primary);
      font-size: 36px;
      line-height: 44px;
    }
  }

  &-bottom {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;

    > .t-icon {
      cursor: pointer;
    }
  }

  &-block {
    display: flex;
    align-items: center;
    justify-content: center;
    line-height: 22px;
    color: var(--tdvns-text-color-placeholder);
  }

  &-trend {
    margin-left: 8px;
  }

  &-left {
    position: absolute;
    top: 0;
    right: 32px;

    > span {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 56px;
      height: 56px;
      background: var(--tdvns-brand-color-1);
      border-radius: 50%;

      .t-icon {
        font-size: 24px;
        color: var(--tdvns-brand-color);
      }
    }
  }

  // 针对第一个卡片需要反色处理
  &--main-color {
    background: var(--tdvns-brand-color);
    color: var(--tdvns-text-color-primary);

    :deep(.t-card__title),
    .dashboard-item-top span,
    .dashboard-item-bottom {
      color: var(--tdvns-text-color-anti);
    }

    .dashboard-item-block {
      color: var(--tdvns-text-color-anti);
      opacity: 0.6;
    }

    .dashboard-item-bottom {
      color: var(--tdvns-text-color-anti);
    }
  }
}
</style>
