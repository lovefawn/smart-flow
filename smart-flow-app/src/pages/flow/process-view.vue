<template>
  <view class="process-view-page">
    <!-- 标准导航栏 -->
    <uni-nav-bar title="流程图" :border="false" fixed left-icon="back" @clickLeft="goBack" class="bold-title"></uni-nav-bar>

    <view class="content-container">
      <!-- 加载状态 -->
      <view v-if="loading" class="loading-container">
        <uni-load-more status="loading"></uni-load-more>
      </view>

      <!-- 流程信息 -->
      <view v-else-if="processInfo" class="process-container">
        <!-- 基本信息 -->
        <view class="info-card">
          <view class="card-title">流程信息</view>
          <view class="info-item">
            <text class="label">流程名称：</text>
            <text class="value">{{ processInfo.flowName || '未知流程' }}</text>
          </view>
          <view class="info-item">
            <text class="label">流程状态：</text>
            <text class="value status" :class="getStatusClass(processInfo.flowStatus)">
              {{ getStatusText(processInfo.flowStatus) }}
            </text>
          </view>
          <view class="info-item">
            <text class="label">当前节点：</text>
            <text class="value">{{ processInfo.nodeName || '未知节点' }}</text>
          </view>
          <view class="info-item">
            <text class="label">发起时间：</text>
            <text class="value">{{ formatDateTime(processInfo.createTime) }}</text>
          </view>
          <view class="info-item" v-if="processInfo.businessId">
            <text class="label">业务ID：</text>
            <text class="value">{{ processInfo.businessId }}</text>
          </view>
        </view>

        <!-- 流程图占位 -->
        <view class="chart-card">
          <view class="card-title">流程图</view>
          <view class="chart-placeholder">
            <image src="/static/images/flow-chart.png" class="chart-image" mode="aspectFit"></image>
            <text class="chart-text">流程图功能开发中...</text>
          </view>
        </view>

        <!-- 历史记录 -->
        <view class="history-card">
          <view class="card-title">处理历史</view>
          <view v-if="historyList.length === 0" class="empty-history">
            <text>暂无处理历史</text>
          </view>
          <view v-else class="history-list">
            <view class="history-item" v-for="(item, index) in historyList" :key="index">
              <view class="history-node">{{ item.nodeName }}</view>
              <view class="history-user">{{ item.approver }}</view>
              <view class="history-time">{{ formatDateTime(item.createTime) }}</view>
              <view class="history-message">{{ item.message || '无处理意见' }}</view>
            </view>
          </view>
        </view>
      </view>

      <!-- 错误状态 -->
      <view v-else class="error-container">
        <image src="/static/images/error.png" class="error-image"></image>
        <text class="error-text">流程信息加载失败</text>
        <button class="retry-btn" @click="loadProcessInfo">重试</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { instanceApi } from '@/api/flow/instance-api.js';

// 页面参数
const props = defineProps({
  instanceId: String
});

// 响应式数据
const loading = ref(false);
const processInfo = ref(null);
const historyList = ref([]);

// 获取流程信息
const loadProcessInfo = async () => {
  if (!props.instanceId) {
    uni.showToast({
      title: '实例ID不能为空',
      icon: 'none'
    });
    return;
  }

  try {
    loading.value = true;
    
    // 这里可以调用获取流程实例详情的API
    // const res = await instanceApi.getInstanceById(props.instanceId);
    
    // 模拟数据
    processInfo.value = {
      flowName: '请假流程',
      flowStatus: '1',
      nodeName: '主管审批',
      createTime: new Date().toISOString(),
      businessId: '12345'
    };
    
    historyList.value = [
      {
        nodeName: '发起申请',
        approver: '张三',
        createTime: new Date().toISOString(),
        message: '申请请假'
      }
    ];
    
  } catch (error) {
    console.error('获取流程信息失败:', error);
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

// 获取状态样式类
const getStatusClass = (status) => {
  switch (status) {
    case '1': return 'status-active';
    case '2': return 'status-finished';
    case '8': return 'status-finished';
    case '9': return 'status-back';
    default: return 'status-pending';
  }
};

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case '1': return '审批中';
    case '2': return '审批通过';
    case '8': return '已完成';
    case '9': return '已退回';
    default: return '待处理';
  }
};

// 返回
const goBack = () => {
  uni.navigateBack();
};

// 页面加载时获取数据
onMounted(() => {
  loadProcessInfo();
});
</script>

<style lang="scss" scoped>
.process-view-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.content-container {
  padding: 20rpx;
  margin-top: 44px; // 导航栏高度
}

.loading-container {
  padding: 100rpx 0;
  text-align: center;
}

.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
  
  .error-image {
    width: 200rpx;
    height: 200rpx;
    margin-bottom: 20rpx;
  }
  
  .error-text {
    font-size: 28rpx;
    color: #999;
    margin-bottom: 40rpx;
  }
  
  .retry-btn {
    background: #007aff;
    color: #fff;
    border: none;
    border-radius: 20rpx;
    padding: 16rpx 32rpx;
    font-size: 28rpx;
  }
}

.process-container {
  .info-card, .chart-card, .history-card {
    background: #fff;
    border-radius: 12rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.1);
    
    .card-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 24rpx;
      padding-bottom: 16rpx;
      border-bottom: 2rpx solid #f0f0f0;
    }
  }
  
  .info-item {
    display: flex;
    align-items: flex-start;
    margin-bottom: 20rpx;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .label {
      font-size: 28rpx;
      color: #666;
      width: 160rpx;
      flex-shrink: 0;
    }
    
    .value {
      font-size: 28rpx;
      color: #333;
      flex: 1;
      
      &.status {
        font-weight: bold;
        
        &.status-pending {
          color: #ff9500;
        }
        &.status-active {
          color: #007aff;
        }
        &.status-finished {
          color: #34c759;
        }
        &.status-back {
          color: #ff3b30;
        }
      }
    }
  }
  
  .chart-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60rpx 0;
    
    .chart-image {
      width: 120rpx;
      height: 120rpx;
      margin-bottom: 20rpx;
      opacity: 0.5;
    }
    
    .chart-text {
      font-size: 28rpx;
      color: #999;
    }
  }
  
  .empty-history {
    text-align: center;
    padding: 60rpx 0;
    color: #999;
    font-size: 28rpx;
  }
  
  .history-list {
    .history-item {
      padding: 20rpx 0;
      border-bottom: 1rpx solid #f0f0f0;
      
      &:last-child {
        border-bottom: none;
      }
      
      .history-node {
        font-size: 30rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 8rpx;
      }
      
      .history-user {
        font-size: 26rpx;
        color: #666;
        margin-bottom: 8rpx;
      }
      
      .history-time {
        font-size: 24rpx;
        color: #999;
        margin-bottom: 8rpx;
      }
      
      .history-message {
        font-size: 28rpx;
        color: #333;
        background: #f7f8f9;
        padding: 12rpx 16rpx;
        border-radius: 8rpx;
      }
    }
  }
}

// 导航栏标题加粗
:deep(.bold-title) {
  .uni-navbar__header-container {
    .uni-navbar__header-container-inner {
      font-weight: 600 !important;
      font-size: 34rpx !important;
    }
  }
}
</style>
