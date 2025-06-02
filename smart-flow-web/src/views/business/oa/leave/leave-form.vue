<!--
  * OA 请假申请表
  *
  * @Author:    lf
  * @Date:      2025-05-31 20:00:10
  * @Copyright  lf
-->
<template>
  <a-drawer :title="form.leaveId ? '编辑' : '添加'" :width="500" :open="visibleFlag" @close="onClose" :maskClosable="false"
    :destroyOnClose="true">
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }">
      <a-form-item label="请假类型" name="type">
        <DictSelect :dict-code="DICT_CODE_ENUM.LEAVE_TYPE" placeholder="请假类型" v-model:value="form.type" width="100%" />
      </a-form-item>
      <a-form-item label="开始时间" name="startTime">
        <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="form.startTime" style="width: 100%" placeholder="开始时间" />
      </a-form-item>
      <a-form-item label="结束时间" name="endTime">
        <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="form.endTime" style="width: 100%" placeholder="结束时间" />
      </a-form-item>
      <a-form-item label="天数" name="day">
        <a-input-number v-model:value="form.day" style="width: 100%" disabled placeholder="自动计算" />
      </a-form-item>
      <!-- 将请假原因移到最后并改为多行输入 -->
      <a-form-item label="请假原因" name="reason">
        <a-textarea v-model:value="form.reason" placeholder="请输入请假原因" :rows="4" style="width: 100%" />
      </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">取消</a-button>
        <a-button type="primary" v-if="!form.instanceId" @click="onSubmit">保存</a-button>
        <a-button type="primary" v-if="form.leaveId && !form.instanceId" @click="handleStartWorkFlow">提交</a-button>
        <a-button type="primary" v-if="form.leaveId && form.instanceId && form.flowStatus && form.flowStatus != 8"
          @click="approval">审批</a-button>

      </a-space>
    </template>
  </a-drawer>
</template>
<script setup>
import { reactive, ref, nextTick, watch } from 'vue';
import _ from 'lodash';
import { message } from 'ant-design-vue';
import { SmartLoading } from '/@/components/framework/smart-loading';
import { leaveApi } from '/@/api/business/oa/leave-api';
import { instanceApi } from '/@/api/flow/instance-api';
import { smartSentry } from '/@/lib/smart-sentry';
import { DICT_CODE_ENUM } from "/@/constants/support/dict-const.js";
import DictSelect from "/@/components/support/dict-select/index.vue";
import dayjs from 'dayjs';

// ------------------------ 事件 ------------------------

const emits = defineEmits(['reloadList']);

// ------------------------ 显示与隐藏 ------------------------
// 是否显示
const visibleFlag = ref(false);

function show(rowData) {
  Object.assign(form, formDefault);
  if (rowData && !_.isEmpty(rowData)) {
    Object.assign(form, rowData);
  }
  // 使用字典时把下面这注释修改成自己的字典字段 有多个字典字段就复制多份同理修改 不然打开表单时不显示字典初始值
  // if (form.status && form.status.length > 0) {
  //   form.status = form.status.map((e) => e.valueCode);
  // }
  visibleFlag.value = true;
  nextTick(() => {
    formRef.value.clearValidate();
  });
}

function onClose() {
  Object.assign(form, formDefault);
  visibleFlag.value = false;
}

// ------------------------ 表单 ------------------------

// 组件ref
const formRef = ref();

const formDefault = {
  type: undefined, //请假类型
  reason: undefined, //请假原因
  startTime: undefined, //开始时间
  endTime: undefined, //结束时间
  day: undefined, //天数
};

let form = reactive({ ...formDefault });

const rules = {
  type: [{ required: true, message: '请假类型 必填' }],
  reason: [{ required: true, message: '请假原因 必填' }],
  startTime: [{ required: true, message: '开始时间 必填' }],
  endTime: [{ required: true, message: '结束时间 必填' }],
};

// 计算天数差异
watch(
  () => [form.startTime, form.endTime],
  ([newStart, newEnd]) => {
    if (newStart && newEnd) {
      const start = dayjs(newStart);
      const end = dayjs(newEnd);
      form.day = end.diff(start, 'day');
    }
  },
  { immediate: true }
);

// 点击确定，验证表单
async function onSubmit() {
  try {
    await formRef.value.validateFields();
    save();
  } catch (err) {
    message.error('参数验证错误，请仔细填写表单数据!');
  }
}

// 新建、编辑API
async function save() {
  SmartLoading.show();
  try {
    if (form.leaveId) {
      await leaveApi.update(form);
    } else {
      await leaveApi.add(form);
    }
    message.success('操作成功');
    emits('reloadList');
    onClose();
  } catch (err) {
    smartSentry.captureError(err);
  } finally {
    SmartLoading.hide();
  }
}

//提交申请
const submitFlowData = reactive({});
  const handleStartWorkFlow = async () => {
    try {
      SmartLoading.show();
      submitFlowData.flowCode = "leave";
      submitFlowData.businessId = form.leaveId;
      //流程变量
      let taskVariables = {
        flowCode : "leave",
        businessId : form.leaveId,
      };
      submitFlowData.variables = taskVariables;
      const resp = await instanceApi.startWorkFlow(submitFlowData);
      console.log(resp);
      message.success('提交成功');
      emits('reloadList');
      onClose();
    } finally {
      SmartLoading.hide();
    }
  };
  const completeTaskForm = reactive({});
  const approval = async () => {
    try {
    SmartLoading.show();
    completeTaskForm.flowCode = "leave";
    completeTaskForm.businessId = form.leaveId;
    completeTaskForm.instanceId = form.instanceId;
    completeTaskForm.message = form.message;
      //流程变量
      let taskVariables = {
        flowCode : "leave",
        businessId : form.leaveId,
      };
      completeTaskForm.variables = taskVariables;
    const resp = await instanceApi.approveTaskByInstance(completeTaskForm);
    message.success('审批成功');
    emits('reloadList');
    onClose();
    } finally {
      SmartLoading.hide();
    }
  };

defineExpose({
  show,
});
</script>
