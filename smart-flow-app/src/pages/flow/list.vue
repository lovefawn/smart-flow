<template>
  <view>
    <y-tabs v-model="active" sticky :offsetTop="43" color="#007aff">
      <y-tab v-for="item in tabsList" :key="item.value" :title="item.title"/>
    </y-tabs>

    <TodoList v-if="active === 0" />
    <DiscountList v-if="active === 1" />
    <IotList v-if="active === 2" />
  </view>
</template>

<script setup>
  import TodoList from './components/todo-list.vue';
  import DiscountList from './components/discount-list.vue';
  import IotList from './components/iot-list.vue';

  import { ref } from 'vue';

  const active = ref(0);

  const tabsList = [
    {
      title: '代办任务',
      value: 0,
    },
    {
      title: '已办任务',
      value: 1,
    },
    {
      title: '抄送任务',
      value: 2,
    },
  ];
</script>

<style lang="scss" scoped>
  page {
    background-color: #f5f5f5;
  }
</style>
