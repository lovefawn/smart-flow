<!--
  * OA 请假申请表
  *
  * @Author:    lf
  * @Date:      2025-05-31 20:00:10
  * @Copyright  lf
-->
<template>
  <a-drawer
      title="审批"
      :width="500"
      :open="openFlag"
      @close="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 5 }" >
        <a-form-item label="请假类型"  name="type">
          <DictSelect :dict-code="DICT_CODE_ENUM.LEAVE_TYPE" placeholder="请假类型" v-model:value="form.type" width="100%" />
        </a-form-item>
        <a-form-item label="开始时间"  name="startTime">
          <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="form.startTime" style="width: 100%" placeholder="开始时间" />
        </a-form-item>
        <a-form-item label="结束时间"  name="endTime">
          <a-date-picker valueFormat="YYYY-MM-DD" v-model:value="form.endTime" style="width: 100%" placeholder="结束时间" />
        </a-form-item>
        <a-form-item label="天数"  name="day">
          <a-input-number v-model:value="form.day" style="width: 100%" disabled placeholder="自动计算" />
        </a-form-item>
      <!-- 将请假原因移到最后并改为多行输入 -->
      <a-form-item label="请假原因" name="reason">
        <a-textarea v-model:value="form.reason" placeholder="请输入请假原因" :rows="4" style="width: 100%" />
      </a-form-item>
      <a-divider />
      <a-form-item label="审批意见" name="message">
        <a-textarea v-model:value="form.message"  placeholder="请输入审批意见" :auto-size="{ minRows: 3, maxRows: 8 }" />
      </a-form-item>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">关闭</a-button>
<!--        <a-button type="primary" @click="onSubmit">保存</a-button>-->
        <a-button type="primary" v-if="form.leaveId && form.instanceId" @click="approval">审批</a-button>

      </a-space>
    </template>
  </a-drawer>
</template>
<script setup>
  import { reactive, ref, nextTick, onMounted, watch } from 'vue';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { leaveApi } from '/@/api/business/oa/leave-api';
  import {DICT_CODE_ENUM} from "/@/constants/support/dict-const.js";
  import DictSelect from "/@/components/support/dict-select/index.vue";
  import dayjs from 'dayjs';
  import {instanceApi} from "/@/api/flow/instance-api.js";

  // ------------------------ 事件 ------------------------

  const emits = defineEmits(['update:openFlag', 'reloadList']);

  // ------------------------ 显示与隐藏 ------------------------

  const props = defineProps({
    openFlag: { type: Boolean, default: false },
    businessId: { type: String },
    taskId: { type: String },
  });

  // 显示
  async function initData(leaveId) {
    console .log('initData', leaveId);
    if (leaveId) {
      const result = await leaveApi.getDetail(leaveId);
      Object.assign(form, result.data);
      console .log('result', form);

    }
  }
  onMounted(() => {
    initData(props.businessId)
  })

  function onClose() {
    emits('update:openFlag', false);
    emits('reloadList');
  }

  // ------------------------ 表单 ------------------------

  // 组件ref
  const formRef = ref();

  const formDefault = {
    leaveId: undefined,
      type: undefined, //请假类型
      reason: undefined, //请假原因
      startTime: undefined, //开始时间
      endTime: undefined, //结束时间
      day: undefined, //天数
      message: undefined,
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

  const completeTaskForm = reactive({});
  //提交审批
  async function approval() {
    try {
      SmartLoading.show();
      completeTaskForm.flowCode = "leave";
      completeTaskForm.businessId = form.leaveId;
      completeTaskForm.taskId = props.taskId;
      completeTaskForm.message = form.message;
      //流程变量
      let taskVariables = {
        flowCode : "leave",
        businessId : form.leaveId,
      };
      completeTaskForm.variables = taskVariables;
      const resp = await instanceApi.completeTask(completeTaskForm);
      message.success('审批成功');
      emits('reloadList');
      onClose();
    } finally {
      SmartLoading.hide();
    }
  }
</script>
