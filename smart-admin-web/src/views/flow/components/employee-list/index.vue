<!--
  *  员工 列表
  *
  * @Author:    1024创新实验室-主任：卓大
  * @Date:      2022-08-08 20:46:18
  * @Wechat:    zhuda1024
  * @Email:     lab1024@163.com
  * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
-->
<template>
  <a-card class="employee-container">
    <div class="header">
      <a-typography-title :level="5">部门人员</a-typography-title>
      <div class="query-operate">
        <a-input-search v-model:value.trim="params.keyword" placeholder="姓名/手机号/登录账号" @search="queryEmployeeByKeyword(true)">
          <template #enterButton>
            <a-button type="primary">
              <template #icon>
                <SearchOutlined />
              </template>
              查询
            </a-button>
          </template>
        </a-input-search>
        <a-button @click="reset" class="smart-margin-left10">
          <template #icon>
            <ReloadOutlined />
          </template>
          重置
        </a-button>
        <a-button type="primary" :disabled="selectedRowKeys.length === 0"  @click="submitForm" class="smart-margin-left10">
          确定
        </a-button>
      </div>
    </div>
    <a-row :gutter="10" class="mb8">
      <a-tag color="blue" style="margin-right: 10px" v-for="tag in selectedRowKeys" :key="tag" closable @close="handleClose(tag)">{{tag}}</a-tag>
    </a-row>
    <a-table
      :row-selection="{ type: rowSelectionType, selectedRowKeys: selectedRowKeys, columnWidth:30, onChange: onSelectChange }"
      size="small"
      :columns="columns"
      :data-source="tableData"
      :pagination="false"
      :loading="tableLoading"
      row-key="employeeId"
      bordered
    >
      <template #bodyCell="{ text, column }">
        <template v-if="column.dataIndex === 'disabledFlag'">
          <a-tag :color="text ? 'error' : 'processing'">{{ text ? '禁用' : '启用' }}</a-tag>
        </template>
      </template>
    </a-table>
    <div class="smart-query-table-page">
      <a-pagination
        showSizeChanger
        showQuickJumper
        show-less-items
        :pageSizeOptions="PAGE_SIZE_OPTIONS"
        :defaultPageSize="params.pageSize"
        v-model:current="params.pageNum"
        v-model:pageSize="params.pageSize"
        :total="total"
        @change="queryEmployee"
        @showSizeChange="queryEmployee"
        :show-total="showTableTotal"
      />
    </div>
  </a-card>
</template>
<script setup>
  import _ from 'lodash';
  import { computed,reactive, ref, watch } from 'vue';
  import { employeeApi } from '/@/api/system/employee-api';
  import { instanceApi} from "/@/api/flow/instance-api.js";
  import { PAGE_SIZE } from '/@/constants/common-const';
  import { PAGE_SIZE_OPTIONS, showTableTotal } from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';

  // ----------------------- 以下是字段定义 emits props ---------------------

  const props = defineProps({
    departmentId: Number,
    breadcrumb: Array,
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

  const emit = defineEmits(["submitForm"]);

  const checkedItemList = ref([]); // 已选的itemList
  // ----------------------- 表格/列表/ 搜索 ---------------------
  //字段
  const columns = ref([
    {
      title: '姓名',
      dataIndex: 'actualName',
      width: 55,
    },
    {
      title: '登录账号',
      dataIndex: 'loginName',
      width: 60,
    },
    {
      title: '手机',
      dataIndex: 'phone',
      width: 80,
    },
    {
      title: '状态',
      dataIndex: 'disabledFlag',
      width: 30,
    },
    {
      title: '角色',
      dataIndex: 'roleNameList',
      width: 40,
    },
    {
      title: '部门',
      dataIndex: 'departmentName',
      width: 100,
    },
  ]);
  const tableData = ref();

  let defaultParams = {
    departmentId:undefined,
    disabledFlag: false,
    keyword: undefined,
    taskId: undefined,
    operatorType: undefined,
    pageNum: 1,
    pageSize: PAGE_SIZE,
    sortItemList: undefined,
  };
  const params = reactive({ ...defaultParams });
  const total = ref(0);

  // 搜索重置
  function reset() {
    Object.assign(params, defaultParams);
    queryEmployee();
  }

  const tableLoading = ref(false);
  // 查询
  async function queryEmployee() {
    tableLoading.value = true;
    try {
      params.departmentId = props.departmentId;
      params.taskId = props.postParams.taskId;
      params.operatorType = props.postParams.operatorType;
      let res = await instanceApi.interactiveTypeEmployeeEntity(params);
      for (const item of res.data.list) {
        item.roleNameList = _.join(item.roleNameList, ',');
      }
      tableData.value = res.data.list;
      total.value = res.data.total;
      // 清除选中
      selectedRowKeys.value = [];
      selectedRows.value = [];
    } catch (error) {
      smartSentry.captureError(error);
    } finally {
      tableLoading.value = false;
    }
  }

  // 根据关键字 查询
  async function queryEmployeeByKeyword(allDepartment) {
    tableLoading.value = true;
    try {
      params.pageNum = 1;
      params.taskId = props.postParams.taskId;
      params.operatorType = props.postParams.operatorType;
      params.departmentId = allDepartment ? undefined : props.departmentId;
      let res = await instanceApi.interactiveTypeEmployeeEntity(params);
      for (const item of res.data.list) {
        item.roleNameList = _.join(item.roleNameList, ',');
      }
      tableData.value = res.data.list;
      total.value = res.data.total;
      // 清除选中
      selectedRowKeys.value = [];
      selectedRows.value = [];
    } catch (error) {
      smartSentry.captureError(error);
    } finally {
      tableLoading.value = false;
    }
  }

  watch(
    () => props.departmentId,
    () => {
      if (props.departmentId !== params.departmentId) {
        params.pageNum = 1;
        queryEmployee();
      }
    },
    { immediate: true }
  );

  // ----------------------- 多选操作 ---------------------

  let rowSelectionType= computed(() => {
    if (['转办', '委派'].includes(props.type)) {
      return 'radio';
    }
    return 'checkbox';
  });
  let selectedRowKeys = ref([]);
  let selectedRows = ref([]);
  // 是否有选中：用于 批量操作按钮的禁用
  const hasSelected = computed(() => selectedRowKeys.value.length > 0);

  function onSelectChange(keyArray, selectRows) {
    selectedRowKeys.value = keyArray;
    selectedRows.value = selectRows;
  }

  // 删除标签
  function handleClose(tag) {
// 1. 删除 selectedRowKeys 中包含 tag 的项
    selectedRowKeys.value = selectedRowKeys.value.filter(key => key !== tag);

    // 2. 删除 selectedRows 中 employeeId=tag 的对象
    selectedRows.value = selectedRows.value.filter(row => row.employeeId !== tag);
  }

  // 提交按钮
  function submitForm() {
    emit("submitForm", selectedRowKeys.value);
  }

</script>
<style scoped lang="less">
  .employee-container {
    height: 100%;
  }
  .header {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
  }
  .query-operate {
    margin-left: auto;
    display: flex;
    align-items: center;
  }

  .btn-group {
    margin: 10px 0;
    .btn {
      margin-right: 8px;
    }
  }
</style>
