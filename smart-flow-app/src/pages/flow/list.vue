<template>
  <view class="flow-page">
    <!-- 标准导航栏 -->
    <uni-nav-bar title="流程" :border="false" fixed class="bold-title"></uni-nav-bar>

    <view :style="{ paddingBottom: showCustomTabbar ? '120rpx' : '0' }">
      <y-tabs v-model="active" sticky :offsetTop="43" color="#007aff">
        <y-tab v-for="item in tabsList" :key="item.value" :title="item.title"/>
      </y-tabs>

      <!-- 下拉刷新容器 -->
      <scroll-view
        class="scroll-container"
        scroll-y
        refresher-enabled
        :refresher-triggered="refresherTriggered"
        @refresherrefresh="onRefresh"
        @scrolltolower="onLoadMore"
        :style="{ height: scrollHeight + 'px' }"
      >
        <TodoList v-if="active === 0" ref="todoListRef" />
        <DiscountList v-if="active === 1" ref="doneListRef" />
        <IotList v-if="active === 2" ref="copyListRef" />
      </scroll-view>

      <!-- 自定义底部导航栏 -->
      <SmartTabbar
        v-if="showCustomTabbar"
        ref="smartTabbar"
        :current="currentTabIndex"
        @change="onTabbarChange"
      />
    </view>
  </view>
</template>

<script setup>
import TodoList from './components/todo-list.vue';
import DiscountList from './components/discount-list.vue';
import IotList from './components/iot-list.vue';
import SmartTabbar from '@/components/smart-tabbar/smart-tabbar.vue';
import { useTabbarManager } from '@/utils/tabbar-manager.js';
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import { onShow } from '@dcloudio/uni-app';

const active = ref(0);
const refresherTriggered = ref(false);
const scrollHeight = ref(0);

// 组件引用
const todoListRef = ref(null);
const doneListRef = ref(null);
const copyListRef = ref(null);

// 使用统一的底部导航栏管理
const {
  currentTabIndex,
  showCustomTabbar,
  onTabbarChange,
  onPageShow
} = useTabbarManager(1, ref) // 流程页面是索引 1

const tabsList = [
  {
    title: '待办任务',
    value: 0,
  },
  {
    title: '已办任务',
    value: 1,
  },
  {
    title: '抄送任务',
    value: 2,
  },
];

// 计算滚动容器高度
const calculateScrollHeight = () => {
  const systemInfo = uni.getSystemInfoSync();
  const windowHeight = systemInfo.windowHeight;
  const navBarHeight = 44; // 导航栏高度
  const tabsHeight = 44; // tabs高度
  const tabbarHeight = showCustomTabbar.value ? 60 : 0; // 底部导航栏高度

  scrollHeight.value = windowHeight - navBarHeight - tabsHeight - tabbarHeight;
};

// 下拉刷新
const onRefresh = async () => {
  refresherTriggered.value = true;

  try {
    // 根据当前激活的tab刷新对应的列表
    switch (active.value) {
      case 0:
        if (todoListRef.value) {
          await todoListRef.value.refresh();
        }
        break;
      case 1:
        if (doneListRef.value) {
          await doneListRef.value.refresh();
        }
        break;
      case 2:
        if (copyListRef.value) {
          await copyListRef.value.refresh();
        }
        break;
    }
  } catch (error) {
    console.error('刷新失败:', error);
  } finally {
    refresherTriggered.value = false;
  }
};

// 上拉加载更多
const onLoadMore = () => {
  // 根据当前激活的tab加载更多数据
  switch (active.value) {
    case 0:
      if (todoListRef.value) {
        todoListRef.value.loadMore();
      }
      break;
    case 1:
      if (doneListRef.value) {
        doneListRef.value.loadMore();
      }
      break;
    case 2:
      if (copyListRef.value) {
        copyListRef.value.loadMore();
      }
      break;
  }
};

onMounted(() => {
  nextTick(() => {
    calculateScrollHeight();
  });

  // 监听任务处理完成事件
  uni.$on('refreshTaskList', () => {
    // 刷新当前激活的tab
    onRefresh();
  });
});

onShow(() => {
  // 调用统一的页面显示处理
  onPageShow();
  // 重新计算高度
  calculateScrollHeight();
  // 页面显示时也刷新一下数据
  onRefresh();
});

onUnmounted(() => {
  // 清理事件监听
  uni.$off('refreshTaskList');
});
</script>

<style lang="scss" scoped>
.flow-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.scroll-container {
  flex: 1;
  overflow: hidden;
}

// 导航栏标题加粗和调整字号
  :deep(.bold-title) {
    .uni-navbar__header-container {
      .uni-navbar__header-container-inner {
        font-weight: 600 !important;
        font-size: 34rpx !important;
      }
    }

    .uni-navbar__header-btns {
      font-weight: 600 !important;
      font-size: 34rpx !important;
    }

    .uni-navbar__content {
      font-weight: 600 !important;
      font-size: 34rpx !important;
    }

    // 更通用的选择器
    .uni-navbar__title {
      font-weight: 600 !important;
      font-size: 34rpx !important;
    }

    // 针对可能的文本元素
    text, .text {
      font-weight: 600 !important;
      font-size: 34rpx !important;
    }
  }

  page {
    background-color: #f5f5f5;
  }
</style>
