<template>
  <view class="list-container">
    <view class="list-item" @click="gotoDetail(item.noticeId)" v-for="item in list" :key="item.noticeId">
      <view class="item-header">
        <view class="title-section">
          <view class="notice-type">{{ item.noticeTypeName }}</view>
          <view class="title">{{ item.title }}</view>
        </view>
        <view class="status-badge" :class="{ 'read': item.viewFlag, 'unread': !item.viewFlag }">
          {{ item.viewFlag ? '已读' : '未读' }}
        </view>
      </view>

      <view class="item-meta" v-if="item.author || item.source">
        <text class="meta-item" v-if="item.author">作者：{{ item.author }}</text>
        <text class="meta-item" v-if="item.source">来源：{{ item.source }}</text>
      </view>

      <view class="item-footer">
        <view class="publish-time">{{ item.publishTime }}</view>
        <view class="arrow-icon">
          <uni-icons type="right" size="14" color="#c0c4cc"></uni-icons>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
  const props = defineProps({
    marginTop: {
      type: String,
      default: '0',
    },
    list: {
      type: Array,
      default: [],
    },
  });

  function gotoDetail(noticeId) {
    uni.navigateTo({ url: '/pages/notice/notice-detail?noticeId=' + noticeId });
  }
</script>

<style lang="scss" scoped>
  .list-container {
    margin-top: v-bind(marginTop);
    padding: 0 32rpx;
  }

  .list-item {
    background: #fff;
    margin-bottom: 16rpx;
    border-radius: 16rpx;
    padding: 32rpx;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
    transition: all 0.2s ease;
    position: relative;
    overflow: hidden;

    &:active {
      transform: scale(0.98);
      box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
    }

    &:last-child {
      margin-bottom: 32rpx;
    }
  }

  .item-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16rpx;
  }

  .title-section {
    flex: 1;
    margin-right: 16rpx;
  }

  .notice-type {
    font-size: 24rpx;
    color: #007aff;
    background: rgba(0, 122, 255, 0.1);
    padding: 6rpx 12rpx;
    border-radius: 8rpx;
    display: inline-block;
    margin-bottom: 12rpx;
    font-weight: 500;
  }

  .title {
    font-size: 30rpx;
    font-weight: 600;
    color: #333;
    line-height: 1.4;
    word-break: break-all;
  }

  .status-badge {
    padding: 8rpx 16rpx;
    border-radius: 12rpx;
    font-size: 22rpx;
    font-weight: 500;
    white-space: nowrap;

    &.unread {
      background: #ff4757;
      color: #fff;
    }

    &.read {
      background: #f8f9fa;
      color: #6c757d;
      border: 1rpx solid #e9ecef;
    }
  }

  .item-meta {
    margin-bottom: 16rpx;
  }

  .meta-item {
    font-size: 26rpx;
    color: #666;
    margin-right: 24rpx;

    &:last-child {
      margin-right: 0;
    }
  }

  .item-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .publish-time {
    font-size: 24rpx;
    color: #999;
  }

  .arrow-icon {
    opacity: 0.6;
  }
</style>
