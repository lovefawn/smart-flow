<template>
  <view class="smart-card notice-container">
    <!-- 标题栏 -->
    <view class="notice-header smart-flex smart-justify--between smart-align--center smart-mb--md">
      <text class="notice-title">通知公告</text>
      <text class="notice-more" @click="onMore">更多</text>
    </view>

    <!-- 通知列表 -->
    <view class="notice-list">
      <view
        v-for="item in data"
        :key="item.noticeId"
        class="smart-list-item smart-list-item--clickable"
        @click="goto(item.noticeId)"
      >
        <view class="smart-list-item__content">
          <text class="smart-list-item__title smart-truncate">{{ item.title }}</text>
          <text class="smart-list-item__subtitle">{{ item.publishDate }}</text>
        </view>
        <view class="smart-list-item__action">
          <text class="arrow-icon">></text>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="!loading && data.length === 0" class="notice-empty smart-text--center smart-py--xl">
      <text class="empty-text">暂无通知公告</text>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="notice-loading smart-text--center smart-py--xl">
      <text class="loading-text">加载中...</text>
    </view>
  </view>
</template>

<script setup>
  import { ref } from 'vue';
  import { noticeApi } from '@/api/business/oa/notice-api';
  import { smartSentry } from '@/lib/smart-sentry';
  import { onShow } from '@dcloudio/uni-app';

  const queryForm = {
    pageNum: 1,
    pageSize: 5,
    searchCount: false,
  };

  let data = ref([]);

  const loading = ref(false);
  // 查询列表
  async function queryNoticeList() {
    try {
      loading.value = true;
      const result = await noticeApi.queryEmployeeNotice(queryForm);
      data.value = result.data.list;
    } catch (err) {
      smartSentry.captureError(err);
    } finally {
      loading.value = false;
    }
  }

  // 跳转详情
  function goto(noticeId) {
    uni.navigateTo({ url: '/pages/notice/notice-detail?noticeId=' + noticeId });
  }

  onShow(() => {
    queryNoticeList();
  });

  // 查看更多
  function onMore() {
    uni.navigateTo({
      url: '/pages/notice/notice-index',
    });
  }
</script>

<style lang="scss" scoped>
  .notice-container {
    width: 700rpx;
    margin: 30rpx auto 0;
    background: #ffffff;
    border-radius: 12rpx;
    box-shadow: 0px 3px 4px 0px rgba(24, 144, 255, 0.06);
    padding: 26rpx 30rpx 20rpx;
    box-sizing: border-box;
  }

  .notice-header {
    padding-bottom: 32rpx;
    border-bottom: 1rpx solid #ededed;
  }

  .notice-title {
    font-size: 32rpx;
    font-weight: 600;
    color: #444;
  }

  .notice-more {
    font-size: 28rpx;
    color: #007aff;
    font-weight: 500;
    cursor: pointer;
    transition: color 0.2s ease;

    &:active {
      color: #0056cc;
    }
  }

  .notice-list {
    .smart-list-item {
      border-bottom: 1rpx solid #ededed;
      padding: 32rpx 0;

      &:last-child {
        border-bottom: none;
      }

      &:active {
        background-color: #fafafa;
      }
    }
  }

  .arrow-icon {
    font-size: 28rpx;
    color: #999;
    font-weight: bold;
  }

  .notice-empty,
  .notice-loading {
    .empty-text,
    .loading-text {
      font-size: 28rpx;
      color: #999;
    }
  }
</style>
