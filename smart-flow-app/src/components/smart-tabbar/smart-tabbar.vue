<template>
  <view class="smart-tabbar" :class="{ 'smart-tabbar--safe-area': safeArea }">
    <!-- 顶部边框线 -->
    <view class="smart-tabbar__border"></view>
    
    <!-- 导航项列表 -->
    <view class="smart-tabbar__content">
      <view 
        v-for="(item, index) in tabList" 
        :key="index"
        class="smart-tabbar__item"
        :class="{ 'smart-tabbar__item--active': currentIndex === index }"
        @click="switchTab(item, index)"
      >
        <!-- 图标容器 -->
        <view class="smart-tabbar__icon-wrapper">
          <image 
            class="smart-tabbar__icon" 
            :src="currentIndex === index ? item.selectedIconPath : item.iconPath"
            mode="aspectFit"
          />
          <!-- 徽章 -->
          <view 
            v-if="item.badge && item.badge > 0" 
            class="smart-tabbar__badge"
            :class="{ 'smart-tabbar__badge--dot': item.badge === true }"
          >
            <text v-if="item.badge !== true" class="smart-tabbar__badge-text">
              {{ item.badge > 99 ? '99+' : item.badge }}
            </text>
          </view>
        </view>
        
        <!-- 文字标签 -->
        <text class="smart-tabbar__text">{{ item.text }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  // 当前选中的索引
  current: {
    type: Number,
    default: 0
  },
  // 是否适配安全区域
  safeArea: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['change'])

// 当前选中索引
const currentIndex = ref(props.current)

// 固定的标签列表 - 与 pages.json 中的 tabBar 保持一致
const tabList = [
  {
    pagePath: '/pages/home/index',
    iconPath: '/static/images/tabbar/home-icon.png',
    selectedIconPath: '/static/images/tabbar/home-icon-h.png',
    text: '首页',
    badge: 0
  },
  {
    pagePath: '/pages/flow/list',
    iconPath: '/static/images/tabbar/list-icon.png',
    selectedIconPath: '/static/images/tabbar/list-icon-h.png',
    text: '流程',
    badge: 0
  },
  {
    pagePath: '/pages/message/message',
    iconPath: '/static/images/tabbar/message-icon.png',
    selectedIconPath: '/static/images/tabbar/message-icon-h.png',
    text: '消息',
    badge: 3
  },
  {
    pagePath: '/pages/mine/mine',
    iconPath: '/static/images/tabbar/mine-icon.png',
    selectedIconPath: '/static/images/tabbar/mine-icon-h.png',
    text: '我的',
    badge: 0
  }
]

// 切换标签
function switchTab(item, index) {
  if (currentIndex.value === index) return

  // 更新当前索引
  currentIndex.value = index

  // 发送事件
  emit('change', { item, index })

  // 页面跳转 - 只使用 switchTab，因为这些都是 tabBar 页面
  uni.switchTab({
    url: item.pagePath,
    success: () => {
      console.log('页面跳转成功:', item.pagePath)
    },
    fail: (err) => {
      console.error('页面跳转失败:', err, item.pagePath)
    }
  })
}

// 更新徽章
function updateBadge(index, badge) {
  if (tabList[index]) {
    tabList[index].badge = badge
  }
}

// 监听 props.current 的变化
watch(() => props.current, (newValue) => {
  currentIndex.value = newValue
}, { immediate: true })

// 暴露方法
defineExpose({
  updateBadge,
  switchTab
})
</script>

<style lang="scss" scoped>
.smart-tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: #ffffff;
  
  &--safe-area {
    padding-bottom: env(safe-area-inset-bottom);
  }
  
  &__border {
    height: 1rpx;
    background: #f0f0f0;
  }
  
  &__content {
    display: flex;
    height: 100rpx;
    align-items: center;
  }
  
  &__item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 8rpx 0;
    position: relative;
    transition: all 0.3s ease;
    
    &:active {
      transform: scale(0.95);
    }
    
    &--active {
      .smart-tabbar__icon {
        transform: scale(1.1);
      }
      
      .smart-tabbar__text {
        color: #007aff;
        font-weight: 600;
      }
    }
  }
  
  &__icon-wrapper {
    position: relative;
    margin-bottom: 6rpx;
  }
  
  &__icon {
    width: 48rpx;
    height: 48rpx;
    transition: transform 0.3s ease;
  }
  
  &__text {
    font-size: 20rpx;
    color: #777777;
    line-height: 1.2;
    transition: all 0.3s ease;
  }
  
  &__badge {
    position: absolute;
    top: -8rpx;
    right: -8rpx;
    min-width: 32rpx;
    height: 32rpx;
    background: #ff3b30;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 2rpx solid #ffffff;
    
    &--dot {
      width: 16rpx;
      height: 16rpx;
      min-width: 16rpx;
      border-radius: 8rpx;
      top: -4rpx;
      right: -4rpx;
    }
  }
  
  &__badge-text {
    font-size: 18rpx;
    color: #ffffff;
    font-weight: 600;
    line-height: 1;
    padding: 0 6rpx;
  }
  
}

// 响应式适配
@media (max-width: 750rpx) {
  .smart-tabbar {
    &__content {
      height: 96rpx;
    }
    
    &__icon {
      width: 44rpx;
      height: 44rpx;
    }
    
    &__text {
      font-size: 18rpx;
    }
  }
}
</style>
