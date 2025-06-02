<template>
  <view class="notice-detail-page">
    <!-- 标准导航栏 -->
    <uni-nav-bar title="公告详情" :border="false" fixed class="bold-title" left-icon="left" @clickLeft="goBack" />

    <view class="container">
      <view class="title-section">
        <view class="main-title">{{ noticeDetail.title }}</view>
        <view class="document-number" v-if="noticeDetail.documentNumber">
          ({{ noticeDetail.documentNumber }})
        </view>
        <view class="sub-title" v-if="noticeDetail.subTitle">{{ noticeDetail.subTitle }}</view>
      </view>
      <view class="content-section">
        <rich-text :nodes="noticeDetail.content" />
      </view>
    </view>
  </view>
</template>

<script setup>
  import { reactive } from 'vue';
  import { noticeApi } from '@/api/business/oa/notice-api';
  import { onLoad } from '@dcloudio/uni-app';
  import { smartSentry } from '@/lib/smart-sentry';

  const noticeDetail = reactive({
    title: '',
    subTitle: '',
    documentNumber: '',
    content: '',
  });

  async function getNoticeDetail(noticeId) {
    try {
      uni.showLoading({ title: '加载中' });
      let res = await noticeApi.view(noticeId);
      noticeDetail.title = res.data.title;
      noticeDetail.content = res.data.contentHtml;
      noticeDetail.documentNumber = res.data.documentNumber;
      let subTitleArray = [];
      if (res.data.author) {
        subTitleArray.push(res.data.author);
      }
      if (res.data.publishTime) {
        subTitleArray.push(res.data.publishTime);
      }
      noticeDetail.subTitle = subTitleArray.join(' | ');
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      uni.hideLoading();
    }
  }

  onLoad((option) => {
    uni.pageScrollTo({
      scrollTop: 0,
    });
    getNoticeDetail(option.noticeId);
  });

  /**
   * 返回按钮处理
   */
  function goBack() {
    uni.navigateBack({
      delta: 1
    });
  }
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

  .notice-detail-page {
    background-color: #f5f5f5;
    min-height: 100vh;
  }

  .container {
    background: #fff;
    margin: 16rpx 32rpx;
    border-radius: 16rpx;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
    overflow: hidden;
  }

  .title-section {
    padding: 48rpx 32rpx 32rpx;
    text-align: center;
    border-bottom: 1rpx solid #f0f0f0;
  }

  .main-title {
    font-size: 36rpx;
    font-weight: 600;
    color: #333;
    line-height: 1.4;
    margin-bottom: 16rpx;
  }

  .document-number {
    font-size: 28rpx;
    color: #666;
    margin-bottom: 16rpx;
  }

  .sub-title {
    font-size: 26rpx;
    color: #999;
    line-height: 1.3;
  }

  .content-section {
    padding: 32rpx;

    :deep(rich-text) {
      line-height: 1.6;
      color: #333;
      font-size: 28rpx;

      p {
        margin-bottom: 16rpx;
        line-height: 1.6;
      }

      img {
        max-width: 100%;
        height: auto;
        border-radius: 8rpx;
        margin: 16rpx 0;
      }

      h1, h2, h3, h4, h5, h6 {
        font-weight: 600;
        margin: 24rpx 0 16rpx;
        color: #333;
      }

      ul, ol {
        padding-left: 32rpx;
        margin: 16rpx 0;
      }

      li {
        margin-bottom: 8rpx;
        line-height: 1.6;
      }
    }
  }
</style>
