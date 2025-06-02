# SmartFlow 设计系统

SmartFlow 设计系统是一套统一的 UI 设计规范和组件库，旨在为整个应用提供一致的视觉体验和交互模式。

## 🎨 设计原则

### 1. 一致性 (Consistency)
- 统一的颜色系统
- 一致的间距规范
- 标准化的组件样式

### 2. 简洁性 (Simplicity)
- 清晰的视觉层次
- 简洁的交互流程
- 直观的用户界面

### 3. 可访问性 (Accessibility)
- 良好的对比度
- 合适的字体大小
- 清晰的状态反馈

## 📐 设计系统结构

```
src/theme/
├── index.scss          # 主入口文件
├── typography.scss     # 文字样式系统
├── components.scss     # 组件样式系统
├── layout.scss         # 布局样式系统
├── states.scss         # 状态样式系统
└── README.md          # 设计系统文档
```

## 🎯 使用指南

### 颜色系统

#### 主色调
```scss
$primary-500: #1A9AFF  // 主色调
$primary-600: #1976D2  // 深色变体
$primary-400: #42A5F5  // 浅色变体
```

#### 语义化颜色
```scss
$success-color: #1CE36D  // 成功
$warning-color: #FF6C00  // 警告
$error-color: #FF3924    // 错误
$info-color: #1A9AFF     // 信息
```

#### 中性色
```scss
$text-primary: #212121     // 主要文字
$text-secondary: #616161   // 次要文字
$text-tertiary: #9E9E9E    // 辅助文字
```

### 字体系统

#### 字体大小
```scss
$font-size-xs: 20rpx     // 10px - 极小文字
$font-size-sm: 24rpx     // 12px - 小文字
$font-size-base: 28rpx   // 14px - 基础文字
$font-size-md: 32rpx     // 16px - 中等文字
$font-size-lg: 36rpx     // 18px - 大文字
$font-size-xl: 40rpx     // 20px - 超大文字
$font-size-xxl: 48rpx    // 24px - 标题文字
```

#### 字体粗细
```scss
$font-weight-light: 300
$font-weight-normal: 400
$font-weight-medium: 500
$font-weight-semibold: 600
$font-weight-bold: 700
```

### 间距系统

```scss
$spacing-xs: 8rpx     // 4px
$spacing-sm: 16rpx    // 8px
$spacing-base: 24rpx  // 12px
$spacing-md: 32rpx    // 16px
$spacing-lg: 48rpx    // 24px
$spacing-xl: 64rpx    // 32px
$spacing-xxl: 96rpx   // 48px
```

### 圆角系统

```scss
$radius-xs: 4rpx      // 2px
$radius-sm: 8rpx      // 4px
$radius-base: 12rpx   // 6px
$radius-md: 16rpx     // 8px
$radius-lg: 24rpx     // 12px
$radius-xl: 32rpx     // 16px
$radius-round: 50%
$radius-pill: 9999rpx
```

### 阴影系统

```scss
$shadow-xs: 0 2rpx 8rpx rgba(0, 0, 0, 0.04)
$shadow-sm: 0 2rpx 16rpx rgba(0, 0, 0, 0.08)
$shadow-base: 0 4rpx 24rpx rgba(0, 0, 0, 0.12)
$shadow-lg: 0 8rpx 32rpx rgba(0, 0, 0, 0.16)
$shadow-xl: 0 16rpx 48rpx rgba(0, 0, 0, 0.20)
```

## 🧩 组件使用

### 页面容器
```html
<view class="smart-page">
  <view class="smart-container">
    <!-- 页面内容 -->
  </view>
</view>
```

### 卡片组件
```html
<view class="smart-card">
  <!-- 卡片内容 -->
</view>

<!-- 带阴影的卡片 -->
<view class="smart-card smart-card--elevated">
  <!-- 卡片内容 -->
</view>
```

