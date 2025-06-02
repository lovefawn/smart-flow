/**
 * 流程定义 api 封装
 *
 * @Author:    lovefawn
 * @Date:      2025-04-01 23:31:38
 */
import { postRequest, getRequest, postDownload } from '/@/lib/axios';

export const definitionApi = {
  /**
   * 分页查询  @author  lovefawn
   */
  queryPage: (param) => {
    return postRequest('/flow/definition/queryPage', param);
  },

  /**
   * 增加  @author  lovefawn
   */
  add: (param) => {
    return postRequest('/flow/definition/add', param);
  },

  /**
   * 修改  @author  lovefawn
   */
  update: (param) => {
    return postRequest('/flow/definition/update', param);
  },

  /**
   * 删除  @author  lovefawn
   */
  delete: (id) => {
    return getRequest(`/flow/definition/delete/${id}`);
  },

  /**
   * 批量删除  @author  lovefawn
   */
  batchDelete: (idList) => {
    return postRequest('/flow/definition/batchDelete', idList);
  },

  /**
   * 查询列表  @author  lovefawn
   */
  queryList: () => {
    return getRequest('/flow/definition/queryList');
  },

  // 查看流程图
  chartDef: (definitionId) => {
    return getRequest('/flow/definition/chartDef/' + definitionId);
  },
  // 发布流程定义
  publish: (id) => {
    return getRequest('/flow/definition/publish/' + id);
  },

  // 取消发布流程定义
  unPublish: (id) => {
    return getRequest('/flow/definition/unPublish/' + id);
  },

  // 复制流程定义
  copyDef: (id) => {
    return getRequest('/flow/definition/copyDef/' + id);
  },
  // 激活流程
  active: (definitionId) => {
    return getRequest('/flow/definition/active/' + definitionId);
  },

  // 挂起流程
  unActive: (definitionId) => {
    return getRequest('/flow/definition/unActive/' + definitionId);
  },
  // 导入流程定义
  importDefinition: (file) => {
    return postRequest('/flow/definition/importDefinition', file);
  },
  // 导出流程定义
  export: (record) => {
    return postDownload('/flow/definition/exportDefinition/' + record.id, record.flowCode + '_' + record.version + '.json');
  }
};
