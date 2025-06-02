<!--
  * OA 请假申请表
  *
  * @Author:    lf
  * @Date:      2025-05-31 20:00:10
  * @Copyright  lf
-->
<template>
  <!---------- 查询表单form begin ----------->
  <a-form class="smart-query-form">
    <a-row class="smart-query-form-row">
      <a-form-item label="请假类型" class="smart-query-form-item">
        <DictSelect :dict-code="DICT_CODE_ENUM.LEAVE_TYPE" placeholder="请假类型" v-model:value="queryForm.type" width="200px" />
      </a-form-item>
      <a-form-item label="开始时间" class="smart-query-form-item">
        <a-range-picker v-model:value="queryForm.startTime" :presets="defaultTimeRanges" style="width: 200px" @change="onChangeStartTime" />
      </a-form-item>
      <a-form-item label="请假原因" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.reason" placeholder="请假原因" />
      </a-form-item>
      <a-form-item label="流程状态" class="smart-query-form-item">
        <SmartEnumSelect width="100%" v-model:value="queryForm.flowStatus" placeholder="请选择状态" enum-name="FLOW_STATUS_ENUM" />
      </a-form-item>
      <a-form-item class="smart-query-form-item">
        <a-button type="primary" @click="onSearch">
          <template #icon>
            <SearchOutlined />
          </template>
          查询
        </a-button>
        <a-button @click="resetQuery" class="smart-margin-left10">
          <template #icon>
            <ReloadOutlined />
          </template>
          重置
        </a-button>
      </a-form-item>
    </a-row>
  </a-form>
  <!---------- 查询表单form end ----------->

  <a-card size="small" :bordered="false" :hoverable="true">
    <!---------- 表格操作行 begin ----------->
    <a-row class="smart-table-btn-block">
      <div class="smart-table-operate-block">
        <a-button @click="showForm" type="primary" size="small">
          <template #icon>
            <PlusOutlined />
          </template>
          新建
        </a-button>
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="columns" :tableId="null" :refresh="queryData" />
      </div>
    </a-row>
    <!---------- 表格操作行 end ----------->

    <!---------- 表格 begin ----------->
    <a-table
      size="small"
      :dataSource="tableData"
      :columns="columns"
      rowKey="leaveId"
      bordered
      :loading="tableLoading"
      :pagination="false"
    >
      <template #bodyCell="{ text, record, column }">
        <template v-if="column.dataIndex === 'type'">
          <DictLabel :dict-code="DICT_CODE_ENUM.LEAVE_TYPE || 'LEAVE_TYPE'" :data-value="text" />
        </template>
        <template v-if="column.dataIndex === 'flowStatus'">
          <span>{{ text ? $smartEnumPlugin.getDescByValue('FLOW_STATUS_ENUM', Number(text)) : '' }}</span>
        </template>
        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button v-if="!record.instanceId" @click="showForm(record)" type="link">编辑</a-button>
            <a-button v-if="!record.instanceId" @click="onDelete(record)" danger type="link">删除</a-button>
            <a-button type="link" v-if="record.instanceId && record.flowStatus && record.flowStatus != 8" @click="showForm(record)">审批</a-button>
            <a-button type="link" v-if="record.instanceId && record.flowStatus && record.flowStatus == 8" @click="showForm(record)">查看</a-button>

          </div>
        </template>
      </template>
    </a-table>
    <!---------- 表格 end ----------->

    <div class="smart-query-table-page">
      <a-pagination
        showSizeChanger
        showQuickJumper
        show-less-items
        :pageSizeOptions="PAGE_SIZE_OPTIONS"
        :defaultPageSize="queryForm.pageSize"
        v-model:current="queryForm.pageNum"
        v-model:pageSize="queryForm.pageSize"
        :total="total"
        @change="queryData"
        @showSizeChange="queryData"
        :show-total="(total) => `共${total}条`"
      />
    </div>

    <LeaveForm ref="formRef" @reloadList="queryData" />
  </a-card>
