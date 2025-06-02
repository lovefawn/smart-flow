<template>
  <view class="smart-card menu-container">
    <view class="smart-flex smart-flex--wrap smart-justify--between">
      <!-- 首页切换 -->
      <!-- <view class="menu-item" @click="">
        <view class="menu-item__icon">
          <image src="@/static/images/index/ic_home_menu1.png" mode="aspectFit"></image>
        </view>
        <text class="menu-item__text">首页切换</text>
      </view> -->

      <!-- 通知公告 -->
      <view class="menu-item" @click="navigateTo('/pages/notice/notice-index')">
        <view class="menu-item__icon">
          <image src="@/static/images/index/ic_home_menu2.png" mode="aspectFit"></image>
        </view>
        <text class="menu-item__text">通知公告</text>
      </view>

      <!-- 客户线索 -->
      <view class="menu-item" @click="navigateTo('/pages/enterprise/enterprise-list')">
        <view class="menu-item__icon">
          <image src="@/static/images/index/ic_home_menu3.png" mode="aspectFit"></image>
        </view>
        <text class="menu-item__text">客户线索</text>
      </view>

      <!-- 工作流 -->
      <view class="menu-item" @click="switchTab('/pages/flow/list')">
        <view class="menu-item__icon">
          <image src="@/static/images/index/ic_home_menu4.png" mode="aspectFit"></image>
        </view>
        <text class="menu-item__text">工作流</text>
      </view>

      <!-- 接口加密 -->
      <view class="menu-item" @click="developing">
        <view class="menu-item__icon">
          <image src="@/static/images/index/ic_home_menu10.png" mode="aspectFit"></image>
        </view>
        <text class="menu-item__text">接口加密</text>
      </view>
    </view>
  </view>
</template>

<script setup>
  import { SmartToast } from '@/lib/smart-support';


  function navigateTo(url) {
    uni.navigateTo({
      url,
    });
  }
  function switchTab(url) {
    uni.switchTab({
      url,
    });
  }

  function developing() {
    SmartToast.toast('敬请期待');
  }
</script>

<style lang="scss" scoped>
  .menu-container {
    width: 700rpx;
    margin: 30rpx auto 0;
    background: #ffffff;
    border-radius: 12rpx;
    box-shadow: 0px 3px 4px 0px rgba(24, 144, 255, 0.06);
    padding: 40rpx;
    box-sizing: border-box;
  }

  .menu-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 134rpx;
    height: 134rpx;
    border-radius: 16rpx;
    transition: all 0.2s ease;
    cursor: pointer;

    &:active {
      background-color: #fafafa;
      transform: scale(0.95);
    }

    &__icon {
      width: 80rpx;
      height: 80rpx;
      margin-bottom: 16rpx;
      display: flex;
      align-items: center;
      justify-content: center;

      image {
        width: 100%;
        height: 100%;
      }
    }

    &__text {
      font-size: 24rpx;
      color: #444;
      font-weight: 500;
      text-align: center;
      line-height: 1.2;
    }
  }
</style>
