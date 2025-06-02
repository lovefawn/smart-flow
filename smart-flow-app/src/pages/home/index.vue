<template>
  <view class="smart-page">
    <!-- 标准导航栏 -->
    <uni-nav-bar title="首页" :border="false" fixed class="bold-title">
      <template #right>
        <view class="nav-actions">
          <view class="nav-icon" @click="handleScan">
            <image src="@/static/images/index/ic_scan.png" mode="aspectFit"></image>
          </view>
          <view class="nav-icon" @click="handleSearch">
            <image src="@/static/images/index/ic_search.png" mode="aspectFit"></image>
          </view>
        </view>
      </template>
    </uni-nav-bar>

    <!-- 主要内容区域 -->
    <view class="smart-container" :style="{ paddingBottom: showCustomTabbar ? '120rpx' : '0' }">

      <Banner />

      <!-- 功能菜单 -->
      <Menu @changeHome="changeHome" />
            <!-- 数据统计卡片 -->
      <Statistics/>

      <!-- 通知公告 -->
      <Notice />
    </view>

    <!-- 自定义底部导航栏 -->
    <SmartTabbar
      v-if="showCustomTabbar"
      ref="smartTabbar"
      :current="currentTabIndex"
      @change="onTabbarChange"
    />
  </view>
</template>

<script setup>
  import Banner from './components/banner.vue';
  import Menu from './components/menu.vue';
  import Statistics from './components/statistics.vue';
  import Notice from './components/notice.vue';
  import Goods from './components/goods.vue';
  import SmartTabbar from '@/components/smart-tabbar/smart-tabbar.vue';
  import { useTabbarManager } from '@/utils/tabbar-manager.js';
  import { ref } from 'vue';
  import { onShow } from '@dcloudio/uni-app';

  const showBannerFlag = ref(false);

  // 使用统一的底部导航栏管理
  const {
    currentTabIndex,
    showCustomTabbar,
    onTabbarChange,
    onPageShow
  } = useTabbarManager(0, ref) // 首页是索引 0

  function changeHome() {
    showBannerFlag.value = !showBannerFlag.value;
  }

  // 新增的方法
  function handleScan() {
    uni.showToast({
      title: '扫码功能',
      icon: 'none'
    });
  }

  function handleSearch() {
    uni.navigateTo({
      url: '/pages/search/search'
    });
  }

  onShow(() => {
    uni.pageScrollTo({
      scrollTop: 0,
      duration: 300,
    });

    // 调用统一的页面显示处理
    onPageShow();
  });
</script>

<style lang="scss" scoped>
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

  :deep(.uni-navbar__header-btns) {
    width: 150rpx !important;
  }

  .nav-actions {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  .nav-icon {
    width: 56rpx;
    height: 56rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8rpx;
    transition: background-color 0.2s ease;

    &:active {
      background-color: #f5f5f5;
    }

    image {
      width: 40rpx;
      height: 40rpx;
    }
  }
</style>
