
<template>
  <a-modal
      :title="form.positionId ? '编辑' : '添加'"
      width="600px"
      :open="visibleFlag"
      @cancel="onClose"
      :maskClosable="false"
      :destroyOnClose="true"
      forceRender
  >
    <a-form ref="formRef" :model="form" :rules="rules" :label-col="{ span: 6 }">
      <a-tabs v-model:activeKey="tabsValue">
        <a-tab-pane key="1" tab="基础设置"/>
        <a-tab-pane key="2" tab="监听器" force-render/>
      </a-tabs>
      <div v-show="tabsValue === '1'">
      <a-form-item label="流程编码" name="flowCode">
        <a-input style="width: 100%" v-model:value="form.flowCode" placeholder="流程编码" />
      </a-form-item>
      <a-form-item label="流程名称" name="flowName">
        <a-input style="width: 100%" v-model:value="form.flowName" placeholder="流程名称" />
      </a-form-item>
      <a-form-item label="流程类别" name="category">
        <a-input style="width: 100%" v-model:value="form.category" placeholder="流程类别" />
      </a-form-item>
        <a-form-item label="审批表单" name="formCustom">
        <a-select v-model:value="form.formCustom" style="width: 100%"  :allowClear="true">
          <a-select-option value="N">表单路径</a-select-option>
        </a-select>
        </a-form-item>
        <a-form-item label="审批表单路径" name="formPath" v-if="form.formCustom === 'N'">
          <a-input style="width: 100%" v-model:value="form.formPath" placeholder="请输入审批表单路径"  />
        </a-form-item>
      </div>
      <div v-show="tabsValue === '2'">
        <a-form-item name="listenerRows" class="listenerItem">
          <a-table
              :dataSource="form.listenerRows"
              :columns="columns"
              :pagination="false"
              size="small"
          >
            <template #bodyCell="{ column, record, index }">
              <template v-if="column.dataIndex === 'listenerType'">
                <a-form-item
                    :name="['listenerRows', index, 'listenerType']"
                    :rules="rules.listenerType"
                >
                  <a-select
                      v-model:value="record.listenerType"
                      placeholder="请选择类型"
                  >
                    <a-select-option value="start">开始</a-select-option>
                    <a-select-option value="assignment">分派</a-select-option>
                    <a-select-option value="finish">完成</a-select-option>
                    <a-select-option value="create">创建</a-select-option>
                  </a-select>
                </a-form-item>
              </template>

              <template v-if="column.dataIndex === 'listenerPath'">
                <a-form-item
                    :name="['listenerRows', index, 'listenerPath']"
                    :rules="rules.listenerPath"
                >
                  <a-input
                      v-model:value="record.listenerPath"
                      placeholder="请输入路径"
                  />
                </a-form-item>
              </template>

              <template v-if="column.dataIndex === 'action'">
                <a-button
                    type="link"
                    danger
                    size="small"
                    @click="handleDeleteRow(index)"
                >
                  <template #icon><delete-outlined /></template>
                </a-button>
              </template>
            </template>
          </a-table>

          <a-button
              type="primary"
              style="margin-top: 10px"
              @click="handleAddRow"
          >
            增加行
          </a-button>
        </a-form-item>
      </div>
    </a-form>

    <template #footer>
      <a-space>
        <a-button @click="onClose">取消</a-button>
        <a-button type="primary" @click="onSubmit">保存</a-button>
      </a-space>
    </template>
  </a-modal>
</template>
<script setup>
  import { reactive, ref, nextTick } from 'vue';
  import _ from 'lodash';
  import { message } from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { definitionApi } from '/@/api/flow/definition-api';
  import { smartSentry } from '/@/lib/smart-sentry';

  // ------------------------ 事件 ------------------------

  const emits = defineEmits(['reloadList']);

  // ------------------------ 显示与隐藏 ------------------------
  // 是否显示
  const visibleFlag = ref(false);
  // 显示标签页
  const tabsValue = ref('1');

  const columns = [
    {
      title: '类型',
      dataIndex: 'listenerType',
      width: 150,
    },
    {
      title: '路径',
      dataIndex: 'listenerPath',
    },
    {
      title: '操作',
      dataIndex: 'action',
      width: 65,
    },
  ];

  function handleAddRow() {
    form.listenerRows.push({
      listenerType: undefined, // 初始未选择类型
      listenerPath: ''        // 初始空路径
    });
  }
  function handleDeleteRow(index) {
    form.listenerRows.splice(index, 1);
  }

  function show(rowData) {
    Object.assign(form, formDefault);
    if (rowData && !_.isEmpty(rowData)) {
      Object.assign(form, rowData);
    }
    if (rowData.listenerType) {
      const listenerTypes = rowData.listenerType.split(",");
      const listenerPaths = rowData.listenerPath.split("@@");
      form.listenerRows = listenerTypes.map((type, index) => ({
        listenerType: type,
        listenerPath: listenerPaths[index]
      }));
    } else {
      form.listenerRows = [];
    }
    visibleFlag.value = true;
    nextTick(() => {
      formRef.value.clearValidate();

      // 解决弹窗错误信息显示,没有可忽略
      const domArr = document.getElementsByClassName('ant-modal');
      if (domArr && domArr.length > 0) {
        Array.from(domArr).forEach((item) => {
          if (item.childNodes && item.childNodes.length > 0) {
            Array.from(item.childNodes).forEach((child) => {
              if (child.setAttribute) {
                child.setAttribute('aria-hidden', 'false');
              }
            });
          }
        });
      }
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
    id: undefined,
    flowCode: undefined,
    flowName: undefined,
    category: undefined,
    formCustom: 'N',
    formPath: undefined,
    listenerRows: [], // 新增初始化监听器数组
  };

  let form = reactive({ ...formDefault });

  const rules = {
    flowCode: [{ required: true, message: '请输入流程编码' }],
    flowName: [{ required: true, message: '请输入流程名称' }],
    formCustom: [{ required: true, message: '请选择审批表单' }],
    listenerType: [{ required: true, message: '监听器不能为空', trigger: ['change', 'blur'] }],
    listenerPath: [{ required: true, message: '监听器不能为空', trigger: ['change', 'blur'] }]
  };

  // 点击确定，验证表单
  async function onSubmit() {
    try {
      await formRef.value.validateFields();
      form.listenerType = form.listenerRows.map(row => row.listenerType).join(",")
      form.listenerPath = form.listenerRows.map(row => row.listenerPath).join("@@")
      console.log(form)
      save();
    } catch (err) {
      message.error('参数验证错误，请仔细填写表单数据!');
    }
  }

  // 新建、编辑API
  async function save() {
    SmartLoading.show();
    try {
      if (form.id) {
        await definitionApi.update(form);
      } else {
        await definitionApi.add(form);
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

  defineExpose({
    show,
  });
</script>
<style scoped>
/* 关键优化点：覆盖表单项和表格边距 */
.listenerItem :deep() .ant-form-item {
  margin-top: 5px !important;
  margin-bottom: 5px !important;
}
.listenerItem :deep() .ant-table {
  margin-top: 5px !important;
  margin-bottom: 5px !important;
}
</style>
