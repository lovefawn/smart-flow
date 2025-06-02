<template>
  <view class="done-container">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <uni-load-more status="loading" :content-text="loadingText"></uni-load-more>
    </view>

    <!-- 空状态 -->
    <view v-else-if="doneList.length === 0" class="empty-container">
      <image src="/static/images/empty.png" class="empty-image"></image>
      <text class="empty-text">暂无已办任务</text>
    </view>

    <!-- 任务列表 -->
    <view v-else>
      <view class="item" v-for="(item, index) in doneList" :key="item.id" @click="handleTaskClick(item)">
        <view class="header">
          <view class="header-left">
            <view class="header-left-title">{{ item.flowName || '流程任务' }}</view>
            <view class="header-left-time">完成时间：{{ formatDate(item.updateTime) }}</view>
          </view>
          <view class="header-right-status" :class="getTaskResultClass(item.flowStatus)">
            {{ getTaskResultText(item.flowStatus) }}
          </view>
        </view>
        <view class="task-info">
          <view class="info-row">
            <view class="info-label">任务节点：</view>
            <view class="info-value">{{ item.nodeName || '已处理' }}</view>
          </view>
          <view class="info-row">
            <view class="info-label">处理意见：</view>
            <view class="info-value">{{ item.message || '无' }}</view>
          </view>
          <view class="info-row">
            <view class="info-label">处理时间：</view>
            <view class="info-value">{{ formatDateTime(item.updateTime) }}</view>
          </view>
          <view class="info-row" v-if="item.businessId">
            <view class="info-label">业务ID：</view>
            <view class="info-value">{{ item.businessId }}</view>
          </view>
        </view>
        <view class="footer">
          <view class="footer-left">任务ID：{{ item.id }}</view>
          <view class="footer-right">
            <button class="view-btn" @click.stop="viewDetail(item)">查看详情</button>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { instanceApi } from '@/api/flow/instance-api.js';

// 响应式数据
const loading = ref(false);
const doneList = ref([]);
const queryForm = ref({
  pageNum: 1,
  pageSize: 10
});
const total = ref(0);
const hasMore = ref(true);

const loadingText = ref({
  contentdown: '下拉刷新',
  contentrefresh: '正在刷新...',
  contentnomore: '没有更多数据了'
});

// 获取已办任务列表
const getDoneList = async (isRefresh = false) => {
  if (loading.value) return;

  try {
    loading.value = true;

    if (isRefresh) {
      queryForm.value.pageNum = 1;
      doneList.value = [];
    }

    const res = await instanceApi.donePage(queryForm.value);

    if (res.code === 0) {
      const newList = res.data.list || [];

      if (isRefresh) {
        doneList.value = newList;
      } else {
        doneList.value = [...doneList.value, ...newList];
      }

      total.value = res.data.total || 0;
      hasMore.value = doneList.value.length < total.value;

      // 如果有更多数据，页码+1
      if (hasMore.value) {
        queryForm.value.pageNum++;
      }
    } else {
      uni.showToast({
        title: res.msg || '获取数据失败',
        icon: 'none'
      });
    }
  } catch (error) {
    console.error('获取已办任务失败:', error);
    uni.showToast({
      title: '网络错误，请重试',
      icon: 'none'
    });
  } finally {
    loading.value = false;
  }
};

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
};

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

// 获取任务结果样式类
const getTaskResultClass = (flowStatus) => {
  switch (flowStatus){
    case '0': return 'status-pending';      // 待提交
    case '1': return 'status-active';       // 审批中
    case '2': return 'status-finished';     // 审批通过
    case '3': return 'status-finished';     // 自动通过
    case '4': return 'status-terminated';   // 终止
    case '5': return 'status-invalid';      // 作废
    case '6': return 'status-revoked';      // 撤销
    case '7': return 'status-retrieved';    // 取回
    case '8': return 'status-finished';     // 已完成
    case '9': return 'status-returned';     // 已退回
    case '10': return 'status-expired';     // 失效
    default: return 'status-unknown';
  }
};

