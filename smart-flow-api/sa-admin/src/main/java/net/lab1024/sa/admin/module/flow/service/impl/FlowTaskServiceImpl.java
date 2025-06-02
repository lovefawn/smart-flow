package net.lab1024.sa.admin.module.flow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import net.lab1024.sa.admin.module.flow.domain.form.ApproveTaskInsForm;
import net.lab1024.sa.admin.module.flow.domain.form.CompleteTaskForm;
import net.lab1024.sa.admin.module.flow.domain.form.StartProcessForm;
import net.lab1024.sa.admin.module.flow.domain.vo.FlowCopyVO;
import net.lab1024.sa.admin.module.flow.domain.vo.FlowTaskVO;
import net.lab1024.sa.admin.module.flow.domain.vo.StartProcessReturnVO;
import net.lab1024.sa.admin.module.flow.enums.BusinessStatusEnum;
import net.lab1024.sa.admin.module.flow.handler.FlowProcessEventHandler;
import net.lab1024.sa.admin.module.flow.mapper.WarmFlowMapper;
import net.lab1024.sa.admin.module.flow.service.FlowTaskService;
import net.lab1024.sa.admin.module.system.employee.domain.entity.EmployeeEntity;
import net.lab1024.sa.admin.module.system.employee.service.EmployeeService;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.*;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.entity.HisTask;
import org.dromara.warm.flow.core.entity.Instance;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.core.enums.SkipType;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.service.InsService;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.orm.entity.FlowHisTask;
import org.dromara.warm.flow.orm.entity.FlowInstance;
import org.dromara.warm.flow.orm.entity.FlowTask;
import org.dromara.warm.flow.orm.mapper.FlowHisTaskMapper;
import org.dromara.warm.flow.orm.mapper.FlowInstanceMapper;
import org.dromara.warm.flow.orm.mapper.FlowTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.lab1024.sa.admin.module.flow.contstant.FlowConstant.*;

/**
 * 流程执行SERVICEIMPL
 *
 * @author WARM
 * @since 2023/5/29 13:09
 */
@Service
@RequiredArgsConstructor
public class FlowTaskServiceImpl implements FlowTaskService {

    @Resource
    private WarmFlowMapper flowMapper;
    @Resource
    private TaskService taskService;
    @Resource
    private InsService insService;
    @Resource
    private DefService defService;
    @Resource
    private FlowInstanceMapper flowInstanceMapper;
    @Resource
    private FlowTaskMapper flowTaskMapper;
    @Resource
    private FlowProcessEventHandler flowProcessEventHandler;
    @Resource
    private EmployeeService employeeService;
    @Autowired
    private FlowHisTaskMapper flowHisTaskMapper;

    @Override
    public IPage<FlowTaskVO> toDoPage(IPage<FlowTaskVO> page, Task task) {
        return flowMapper.toDoPage(page,task);
    }

    @Override
    public IPage<FlowHisTask> donePage(IPage<FlowHisTask> page, HisTask hisTask) {
        return flowMapper.donePage(page,hisTask);
    }

    @Override
    public IPage<FlowHisTask> copyPage(IPage<FlowHisTask> page, FlowTask flowTask) {
        return flowMapper.copyPage(page,flowTask);
    }

