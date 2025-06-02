<template>
  <view class="notice-page">
    <!-- 标准导航栏 -->
    <uni-nav-bar title="通知公告" :border="false" fixed class="bold-title" left-icon="left" @clickLeft="goBack" />

    <mescroll-body @init="mescrollInit" :down="{ auto: false }" @down="onDown" @up="onUp">
      <!-- 搜索和筛选区域 -->
      <view class="search-section">
        <view class="search-container">
          <view class="search-input">
            <uni-easyinput
              prefixIcon="search"
              :clearable="true"
              trim="all"
              v-model="queryForm.keywords"
              placeholder="搜索：标题、作者、来源等"
              @confirm="search"
              @clear="search"
            />
          </view>
          <view class="filter-btn" @click="showQueryFormPopUp">
            <uni-icons type="settings" size="20" color="#007aff"></uni-icons>
            <text class="filter-text">筛选</text>
          </view>
        </view>
      </view>

      <!--筛选条件提示-->
      <uni-notice-bar
        @close="onCloseQueryFormTips"
        v-if="showQueryFormTipsFlag"
        class="query-bar"
        background-color="#007aff"
        color="white"
        show-close
        single
        :text="queryFormTips"
      />

      <!-- 筛选条件表单弹窗 -->
      <NoticeQueryFormPopUp ref="noticeQueryFormPopUpRef" @close="onCloseQueryFormPopUp" />

      <!-- 列表 -->
      <NoticeList :list="noticeListData" :margin-top="queryFormTipsMarginTop" />
    </mescroll-body>
  </view>
</template>

<script setup>
  import { reactive, ref } from 'vue';
  import NoticeQueryFormPopUp from './components/notice-query-form-popup.vue';
  import { noticeApi } from '@/api/business/oa/notice-api';
  import { onPageScroll, onReachBottom } from '@dcloudio/uni-app';
  import useMescroll from '@/uni_modules/uni-mescroll/hooks/useMescroll';
  import { smartSentry } from '@/lib/smart-sentry';
  import NoticeList from './components/notice-list.vue';
  import _ from 'lodash';

  // --------------------------- 筛选条件弹窗 ---------------------------------
  const noticeQueryFormPopUpRef = ref();

  /**
   * 显示 筛选弹窗
   */
  function showQueryFormPopUp() {
    noticeQueryFormPopUpRef.value.show();
  }

  /**
   * 监听 筛选弹窗 关闭
   */
  function onCloseQueryFormPopUp(param) {
    if (param === null) {
      return;
    }
    Object.assign(queryForm, param);
    showOrHideQueryFormTips();
    query(getMescroll(), true, buildQueryParam(1));
    uni.pageScrollTo({
      scrollTop: 0,
    });
  }

  // --------------------------- 筛选条件tips ---------------------------------
  const queryFormTips = ref(null);
  const showQueryFormTipsFlag = ref(false);
  const queryFormTipsMarginTop = ref('0px');

  /**
   * 查询提示
   */
  function buildQueryFormTips() {
    let tips = null;
    if (queryForm.keywords) {
      tips = '搜索：' + queryForm.keywords;
    }
    if (queryForm.noticeTypeName) {
      tips = tips ? tips + '，' : '';
      tips = tips + '类型：' + queryForm.noticeTypeName;
    }
    if (queryForm.publishTimeBegin) {
      tips = tips ? tips + '，' : '';
      tips = tips + '发布开始时间：' + queryForm.publishTimeBegin;
    }
    if (queryForm.publishTimeEnd) {
      tips = tips ? tips + '，' : '';
      tips = tips + '发布截止时间：' + queryForm.publishTimeEnd;
    }
    return tips;
  }

  /**
   * 显示或者隐藏tips
   */
  function showOrHideQueryFormTips() {
    let tips = buildQueryFormTips();
    queryFormTipsMarginTop.value = _.isEmpty(tips) ? '0px' : '50rpx';
    showQueryFormTipsFlag.value = !_.isEmpty(tips);
    queryFormTips.value = tips;
  }

  /**
   * 关闭筛选条件 tips，清空搜索条件
   */
  function onCloseQueryFormTips() {
    Object.assign(queryForm, defaultForm);
    queryFormTipsMarginTop.value = '0px';
    showQueryFormTipsFlag.value = false;
    queryFormTips.value = '';
    search();
  }

  // --------------------------- 查询 ---------------------------------

  const defaultForm = {
    noticeTypeId: undefined, //分类
    noticeTypeName: undefined, //分类名称
    keywords: '', //标题、作者、来源
    publishTimeBegin: null, //发布-开始时间
    publishTimeEnd: null, //发布-截止时间
    pageNum: 1,
    pageSize: 10,
  };

  // 查询表单
  const queryForm = reactive({ ...defaultForm });
  // 通知列表数据
  const noticeListData = ref([]);

  function buildQueryParam(pageNum) {
    queryForm.pageNum = pageNum;
    return Object.assign({}, queryForm, { pageNum });
  }

  async function query(mescroll, isDownFlag, param) {
    try {
      let res = await noticeApi.queryEmployeeNotice(param);
      if (!isDownFlag) {
        noticeListData.value = noticeListData.value.concat(res.data.list);
      } else {
        noticeListData.value = res.data.list;
      }
      mescroll.endSuccess(res.data.list.length, res.data.pages > res.data.pageNum);
    } catch (e) {
      smartSentry.captureError(e);
      //联网失败, 结束加载
      mescroll.endErr();
    }
  }

  const { mescrollInit, getMescroll } = useMescroll(onPageScroll, onReachBottom);

  /**
   * 搜索
   */
  function search() {
    showOrHideQueryFormTips();
    query(getMescroll(), true, buildQueryParam(1));
    uni.pageScrollTo({
      scrollTop: 0,
    });
  }

  /**
   * 下拉刷新
   */
  function onDown(mescroll) {
    queryForm.pageNum = 1;
    query(mescroll, true, buildQueryParam(1));
  }

  /**
   * 上拉加载更多
   */
  function onUp(mescroll) {
    query(mescroll, false, buildQueryParam(mescroll.num));
  }

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

  .notice-page {
    background-color: #f5f5f5;
    min-height: 100vh;
  }

  .search-section {
    background: #fff;
    padding: 24rpx 32rpx;
    margin-bottom: 16rpx;
    border-radius: 0 0 16rpx 16rpx;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
  }

  .search-container {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  .search-input {
    flex: 1;

    :deep(.uni-easyinput__content) {
      background: #f8f9fa;
      border-radius: 12rpx;
      border: none;
      height: 72rpx;
    }

    :deep(.uni-easyinput__content-input) {
      font-size: 28rpx;
      color: #333;
    }

    :deep(.uni-easyinput__placeholder-class) {
      color: #999;
      font-size: 28rpx;
    }
  }

  .filter-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    width: 80rpx;
    height: 72rpx;
    background: #f8f9fa;
    border-radius: 12rpx;
    transition: all 0.2s ease;

    &:active {
      background: #e9ecef;
      transform: scale(0.95);
    }
  }

  .filter-text {
    font-size: 20rpx;
    color: #007aff;
    margin-top: 4rpx;
    font-weight: 500;
  }

  .query-bar {
    position: fixed;
    z-index: 999;
    border-radius: 0 0 16rpx 16rpx;
  }
</style>
