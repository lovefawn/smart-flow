<!--
  * 组织架构
  *
  * @Author:    1024创新实验室-主任：卓大
  * @Date:      2022-08-08 20:46:18
  * @Wechat:    zhuda1024
  * @Email:     lab1024@163.com
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
-->
<template>
  <div class="height100">
    <a-row :gutter="16" class="height100">
      <a-col :span="6">
        <DepartmentTree ref="departmentTree" />
      </a-col>

      <a-col :span="18" class="height100">
        <div class="employee-box height100">
          <EmployeeList
            style="flex-grow: 2.5"
            class="employee"
            :departmentId="selectedDepartmentId"
            :selectUser="selectUser"
            :postParams="postParams"
            :type="type"
            @submitForm="submitForm"
          />
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<script setup name="User">
  import _ from 'lodash';
  import { computed, ref } from 'vue';
  import DepartmentTree from './department-tree/index.vue';
  import EmployeeList from './employee-list/index.vue';

  const props = defineProps({
    userVisible: {
      type: Boolean,
    },
    selectUser: {
      type: Array,
    },
    postParams: {
      type: Object,
    },
    type: {
      type: String,
    },
  });
  const emit = defineEmits(['update:userVisible', 'handleUserSelect']);
  const departmentTree = ref();

  // 部门 面包屑
  const breadcrumb = computed(() => {
    if (departmentTree.value) {
      return departmentTree.value.breadcrumb;
    }
    return [];
  });

  // 当前选中部门的孩子
  const selectedDepartmentChildren = computed(() => {
    if (departmentTree.value) {
      return departmentTree.value.selectedDepartmentChildren;
    }
    return [];
  });

  // 当前选中的部门id
  const selectedDepartmentId = computed(() => {
    if (departmentTree.value) {
      let selectedKeys = departmentTree.value.selectedKeys;
      return _.isEmpty(selectedKeys) ? null : selectedKeys[0];
    }
    return null;
  });

  // 取消按钮
  function cancel() {
    emit('update:userVisible', false);
  }

  // 提交按钮
  function submitForm(selectedRowKeys) {
    console.log(selectedRowKeys)
    emit('handleUserSelect', selectedRowKeys);
    cancel();
  }
</script>
<style scoped lang="less">
  .height100 {
    height: 100%;
  }

  .employee-box {
    display: flex;
    flex-direction: column;

    .employee {
      flex-grow: 2;
    }
  }
</style>
