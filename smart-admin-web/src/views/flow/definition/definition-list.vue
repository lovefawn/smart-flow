
<template>
  <!---------- 查询表单form begin ----------->
  <a-form class="smart-query-form">
    <a-row class="smart-query-form-row">
      <a-form-item label="流程编码" class="smart-query-form-item">
        <a-input style="width: 200px" v-model:value="queryForm.flowCode" placeholder="请输入流程编码" />
      </a-form-item>
      <a-form-item label="流程名称" class="smart-query-form-item">
        <a-input style="width: 150px" v-model:value="queryForm.flowName" placeholder="请输入流程名称" />
      </a-form-item>
      <a-form-item label="流程类别" class="smart-query-form-item">
        <a-input style="width: 150px" v-model:value="queryForm.category" placeholder="请输入流程类别" />
      </a-form-item>
      <a-form-item label="流程版本" class="smart-query-form-item">
        <a-input style="width: 150px" v-model:value="queryForm.version" placeholder="请输入流程版本" />
      </a-form-item>
      <a-form-item class="smart-query-form-item">
        <a-button type="primary" @click="queryData">
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
        <a-button @click="showForm" type="primary">
          <template #icon>
            <PlusOutlined />
          </template>
          新建
        </a-button>
        <a-button
            @click="handleImport"
            v-privilege="['flow:definition:importDefinition']"
        >
          <template #icon>
            <PlusOutlined />
          </template>导入流程定义</a-button>
<!--        <a-button @click="confirmBatchDelete" type="primary" danger :disabled="selectedRowKeyList.length === 0">-->
<!--          <template #icon>-->
<!--            <DeleteOutlined />-->
<!--          </template>-->
<!--          批量删除-->
<!--        </a-button>-->
      </div>
      <div class="smart-table-setting-block">
        <TableOperator v-model="columns" :tableId="TABLE_ID_CONST.SYSTEM.EMPLOYEE" :refresh="queryData" />
      </div>
    </a-row>
    <!---------- 表格操作行 end ----------->

    <!---------- 表格 begin ----------->
    <a-table
      size="small"
      :dataSource="tableData"
      :columns="columns"
      rowKey="id"
      bordered
      :loading="tableLoading"
      :pagination="false"
      :row-selection="{ selectedRowKeys: selectedRowKeyList, onChange: onSelectChange }"
    >
      <template #bodyCell="{ text, record, column }">
        <template v-if="column.dataIndex === 'version'">
          <a-tag color="default">{{text}}</a-tag>
        </template>
        <template v-if="column.dataIndex === 'isPublish'">
          <a-tag v-show="text==0" color="error">未发布</a-tag>
          <a-tag v-show="text==1" color="success">已发布</a-tag>
          <a-tag v-show="text==9" color="error">已失效</a-tag>
        </template>
        <template v-if="column.dataIndex === 'activityStatus'">
          <a-tag v-show="!text" color="error">否</a-tag>
          <a-tag v-show="text" color="success">是</a-tag>
        </template>
        <template v-if="column.dataIndex === 'action'">
          <div class="smart-table-operate">
            <a-button
                type="link"
                @click="handleDesign(record.id, record.isPublish)"
            >流程设计</a-button>
            <a-button
                type="link"
                @click="toFlowImage(record.id)"
            >流程图</a-button>
            <a-button
                type="link"
                v-if="record.isPublish === 0"
                @click="handlePublish(record.id)"
            >发布</a-button>
            <a-button
                type="link"
                v-if="record.isPublish === 1"
                @click="handleUpPublish(record.id)"
            >取消发布</a-button>
            <a-button
                type="link"
                v-if="record.activityStatus === 0"
                @click="toActive(record.id)"
            >激活</a-button>
            <a-button
                type="link"
                v-if="record.activityStatus === 1"
                @click="toUnActive(record.id)"
            >挂起</a-button>
            <a-button
                type="link"
                @click="handleCopyDef(record.id)"
                v-privilege="['flow:definition:upPublish']"
            >复制流程</a-button>
            <a-button
                type="link"
                @click="handleExport(record)"
                v-privilege="['flow:definition:exportDefinition']"
            >导出流程</a-button>
            <a-button @click="showForm(record)" type="link"
                      v-if="record.isPublish === 0"
            >编辑</a-button>
            <a-button @click="onDelete(record)" danger type="link"
                      v-if="record.isPublish === 0"
            >删除</a-button>
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

    <DefinitionForm ref="formRef" @reloadList="queryData" />
  </a-card>
  <!-- 用户导入对话框 -->
  <a-modal
      :title="upload.title"
      v-model:visible="upload.open"
      width="400px"
  >
    <a-upload-dragger
        name="file"
        v-model:fileList="fileList"
        :multiple="false"
        :max-count="1"
        accept=".json"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :before-upload="beforeUpload"
        @change="handleFileChange"
        :show-upload-list="true"
    >
      <p class="ant-upload-drag-icon">
        <cloud-upload-outlined />
      </p>
      <p class="ant-upload-text">点击或将JSON文件拖拽到此区域</p>
      <p class="ant-upload-hint">仅支持.json格式文件</p>
    </a-upload-dragger>

    <template #footer>
      <a-space>
        <a-button
            @click="submitFileForm"
            :loading="upload.isUploading"
        >确定</a-button>
        <a-button @click="upload.open = false">取消</a-button>
      </a-space>
    </template>
  </a-modal>

  <a-modal title="流程图"
           :open="flowChart"
           width="80%"
           @cancel="onClose"
           @ok="onClose">
    <img :src="imgUrl" width="100%" style="margin:0 auto"/>
  </a-modal>
