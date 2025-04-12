<template>
  <div class="app-container">
    <!---------- 查询表单form begin ----------->
    <a-form class="smart-query-form">
      <a-row class="smart-query-form-row">
        <a-form-item label="任务名称" class="smart-query-form-item">
          <a-input
              v-model:value="queryForm.nodeName"
              placeholder="请输入任务名称"
              @pressEnter="handleQuery"
          />
        </a-form-item>

        <a-form-item label="流程状态" class="smart-query-form-item">
          <SmartEnumSelect width="100%" v-model:value="queryForm.flowStatus" placeholder="请选择状态" enum-name="FLOW_STATUS_ENUM" />
        </a-form-item>

        <a-form-item label="创建时间" class="smart-query-form-item">
          <a-range-picker v-model:value="queryForm.createTime" :presets="defaultTimeRanges" style="width: 220px" @change="dateChange" />

        </a-form-item>

        <a-form-item class="smart-query-form-item">
          <a-button type="primary" @click="handleQuery">
            <template #icon><SearchOutlined /></template>
            搜索
          </a-button>
          <a-button @click="resetQuery" class="smart-margin-left10">
            <template #icon><ReloadOutlined /></template>
            重置
          </a-button>
        </a-form-item>
      </a-row>
    </a-form>

    <a-card size="small" :bordered="false">
      <!---------- 表格 begin ----------->
      <a-table
          size="small"
          :columns="columns"
          :dataSource="instanceList"
          :loading="loading"
          :pagination="false"
          :row-selection="{ selectedRowKeys: selectedIds, onChange: onSelectChange }"
          rowKey="id"
          bordered
      >
        <template #bodyCell="{ text, record, column }">
          <template v-if="column.dataIndex === 'flowStatus'">
            <span>{{ text ? $smartEnumPlugin.getDescByValue('FLOW_STATUS_ENUM', Number(text)) : '' }}</span>
          </template>
          <template v-if="column.dataIndex === 'activityStatus'">
            <a-tag>{{$smartEnumPlugin.getDescByValue('FLAG_NUMBER_ENUM', text)}}</a-tag>
          </template>


<!--          <template v-if="column.dataIndex === 'createTime'">-->
<!--            <span>{{ parseTime(text) }}</span>-->
<!--          </template>-->

          <template v-if="column.dataIndex === 'action'">
            <div class="smart-table-operate">
              <a-button type="link" @click="handle(record, false)">办理</a-button>
<!--              <a-button type="link" @click="handle(record, true)">任意跳转</a-button>-->
              <a-button type="link" @click="transferShow(record, '转办')">转办</a-button>
              <a-button type="link" @click="transferShow(record, '加签')">加签</a-button>
              <a-button type="link" @click="transferShow(record, '委派')">委派</a-button>
              <a-button type="link" @click="transferShow(record, '减签')">减签</a-button>
              <a-button
                  type="link"
                  v-if="record.activityStatus === 0"
                  @click="toActive(record.instanceId)"
              >激活</a-button>
              <a-button
                  type="link"
                  v-if="record.activityStatus === 1"
                  @click="toUnActive(record.instanceId)"
              >挂起</a-button>
              <a-button type="link" @click="toFlowImage(record.instanceId)">流程图</a-button>
              <a-button type="link" @click="toFlowDoneList(record.instanceId)">审批记录</a-button>
            </div>
          </template>
        </template>
      </a-table>

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
            @change="getList"
            @showSizeChange="getList"
            :show-total="(total) => `共${total}条`"
        />
      </div>
    </a-card>

    <!---------- 流程图弹窗 ----------->
    <a-modal
        title="流程图"
        :open="flowChart"
        width="70%"
        @cancel="flowChart = false"
        @ok="flowChart = false"
    >
      <img :src="imgUrl" width="100%" style="margin:0 auto"/>
    </a-modal>

    <!---------- 审批记录弹窗 ----------->
    <a-modal
        title="审批记录"
        :open="flowDone"
        width="70%"
        @cancel="flowDone = false"
        @ok="flowDone = false"
    >
      <flowDoneList :instanceId="instanceId" />
    </a-modal>

    <component :is="DynamicDrawer" v-if="isLoaded"  v-model:openFlag="isOpen" :businessId="businessId" :taskId="taskId" @reloadList="getList();"/>

    <!---------- 用户选择弹窗 ----------->
    <a-modal
        :title="`${dialogTitle}用户选择`"
        :open="userVisible"
        :footer="[]"
        width="80%"
        @cancel="userVisible = false"
        v-if="userVisible"
    >
      <selectUser
          :key="modalKey"
          :postParams="postParams"
          :type="dialogTitle"
          v-model:selectUser="form.assigneePermission"
          @handleUserSelect="handleUserSelect"
      />
    </a-modal>
  </div>
</template>

<script setup>
import {reactive, ref, shallowRef, markRaw, onMounted, defineAsyncComponent, nextTick} from 'vue';
import { Modal, message } from 'ant-design-vue';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons-vue';
import selectUser from '/@/views/flow/components/select-user.vue';
import {instanceApi} from "/@/api/flow/instance-api.js";
import {definitionApi} from "/@/api/flow/definition-api.js";
import SmartEnumSelect from "/@/components/framework/smart-enum-select/index.vue";
import flowDoneList from "/@/views/flow/components/flow-done/index.vue"
import {defaultTimeRanges} from "/@/lib/default-time-ranges.js";
import {PAGE_SIZE_OPTIONS} from "/@/constants/common-const.js";