// 获取任务结果文本
const getTaskResultText = (flowStatus) => {
  switch (flowStatus) {
    case '0': return '待提交';
    case '1': return '审批中';
    case '2': return '审批通过';
    case '3': return '自动通过';
    case '4': return '终止';
    case '5': return '作废';
    case '6': return '撤销';
    case '7': return '取回';
    case '8': return '已完成';
    case '9': return '已退回';
    case '10': return '失效';
    default: return '未知状态';
  }
};

// 点击任务项
const handleTaskClick = (item) => {
  uni.navigateTo({
    url: `/pages/flow/task-done-detail?taskId=${item.id}&instanceId=${item.instanceId}&type=done`
  });
};

// 查看详情
const viewDetail = (item) => {
  uni.navigateTo({
    url: `/pages/flow/task-done-detail?taskId=${item.id}&instanceId=${item.instanceId}&type=done`
  });
};

// 下拉刷新
const onRefresh = () => {
  getDoneList(true);
};

// 上拉加载更多
const onLoadMore = () => {
  if (hasMore.value && !loading.value) {
    getDoneList(false);
  }
};

// 组件挂载时获取数据
onMounted(() => {
  getDoneList(true);
});

// 暴露方法给父组件
defineExpose({
  refresh: onRefresh,
  loadMore: onLoadMore
});
</script>

<style lang="scss" scoped>
.done-container {
  padding: 20rpx;
}

.loading-container {
  padding: 40rpx 0;
  text-align: center;
}

.empty-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;

  .empty-image {
    width: 200rpx;
    height: 200rpx;
    margin-bottom: 20rpx;
  }

  .empty-text {
    font-size: 28rpx;
    color: #999;
  }
}

.item {
  width: 100%;
  margin-bottom: 20rpx;
  background: #ffffff;
  border-radius: 12rpx;
  box-shadow: 0px 3px 4px 0px rgba(24, 144, 255, 0.06);
  padding: 26rpx 30rpx 20rpx;
  box-sizing: border-box;

  .header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding-bottom: 20rpx;
    border-bottom: 1rpx dashed #ededed;
    margin-bottom: 20rpx;

    .header-left {
      flex: 1;
      margin-right: 20rpx;

      .header-left-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 8rpx;
      }

      .header-left-time {
        font-size: 24rpx;
        color: #999;
      }
    }

    .header-right-status {
      padding: 8rpx 16rpx;
      border-radius: 20rpx;
      font-size: 24rpx;
      color: #fff;
      white-space: nowrap;

      &.status-pending {
        background: #ff9500;        // 待提交 - 橙色
      }
      &.status-active {
        background: #007aff;        // 审批中 - 蓝色
      }
      &.status-finished {
        background: #34c759;        // 审批通过/自动通过/已完成 - 绿色
      }
      &.status-terminated {
        background: #8e8e93;        // 终止 - 灰色
      }
      &.status-invalid {
        background: #8e8e93;        // 作废 - 灰色
      }
      &.status-revoked {
        background: #ff9500;        // 撤销 - 橙色
      }
      &.status-retrieved {
        background: #ff9500;        // 取回 - 橙色
      }
      &.status-returned {
        background: #ff3b30;        // 已退回 - 红色
      }
      &.status-expired {
        background: #8e8e93;        // 失效 - 灰色
      }
      &.status-unknown {
        background: #8e8e93;        // 未知状态 - 灰色
      }
    }
  }

  .task-info {
    background: #f7f8f9;
    border-radius: 8rpx;
    padding: 20rpx;
    margin-bottom: 20rpx;

    .info-row {
      display: flex;
      align-items: center;
      margin-bottom: 12rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .info-label {
        font-size: 28rpx;
        color: #666;
        width: 140rpx;
        flex-shrink: 0;
      }

      .info-value {
        font-size: 28rpx;
        color: #333;
        flex: 1;
      }
    }
  }

  .footer {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .footer-left {
      font-size: 24rpx;
      color: #999;
      flex: 1;
    }

    .footer-right {
      .view-btn {
        background: #f0f0f0;
        color: #666;
        border: none;
        border-radius: 20rpx;
        padding: 12rpx 24rpx;
        font-size: 28rpx;
        line-height: 1;
      }
    }
  }
}
</style>