</template>
<script setup>
  import { reactive, ref, onMounted } from 'vue';
  import {message, Modal, Upload} from 'ant-design-vue';
  import { SmartLoading } from '/@/components/framework/smart-loading';
  import { definitionApi } from '/@/api/flow/definition-api';
  import { PAGE_SIZE_OPTIONS } from '/@/constants/common-const';
  import { smartSentry } from '/@/lib/smart-sentry';
  import TableOperator from '/@/components/support/table-operator/index.vue';
  import DefinitionForm from './definition-form.vue';
  import _ from 'lodash';
  import { TABLE_ID_CONST } from '/@/constants/support/table-id-const';
  import {useRouter} from "vue-router";
  // ---------------------------- 表格列 ----------------------------

  const columns = ref([
    {
      title: '流程编码',
      dataIndex: 'flowCode',
      ellipsis: true,
    },
    {
      title: '流程名称',
      dataIndex: 'flowName',
      ellipsis: true,
    },
    {
      title: '流程版本',
      dataIndex: 'version',
      ellipsis: true,
      align: 'center',
    },
    {
      title: '流程类别',
      dataIndex: 'category',
      ellipsis: true,
    },
    {
      title: '是否发布',
      dataIndex: 'isPublish',
      ellipsis: true,
      align: 'center',
    },
    {
      title: '激活状态',
      dataIndex: 'activityStatus',
      ellipsis: true,
      align: 'center',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      ellipsis: true,
    },
    {
      title: '操作',
      dataIndex: 'action',
      fixed: 'right',
      width: 250,
    },
  ]);

  // ---------------------------- 查询数据表单和方法 ----------------------------

  const router = useRouter();
  const queryFormState = {
    flowCode: null,
    flowName: null,
    category: null,
    version: null,
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
  const fileList = ref([]);
  /** 导入按钮操作 */
  function handleImport() {
    upload.title = "用户导入";
    upload.open = true;
    fileList.value = [];
  }

  // 查询数据
  async function queryData() {
    tableLoading.value = true;
    try {
      let queryResult = await definitionApi.queryPage(queryForm);
      tableData.value = queryResult.data.list;
      total.value = queryResult.data.total;
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      tableLoading.value = false;
    }
  }

  onMounted(queryData);

  // ---------------------------- 添加/修改 ----------------------------
  const formRef = ref();

  function showForm(data) {
    formRef.value.show(data);
  }

  // 响应式数据
  const upload = reactive({
    open: false,
    isUploading: false,
    title: '',
    url: import.meta.env.VITE_APP_API_URL + "/flow/definition/importDefinition",
    headers: {},
    updateSupport: false
  });

  // 文件校验
  const beforeUpload = file => {
    const isJson = file.type === 'application/json';
    if (!isJson) {
      message.error('仅支持JSON格式文件!');
      return Upload.LIST_IGNORE;
    }
    return false;
  };
  // 上传状态处理
  const handleFileChange = info => {
    if (info.file.status === 'uploading') {
      upload.isUploading = true;
    }
  };

  // 提交处理
  const submitFileForm = async () => {
    const formData = new FormData();
    fileList.value.forEach((file) => {
      formData.append('file', file.originFileObj);
    });

    SmartLoading.show();
    try {
      let res = await definitionApi.importDefinition(formData);
      message.success(res.msg);
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
      queryData();
      upload.open = false;
    }
  };

  /** 流程设计按钮操作 */
  function handleDesign(id, isPublish) {
    const params = { disabled: isPublish === 1, pageNum: queryForm.pageNum };
    router.push({ path: "/flow/flow-design/index/" + id, query: params });
  }

  /** 发布按钮操作 */
  function handlePublish(id) {
    Modal.confirm({
      title: '系统提示',
      content: `是否确认发布流程定义编号为"${id}"的数据项？`,
      okText: '确认',
      cancelText: '取消',
      onOk() {
        return new Promise((resolve, reject) => {
          definitionApi.publish(id)
              .then(() => {
                queryData();
                message.success('发布成功');
                resolve();
              })
              .catch(error => {
                message.error('发布失败');
                reject(error);
              });
        });
      },
      onCancel() {}
    });
  }

  /** 取消发布按钮操作 */
  function handleUpPublish(id) {
    Modal.confirm({
      title: '系统提示',
      content: `是否确认取消发布流程定义编号为"${id}"的数据项？`,
      okText: '确认',
      cancelText: '取消',
      onOk() {
        return new Promise((resolve, reject) => {
          definitionApi.unPublish(id)
              .then(() => {
                queryData();
                message.success('取消成功');
                resolve();
              })
              .catch(error => {
                message.error('取消失败');
                reject(error);
              });
        });
      },
      onCancel() {}
    });
  }
  /** 挂起按钮操作 */
  function toUnActive(id) {
    Modal.confirm({
      title: '系统提示',
      content: `是否确认挂起流程定义编号为"${id}"的数据项？`,
      okText: '确认',
      cancelText: '取消',
      onOk() {
        return new Promise((resolve, reject) => {
          definitionApi.unActive(id)
              .then(() => {
                queryData();
                message.success('挂起成功');
                resolve();
              })
              .catch(error => {
                message.error('挂起失败');
                reject(error);
              });
        });
      },
      onCancel() {}
    });
  }

  /** 激活按钮操作 */
  function toActive(id) {
    Modal.confirm({
      title: '系统提示',
      content: `是否确认激活流程定义编号为"${id}"的数据项？`,
      okText: '确认',
      cancelText: '取消',
      onOk() {
        return new Promise((resolve, reject) => {
          definitionApi.active(id)
              .then(() => {
                queryData();
                message.success('激活成功');
                resolve();
              })
              .catch(error => {
                message.error('激活失败');
                reject(error);
              });
        });
      },
      onCancel() {}
    });
  }
  /** 复制流程按钮操作 */
  function handleCopyDef(id) {
    Modal.confirm({
      title: '系统提示',
      content: `是否确认复制流程定义编号为"${id}"的数据项？`,
      okText: '确认',
      cancelText: '取消',
      onOk() {
        return new Promise((resolve, reject) => {
          definitionApi.copyDef(id)
              .then(() => {
                queryData();
                message.success('复制成功');
                resolve();
              })
              .catch(error => {
                message.error('复制失败');
                reject(error);
              });
        });
      }
    });
  }
  async function handleExport(record) {
    await definitionApi.export(record);
  }

  // ---------------------------- 单个删除 ----------------------------
  //确认删除
  function onDelete(data) {
    Modal.confirm({
      title: '提示',
      content: '确定要删除吗?',
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
      await definitionApi.delete(data.id);
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
    if (_.isEmpty(selectedRowKeyList.value)) {
      message.success('请选择要删除的数据');
      return;
    }
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
      await definitionApi.batchDelete(selectedRowKeyList.value);
      message.success('删除成功');
      queryData();
    } catch (e) {
      smartSentry.captureError(e);
    } finally {
      SmartLoading.hide();
    }
  }
  const imgUrl = ref("");
  const flowChart = ref(false);
  function toFlowImage(id) {
    definitionApi.chartDef(id).then(response => {
      flowChart.value = true
      imgUrl.value = "data:image/gif;base64," + response.data;
    });
  }
  function onClose() {
    flowChart.value = false;
  };

</script>
