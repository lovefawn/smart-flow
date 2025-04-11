package net.lab1024.sa.admin.module.flow.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class WarmFlowInteractiveTypeVO implements Serializable {
    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 增加办理人
     */
    private List<String> addHandlers;

    /**
     * 操作类型[2:转办,6:加签,3:委派,7:减签]
     */
    private Integer operatorType;

    /**
     * 部门ID
     */
    private Long departmentId;

    private List<String> userIds;

    /** 请求参数 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public Long getTaskId() {
        return taskId;
    }

    public WarmFlowInteractiveTypeVO setTaskId(Long taskId) {
        this.taskId = taskId;
        return this;
    }

    public List<String> getAddHandlers() {
        return addHandlers;
    }

    public WarmFlowInteractiveTypeVO setAddHandlers(List<String> addHandlers) {
        this.addHandlers = addHandlers;
        return this;
    }

    public Integer getOperatorType() {
        return operatorType;
    }

    public WarmFlowInteractiveTypeVO setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
        return this;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public WarmFlowInteractiveTypeVO setUserIds(List<String> userIds) {
        this.userIds = userIds;
        return this;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

}