</template>
<script setup>
  import { reactive, ref, onMounted } from 'vue';
  import { message, Modal } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { leaveApi } from '/@/api/business/oa/leave-api';
  import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import LeaveForm from './leave-form.vue';
  import DictSelect from '/@/components/support/dict-select/index.vue';
  import { defaultTimeRanges } from '/@/lib/default-time-ranges';
  import { DICT_CODE_ENUM } from '/@/constants/support/dict-const.js';
  import DictLabel from '/@/components/support/dict-label/index.vue';
  import SmartEnumSelect from '/@/components/framework/smart-enum-select/index.vue';

  // ---------------------------- 表格列 ----------------------------

  const columns = ref([
    // {
    //   title: '编号',
    //   dataIndex: 'leaveId',
    //   align : 'center',
    //   ellipsis: true,
    // },
    {
      title: '申请时间',
      dataIndex: 'createTime',
      ellipsis: true,
      width: 180,
    },
    {
      title: '发起人',
      dataIndex: 'createUserName',
      align : 'center',
      ellipsis: true,
    },
    {
      title: '请假类型',
      dataIndex: 'type',
      align : 'center',
      ellipsis: true,
    },
    {
      title: '请假原因',
      dataIndex: 'reason',
      ellipsis: true,
    },
    {
      title: '开始时间',
      dataIndex: 'startTime',
      ellipsis: true,
    },
    {
      title: '结束时间',
      dataIndex: 'endTime',
      ellipsis: true,
    },
    {
      title: '请假天数',
      dataIndex: 'day',
      align : 'center',
      ellipsis: true,
    },
    {
      title: '审批节点',
      dataIndex: 'nodeName',
      align : 'center',
      ellipsis: true,
    },
    {
      title: '流程状态',
      dataIndex: 'flowStatus',
      align : 'center',
      ellipsis: true,
    },

    {
      title: '操作',
      dataIndex: 'action',
      fixed: 'right',
      width: 90,
    },
  ]);

  // ---------------------------- 查询数据表单和方法 ----------------------------

  const queryFormState = {
    type: undefined, //请假类型
    startTime: [], //开始时间
    startTimeBegin: undefined, //开始时间 开始
    startTimeEnd: undefined, //开始时间 结束
    reason: undefined, //请假原因
    flowStatus: undefined, //流程状态
    pageNum: 1,
    pageSize: 10,
  };
  // 查询表单form
  const queryForm = reactive({ ...queryFormState });
  // 表格加载loading
  const tableLoading = ref(false);
  // 表格数据
  const tableData = ref([]);
  // 总数
  const total = ref(0);

  // 重置查询条件
  function resetQuery() {
    let pageSize = queryForm.pageSize;
    Object.assign(queryForm, queryFormState);
    queryForm.pageSize = pageSize;
    queryData();
  }

  // 搜索
  function onSearch() {
    queryForm.pageNum = 1;
    queryData();
  }

  // 查询数据
  async function queryData() {
    tableLoading.value = true;
    try {
      let queryResult = await leaveApi.queryPage(queryForm);
      tableData.value = queryResult.data.list;
      total.value = queryResult.data.total;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  function onChangeStartTime(dates, dateStrings) {
    queryForm.startTimeBegin = dateStrings[0];
    queryForm.startTimeEnd = dateStrings[1];
  }

  onMounted(queryData);

  // ---------------------------- 添加/修改 ----------------------------
  const formRef = ref();

  function showForm(data) {
    formRef.value.show(data);
  }

  // ---------------------------- 单个删除 ----------------------------
  //确认删除
  function onDelete(data) {
    Modal.confirm({
      title: '提示',
      content: '确定要删除选吗?',
      okText: '删除',
      okType: 'danger',
      onOk() {
        requestDelete(data);
      },
      cancelText: '取消',
      onCancel() {},
    });
  }

  //请求删除
  async function requestDelete(data) {
    SmartLoading.show();
    try {
      let deleteForm = {
        goodsIdList: selectedRowKeyList.value,
      };
      await leaveApi.delete(data.leaveId);
      message.success('删除成功');
      queryData();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }

  // ---------------------------- 批量删除 ----------------------------

  // 选择表格行
  const selectedRowKeyList = ref([]);

  function onSelectChange(selectedRowKeys) {
    selectedRowKeyList.value = selectedRowKeys;
  }

  // 批量删除
  function confirmBatchDelete() {
    Modal.confirm({
      title: '提示',
      content: '确定要批量删除这些数据吗?',
      okText: '删除',
      okType: 'danger',
      onOk() {
        requestBatchDelete();
      },
      cancelText: '取消',
      onCancel() {},
    });
  }

  //请求批量删除
  async function requestBatchDelete() {
    try {
      SmartLoading.show();
      await leaveApi.batchDelete(selectedRowKeyList.value);
      message.success('删除成功');
      queryData();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }
</script>
