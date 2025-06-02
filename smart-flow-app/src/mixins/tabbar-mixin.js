/**
 * 底部导航栏混入
 * 提供统一的底部导航栏管理功能
 */

export default {
  data() {
    return {
      // 是否显示自定义底部导航栏
      showCustomTabbar: true,
      // 当前选中的标签索引
      currentTabIndex: 0
    }
  },
  
  onShow() {
    // 页面显示时更新当前标签索引
    this.updateCurrentTabIndex()
    
    // 隐藏系统底部导航栏
    if (this.showCustomTabbar) {
      uni.hideTabBar({
        animation: false
      })
    }
  },
  
  onHide() {
    // 页面隐藏时可以选择显示系统底部导航栏
    if (!this.showCustomTabbar) {
      uni.showTabBar({
        animation: false
      })
    }
  },
  
  methods: {
    /**
     * 更新当前标签索引
     */
    updateCurrentTabIndex() {
      const pages = getCurrentPages()
      if (pages.length === 0) return
      
      const currentPage = pages[pages.length - 1]
      const currentPath = '/' + currentPage.route
      
      // 标签页路径映射
      const tabPaths = [
        '/pages/home/index',
        '/pages/flow/list', 
        '/pages/message/message',
        '/pages/mine/mine'
      ]
      
      const index = tabPaths.findIndex(path => path === currentPath)
      this.currentTabIndex = index >= 0 ? index : 0
    },
    
    /**
     * 底部导航栏切换事件
     */
    onTabbarChange(event) {
      const { item, index } = event
      this.currentTabIndex = index
      
      // 可以在这里添加切换时的额外逻辑
      console.log('切换到标签:', item.text, '索引:', index)
    },
    
    /**
     * 更新消息徽章
     */
    updateMessageBadge(count) {
      if (this.$refs.smartTabbar) {
        this.$refs.smartTabbar.updateBadge(2, count) // 消息是第3个标签（索引为2）
      }
    },
    
    /**
     * 显示自定义底部导航栏
     */
    showCustomTabBar() {
      this.showCustomTabbar = true
      uni.hideTabBar({
        animation: true
      })
    },
    
    /**
     * 隐藏自定义底部导航栏
     */
    hideCustomTabBar() {
      this.showCustomTabbar = false
      uni.showTabBar({
        animation: true
      })
    },
    
    /**
     * 切换底部导航栏显示模式
     */
    toggleTabBarMode() {
      if (this.showCustomTabbar) {
        this.hideCustomTabBar()
      } else {
        this.showCustomTabBar()
      }
    }
  }
}
