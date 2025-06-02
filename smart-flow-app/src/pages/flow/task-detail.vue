<template>
  <view class="task-detail-page">
    <!-- 标准导航栏 -->
    <uni-nav-bar title="任务详情" :border="false" fixed left-icon="back" @clickLeft="goBack" class="bold-title"></uni-nav-bar>

    <view class="content-container">
      <!-- 加载状态 -->
      <view v-if="loading" class="loading-container">
        <uni-load-more status="loading"></uni-load-more>
      </view>

      <!-- 任务详情 -->
      <view v-else-if="taskDetail" class="detail-container">
        <!-- 基本信息 -->
        <view class="info-card">
          <view class="card-title">基本信息</view>
          <view class="info-item">
            <text class="label">流程名称：</text>
            <text class="value">{{ taskDetail.flowName || '未知流程' }}</text>
          </view>
          <view class="info-item">
            <text class="label">任务节点：</text>
            <text class="value">{{ taskDetail.nodeName || '未知节点' }}</text>
          </view>
          <view class="info-item">
            <text class="label">任务状态：</text>
            <text class="value status" :class="getStatusClass(taskDetail.flowStatus)">
              {{ getStatusText(taskDetail.flowStatus) }}
            </text>
          </view>
          <view class="info-item">
            <text class="label">发起人：</text>
            <text class="value">{{ taskDetail.initiator || '系统' }}</text>
          </view>
          <view class="info-item">
            <text class="label">创建时间：</text>
            <text class="value">{{ formatDateTime(taskDetail.createTime) }}</text>
          </view>
          <view class="info-item" v-if="taskDetail.updateTime">
            <text class="label">更新时间：</text>
            <text class="value">{{ formatDateTime(taskDetail.updateTime) }}</text>
          </view>
          <view class="info-item" v-if="taskDetail.businessId">
            <text class="label">业务ID：</text>
            <text class="value">{{ taskDetail.businessId }}</text>
          </view>
        </view>

        <!-- 申请表单信息 -->
        <view v-if="taskDetail.businessForm" class="info-card">
          <view class="card-title">申请信息</view>

          <!-- 请假申请表单 -->
          <view v-if="isLeaveForm" class="form-content">
            <view class="info-item">
              <text class="label">请假类型：</text>
              <text class="value">{{ getLeaveTypeText(taskDetail.businessForm.type) }}</text>
            </view>
            <view class="info-item">
              <text class="label">请假原因：</text>
              <text class="value">{{ taskDetail.businessForm.reason || '无' }}</text>
            </view>
            <view class="info-item">
              <text class="label">开始时间：</text>
              <text class="value">{{ taskDetail.businessForm.startTime }}</text>
            </view>
            <view class="info-item">
              <text class="label">结束时间：</text>
              <text class="value">{{ taskDetail.businessForm.endTime }}</text>
            </view>
            <view class="info-item">
              <text class="label">请假天数：</text>
              <text class="value highlight">{{ taskDetail.businessForm.day }} 天</text>
            </view>
            <view class="info-item">
              <text class="label">申请时间：</text>
              <text class="value">{{ formatDateTime(taskDetail.businessForm.createTime) }}</text>
            </view>
          </view>

          <!-- 通用表单信息（其他类型的申请） -->
          <view v-else class="form-content">
            <view class="info-item" v-for="(value, key) in filteredBusinessForm" :key="key">
              <text class="label">{{ formatFieldLabel(key) }}：</text>
              <text class="value" :class="{ 'long-text': key === 'contentText' || key === 'contentHtml' }">
                {{ formatFieldValue(value, key) }}
              </text>
            </view>
          </view>
        </view>

        <!-- 处理信息（已办任务显示） -->
        <view v-if="type === 'done' && taskDetail.message" class="info-card">
          <view class="card-title">处理信息</view>
          <view class="info-item">
            <text class="label">处理意见：</text>
            <text class="value">{{ taskDetail.message }}</text>
          </view>
          <view class="info-item" v-if="taskDetail.skipType">
            <text class="label">处理结果：</text>
            <text class="value result" :class="getResultClass(taskDetail.skipType)">
              {{ getResultText(taskDetail.skipType) }}
            </text>
          </view>
        </view>

        <!-- 流程变量（如果有） -->
        <view v-if="taskDetail.variable && Object.keys(taskDetail.variable).length > 0" class="info-card">
          <view class="card-title">流程变量</view>
          <view class="info-item" v-for="(value, key) in taskDetail.variable" :key="key">
            <text class="label">{{ key }}：</text>
            <text class="value">{{ value }}</text>
          </view>
        </view>

        <!-- 审批历史 -->
        <view v-if="approvalHistory.length > 0" class="info-card">
          <view class="card-title">审批历史</view>
          <view class="approval-timeline">
            <view
              v-for="(record, index) in approvalHistory"
              :key="record.id || index"
              class="timeline-item"
              :class="{ 'is-last': index === approvalHistory.length - 1 }"
            >
              <view class="timeline-dot" :class="getApprovalStatusClass(record.skipType)"></view>
              <view class="timeline-content">
                <view class="timeline-header">
                  <view class="timeline-title">{{ record.nodeName || '审批节点' }}</view>
                  <view class="timeline-time">{{ formatDateTime(record.updateTime) }}</view>
                </view>
                <view class="timeline-info">
                  <view class="info-row">
                    <text class="info-label">处理人：</text>
                    <text class="info-value">{{ record.approver || record.createUserName || '系统' }}</text>
                  </view>
                  <view class="info-row">
                    <text class="info-label">处理结果：</text>
                    <text class="info-value result" :class="getResultClass(record.skipType)">
                      {{ getResultText(record.skipType) }}
                    </text>
                  </view>
                  <view class="info-row" v-if="record.message">
                    <text class="info-label">处理意见：</text>
                    <text class="info-value">{{ record.message }}</text>
                  </view>
                  <view class="info-row" v-if="record.createTime">
                    <text class="info-label">处理时间：</text>
                    <text class="info-value">{{ formatDateTime(record.createTime) }}</text>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 操作按钮（待办任务显示） -->
        <view v-if="type !== 'done' && type !== 'copy'" class="action-buttons">
          <button class="btn btn-primary" @click="handleTask">处理任务</button>
        </view>
      </view>

      <!-- 错误状态 -->
      <view v-else class="error-container">
        <image src="/static/images/error.png" class="error-image"></image>
        <text class="error-text">任务详情加载失败</text>
        <button class="retry-btn" @click="loadTaskDetail">重试</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { instanceApi } from '@/api/flow/instance-api.js';

