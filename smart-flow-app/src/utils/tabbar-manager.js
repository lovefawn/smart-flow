/**
 * 底部导航栏管理工具
 * 统一管理底部导航栏的状态和逻辑
 */

// 标签页路径映射
const TAB_PATHS = {
  '/pages/home/index': 0,
  '/pages/flow/list': 1,
  '/pages/message/message': 2,
  '/pages/mine/mine': 3
}

// 反向映射
const TAB_INDEXES = {
  0: '/pages/home/index',
  1: '/pages/flow/list',
  2: '/pages/message/message',
  3: '/pages/mine/mine'
}

/**
 * 获取当前页面对应的标签索引
 * @returns {number} 标签索引，默认为 0
 */
export function getCurrentTabIndex() {
  try {
    const pages = getCurrentPages()
    if (pages.length === 0) return 0
    
    const currentPage = pages[pages.length - 1]
    const currentPath = '/' + currentPage.route
    
    console.log('当前页面路径:', currentPath)
    
    const index = TAB_PATHS[currentPath]
    const result = index !== undefined ? index : 0
    
    console.log('对应的标签索引:', result)
    return result
  } catch (error) {
    console.error('获取当前标签索引失败:', error)
    return 0
  }
}

/**
 * 根据索引获取页面路径
 * @param {number} index 标签索引
 * @returns {string} 页面路径
 */
export function getPathByIndex(index) {
  return TAB_INDEXES[index] || TAB_INDEXES[0]
}

/**
 * 检查是否为标签页
 * @param {string} path 页面路径
 * @returns {boolean} 是否为标签页
 */
export function isTabPage(path) {
  return TAB_PATHS.hasOwnProperty(path)
}

/**
 * 隐藏系统底部导航栏
 */
export function hideSystemTabBar() {
  try {
    uni.hideTabBar({
      animation: false,
      success: () => {
        console.log('隐藏系统底部导航栏成功')
      },
      fail: (err) => {
        console.warn('隐藏系统底部导航栏失败:', err)
      }
    })
  } catch (error) {
    console.error('隐藏系统底部导航栏异常:', error)
  }
}

/**
 * 显示系统底部导航栏
 */
export function showSystemTabBar() {
  try {
    uni.showTabBar({
      animation: false,
      success: () => {
        console.log('显示系统底部导航栏成功')
      },
      fail: (err) => {
        console.warn('显示系统底部导航栏失败:', err)
      }
    })
  } catch (error) {
    console.error('显示系统底部导航栏异常:', error)
  }
}

/**
 * 跳转到指定标签页
 * @param {number} index 标签索引
 * @param {Function} callback 跳转完成回调
 */
export function switchToTab(index, callback) {
  const path = getPathByIndex(index)
  
  try {
    uni.switchTab({
      url: path,
      success: () => {
        console.log('跳转成功:', path)
        callback && callback(true, path)
      },
      fail: (err) => {
        console.error('跳转失败:', err, path)
        callback && callback(false, path, err)
      }
    })
  } catch (error) {
    console.error('跳转异常:', error)
    callback && callback(false, path, error)
  }
}

/**
 * 创建标签页管理的 composable
 * @param {number} defaultIndex 默认标签索引
 * @param {Function} ref Vue 的 ref 函数
 * @returns {Object} 管理对象
 */
export function useTabbarManager(defaultIndex = 0, ref) {
  const currentTabIndex = ref(defaultIndex)
  const showCustomTabbar = ref(true)

  // 初始化当前标签索引
  function initTabIndex() {
    const index = getCurrentTabIndex()
    currentTabIndex.value = index
    console.log('初始化标签索引:', index)
  }

  // 标签切换事件处理
  function onTabbarChange(event) {
    const { item, index } = event
    currentTabIndex.value = index
    console.log('标签切换:', item.text, '索引:', index)
  }

  // 页面显示时的处理
  function onPageShow() {
    // 更新当前标签索引
    initTabIndex()

    // 隐藏系统底部导航栏
    if (showCustomTabbar.value) {
      hideSystemTabBar()
    }
  }

  // 页面隐藏时的处理
  function onPageHide() {
    // 可以在这里添加页面隐藏时的逻辑
  }

  return {
    currentTabIndex,
    showCustomTabbar,
    initTabIndex,
    onTabbarChange,
    onPageShow,
    onPageHide
  }
}

// 默认导出
export default {
  getCurrentTabIndex,
  getPathByIndex,
  isTabPage,
  hideSystemTabBar,
  showSystemTabBar,
  switchToTab,
  useTabbarManager
}
