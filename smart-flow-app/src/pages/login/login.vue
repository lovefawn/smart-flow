<template>
  <view class="smart-page">
    <view class="smart-container smart-container--padded">
      <!-- Logo 区域 -->
      <view class="login-header">
        <view class="logo-wrapper">
          <image src="@/static/images/login/login-logo.png" class="logo-image" mode="aspectFit"></image>
        </view>
        <text class="app-title">SmartFlow</text>
        <text class="app-subtitle">智能流程管理系统</text>
      </view>

      <!-- 登录表单 -->
      <view class="login-form-card">
        <!-- 用户名输入 -->
        <view class="smart-input smart-mb--md">
          <view class="input-wrapper">
            <image src="@/static/images/login/login-username.png" class="input-icon"></image>
            <uni-easyinput
              class="input-field"
              placeholder="请输入用户名"
              :clearable="true"
              placeholderStyle="color:#CCCCCC"
              border="none"
              v-model="loginForm.loginName"
            />
          </view>
        </view>

        <!-- 邮箱验证码输入 -->
        <view v-if="emailCodeShowFlag" class="smart-input smart-mb--md">
          <view class="input-wrapper">
            <image src="@/static/images/login/login-password.png" class="input-icon"></image>
            <uni-easyinput
              class="input-field"
              placeholder="请输入邮箱验证码"
              :clearable="true"
              placeholderStyle="color:#CCCCCC"
              border="none"
              v-model="loginForm.emailCode"
            />
            <button @click="sendSmsCode" class="smart-btn smart-btn--primary smart-btn--small code-btn" :disabled="emailCodeButtonDisabled">
              {{ emailCodeTips }}
            </button>
          </view>
        </view>

        <!-- 密码输入 -->
        <view class="smart-input smart-mb--md">
          <view class="input-wrapper">
            <image src="@/static/images/login/login-password.png" class="input-icon"></image>
            <uni-easyinput
              class="input-field"
              placeholder="请输入密码"
              :clearable="true"
              :password="true"
              placeholderStyle="color:#CCCCCC"
              border="none"
              v-model="loginForm.password"
            />
          </view>
        </view>

        <!-- 验证码输入 -->
        <view class="smart-input smart-mb--md">
          <view class="input-wrapper">
            <image src="@/static/images/login/login-password.png" class="input-icon"></image>
            <uni-easyinput
              class="input-field captcha-input"
              placeholder="请输入验证码"
              :clearable="true"
              :password="false"
              placeholderStyle="color:#CCCCCC"
              border="none"
              v-model="loginForm.captchaCode"
            />
            <image class="captcha-img" :src="captchaBase64Image" @click="getCaptcha" mode="aspectFit" />
          </view>
        </view>

        <!-- 辅助链接 -->
        <view class="login-links smart-flex smart-justify--between smart-align--center smart-mb--lg">
          <text class="link-text">验证码登录</text>
          <text class="link-text">忘记密码？</text>
        </view>

        <!-- 登录按钮 -->
        <button @click="login" class="smart-btn smart-btn--primary smart-w--full smart-mb--md login-button">
          登录
        </button>

        <!-- 注册按钮 -->
        <button @click="login" class="smart-btn smart-btn--secondary smart-w--full smart-mb--lg register-button">
          创建账号
        </button>

        <OtherWayBox />
        <LoginCheckBox class="login-check-box" ref="loginCheckBoxRef" />
      </view>
    </view>
  </view>