// 页面参数
const props = defineProps({
  taskId: String,
  instanceId: String,
  type: {
    type: String,
    default: 'todo' // todo, done, copy
  }
});

// 响应式数据
const loading = ref(false);
const taskDetail = ref(null);
const approvalHistory = ref([]);

// 计算属性：判断是否为请假表单
const isLeaveForm = computed(() => {
  return Object.prototype.hasOwnProperty.call(taskDetail.value?.businessForm || {}, 'leaveId') ||
         taskDetail.value?.flowName?.includes('请假');
});

// 计算属性：过滤后的业务表单数据（用于通用表单显示）
const filteredBusinessForm = computed(() => {
  if (!taskDetail.value?.businessForm) return {};

  const form = taskDetail.value.businessForm;
  // 排除系统字段和流程相关字段
  const excludeFields = [
    'instanceId', 'nodeCode', 'nodeName', 'nodeType', 'flowStatus',
    'deletedFlag', 'createUserId', 'updateTime', 'createTime'
  ];

  const filtered = {};
  Object.keys(form).forEach(key => {
    if (!excludeFields.includes(key) && form[key] !== null && form[key] !== '') {
      filtered[key] = form[key];
    }
  });

  // 按照重要性排序字段
  const sortedKeys = Object.keys(filtered).sort((a, b) => {
    const priority = {
      'title': 1,
      'noticeId': 2,
      'noticeTypeId': 3,
      'author': 4,
      'source': 5,
      'documentNumber': 6,
      'publishTime': 7,
      'contentText': 8,
      'contentHtml': 9,
      'attachment': 10,
      'allVisibleFlag': 11,
      'scheduledPublishFlag': 12,
      'pageViewCount': 13,
      'userViewCount': 14,
      // 请假相关字段
      'leaveId': 2,
      'type': 3,
      'reason': 4,
      'startTime': 5,
      'endTime': 6,
      'day': 7
    };
    return (priority[a] || 999) - (priority[b] || 999);
  });

  const sortedFiltered = {};
  sortedKeys.forEach(key => {
    sortedFiltered[key] = filtered[key];
  });

  return sortedFiltered;
});

