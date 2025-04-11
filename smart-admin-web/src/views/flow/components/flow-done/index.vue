<template>
  <a-card>
  <a-table
      size="small"
      :columns="columns"
      :data-source="tableData"
      :pagination="false"
      :loading="tableLoading"
      row-key="employeeId"
      bordered
  >
    <template #bodyCell="{ text, column }">
      <template v-if="column.dataIndex === 'flowStatus'">
        <a-tag color="blue" v-if="!!text">{{ text ? $smartEnumPlugin.getDescByValue('FLOW_STATUS_ENUM', Number(text)) : '' }}</a-tag>
      </template>
    </template>
  </a-table>
  </a-card>
</template>
<script setup name="flowDoneList">
import { onMounted,reactive, ref } from 'vue';
import { instanceApi} from "/@/api/flow/instance-api.js";
import { smartSentry } from '/@/lib/smart-sentry';
const props = defineProps({
  instanceId: String,
});
// ----------------------- 以下是字段定义 emits props ---------------------
//字段
const columns = ref([
  { title: '序号',width: 50,customRender: ({ index }) => index + 1 },
  { title: '开始节点名称', dataIndex: 'nodeName' },
  { title: '结束节点名称', dataIndex: 'targetNodeName' },
  { title: '审批人', dataIndex: 'approver' },
  { title: '流程状态', dataIndex: 'flowStatus' },
  { title: '审批意见', dataIndex: 'message' },
  { title: '创建时间', dataIndex: 'createTime' },
]);
const tableData = ref();

const tableLoading = ref(false);
// 查询
async function queryList() {
  tableLoading.value = true;
  try {

    let res = await instanceApi.doneList(props.instanceId);
    tableData.value = res.data;
  } catch (error) {
    smartSentry.captureError(error);
  } finally {
    tableLoading.value = false;
  }
}
// 初始化加载数据
onMounted(() => {
  queryList();
});
</script>