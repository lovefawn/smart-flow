<template>
  <view class="stats-container">

    <view class="stats-grid">
      <view class="stats-item stats-item--primary">
        <view class="stats-item__label">本月销售额</view>
        <view class="stats-item__value">¥32,226.00</view>
        <view class="stats-item__trend">+12.5%</view>
      </view>

      <view class="stats-item">
        <view class="stats-item__label">销售目标</view>
        <view class="stats-item__value">¥50,000.00</view>
        <view class="stats-item__progress">
          <view class="progress-bar">
            <view class="progress-fill" style="width: 64.5%"></view>
          </view>
        </view>
      </view>

      <view class="stats-item">
        <view class="stats-item__label">完成率</view>
        <view class="stats-item__value">85%</view>
      </view>

      <view class="stats-item">
        <view class="stats-item__label">新增客户</view>
        <view class="stats-item__value">+1024</view>
      </view>
    </view>
  </view>
</template>

<script></script>

<style lang="scss" scoped>
  .stats-container {
    width: 700rpx;
    margin: 30rpx auto 0;
    background: #ffffff;
    border-radius: 12rpx;
    box-shadow: 0px 3px 4px 0px rgba(24, 144, 255, 0.06);
    padding: 26rpx 30rpx 20rpx;
    box-sizing: border-box;
  }

  .stats-header {
    margin-bottom: 32rpx;
    padding-bottom: 24rpx;
    border-bottom: 1rpx solid #ededed;
  }

  .stats-title {
    display: block;
    font-size: 32rpx;
    font-weight: 600;
    color: #444;
    margin-bottom: 8rpx;
  }

  .stats-subtitle {
    display: block;
    font-size: 24rpx;
    color: #777;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24rpx;
  }

  .stats-item {
    padding: 24rpx;
    background: #f7f8f9;
    border-radius: 8rpx;
    position: relative;

    &--primary {
      background: linear-gradient(135deg, #007aff, #0056cc);
      color: #ffffff;

      .stats-item__label,
      .stats-item__value,
      .stats-item__trend {
        color: #ffffff;
      }
    }

    &__label {
      font-size: 24rpx;
      color: #777;
      margin-bottom: 8rpx;
      display: block;
    }

    &__value {
      font-size: 36rpx;
      font-weight: 700;
      color: #444;
      margin-bottom: 8rpx;
      display: block;
    }

    &__trend {
      font-size: 20rpx;
      color: #52c41a;
      font-weight: 500;
      display: block;
    }

    &__progress {
      margin-top: 12rpx;
    }
  }

  .progress-bar {
    width: 100%;
    height: 6rpx;
    background: #e5e7eb;
    border-radius: 3rpx;
    overflow: hidden;
  }

  .progress-fill {
    height: 100%;
    background: #007aff;
    border-radius: 3rpx;
    transition: width 0.3s ease;
  }
</style>