</template>
<script setup>
  import { reactive, ref } from 'vue';
  import { onShow } from '@dcloudio/uni-app';
  import OtherWayBox from './components/other-way-box.vue';
  import LoginCheckBox from './components/login-check-box.vue';
  import { loginApi } from '@/api/system/login-api';
  import { LOGIN_DEVICE_ENUM } from '@/constants/system/login-device-const';
  import { encryptData } from '@/lib/encrypt';
  import { useUserStore } from '@/store/modules/system/user';
  import { smartSentry } from '@/lib/smart-sentry';

  const loginForm = reactive({
    loginName: 'admin',
    password: '123456',
    captchaCode: '',
    captchaUuid: '',
    loginDevice: LOGIN_DEVICE_ENUM.H5.value,
  });

  const loginCheckBoxRef = ref();
  async function login() {
    if (!loginCheckBoxRef.value.agreeFlag) {
      uni.showToast({
        icon: 'none',
        title: '请阅读并同意《用户协议》、《隐私政策》',
      });
      return;
    }
    if (!loginForm.loginName) {
      uni.showToast({
        icon: 'none',
        title: '请输入用户名',
      });
      return;
    }
    if (!loginForm.password) {
      uni.showToast({
        icon: 'none',
        title: '请输入密码',
      });
      return;
    }

    try {
      uni.showLoading({ title: '登录中' });
      // 密码加密
      let encryptPasswordForm = Object.assign({}, loginForm, {
        password: encryptData(loginForm.password),
      });
      const res = await loginApi.login(encryptPasswordForm);
      stopRefreshCaptchaInterval();
      uni.showToast({ title: '登录成功' });
      //更新用户信息到 pinia
      useUserStore().setUserLoginInfo(res.data);

      uni.switchTab({ url: '/pages/home/index' });
    } catch (e) {
      if (e.data && e.data.code !== 0) {
        loginForm.captchaCode = '';
        getCaptcha();
      }
      smartSentry.captureError(e);
      uni.hideLoading();
    }
  }

  //--------------------- 验证码 ---------------------------------

  const captchaBase64Image = ref('');

  async function getCaptcha() {
    try {
      let captchaResult = await loginApi.getCaptcha();
      captchaBase64Image.value = captchaResult.data.captchaBase64Image;
      console.log(captchaResult.data.captchaBase64Image, 2);
      loginForm.captchaUuid = captchaResult.data.captchaUuid;
      beginRefreshCaptchaInterval(captchaResult.data.expireSeconds);
    } catch (e) {
      console.log(e);
    }
  }

  let refreshCaptchaInterval = null;

  function beginRefreshCaptchaInterval(expireSeconds) {
    if (refreshCaptchaInterval === null) {
      refreshCaptchaInterval = setInterval(getCaptcha, (expireSeconds - 5) * 1000);
    }
  }

  function stopRefreshCaptchaInterval() {
    if (refreshCaptchaInterval != null) {
      clearInterval(refreshCaptchaInterval);
      refreshCaptchaInterval = null;
    }
  }

  const emailCodeShowFlag = ref(false);
  let emailCodeTips = ref('获取邮箱验证码');
  let emailCodeButtonDisabled = ref(false);
  // 定时器
  let countDownTimer = null;
  // 开始倒计时
  function runCountDown() {
    emailCodeButtonDisabled.value = true;
    let countDown = 60;
    emailCodeTips.value = `${countDown}秒后重新获取`;
    countDownTimer = setInterval(() => {
      if (countDown > 1) {
        countDown--;
        emailCodeTips.value = `${countDown}秒后重新获取`;
      } else {
        clearInterval(countDownTimer);
        emailCodeButtonDisabled.value = false;
        emailCodeTips.value = '获取验证码';
      }
    }, 1000);
  }

  // 获取双因子登录标识
  async function getTwoFactorLoginFlag() {
    try {
      let result = await loginApi.getTwoFactorLoginFlag();
      emailCodeShowFlag.value = result.data;
    } catch (e) {
      smartSentry.captureError(e);
    }
  }
  // 发送邮箱验证码
  async function sendSmsCode() {
  try {
    uni.showLoading();
    let result = await loginApi.sendLoginEmailCode(loginForm.loginName);
    message.success('验证码发送成功!请登录邮箱查看验证码~');
    runCountDown();
  } catch (e) {
    smartSentry.captureError(e);
  } finally {
    uni.hideLoading();
  }
  }
  onShow(()=>{
    getCaptcha()
    getTwoFactorLoginFlag();
  });
</script>
<style lang="scss" scoped>
  .login-header {
    text-align: center;
    padding: 120rpx 0 80rpx;
  }

  .logo-wrapper {
    margin-bottom: 40rpx;
  }

  .logo-image {
    width: 320rpx;
    height: 320rpx;
  }

  .app-title {
    display: block;
    font-size: 48rpx;
    font-weight: 700;
    color: #444;
    margin-bottom: 16rpx;
  }

  .app-subtitle {
    display: block;
    font-size: 28rpx;
    color: #777;
    font-weight: 400;
  }

  .smart-input {
    .input-wrapper {
      display: flex;
      align-items: center;
      background-color: #f7f8f9;
      border-radius: 12rpx;
      padding: 32rpx;
      min-height: 100rpx;
      box-sizing: border-box;

      .input-icon {
        width: 44rpx;
        height: 44rpx;
        margin-right: 32rpx;
        flex-shrink: 0;
      }

      .input-field {
        flex: 1;
        background-color: transparent;
      }

      .captcha-input {
        flex: 0.6;
      }

      .captcha-img {
        width: 40%;
        height: 80rpx;
        margin-left: 16rpx;
        border-radius: 8rpx;
        cursor: pointer;
      }

      .code-btn {
        margin-left: 16rpx;
        padding: 16rpx 32rpx;
        min-width: 160rpx;
      }
    }
  }

  .login-links {
    .link-text {
      font-size: 28rpx;
      color: #777;
      cursor: pointer;
      transition: color 0.2s ease;

      &:active {
        color: #007aff;
      }
    }
  }

  .login-button,
  .register-button {
    height: 90rpx;
    font-size: 32rpx;
    font-weight: 500;
    border-radius: 12rpx;
    box-shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.08);
  }

  // 深度选择器样式
  :deep(.uni-easyinput__content) {
    background-color: transparent !important;
  }

  :deep(.is-input-border) {
    border: none;
  }

  .login-form-card {
    width: 700rpx;
    margin: 30rpx auto 0;
    background: #ffffff;
    border-radius: 12rpx;
    box-shadow: 0px 3px 4px 0px rgba(24, 144, 255, 0.06);
    padding: 40rpx;
    box-sizing: border-box;
  }

  .login-check-box {
    margin-top: 48rpx;
  }
</style>