// 获取任务详情
const loadTaskDetail = async () => {
  if (!props.taskId) {
    uni.showToast({
      title: '任务ID不能为空',
      icon: 'none'
    });
    return;
  }

  try {
    loading.value = true;
    const res = await instanceApi.getTaskById(props.taskId);

    if (res.code === 0) {
      taskDetail.value = res.data;
      // 获取审批历史
      if (res.data.instanceId) {
        await loadApprovalHistory(res.data.instanceId);
      }
    } else {
      uni.showToast({
        title: res.msg || '获取任务详情失败',
        icon: 'none'
      });
    }
  } catch (error) {
    console.error('获取任务详情失败:', error);
    uni.showToast({
      title: '网络错误，请重试',
      icon: 'none'
    });
  } finally {
    loading.value = false;
  }
};

// 获取审批历史
const loadApprovalHistory = async (instanceId) => {
  try {
    console.log('开始获取审批历史，instanceId:', instanceId);
    const res = await instanceApi.doneList(instanceId);

    console.log('审批历史API响应:', res);

    if (res.code === 0 && res.data) {
      // 按时间排序，最新的在前面
      approvalHistory.value = res.data.sort((a, b) => {
        const timeA = new Date(a.updateTime || a.createTime);
        const timeB = new Date(b.updateTime || b.createTime);
        return timeB - timeA;
      });
      console.log('审批历史设置成功:', approvalHistory.value);
    } else {
      console.log('审批历史API返回错误或无数据:', res);
      approvalHistory.value = [];
    }
  } catch (error) {
    console.error('获取审批历史失败:', error);
    approvalHistory.value = [];
  }
};

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

// 获取请假类型文本
const getLeaveTypeText = (type) => {
  const typeMap = {
    '1': '事假',
    '2': '病假',
    '3': '年假',
    '4': '调休',
    '5': '婚假',
    '6': '产假',
    '7': '陪产假',
    '8': '丧假',
    '9': '其他'
  };
  return typeMap[type] || '未知类型';
};

// 格式化字段标签（优先使用businessFormMetaData中的中文名）
const formatFieldLabel = (key) => {
  // 首先尝试从businessFormMetaData中获取中文名
  if (taskDetail.value?.businessFormMetaData?.columns) {
    const column = taskDetail.value.businessFormMetaData.columns.find(col =>
      col.englishName === key ||
      col.englishName === toSnakeCase(key) ||
      toCamelCase(col.englishName) === key
    );
    if (column && column.chineseName && column.chineseName.trim() !== '') {
      return column.chineseName;
    }
  }

  // 如果没有找到，使用默认映射
  const labelMap = {
    'leaveId': '请假ID',
    'type': '类型',
    'reason': '原因',
    'startTime': '开始时间',
    'endTime': '结束时间',
    'day': '天数',
    'createTime': '创建时间',
    'updateTime': '更新时间',
    'title': '标题',
    'content': '内容',
    'amount': '金额',
    'description': '描述',
    'noticeId': '公告ID',
    'noticeTypeId': '公告类型',
    'allVisibleFlag': '全部可见',
    'scheduledPublishFlag': '定时发布',
    'publishTime': '发布时间',
    'contentText': '文本内容',
    'contentHtml': 'HTML内容',
    'attachment': '附件',
    'pageViewCount': '页面浏览量',
    'userViewCount': '用户浏览量',
    'source': '来源',
    'author': '作者',
    'documentNumber': '文号',
    'instanceId': '流程实例ID',
    'nodeName': '当前节点',
    'flowStatus': '流程状态',
    'deletedFlag': '删除标志',
    'createUserId': '创建人ID'
  };
  return labelMap[key] || key;
};

