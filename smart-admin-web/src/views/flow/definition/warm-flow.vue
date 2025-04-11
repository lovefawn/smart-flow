<template>
  <div class="container" ref="container">
    <iframe ref="iframe" :src="iframeUrl" frameborder="0" width="100%" height="100%"></iframe>
  </div>
</template>

<script setup name="WarmFlow">
import {useUserStore} from "/@/store/modules/system/user.js";

const { proxy } = getCurrentInstance();
import {getCurrentInstance, onMounted, onUnmounted, ref} from 'vue';

const iframeUrl = ref(import.meta.env.VITE_APP_API_URL + `/warm-flow-ui/index.html?id=${proxy.$route.params.id}&disabled=${proxy.$route.query.disabled}&Authorization=Bearer ` + + useUserStore().getToken);

onMounted(() => {
  window.addEventListener("message", handleMessage);
});

onUnmounted(() => {
  window.removeEventListener("message", handleMessage);
});

const handleMessage = (event) => {
  switch (event.data.method) {
    case "close":
      close();
      break;
  }
};

/** 关闭按钮 */
function close() {
  const obj = { path: "/flow/definition" };
  proxy.$tab.closeOpenPage(obj);
}
</script>

<style scoped>
.container {
  width: 100%;
  height: calc(100vh - 84px);
}
</style>
