<template>
  <view class="todo-container">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-container">
      <uni-load-more status="loading" :content-text="loadingText"></uni-load-more>
    </view>

    <!-- 空状态 -->
    <view v-else-if="todoList.length === 0" class="empty-container">
      <image src="/static/images/empty.png" class="empty-image"></image>
      <text class="empty-text">暂无待办任务</text>
    </view>

    <!-- 任务列表 -->
    <view v-else>
      <view class="item" v-for="(item, index) in todoList" :key="item.id" @click="handleTaskClick(item)">
        <view class="item-header">
          <view class="header-title">{{ item.flowName || '流程任务' }}</view>
          <view class="header-right" :class="getTaskStatusClass(item.flowStatus)">
            {{ getTaskStatusText(item.flowStatus) }}
          </view>
        </view>
        <view class="task-info">
          <view class="info-row">
            <view class="info-label">任务节点：</view>
            <view class="info-value">{{ item.nodeName || '待处理' }}</view>
          </view>
          <view class="info-row">
            <view class="info-label">发起人：</view>
            <view class="info-value">{{ item.createBy || '系统' }}</view>
          </view>
          <view class="info-row">
            <view class="info-label">创建时间：</view>
            <view class="info-value">{{ formatDate(item.createTime) }}</view>
          </view>
          <view class="info-row" v-if="item.businessId">
            <view class="info-label">业务ID：</view>
            <view class="info-value">{{ item.businessId }}</view>
          </view>
        </view>
        <view class="footer">
          <view class="footer-left">
            <text class="task-id">任务ID：{{ item.id }}</text>
          </view>
          <view class="footer-right">
            <button class="handle-btn" @click.stop="handleTask(item)">处理</button>
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
const todoList = ref([]);
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

// 获取待办任务列表
const getTodoList = async (isRefresh = false) => {
  if (loading.value) return;

  try {
    loading.value = true;

    if (isRefresh) {
      queryForm.value.pageNum = 1;
      todoList.value = [];
    }

    const res = await instanceApi.toDoPage(queryForm.value);

    if (res.code === 0) {
      const newList = res.data.list || [];

      if (isRefresh) {
        todoList.value = newList;
      } else {
        todoList.value = [...todoList.value, ...newList];
      }

      total.value = res.data.total || 0;
      hasMore.value = todoList.value.length < total.value;

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
    console.error('获取待办任务失败:', error);
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

// 获取任务状态样式类
const getTaskStatusClass = (status) => {
  switch (status) {
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

// 获取任务状态文本
const getTaskStatusText = (status) => {
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
    url: `/pages/flow/task-detail?taskId=${item.id}&instanceId=${item.instanceId}`
  });
};

// 处理任务
const handleTask = (item) => {
  console.log('点击处理按钮，任务信息:', item);

  if (!item.id) {
    uni.showToast({
      title: '任务ID不能为空',
      icon: 'none'
    });
    return;
  }

  console.log('准备跳转到任务处理页面:', `/pages/flow/task-handle?taskId=${item.id}&instanceId=${item.instanceId}`);

  uni.navigateTo({
    url: `/pages/flow/task-handle?taskId=${item.id}&instanceId=${item.instanceId}`,
    success: (res) => {
      console.log('页面跳转成功:', res);
    },
    fail: (err) => {
      console.error('页面跳转失败:', err);
      uni.showToast({
        title: '页面跳转失败',
        icon: 'none'
      });
    }
  });
};

// 下拉刷新
const onRefresh = () => {
  getTodoList(true);
};

// 上拉加载更多
const onLoadMore = () => {
  if (hasMore.value && !loading.value) {
    getTodoList(false);
  }
};

// 组件挂载时获取数据
onMounted(() => {
  getTodoList(true);
});

// 暴露方法给父组件
defineExpose({
  refresh: onRefresh,
  loadMore: onLoadMore
});
</script>

<style lang="scss" scoped>
.todo-container {
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

  .item-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    position: relative;
    margin-bottom: 20rpx;

    .header-title {
      flex: 1;
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      margin-right: 20rpx;
    }

    .header-right {
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
      flex: 1;

      .task-id {
        font-size: 24rpx;
        color: #999;
      }
    }

    .footer-right {
      .handle-btn {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: #fff;
        border: none;
        border-radius: 24rpx;
        padding: 14rpx 28rpx;
        font-size: 28rpx;
        font-weight: 500;
        line-height: 1;
        box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
        transition: all 0.3s ease;

        &:active {
          transform: translateY(2rpx);
          box-shadow: 0 2rpx 8rpx rgba(102, 126, 234, 0.4);
        }
      }
    }
  }
}
</style>