// 驼峰转下划线
const toSnakeCase = (str) => {
  return str.replace(/[A-Z]/g, letter => `_${letter.toLowerCase()}`);
};

// 下划线转驼峰
const toCamelCase = (str) => {
  return str.replace(/_([a-z])/g, (match, letter) => letter.toUpperCase());
};

// 格式化字段值
const formatFieldValue = (value, key) => {
  if (value === null || value === undefined) return '无';

  // 布尔值处理
  if (typeof value === 'boolean') return value ? '是' : '否';

  // 特殊字段处理
  if (key === 'contentText' || key === 'contentHtml') {
    // 长文本截断显示
    const text = String(value);
    return text.length > 100 ? text.substring(0, 100) + '...' : text;
  }

  if (key === 'attachment' && value === '') {
    return '无附件';
  }

  if (key === 'noticeTypeId') {
    // 公告类型映射
    const typeMap = {
      '1': '系统公告',
      '2': '业务公告',
      '3': '紧急公告'
    };
    return typeMap[value] || `类型${value}`;
  }

  if (key === 'flowStatus') {
    // 流程状态映射
    const statusMap = {
      '0': '待提交',
      '1': '审批中',
      '2': '审批通过',
      '3': '自动通过',
      '4': '终止',
      '5': '作废',
      '6': '撤销',
      '7': '取回',
      '8': '已完成',
      '9': '已退回',
      '10': '失效'
    };
    return statusMap[value] || '未知状态';
  }

  // 时间格式化
  if (key.includes('Time') && value) {
    try {
      const date = new Date(value);
      return formatDateTime(date.toISOString());
    } catch (e) {
      return String(value);
    }
  }

  // 对象类型转JSON
  if (typeof value === 'object') return JSON.stringify(value);

  return String(value);
};