### 按钮组件
```html
<!-- 主要按钮 -->
<button class="smart-btn smart-btn--primary">
  主要按钮
</button>

<!-- 次要按钮 -->
<button class="smart-btn smart-btn--secondary">
  次要按钮
</button>

<!-- 小尺寸按钮 -->
<button class="smart-btn smart-btn--primary smart-btn--small">
  小按钮
</button>
```

### 输入框组件
```html
<view class="smart-input">
  <label class="smart-input__label">用户名</label>
  <input class="smart-input__field" placeholder="请输入用户名" />
</view>

<!-- 错误状态 -->
<view class="smart-input smart-input--error">
  <input class="smart-input__field" placeholder="请输入用户名" />
  <text class="smart-input__error">用户名不能为空</text>
</view>
```

### 列表项组件
```html
<view class="smart-list-item smart-list-item--clickable">
  <image class="smart-list-item__icon" src="avatar.png" />
  <view class="smart-list-item__content">
    <text class="smart-list-item__title">标题</text>
    <text class="smart-list-item__subtitle">副标题</text>
  </view>
  <view class="smart-list-item__action">></view>
</view>
```

## 🎨 布局系统

### Flexbox 布局
```html
<!-- 水平布局 -->
<view class="smart-flex smart-justify--between smart-align--center">
  <view>左侧内容</view>
  <view>右侧内容</view>
</view>

<!-- 垂直布局 -->
<view class="smart-flex smart-flex--column smart-align--center">
  <view>上方内容</view>
  <view>下方内容</view>
</view>
```

### 间距工具类
```html
<!-- 外边距 -->
<view class="smart-mt--lg smart-mb--md smart-mx--auto">
  内容
</view>

<!-- 内边距 -->
<view class="smart-p--lg smart-px--md smart-py--sm">
  内容
</view>
```

## 📝 文字样式

### 标题样式
```html
<text class="smart-title--h1">一级标题</text>
<text class="smart-title--h2">二级标题</text>
<text class="smart-title--h3">三级标题</text>
```

### 正文样式
```html
<text class="smart-body--large">大号正文</text>
<text class="smart-body--medium">中号正文</text>
<text class="smart-body--small">小号正文</text>
```

### 文字颜色
```html
<text class="smart-color--primary">主要文字</text>
<text class="smart-color--secondary">次要文字</text>
<text class="smart-color--brand">品牌色文字</text>
<text class="smart-color--success">成功状态文字</text>
```

## 🔄 状态样式

### 交互状态
```html
<!-- 悬停效果 -->
<view class="smart-hover">悬停时有动画</view>

<!-- 激活效果 -->
<view class="smart-active">点击时有缩放</view>

<!-- 禁用状态 -->
<view class="smart-disabled">禁用状态</view>
```

### 加载状态
```html
<!-- 加载中 -->
<view class="smart-loading">加载中...</view>

<!-- 脉冲效果 -->
<view class="smart-pulse">脉冲动画</view>

<!-- 骨架屏 -->
<view class="smart-skeleton" style="height: 40rpx;"></view>
```

### 验证状态
```html
<view class="smart-success">成功状态</view>
<view class="smart-warning">警告状态</view>
<view class="smart-error">错误状态</view>
<view class="smart-info">信息状态</view>
```

## 🚀 最佳实践

1. **优先使用设计系统的样式类**，避免写内联样式
2. **保持一致的间距**，使用预定义的间距变量
3. **遵循颜色规范**，使用语义化的颜色变量
4. **合理使用动画**，提升用户体验但不过度
5. **考虑可访问性**，确保足够的对比度和可读性

## 📱 响应式设计

设计系统支持响应式设计，可以根据屏幕尺寸调整样式：

```html
<!-- 小屏幕隐藏 -->
<view class="smart-hide-sm">大屏幕显示</view>

<!-- 大屏幕隐藏 -->
<view class="smart-hide-lg">小屏幕显示</view>
```

## 🔧 自定义扩展

如需扩展设计系统，请遵循以下原则：

1. 保持与现有系统的一致性
2. 使用语义化的命名
3. 添加适当的文档说明
4. 考虑向后兼容性

---

更多详细信息请参考各个样式文件中的注释说明。
