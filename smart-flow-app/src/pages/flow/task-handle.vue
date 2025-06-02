<template>
  <view class="task-handle-page">
    <!-- 标准导航栏 -->
    <uni-nav-bar title="处理任务" :border="false" fixed left-icon="back" @clickLeft="goBack" class="bold-title"></uni-nav-bar>

    <view class="content-container">
      <!-- 加载状态 -->
      <view v-if="loading" class="loading-container">
        <uni-load-more status="loading"></uni-load-more>
      </view>

      <!-- 任务处理表单 -->
      <view v-else-if="taskDetail" class="handle-container">
        <!-- 任务信息 -->
        <view class="info-card">
          <view class="card-title">任务信息</view>
          <view class="info-item">
            <text class="label">流程名称：</text>
            <text class="value">{{ taskDetail.flowName || '未知流程' }}</text>
          </view>
          <view class="info-item">
            <text class="label">任务节点：</text>
            <text class="value">{{ taskDetail.nodeName || '未知节点' }}</text>
          </view>
          <view class="info-item">
            <text class="label">发起人：</text>
            <text class="value">{{ taskDetail.initiator || '系统' }}</text>
          </view>
          <view class="info-item">
            <text class="label">创建时间：</text>
            <text class="value">{{ formatDateTime(taskDetail.createTime) }}</text>
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

        <!-- 处理表单 -->
        <view class="form-card">
          <view class="card-title">处理操作</view>
          
          <!-- 处理类型选择
          <view class="form-item">
            <text class="form-label">处理操作</text>
            <view class="action-buttons">
              <button
                class="action-btn active"
              >
                审批通过
              </button>
            </view>
            <text class="form-tip">当前版本仅支持审批通过操作</text>
          </view> -->

          <!-- 处理意见 -->
          <view class="form-item">
            <text class="form-label">处理意见</text>
            <textarea 
              class="form-textarea"
              v-model="handleForm.message"
              placeholder="请输入处理意见（可选）"
              maxlength="500"
            ></textarea>
            <text class="char-count">{{ handleForm.message.length }}/500</text>
          </view>

          <!-- 流程变量（如果需要） -->
          <view v-if="showVariables" class="form-item">
            <text class="form-label">流程变量</text>
            <view class="variable-item" v-for="(variable, key) in handleForm.variables" :key="key">
              <text class="variable-label">{{ key }}：</text>
              <input 
                class="variable-input"
                v-model="handleForm.variables[key]"
                :placeholder="`请输入${key}`"
              />
            </view>
          </view>
        </view>

        <!-- 提交按钮 -->
        <view class="submit-container">
          <button
            class="submit-btn"
            :class="{ disabled: submitting }"
            :disabled="submitting"
            @click="submitHandle"
          >
            {{ submitting ? '提交中...' : '同意' }}
          </button>
        </view>
      </view>

      <!-- 错误状态 -->
      <view v-else class="error-container">
        <image src="/static/images/error.png" class="error-image"></image>
        <text class="error-text">任务信息加载失败</text>
        <button class="retry-btn" @click="loadTaskDetail">重试</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { instanceApi } from '@/api/flow/instance-api.js';

// 页面参数
const props = defineProps({
  taskId: String,
  instanceId: String
});

// 响应式数据
const loading = ref(false);
const submitting = ref(false);
const taskDetail = ref(null);
const showVariables = ref(false);