// 查询参数
const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  nodeName: null,
  flowStatus: null,
  createTime: null,
});

// 表格数据
const instanceList = ref([]);
const total = ref(0);
const loading = ref(false);
const selectedIds = ref([]);

// 弹窗状态
const flowChart = ref(false);
const flowDone = ref(false);
const userVisible = ref(false);
const imgUrl = ref("");
const dialogTitle = ref("");
const taskId = ref("");
const businessId = ref();
// 表单数据
const form = reactive({});
const postParams = reactive({});

const instanceId = ref(null);

// 动态组件声明（修复响应式警告的关键）
const DynamicDrawer = shallowRef();
const isOpen = ref(false);
const isLoaded = ref(false);

// 表格列配置
const columns = ref([
  { title: '序号',width: 50,customRender: ({ index }) => index + 1 },
  { title: 'ID', dataIndex: 'id' },
  { title: '流程名称', dataIndex: 'flowName', width: 140},
  { title: '任务名称', dataIndex: 'nodeName' },
  { title: '审批人', dataIndex: 'approver' },
  { title: '转办人', dataIndex: 'transferredBy' },
  { title: '委派人', dataIndex: 'delegate' },
  { title: '流程状态', dataIndex: 'flowStatus' ,align: 'center',},
  { title: '激活状态', dataIndex: 'activityStatus' ,align: 'center',},
  { title: '创建时间', dataIndex: 'createTime' },
  { title: '操作', dataIndex: 'action', width: 240, fixed: 'right' }
]);

// 初始化加载数据
onMounted(() => {
  getList();
});

// 获取表格数据
const getList = async () => {
  try {
    loading.value = true;
    const res = await instanceApi.toDoPage(queryForm);
    console.log(res.data.list);
    instanceList.value= res.data.list;
    total.value = res.data.total;
  } catch (e) {
    message.error('获取数据失败');
  } finally {
    loading.value = false;
  }
};

function dateChange(dates, dateStrings) {
  queryForm.startTime = dateStrings[0];
  queryForm.endTime = dateStrings[1];
}

// 查询处理
const handleQuery = () => {
  queryForm.pageNum = 1;
  getList();
};

// 重置查询
const resetQuery = () => {
  Object.keys(queryForm).forEach(key => {
    if (!['pageNum', 'pageSize'].includes(key)) {
      queryForm[key] = null;
    }
  });
  handleQuery();
};

// 分页处理
const handlePageChange = (page, pageSize) => {
  queryForm.pageNum = page;
  queryForm.pageSize = pageSize;
  getList();
};

// 多选处理
const onSelectChange = selectedRowKeys => {
  selectedIds.value = selectedRowKeys;
};

// 办理操作
const handle = async (row, showAnyNode) => {
  try {
    console.log(row)
    // 动态加载组件
    const module = await import('/@/views'+ row.formPath);
    DynamicDrawer.value = markRaw(module.default); // 双重保险
    await nextTick(); // 等待 DOM 更新
    isLoaded.value = true;
    isOpen.value = true;
    taskId.value = row.id;
    businessId.value = row.businessId;
  } catch (error) {
    console.error('审核组件加载失败:', error);
  }
};

// 显示流程图
const toFlowImage = async instanceId => {
  try {
    const res = await definitionApi.flowImage(instanceId);
    imgUrl.value = `data:image/gif;base64,${res.data}`;
    flowChart.value = true;
  } catch (e) {
    message.error('获取流程图失败');
  }
};
//显示审批记录
const toFlowDoneList =  instanceIdValue=> {
  flowDone.value = true;
  instanceId.value = instanceIdValue;
};

const modalKey = ref(Date.now());

// 用户选择处理
const transferShow = (row, title) => {
  dialogTitle.value = title;
  userVisible.value = true;
  modalKey.value = Date.now();  // 关键点3：每次生成新key
  instanceApi.getTaskById(row.id).then(res => {
    form.assigneePermission = res.data.assigneePermission ? res.data.assigneePermission.split(",") : [];
  });
  Object.assign(postParams, {
    taskId: row.id,
    operatorType: getOperatorType(title)
  });
};

// 操作类型映射
const getOperatorType = title => ({
  "转办": "1",
  "加签": "6",
  "委派": "3",
  "减签": "7"
}[title]);

// 激活/挂起操作
const toActive = async instanceId => {
  Modal.confirm({
    title: '系统提示',
    content: `确认激活流程 ${instanceId}?`,
    onOk: async () => {
      await instanceApi.active(instanceId);
      getList();
      message.success('激活成功');
    }
  });
};

const toUnActive = async instanceId => {
  Modal.confirm({
    title: '系统提示',
    content: `确认挂起流程 ${instanceId}?`,
    onOk: async () => {
      await instanceApi.unActive(instanceId);
      getList();
      message.success('挂起成功');
    }
  });
};

// 用户选择回调
const handleUserSelect = selectedRowKeys => {
  console.log(selectedRowKeys);
  form.assigneePermission = selectedRowKeys.map(e => {
    return e.employeeId;
  });
  let assigneePermission = JSON.parse(JSON.stringify(form.assigneePermission));
  let operatorTypeObj = {
    "转办": "2",
    "加签": "6",
    "委派": "3",
    "减签": "7"
  };
  instanceApi.interactiveType(taskId.value, Array.isArray(assigneePermission) ? assigneePermission.join(',') : assigneePermission, operatorTypeObj[dialogTitle.value]).then(() => {
    message.success(`${dialogTitle.value}成功`);
    open.value = false;
    getList();
  });
};
</script>