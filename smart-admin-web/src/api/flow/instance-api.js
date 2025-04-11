import {postRequest, getRequest} from "/@/lib/axios.js";

export const instanceApi = {
    /**
     * 启动流程
     * @param data
     * @returns {*}
     */
startWorkFlow : (data) => {
    return postRequest('/flow/execute/startWorkFlow',data);
},
    /**
     * 审批流程
     * @param data
     * @returns {*}
     */
    approveTaskByInstance : (data) => {
        return postRequest('/flow/execute/approveTaskByInstance',data);
    },

/**
 * 办理流程
 * @param data
 * @returns {*}
 */
completeTask :(data) => {
    return postRequest('/flow/execute/completeTask', data);
},
    /**
     * 查询待办任务列表  @author  lovefawn
     */
    toDoPage: (query) => {
        return getRequest('/flow/execute/toDoPage', query);
    },

    /**
     * 查询已办任务列表  @author  lovefawn
     */
    donePage: (query) => {
        return getRequest('/flow/execute/donePage', query);
    },

    /**
     * 查询抄送任务列表  @author  lovefawn
     */
    copyPage: (query) => {
        return getRequest('/flow/execute/copyPage', query);
    },

    /**
     * 查询实例已办列表  @author  lovefawn
     */
    doneList: (instanceId) => {
        return getRequest(`/flow/execute/doneList/${instanceId}`);
    },

    /**
     * 查询跳转节点列表  @author  lovefawn
     */
    anyNodeList: (instanceId) => {
        return getRequest(`/flow/execute/anyNodeList/${instanceId}`);
    },

    /**
     * 任务操作（转办/加签/委派/减签）  @author  lovefawn
     */
    interactiveType: (params) => {
        return postRequest('/flow/execute/interactiveType', params);
    },

    // 查询用户列表-转办|加签|委派|减签
    interactiveTypeEmployeeEntity:(query) => {return getRequest('/flow/execute/interactiveTypeEmployeeEntity', query);
    },

    /**
     * 通过ID查询任务详情  @author  lovefawn
     */
    getTaskById: (taskId) => {
        return getRequest(`/flow/execute/getTaskById/${taskId}`);
    },

    /**
     * 激活流程实例  @author  lovefawn
     */
    active: (instanceId) => {
        return getRequest(`/flow/execute/active/${instanceId}`);
    },

    /**
     * 挂起流程实例  @author  lovefawn
     */
    unActive: (instanceId) => {
        return getRequest(`/flow/execute/unActive/${instanceId}`);
    },
}