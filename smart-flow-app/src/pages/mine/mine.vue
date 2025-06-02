<template>
  <view class="smart-page">

    <!-- 主要内容区域 -->
    <view class="container" :style="{ paddingBottom: showCustomTabbar ? '120rpx' : '0' }">
      <!-- 用户 -->
      <MineUserBlue v-if="blueUserFlag" />
      <MineUserWhite v-if="!blueUserFlag" />

      <!---功能菜单--->
      <MineMenu @change-style="onChangeStyle" />

      <!---退出--->
      <view class="logout-btn" @click="logout">
        <view class="label">退出登录</view>
      </view>
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
  import MineMenu from './components/mine-menu.vue';
  import MineUserBlue from './components/mine-user-blue.vue';
  import MineUserWhite from './components/mine-user-white.vue';
  import SmartTabbar from '@/components/smart-tabbar/smart-tabbar.vue';
  import { useTabbarManager } from '@/utils/tabbar-manager.js';
  import { ref } from 'vue';
  import { onShow } from '@dcloudio/uni-app';
  import { useUserStore } from '@/store/modules/system/user';
  import { SmartLoading, SmartToast } from '@/lib/smart-support';
  import { smartSentry } from '@/lib/smart-sentry';

  const userStore = useUserStore();
  const blueUserFlag = ref(true);
  function onChangeStyle() {
    blueUserFlag.value = !blueUserFlag.value;
  }

  async function logout() {
    try {
      setTimeout(() => {
        userStore.logout();
        uni.navigateTo({ url: '/pages/login/login' });
      }, 500);
      await SmartLoading.show();
      SmartToast.toast('退出成功');
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  // 使用统一的底部导航栏管理
  const {
    currentTabIndex,
    showCustomTabbar,
    onTabbarChange,
    onPageShow
  } = useTabbarManager(3, ref) // 我的页面是索引 3

  onShow(() => {
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

  .container {
    width: 100%;
    background: #f4f4f4;
    display: flex;
    flex-direction: column;
  }

  .logout-btn {
    margin: 24px 27px 50px;
    height: 44px;
    opacity: 0.5;
    background: $uni-color-error;
    border-radius: 22px;
    font-size: 15px;
    line-height: 44px;
    font-weight: 700;
    text-align: center;
    color: white;
  }
</style>