// 处理表单
const handleForm = reactive({
  taskId: props.taskId,
  instanceId: props.instanceId,
  skipType: '', // PASS, REJECT, BACK
  message: '',
  variables: {},
  flowCopyList: [], // 抄送人员
  messageType: [], // 消息类型
  notice: '', // 消息通知
  assigneeMap: {}, // 弹窗选择的办理人
  ext: '', // 扩展变量
  fileId: '' // 附件ID
});

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
      
      // 如果任务有变量，显示变量编辑区域
      if (res.data.variable && Object.keys(res.data.variable).length > 0) {
        showVariables.value = true;
        handleForm.variables = { ...res.data.variable };
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

// 设置处理类型（当前版本固定为通过）
const setHandleType = (type) => {
  // 当前版本仅支持通过操作
  handleForm.skipType = 'PASS';
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

// 提交处理
const submitHandle = async () => {
  if (!handleForm.taskId) {
    uni.showToast({
      title: '任务ID不能为空',
      icon: 'none'
    });
    return;
  }

  try {
    submitting.value = true;
    
    // 构建提交数据
    const submitData = {
      taskId: handleForm.taskId,
      businessId: taskDetail.value?.businessId || '',
      flowCode: taskDetail.value?.flowCode || '',
      message: handleForm.message || '',
      variables: {
        ...handleForm.variables,
        flowCode: taskDetail.value?.flowCode || '',
        businessId: taskDetail.value?.businessId || ''
      },
    };

    console.log('提交数据:', submitData);
    console.log('businessId:', taskDetail.value?.businessId);
    console.log('flowCode:', taskDetail.value?.flowCode);

    const res = await instanceApi.completeTask(submitData);
    
    if (res.code === 0) {
      uni.showToast({
        title: '任务审批成功',
        icon: 'success'
      });

      // 延迟返回并刷新列表
      setTimeout(() => {
        // 返回到流程列表页面
        uni.navigateBack({
          delta: 2 // 返回两级，跳过详情页直接到列表页
        });

        // 发送事件通知列表刷新
        uni.$emit('refreshTaskList');
      }, 1500);
    } else {
      uni.showToast({
        title: res.msg || '任务审批失败',
        icon: 'none'
      });
    }
  } catch (error) {
    console.error('任务处理失败:', error);
    uni.showToast({
      title: '网络错误，请重试',
      icon: 'none'
    });
  } finally {
    submitting.value = false;
  }
};

// 返回
const goBack = () => {
  uni.navigateBack();
};

// 页面加载时获取数据
onMounted(() => {
  // 设置默认处理类型为通过
  handleForm.skipType = 'PASS';
  loadTaskDetail();
});
</script>

<style lang="scss" scoped>
.task-handle-page {
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

.handle-container {
  .info-card, .form-card {
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
  
  .form-item {
    margin-bottom: 30rpx;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .form-label {
      display: block;
      font-size: 28rpx;
      color: #333;
      margin-bottom: 16rpx;
      font-weight: bold;
    }

    .form-tip {
      display: block;
      font-size: 24rpx;
      color: #999;
      margin-top: 12rpx;
      line-height: 1.4;
    }
    
    .action-buttons {
      display: flex;
      gap: 20rpx;
      
      .action-btn {
        flex: 1;
        height: 80rpx;
        border: 2rpx solid #e0e0e0;
        border-radius: 40rpx;
        background: #fff;
        color: #666;
        font-size: 28rpx;
        
        &.active {
          border-color: #007aff;
          background: #007aff;
          color: #fff;
        }
      }
    }
    
    .form-textarea {
      width: 100%;
      min-height: 200rpx;
      padding: 20rpx;
      border: 2rpx solid #e0e0e0;
      border-radius: 12rpx;
      font-size: 28rpx;
      color: #333;
      background: #fff;
      box-sizing: border-box;
      resize: none;
      
      &:focus {
        border-color: #007aff;
      }
    }
    
    .char-count {
      display: block;
      text-align: right;
      font-size: 24rpx;
      color: #999;
      margin-top: 8rpx;
    }
    
    .variable-item {
      display: flex;
      align-items: center;
      margin-bottom: 16rpx;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .variable-label {
        font-size: 28rpx;
        color: #666;
        width: 120rpx;
        flex-shrink: 0;
      }
      
      .variable-input {
        flex: 1;
        height: 80rpx;
        padding: 0 20rpx;
        border: 2rpx solid #e0e0e0;
        border-radius: 8rpx;
        font-size: 28rpx;
        color: #333;
        background: #fff;
        
        &:focus {
          border-color: #007aff;
        }
      }
    }
  }
}

.submit-container {
  margin-top: 40rpx;
  
  .submit-btn {
    width: 100%;
    height: 88rpx;
    background: linear-gradient(112deg, #007aff 9%, #0056d3 84%);
    color: #fff;
    border: none;
    border-radius: 44rpx;
    font-size: 32rpx;
    font-weight: bold;
    
    &.disabled {
      background: #ccc;
      color: #999;
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
