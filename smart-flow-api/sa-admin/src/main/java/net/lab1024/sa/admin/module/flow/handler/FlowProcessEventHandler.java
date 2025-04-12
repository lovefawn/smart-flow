package net.lab1024.sa.admin.module.flow.handler;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.flow.annoation.ConditionalOnEnable;
import net.lab1024.sa.admin.module.flow.event.ProcessCreateTaskEvent;
import net.lab1024.sa.admin.module.flow.event.ProcessDeleteEvent;
import net.lab1024.sa.admin.module.flow.event.ProcessEvent;
import org.springframework.stereotype.Component;
import net.lab1024.sa.base.common.util.SpringUtils;
import java.util.Map;

/**
 * 流程监听服务
 *
 * @author may
 * @date 2024-06-02
 */
@ConditionalOnEnable
@Slf4j
@Component
public class FlowProcessEventHandler {

    /**
     * 总体流程监听(例如: 草稿，撤销，退回，作废，终止，已完成，单任务完成等)
     *
     * @param flowCode   流程定义编码
     * @param businessId 业务id
     * @param status     状态
     * @param submit     当为true时为申请人节点办理
     */
    public void processHandler(String flowCode, String businessId, String status, Map<String, Object> params, boolean submit) {
        log.info("发布流程事件，租户ID: {}, 流程状态: {}, 流程编码: {}, 业务ID: {}, 是否申请人节点办理: {}", status, flowCode, businessId, submit);
        ProcessEvent processEvent = new ProcessEvent();
        processEvent.setFlowCode(flowCode);
        processEvent.setBusinessId(businessId);
        processEvent.setStatus(status);
        processEvent.setParams(params);
        processEvent.setSubmit(submit);
        SpringUtils.getApplicationContext().publishEvent(processEvent);
    }

    /**
     * 执行创建任务监听
     *
     * @param flowCode   流程定义编码
     * @param nodeCode   审批节点编码
     * @param taskId     任务id
     * @param businessId 业务id
     */
    public void processCreateTaskHandler(String flowCode, String nodeCode, Long taskId, String businessId) {
        log.info("发布流程任务事件, 租户ID: {}, 流程编码: {}, 节点编码: {}, 任务ID: {}, 业务ID: {}",flowCode, nodeCode, taskId, businessId);
        ProcessCreateTaskEvent processCreateTaskEvent = new ProcessCreateTaskEvent();
        processCreateTaskEvent.setFlowCode(flowCode);
        processCreateTaskEvent.setNodeCode(nodeCode);
        processCreateTaskEvent.setTaskId(taskId);
        processCreateTaskEvent.setBusinessId(businessId);
        SpringUtils.getApplicationContext().publishEvent(processCreateTaskEvent);
    }

    /**
     * 删除流程监听
     *
     * @param flowCode    流程定义编码
     * @param businessId  业务ID
     */
    public void processDeleteHandler(String flowCode, String businessId) {
        log.info("发布删除流程事件, 租户ID: {}, 流程编码: {}, 业务ID: {}", flowCode, businessId);
        ProcessDeleteEvent processDeleteEvent = new ProcessDeleteEvent();
        processDeleteEvent.setFlowCode(flowCode);
        processDeleteEvent.setBusinessId(businessId);
        SpringUtils.getApplicationContext().publishEvent(processDeleteEvent);
    }

}
