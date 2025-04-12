package net.lab1024.sa.admin.module.flow.domain.vo;

import org.dromara.warm.flow.orm.entity.FlowTask;

/**
 * 待办任务vo
 *
 * @author warm
 */
public class FlowTaskVO extends FlowTask {

    /**
     * 计划审批人
     */
    private String approver;

    /**
     * 转办人
     */
    private String transferredBy;

    /**
     * 委派人
     */
    private String delegate;

    /**
     * 流程状态
     */
    private String flowStatus;

    /**
     * 激活状态
     */
    private Integer activityStatus;

    public String getApprover() {
        return approver;
    }

    public FlowTaskVO setApprover(String approver) {
        this.approver = approver;
        return this;
    }

    public String getTransferredBy() {
        return transferredBy;
    }

    public FlowTaskVO setTransferredBy(String transferredBy) {
        this.transferredBy = transferredBy;
        return this;
    }

    public String getDelegate() {
        return delegate;
    }

    public FlowTaskVO setDelegate(String delegate) {
        this.delegate = delegate;
        return this;
    }

    public String getFlowStatus() {
        return flowStatus;
    }

    public FlowTaskVO setFlowStatus(String flowStatus) {
        this.flowStatus = flowStatus;
        return this;
    }

    public Integer getActivityStatus() {
        return activityStatus;
    }

    public FlowTaskVO setActivityStatus(Integer activityStatus) {
        this.activityStatus = activityStatus;
        return this;
    }
}