    /**
     * 根据ID反显姓名
     *
     * @param ids 需要反显姓名的用户ID
     * @return {@link List<EmployeeEntity>}
     * @author liangli
     * @date 2024/8/21 17:11
     **/
    @Override
    public List<EmployeeEntity> idReverseDisplayName(Long[] ids) {
        if (Objects.isNull(ids) || ids.length == 0) {
            return null;
        }
        return flowMapper.idReverseDisplayName(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StartProcessReturnVO startWorkFlow(StartProcessForm startProcessForm) {
        String businessId = startProcessForm.getBusinessId();
        if (SmartStringUtil.isEmpty(businessId)) {
            throw new BusinessException("启动工作流时必须包含业务ID");
        }
        // 启动流程实例（提交申请）
        Map<String, Object> variables = startProcessForm.getVariables();
        // 流程发起人
        long userId =  SmartRequestUtil.getRequestUserId();
        variables.put(INITIATOR, String.valueOf(userId));
        // 业务id
        variables.put(BUSINESS_ID, businessId);
        variables.put(BUSINESS_TABLE,startProcessForm.getFlowCode());
        FlowInstance flowInstance = flowInstanceMapper.selectOne(new LambdaQueryWrapper<>(FlowInstance.class)
                .eq(FlowInstance::getBusinessId, businessId));
        if (ObjectUtil.isNotNull(flowInstance)) {
            BusinessStatusEnum.checkStartStatus(flowInstance.getFlowStatus());
            List<Task> taskList = taskService.list(new FlowTask().setInstanceId(flowInstance.getId()));
            taskService.mergeVariable(flowInstance, variables);
            insService.updateById(flowInstance);
            StartProcessReturnVO vo = new StartProcessReturnVO();
            vo.setProcessInstanceId(taskList.get(0).getInstanceId());
            vo.setTaskId(taskList.get(0).getId());
            return vo;
        }
        FlowParams flowParams = new FlowParams();
        flowParams.flowCode(startProcessForm.getFlowCode());
        flowParams.variable(startProcessForm.getVariables());
//        flowParams.flowStatus(String.valueOf(FlowStatusEnum.PENDING_SUBMIT.getValue()));
        Instance instance;
        try {
            instance = insService.start(businessId, flowParams);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
        //更新business表中状态
        String name = flowParams.getFlowCode();
        BaseMapper bizMapper = SpringUtils.getBean(flowParams.getFlowCode()+"Dao");
        if (ObjectUtil.isNotNull(bizMapper)) {
            UpdateWrapper<Object> updateWrapper = new UpdateWrapper<>();
            // 动态设置更新条件（假设字段名与实体属性对应）
            updateWrapper.eq(name.toLowerCase() +"_id", instance.getBusinessId())  // 主键条件
                    .set("instance_id", instance.getId())
                    .set("node_name",instance.getNodeName())// 更新字段
                    .set("flow_status", instance.getFlowStatus());
            bizMapper.update(null, updateWrapper);
        }
        // 申请人执行流程
        List<Task> taskList = taskService.list(new FlowTask().setInstanceId(instance.getId()));
        if (taskList.size() > 1) {
            throw new BusinessException("请检查流程第一个环节是否为申请人！");
        }
        //自动提交申请人流程
        ApproveTaskInsForm approveTaskInsForm = new ApproveTaskInsForm();
        approveTaskInsForm.setInstanceId(instance.getId());
        approveTaskInsForm.setVariables(variables);
        approveTaskByInstance( approveTaskInsForm);
        //返回执行结果
        StartProcessReturnVO vo = new StartProcessReturnVO();
        vo.setProcessInstanceId(instance.getId());
        vo.setTaskId(taskList.get(0).getId());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeTask(CompleteTaskForm completeTaskForm) {
        try {
            // 获取任务ID并查询对应的流程任务和实例信息
            Long taskId = completeTaskForm.getTaskId();
            List<String> messageType = completeTaskForm.getMessageType();
            String notice = completeTaskForm.getNotice();
            // 获取抄送人
            List<FlowCopyVO> flowCopyList = completeTaskForm.getFlowCopyList();
            FlowTask flowTask = flowTaskMapper.selectById(taskId);
            if (ObjectUtil.isNull(flowTask)) {
                throw new BusinessException("流程任务不存在或任务已审批！");
            }
            Instance ins = insService.getById(flowTask.getInstanceId());
            // 获取流程定义信息
            Definition definition = defService.getById(flowTask.getDefinitionId());
            // 检查流程状态是否为草稿、已撤销或已退回状态，若是则执行流程提交监听
            if (BusinessStatusEnum.isDraftOrCancelOrBack(ins.getFlowStatus())) {
                flowProcessEventHandler.processHandler(definition.getFlowCode(), ins.getBusinessId(), ins.getFlowStatus(), null, true);
            }
            // 构建流程参数，包括变量、跳转类型、消息、处理人、权限等信息
            FlowParams flowParams = new FlowParams();
            flowParams.skipType(SkipType.PASS.getKey());
            flowParams.message(completeTaskForm.getMessage());
            flowParams.hisTaskExt(completeTaskForm.getFileId());
            Map<String, Object> variables = completeTaskForm.getVariables();
            flowParams.variable(variables);
            flowParams.flowCode(variables.get("flowCode").toString());
            // 执行任务跳转，并根据返回的处理人设置下一步处理人
            Instance instance = taskService.skip(taskId, flowParams);
//            this.setHandler(instance, flowTask, flowCopyList);

            //设置下一环节处理人
//            setNextHandler(ins.getId());
            //更新business表中状态
            String name = flowParams.getFlowCode();
            BaseMapper bizMapper = SpringUtils.getBean(flowParams.getFlowCode()+"Dao");
            if (ObjectUtil.isNotNull(bizMapper)) {
                UpdateWrapper<Object> updateWrapper = new UpdateWrapper<>();
                // 动态设置更新条件（假设字段名与实体属性对应）
                updateWrapper.eq(name.toLowerCase() +"_id", instance.getBusinessId())  // 主键条件
                        .set("node_name",instance.getNodeName())// 更新字段
                        .set("flow_status", instance.getFlowStatus());
                bizMapper.update(null, updateWrapper);
            }
            return true;
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }
    /**
     * 按照实例id审批
     *
     * @param approveTaskInsForm 流程审批实例审批
     */
    @Override
    public boolean approveTaskByInstance(ApproveTaskInsForm approveTaskInsForm){
        List<FlowTask> flowTaskList =selectByInstId(approveTaskInsForm.getInstanceId());
        CompleteTaskForm completeTaskForm = new CompleteTaskForm();
        SmartBeanUtil.copyProperties(approveTaskInsForm,completeTaskForm);

        if(flowTaskList.size()>0){
            completeTaskForm.setTaskId(flowTaskList.get(0).getId());
        }
        return completeTask(completeTaskForm);
    }

    /**
     * 按照实例id查询任务
     *
     * @param instanceId 流程实例id
     */
    @Override
    public List<FlowTask> selectByInstId(Long instanceId) {
        return flowTaskMapper.selectList(new LambdaQueryWrapper<>(FlowTask.class)
                .eq(FlowTask::getInstanceId, instanceId));
    }
    /**
     * 按照任务id查询任务详情
     *
     * @param taskId 流程实例id
     */
    @Override
    public FlowTaskVO getTaskDetailById(Long taskId){
        FlowTask flowTask = flowTaskMapper.selectById(taskId);
        Definition definition = defService.getById(flowTask.getDefinitionId());
        Instance flowInstance = insService.getById(flowTask.getInstanceId());
        Map<String, Object>  variable = flowInstance.getVariableMap();
        String flowCode = variable.get("flowCode").toString();
        String initiatorId = variable.get("initiator").toString();
        String initiator = employeeService.getById(Long.parseLong(initiatorId)).getActualName();

        BaseMapper bizMapper = SpringUtils.getBean(flowCode+"Dao");
        Map<String,Object> businessForm = new HashMap<>();
        if (ObjectUtil.isNotNull(bizMapper)) {
            Object businessEntity = bizMapper.selectById(flowInstance.getBusinessId());
            if (ObjectUtil.isNotNull(businessEntity)) {
                // 将业务实体转换为Map
                businessForm = BeanUtil.beanToMap(businessEntity);
            }
        }
        // 暂时不获取表元数据，避免复杂的依赖问题
        JSONObject businessFormMetaData = new JSONObject();
         try {
             String tableName = getTableNameByFlowCode(flowCode);
             if (tableName != null) {
                 businessFormMetaData = SmartTableMetadataUtil.getTableMetadata(tableName);
             }
         } catch (Exception e) {
             System.err.println("获取表元数据失败: " + e.getMessage());
         }

        FlowTaskVO flowTaskVO = new FlowTaskVO();
         SmartBeanUtil.copyProperties(flowTask,flowTaskVO);
         flowTaskVO.setFlowCode(definition.getFlowCode());
         flowTaskVO.setFlowName(definition.getFlowName());
         flowTaskVO.setBusinessId(flowInstance.getBusinessId());
         flowTaskVO.setInitiator(initiator);
         flowTaskVO.setBusinessFormMetaData(businessFormMetaData);
         flowTaskVO.setBusinessForm( businessForm);

          return flowTaskVO;
    }

    /**
     * 按照任务id查询任务详情
     *
     * @param taskId 流程实例id
     */
    @Override
    public FlowTaskVO getDoneTaskDetailById(Long taskId){
        FlowHisTask flowHisTask = flowHisTaskMapper.selectById(taskId);
        Definition definition = defService.getById(flowHisTask.getDefinitionId());
        Instance flowInstance = insService.getById(flowHisTask.getInstanceId());
        Map<String, Object>  variable = flowInstance.getVariableMap();
        String flowCode = variable.get("flowCode").toString();
        String initiatorId = variable.get("initiator").toString();
        String initiator = employeeService.getById(Long.parseLong(initiatorId)).getActualName();

        BaseMapper bizMapper = SpringUtils.getBean(flowCode+"Dao");
        Map<String,Object> businessForm = new HashMap<>();
        if (ObjectUtil.isNotNull(bizMapper)) {
            Object businessEntity = bizMapper.selectById(flowInstance.getBusinessId());
            if (ObjectUtil.isNotNull(businessEntity)) {
                // 将业务实体转换为Map
                businessForm = BeanUtil.beanToMap(businessEntity);
            }
        }
        // 暂时不获取表元数据，避免复杂的依赖问题
        JSONObject businessFormMetaData = new JSONObject();
        try {
            String tableName = getTableNameByFlowCode(flowCode);
            if (tableName != null) {
                businessFormMetaData = SmartTableMetadataUtil.getTableMetadata(tableName);
            }
        } catch (Exception e) {
            System.err.println("获取表元数据失败: " + e.getMessage());
        }

        FlowTaskVO flowTaskVO = new FlowTaskVO();
        SmartBeanUtil.copyProperties(flowHisTask,flowTaskVO);
        flowTaskVO.setFlowName(definition.getFlowName());
        flowTaskVO.setBusinessId(flowInstance.getBusinessId());
        flowTaskVO.setInitiator(initiator);
        flowTaskVO.setBusinessFormMetaData(businessFormMetaData);
        flowTaskVO.setBusinessForm( businessForm);

        return flowTaskVO;
    }


    /**
     * 根据flowCode映射到对应的表名
     * @param flowCode 流程代码
     * @return 表名
     */
    private String getTableNameByFlowCode(String flowCode) {
        return "t_"+flowCode;

    }

}
