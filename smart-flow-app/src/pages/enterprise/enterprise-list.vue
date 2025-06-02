<template>
  <view class="smart-page enterprise-list-page">
    <mescroll-body @init="mescrollInit" :down="{ auto: false }" @down="onDown" @up="onUp" :up="{ toTop: { src: '' } }">
      <!-- 搜索栏 -->
      <uni-nav-bar :border="false" fixed :leftWidth="0" rightWidth="70px">
        <view class="search-input">
          <uni-easyinput
            prefixIcon="search"
            :clearable="true"
            trim="all"
            v-model="queryForm.keywords"
            placeholder="搜索：企业名称、联系人、联系电话等"
            @confirm="search"
            @clear="search"
          />
        </view>
        <template #right>
          <view class="search-button smart-flex smart-align--center" @click="search">
            <uni-icons type="search" size="30"></uni-icons>
            <text class="search-text smart-ml--xs">搜索</text>
          </view>
        </template>
      </uni-nav-bar>

      <!-- 企业列表 -->
      <view class="smart-container smart-pt--md">
        <view
          v-for="item in listData"
          :key="item.enterpriseId"
          class="smart-card enterprise-item smart-mb--md"
          @click="gotoDetail(item.enterpriseId)"
        >
          <!-- 企业名称行 -->
          <view class="smart-flex smart-justify--between smart-align--center smart-mb--sm">
            <view class="smart-flex smart-align--center smart-flex-item--1">
              <text class="item-label">公司：</text>
              <text class="smart-title--h5 smart-truncate">{{ item.enterpriseName }}</text>
            </view>
            <view class="contact-button smart-flex smart-align--center" @click.stop="callPhone(item.contactPhone)">
              <uni-icons type="phone" size="18" color="#1A9AFF"></uni-icons>
              <text class="contact-text smart-ml--xs">联系</text>
            </view>
          </view>

          <!-- 企业信息 -->
          <view class="enterprise-info">
            <view class="info-row smart-flex smart-mb--xs">
              <text class="item-label">营业执照：</text>
              <text class="smart-body--small smart-color--secondary smart-truncate">{{ item.unifiedSocialCreditCode }}</text>
            </view>
            <view class="info-row smart-flex smart-mb--xs">
              <text class="item-label">联系人：</text>
              <text class="smart-body--small smart-color--secondary">{{ item.contact }}</text>
            </view>
            <view class="info-row smart-flex">
              <text class="item-label">电话：</text>
              <text class="smart-body--small smart-color--secondary">{{ item.contactPhone }}</text>
            </view>
          </view>
        </view>
      </view>
    </mescroll-body>

    <!-- 添加按钮 -->
    <uni-fab ref="fab" :pattern="fabPattern" horizontal="right" @fabClick="gotoAdd" />
  </view>
</template>

<script setup>
  import { reactive, ref } from 'vue';
  import { enterpriseApi } from '@/api/business/oa/enterprise-api';
  import { onPageScroll, onReachBottom, onShow } from '@dcloudio/uni-app';
  import useMescroll from '@/uni_modules/uni-mescroll/hooks/useMescroll';
  import { smartSentry } from '@/lib/smart-sentry';
  import _ from 'lodash';

  // --------------------------- 查询 ---------------------------------

  const defaultForm = {
    keywords: '', //标题、作者、来源
    pageNum: 1,
    pageSize: 10,
  };

  // 查询表单
  const queryForm = reactive({ ...defaultForm });
  // 列表数据
  const listData = ref([]);

  function buildQueryParam(pageNum) {
    queryForm.pageNum = pageNum;
    return Object.assign({}, queryForm, { pageNum });
  }

  async function query(mescroll, isDownFlag, param) {
    try {
      let res = await enterpriseApi.pageQuery(param);
      if (!isDownFlag) {
        listData.value = listData.value.concat(res.data.list);
      } else {
        listData.value = res.data.list;
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

  onShow(() => {
    search();
  });

  // --------------------------- 添加 ---------------------------------

  const fabPattern = reactive({
    color: 'blue',
    backgroundColor: 'blue',
  });

  function gotoAdd() {
    uni.navigateTo({ url: '/pages/enterprise/enterprise-form' });
  }

  // --------------------------- 详情 ---------------------------------

  function callPhone(phone) {
    uni.makePhoneCall({
      phoneNumber: phone, //仅为示例
    });
  }
  function gotoDetail(id) {
    uni.navigateTo({ url: '/pages/enterprise/enterprise-detail?enterpriseId=' + id });
  }
</script>

<style lang="scss" scoped>
  .enterprise-list-page {
    background-color: $page-bg-color;
  }

  .search-input {
    width: 100%;
    height: 60rpx;
    background: $background-color;
    border-radius: 12rpx;
    margin: 8rpx 0;
    display: flex;
    align-items: center;
  }

  .search-button {
    width: 140rpx;
    height: 88rpx;
    cursor: pointer;
    transition: opacity 0.2s ease;

    &:active {
      opacity: 0.7;
    }

    .search-text {
      font-size: $small-size;
      color: $main-font-color;
    }
  }

  .enterprise-item {
    cursor: pointer;
    transition: all 0.2s ease;

    &:active {
      transform: scale(0.98);
      box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.12);
    }
  }

  .item-label {
    font-size: $small-size;
    color: $auxiliary-color;
    min-width: 120rpx;
    flex-shrink: 0;
  }

  .contact-button {
    padding: 8rpx 16rpx;
    background-color: #e3f2fd;
    border-radius: 8rpx;
    cursor: pointer;
    transition: background-color 0.2s ease;

    &:active {
      background-color: #bbdefb;
    }

    .contact-text {
      font-size: $small-size;
      color: $main-color;
      font-weight: 500;
    }
  }

  .enterprise-info {
    .info-row {
      align-items: flex-start;
    }
  }

  // FAB 按钮样式调整
  :deep(.uni-fab) {
    .uni-fab__content {
      background-color: $main-color !important;
      box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.16) !important;
    }
  }
</style>