// 获取状态样式类
const getStatusClass = (status) => {
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

// 获取状态文本
const getStatusText = (status) => {
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

// 获取处理结果样式类
const getResultClass = (skipType) => {
  switch (skipType) {
    case 'PASS': return 'result-pass';
    case 'REJECT': return 'result-reject';
    case 'BACK': return 'result-back';
    default: return 'result-default';
  }
};

// 获取处理结果文本
const getResultText = (skipType) => {
  switch (skipType) {
    case 'PASS': return '通过';
    case 'REJECT': return '拒绝';
    case 'BACK': return '退回';
    default: return '已处理';
  }
};

// 获取审批状态样式类（用于时间线圆点）
const getApprovalStatusClass = (skipType) => {
  switch (skipType) {
    case 'PASS': return 'dot-pass';
    case 'REJECT': return 'dot-reject';
    case 'BACK': return 'dot-back';
    default: return 'dot-default';
  }
};

// 处理任务
const handleTask = () => {
  uni.navigateTo({
    url: `/pages/flow/task-handle?taskId=${props.taskId}&instanceId=${props.instanceId}`
  });
};

// 返回
const goBack = () => {
  uni.navigateBack();
};

// 页面加载时获取数据
onMounted(() => {
  loadTaskDetail();
});
</script>

<style lang="scss" scoped>
.task-detail-page {
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

.detail-container {
  .info-card {
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
        word-break: break-all;
        
        &.status {
          font-weight: bold;

          &.status-pending {
            color: #ff9500;        // 待提交 - 橙色
          }
          &.status-active {
            color: #007aff;        // 审批中 - 蓝色
          }
          &.status-finished {
            color: #34c759;        // 审批通过/自动通过/已完成 - 绿色
          }
          &.status-terminated {
            color: #8e8e93;        // 终止 - 灰色
          }
          &.status-invalid {
            color: #8e8e93;        // 作废 - 灰色
          }
          &.status-revoked {
            color: #ff9500;        // 撤销 - 橙色
          }
          &.status-retrieved {
            color: #ff9500;        // 取回 - 橙色
          }
          &.status-returned {
            color: #ff3b30;        // 已退回 - 红色
          }
          &.status-expired {
            color: #8e8e93;        // 失效 - 灰色
          }
          &.status-unknown {
            color: #8e8e93;        // 未知状态 - 灰色
          }
        }
        
        &.result {
          font-weight: bold;

          &.result-pass {
            color: #34c759;
          }
          &.result-reject {
            color: #ff3b30;
          }
          &.result-back {
            color: #ff9500;
          }
          &.result-default {
            color: #007aff;
          }
        }

        &.highlight {
          color: #007aff;
          font-weight: bold;
        }

        &.long-text {
          line-height: 1.6;
          word-break: break-all;
          background: #f7f8f9;
          padding: 12rpx 16rpx;
          border-radius: 8rpx;
          margin-top: 8rpx;
          display: block;
          width: 100%;
        }
      }
    }
  }
  
  .action-buttons {
    display: flex;
    justify-content: center;
    margin-top: 40rpx;

    .btn {
      width: 300rpx;
      height: 88rpx;
      border: none;
      border-radius: 44rpx;
      font-size: 32rpx;
      font-weight: bold;

      &.btn-primary {
        background: linear-gradient(112deg, #007aff 9%, #0056d3 84%);
        color: #fff;
      }

      &.btn-secondary {
        background: #f0f0f0;
        color: #666;
      }
    }
  }

  // 审批记录时间线样式
  .approval-timeline {
    .timeline-item {
      position: relative;
      padding-left: 60rpx;
      padding-bottom: 40rpx;

      &:not(.is-last)::after {
        content: '';
        position: absolute;
        left: 20rpx;
        top: 40rpx;
        bottom: -40rpx;
        width: 2rpx;
        background: #e5e5e5;
      }

      .timeline-dot {
        position: absolute;
        left: 12rpx;
        top: 8rpx;
        width: 16rpx;
        height: 16rpx;
        border-radius: 50%;
        border: 2rpx solid #fff;
        box-shadow: 0 0 0 2rpx #e5e5e5;

        &.dot-pass {
          background: #34c759;
          box-shadow: 0 0 0 2rpx #34c759;
        }

        &.dot-reject {
          background: #ff3b30;
          box-shadow: 0 0 0 2rpx #ff3b30;
        }

        &.dot-back {
          background: #ff9500;
          box-shadow: 0 0 0 2rpx #ff9500;
        }

        &.dot-default {
          background: #007aff;
          box-shadow: 0 0 0 2rpx #007aff;
        }
      }

      .timeline-content {
        .timeline-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 16rpx;

          .timeline-title {
            font-size: 30rpx;
            font-weight: bold;
            color: #333;
          }

          .timeline-time {
            font-size: 24rpx;
            color: #999;
          }
        }

        .timeline-info {
          .info-row {
            display: flex;
            align-items: flex-start;
            margin-bottom: 12rpx;

            &:last-child {
              margin-bottom: 0;
            }

            .info-label {
              font-size: 26rpx;
              color: #666;
              width: 160rpx;
              flex-shrink: 0;
            }

            .info-value {
              font-size: 26rpx;
              color: #333;
              flex: 1;
              word-break: break-all;

              &.result {
                font-weight: bold;

                &.result-pass {
                  color: #34c759;
                }
                &.result-reject {
                  color: #ff3b30;
                }
                &.result-back {
                  color: #ff9500;
                }
                &.result-default {
                  color: #007aff;
                }
              }
            }
          }
        }
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
