<template>
  <view class="copy-container">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <uni-load-more status="loading" :content-text="loadingText"></uni-load-more>
    </view>

    <!-- 空状态 -->
    <view v-else-if="copyList.length === 0" class="empty-container">
      <image src="/static/images/empty.png" class="empty-image"></image>
      <text class="empty-text">暂无抄送任务</text>
    </view>

    <!-- 任务列表 -->
    <view v-else>
      <view class="item" v-for="(item, index) in copyList" :key="item.id" :class="getCopyTypeClass(item.flowStatus)" @click="handleTaskClick(item)">
        <view class="header">{{ item.flowName || '流程任务' }}</view>
        <view class="task-info">
          <view class="info-row">
            <view class="info-label">任务节点：</view>
            <view class="info-value">{{ item.nodeName || '抄送' }}</view>
          </view>
          <view class="info-row">
            <view class="info-label">发起人：</view>
            <view class="info-value">{{ item.createBy || '系统' }}</view>
          </view>
          <view class="info-row">
            <view class="info-label">抄送时间：</view>
            <view class="info-value">{{ formatDateTime(item.createTime) }}</view>
          </view>
          <view class="info-row" v-if="item.businessId">
            <view class="info-label">业务ID：</view>
            <view class="info-value">{{ item.businessId }}</view>
          </view>
        </view>
        <view class="footer">
          <view class="footer-left">任务ID：{{ item.id }}</view>
          <view class="footer-state" :class="getCopyTypeClass(item.flowStatus)">
            {{ getCopyStatusText(item.flowStatus) }}
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
const copyList = ref([]);
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

// 获取抄送任务列表
const getCopyList = async (isRefresh = false) => {
  if (loading.value) return;

  try {
    loading.value = true;

    if (isRefresh) {
      queryForm.value.pageNum = 1;
      copyList.value = [];
    }

    const res = await instanceApi.copyPage(queryForm.value);

    if (res.code === 0) {
      const newList = res.data.list || [];

      if (isRefresh) {
        copyList.value = newList;
      } else {
        copyList.value = [...copyList.value, ...newList];
      }

      total.value = res.data.total || 0;
      hasMore.value = copyList.value.length < total.value;

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
    console.error('获取抄送任务失败:', error);
    uni.showToast({
      title: '网络错误，请重试',
      icon: 'none'
    });
  } finally {
    loading.value = false;
  }
};

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

// 获取抄送类型样式类
const getCopyTypeClass = (status) => {
  switch (status) {
    case '0': return 'pending';       // 待提交
    case '1': return 'primary';       // 审批中
    case '2': return 'success';       // 审批通过
    case '3': return 'success';       // 自动通过
    case '4': return 'terminated';    // 终止
    case '5': return 'invalid';       // 作废
    case '6': return 'warning';       // 撤销
    case '7': return 'warning';       // 取回
    case '8': return 'success';       // 已完成
    case '9': return 'danger';        // 已退回
    case '10': return 'expired';      // 失效
    default: return 'default';
  }
};

// 获取抄送状态文本
const getCopyStatusText = (status) => {
  switch (status) {
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
    url: `/pages/flow/task-detail?taskId=${item.id}&instanceId=${item.instanceId}&type=copy`
  });
};

// 下拉刷新
const onRefresh = () => {
  getCopyList(true);
};

// 上拉加载更多
const onLoadMore = () => {
  if (hasMore.value && !loading.value) {
    getCopyList(false);
  }
};

// 组件挂载时获取数据
onMounted(() => {
  getCopyList(true);
});

// 暴露方法给父组件
defineExpose({
  refresh: onRefresh,
  loadMore: onLoadMore
});
</script>

<style lang="scss" scoped>
.copy-container {
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
  border-left: 8rpx solid #4f4d4d;

  &.pending {
    border-color: #ff9500;        // 待提交 - 橙色
  }
  &.primary {
    border-color: #007aff;        // 审批中 - 蓝色
  }
  &.success {
    border-color: #34c759;        // 成功类 - 绿色
  }
  &.terminated {
    border-color: #8e8e93;        // 终止 - 灰色
  }
  &.invalid {
    border-color: #8e8e93;        // 作废 - 灰色
  }
  &.warning {
    border-color: #ff9500;        // 撤销/取回 - 橙色
  }
  &.danger {
    border-color: #ff3b30;        // 已退回 - 红色
  }
  &.expired {
    border-color: #8e8e93;        // 失效 - 灰色
  }
  &.default {
    border-color: #8e8e93;        // 默认 - 灰色
  }

  .header {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    padding-bottom: 20rpx;
    border-bottom: 1rpx solid #ededed;
    margin-bottom: 20rpx;
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
    font-size: 28rpx;

    .footer-left {
      color: #999;
      font-size: 24rpx;
      flex: 1;
    }

    .footer-state {
      font-weight: bold;
      color: #4f4d4d;

      &.pending {
        color: #ff9500;        // 待提交 - 橙色
      }
      &.primary {
        color: #007aff;        // 审批中 - 蓝色
      }
      &.success {
        color: #34c759;        // 成功类 - 绿色
      }
      &.terminated {
        color: #8e8e93;        // 终止 - 灰色
      }
      &.invalid {
        color: #8e8e93;        // 作废 - 灰色
      }
      &.warning {
        color: #ff9500;        // 撤销/取回 - 橙色
      }
      &.danger {
        color: #ff3b30;        // 已退回 - 红色
      }
      &.expired {
        color: #8e8e93;        // 失效 - 灰色
      }
      &.default {
        color: #8e8e93;        // 默认 - 灰色
      }
    }
  }
}
</style>
