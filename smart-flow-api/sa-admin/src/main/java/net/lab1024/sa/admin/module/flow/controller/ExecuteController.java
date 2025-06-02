package net.lab1024.sa.admin.module.flow.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.constant.AdminSwaggerTagConst;
import net.lab1024.sa.admin.module.flow.domain.form.ApproveTaskInsForm;
import net.lab1024.sa.admin.module.flow.domain.form.CompleteTaskForm;
import net.lab1024.sa.admin.module.flow.domain.form.StartProcessForm;
import net.lab1024.sa.admin.module.flow.domain.vo.FlowTaskVO;
import net.lab1024.sa.admin.module.flow.domain.vo.StartProcessReturnVO;
import net.lab1024.sa.admin.module.flow.domain.vo.WarmFlowInteractiveTypeVO;
import net.lab1024.sa.admin.module.flow.service.FlowTaskService;
import net.lab1024.sa.admin.module.flow.service.HhDefService;
import net.lab1024.sa.admin.module.system.department.domain.vo.DepartmentVO;
import net.lab1024.sa.admin.module.system.department.service.DepartmentService;
import net.lab1024.sa.admin.module.system.employee.domain.entity.EmployeeEntity;
import net.lab1024.sa.admin.module.system.employee.domain.vo.EmployeeVO;
import net.lab1024.sa.admin.module.system.employee.service.EmployeeService;
import net.lab1024.sa.admin.module.system.role.domain.vo.RoleSelectedVO;
import net.lab1024.sa.admin.module.system.role.domain.vo.RoleVO;
import net.lab1024.sa.admin.module.system.role.service.RoleEmployeeService;
import net.lab1024.sa.admin.module.system.role.service.RoleService;
import net.lab1024.sa.base.common.code.UnexpectedErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import net.lab1024.sa.base.common.util.SmartStringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.FlowEngine;
import org.dromara.warm.flow.core.entity.*;
import org.dromara.warm.flow.core.enums.CooperateType;
import org.dromara.warm.flow.core.enums.NodeType;
import org.dromara.warm.flow.core.enums.UserType;
import org.dromara.warm.flow.core.service.*;
import org.dromara.warm.flow.core.utils.StreamUtils;
import org.dromara.warm.flow.orm.entity.FlowHisTask;
import org.dromara.warm.flow.orm.entity.FlowTask;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**load
 * 流程实例Controller
 *
 * @author hh
 * @date 2023-04-18
 */
@Validated
@RestController
@Tag(name = AdminSwaggerTagConst.Flow.FLOW_INSTANCE)
@RequestMapping("/flow/execute")
public class ExecuteController {
    @Resource
    private EmployeeService employeeService;

    @Resource
    private DepartmentService deptService;

    @Resource
    private RoleService roleService;

    @Resource
    private HisTaskService hisTaskService;

    @Resource
    private TaskService taskService;

    @Resource
    private NodeService nodeService;

    @Resource
    private InsService insService;

    @Resource
    private UserService flowUserservice;

    @Resource
    private FlowTaskService flowTaskService;

    @Resource
    private HhDefService hhDefService;

    @Resource
    private RoleEmployeeService roleEmployeeService;



    /**
     * 启动任务
     *
     * @param startProcessForm 启动流程参数
     */
    @SaCheckPermission("flow:execute:startTask")
    @PostMapping("/startWorkFlow")
    public ResponseDTO<StartProcessReturnVO> startWorkFlow(@Validated() @RequestBody StartProcessForm startProcessForm) {
        StartProcessReturnVO startProcessReturn = flowTaskService.startWorkFlow(startProcessForm);
        return ResponseDTO.ok( startProcessReturn);
    }
    /**
     * 审批任务
     *
     * @param completeTaskForm 办理任务参数
     */
    @SaCheckPermission("flow:execute:approve")
    @PostMapping("/completeTask")
    public ResponseDTO<Void> completeTask(@RequestBody CompleteTaskForm completeTaskForm) {
        boolean result = flowTaskService.completeTask(completeTaskForm);
        if(result)
            return ResponseDTO.ok();
        else
            return ResponseDTO.error(UnexpectedErrorCode.BUSINESS_HANDING, "流程办理失败");
    }

    /**
     *审批任务
     *
     * @param approveTaskInsForm 办理任务参数
     */
    @SaCheckPermission("flow:execute:approve")
    @PostMapping("/approveTaskByInstance")
    public ResponseDTO<Void> approveTaskByInstance(@RequestBody ApproveTaskInsForm approveTaskInsForm) {
        boolean result = flowTaskService.approveTaskByInstance(approveTaskInsForm);
        if(result)
            return ResponseDTO.ok();
        else
            return ResponseDTO.error(UnexpectedErrorCode.BUSINESS_HANDING, "流程办理失败");
    }


    /**
     * 分页待办任务列表
     */
    @GetMapping("/toDoPage")
    public ResponseDTO<PageResult<FlowTaskVO>> toDoPage(FlowTask flowTask, @RequestParam(defaultValue = "1") int pageNum,
                                                        @RequestParam(defaultValue = "10") int pageSize) {
        long userId =  SmartRequestUtil.getRequestUserId();
        EmployeeEntity employee = employeeService.getById(userId);
        List<String> permissionList = permissionList(String.valueOf(employee.getEmployeeId()), employee.getDepartmentId(), employee);
        flowTask.setPermissionList(permissionList);
        // 使用 PageHelper 分页
        IPage<FlowTaskVO> pageParam = new Page<>(pageNum, pageSize);
        IPage<FlowTaskVO> page = flowTaskService.toDoPage(pageParam, flowTask);
        List<FlowTaskVO> list = page.getRecords();
        List<Long> taskIds = StreamUtils.toList(list, FlowTaskVO::getId);
        List<User> userList = flowUserservice.getByAssociateds(taskIds);
        Map<Long, List<User>> map = StreamUtils.groupByKey(userList, User::getAssociated);
        for (FlowTaskVO taskVo : list) {
            if (SmartStringUtil.isNotNull(taskVo)) {
                List<User> users = map.get(taskVo.getId());
                if (CollectionUtils.isNotEmpty(users)) {
                    for (User user : users) {
                        if (UserType.APPROVAL.getKey().equals(user.getType())) {
                            if (SmartStringUtil.isEmpty(taskVo.getApprover())) {
                                taskVo.setApprover("");
                            }
                            String name = getName(user.getProcessedBy());
                            if (SmartStringUtil.isNotEmpty(name)) taskVo.setApprover(taskVo.getApprover().concat(name).concat(";"));
                        } else if (UserType.TRANSFER.getKey().equals(user.getType())) {
                            if (SmartStringUtil.isEmpty(taskVo.getTransferredBy())) {
                                taskVo.setTransferredBy("");
                            }
                            String name = getName(user.getProcessedBy());
                            if (SmartStringUtil.isNotEmpty(name)) taskVo.setTransferredBy(taskVo.getTransferredBy().concat(name).concat(";"));
                        } else if (UserType.DEPUTE.getKey().equals(user.getType())) {
                            if (SmartStringUtil.isEmpty(taskVo.getDelegate())) {
                                taskVo.setDelegate("");
                            }
                            String name = getName(user.getProcessedBy());
                            if (SmartStringUtil.isNotEmpty(name)) taskVo.setDelegate(taskVo.getDelegate().concat(name).concat(";"));
                        }
                    }
                }
            }
        }
        // 转换为自定义分页响应对象
        PageResult<FlowTaskVO> pageResult = new PageResult<>();
        pageResult.setList(page.getRecords());
        pageResult.setTotal(page.getTotal());
        pageResult.setPageNum( page.getCurrent());
        pageResult.setPageSize( page.getSize());
        pageResult.setPages( page.getPages());
        return ResponseDTO.ok(pageResult);
    }

    /**
     * 分页抄送任务列表
     * author：暗影
     */
    @GetMapping("/copyPage")
    public ResponseDTO<PageResult<FlowHisTask>> copyPage(FlowTask flowTask, @RequestParam(defaultValue = "1") int pageNum,
                                                        @RequestParam(defaultValue = "10") int pageSize) {
        long userId =  SmartRequestUtil.getRequestUserId();
        EmployeeEntity employee = employeeService.getById(userId);
        List<String> permissionList = permissionList(String.valueOf(employee.getEmployeeId()), employee.getDepartmentId(), employee);
        flowTask.setPermissionList(permissionList);
        // 使用 PageHelper 分页
        IPage<FlowHisTask> pageParam = new Page<>(pageNum, pageSize);
        IPage<FlowHisTask> page = flowTaskService.copyPage(pageParam, flowTask);

        // 转换为自定义分页响应对象
        PageResult<FlowHisTask> pageResult = new PageResult<>();
        pageResult.setList(page.getRecords());
        pageResult.setTotal(page.getTotal());
        pageResult.setPageNum( page.getCurrent());
        pageResult.setPageSize( page.getSize());
        pageResult.setPages( page.getPages());
        return ResponseDTO.ok(pageResult);
    }
    /**
     * 分页已办任务列表
     */
    @GetMapping("/donePage")
    public ResponseDTO<PageResult<FlowHisTask>> donePage(FlowHisTask flowHisTask, @RequestParam(defaultValue = "1") int pageNum,
                                                         @RequestParam(defaultValue = "10") int pageSize) {

        long userId =  SmartRequestUtil.getRequestUserId();
        EmployeeEntity employee = employeeService.getById(userId);
        List<String> permissionList = permissionList(String.valueOf(employee.getEmployeeId()), employee.getDepartmentId(), employee);
        flowHisTask.setPermissionList(permissionList);
        // 使用 PageHelper 分页
        IPage<FlowHisTask> pageParam = new Page<>(pageNum, pageSize);
        IPage<FlowHisTask> page = flowTaskService.donePage(pageParam,flowHisTask);
        List<FlowHisTask> list = page.getRecords();
        Map<Long, String> userMap = StreamUtils.toMap(employeeService.queryAllEmployee(false).getData()
                , EmployeeVO::getEmployeeId, EmployeeVO::getActualName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (FlowHisTask hisTask : list) {
                if (SmartStringUtil.isNotEmpty(hisTask.getApprover())) {
                    String name = getName(hisTask.getApprover());
                    hisTask.setApprover(name);
                }
                if (SmartStringUtil.isNotEmpty(hisTask.getCollaborator())) {
                    String[] split = hisTask.getCollaborator().split(",");
                    if (split.length > 1) {
                        List<String> names = new ArrayList<>();
                        for (String s : split) {
                            names.add(userMap.get(Long.valueOf(s)));
                        }
                        hisTask.setCollaborator(StringUtils.join(names, ","));
                    }
                }
            }
        }
        // 转换为自定义分页响应对象
        PageResult<FlowHisTask> pageResult = new PageResult<>();
        pageResult.setList(page.getRecords());
        pageResult.setTotal(page.getTotal());
        pageResult.setPageNum( page.getCurrent());
        pageResult.setPageSize( page.getSize());
        pageResult.setPages( page.getPages());
        return ResponseDTO.ok(pageResult);
    }

    private String getName(String id) {
        Map<Long, String> userMap = StreamUtils.toMap(employeeService.queryAllEmployee(false).getData()
                , EmployeeVO::getEmployeeId, EmployeeVO::getActualName);
        Map<Long, String> deptMap = StreamUtils.toMap(deptService.listAll()
                , DepartmentVO::getDepartmentId, DepartmentVO::getName);
        Map<Long, String> roleMap = StreamUtils.toMap(roleService.getAllRole().getData()
                , RoleVO::getRoleId, RoleVO::getRoleName);
        if (SmartStringUtil.isNotNull(id)) {
            if (id.contains("user:")) {
                String name = userMap.get(Long.valueOf(id.replace("user:", "")));
                if (SmartStringUtil.isNotEmpty(name)) {
                    return "用户:" + name;
                }
            } else if (id.contains("dept:")) {
                String name = deptMap.get(Long.valueOf(id.replace("dept:", "")));
                if (SmartStringUtil.isNotEmpty(name)) {
                    return "部门:" + name;
                }
            } else if (id.contains("role")) {
                String name = roleMap.get(Long.valueOf(id.replace("role:", "")));
                if (SmartStringUtil.isNotEmpty(name)) {
                    return "角色:" + name;
                }
            } else {
                try {
                    long parseLong = Long.parseLong(id);
                    String name = userMap.get(parseLong);
                    if (SmartStringUtil.isNotEmpty(name)) {
                        return "用户:" + name;
                    }
                } catch (NumberFormatException e) {
                    return id;
                }

            }
        }
        return "";
    }


    /**
     * 查询已办任务历史记录
     */
    @GetMapping("/doneList/{instanceId}")
    public ResponseDTO<List<FlowHisTask>> doneList(@PathVariable("instanceId") Long instanceId) {
        List<HisTask> flowHisTasks = hisTaskService.orderById().desc().list(new FlowHisTask().setInstanceId(instanceId));
        Map<Long, String> userMap = StreamUtils.toMap(employeeService.queryAllEmployee(false).getData()
                , EmployeeVO::getEmployeeId, EmployeeVO::getActualName);
        List<FlowHisTask> flowHisTaskList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(flowHisTasks)) {
            for (HisTask hisTask : flowHisTasks) {
                if (SmartStringUtil.isNotEmpty(hisTask.getApprover())) {
                    String name = getName(hisTask.getApprover());
                    hisTask.setApprover(name);
                }
                if (SmartStringUtil.isNotEmpty(hisTask.getCollaborator())) {
                    String[] split = hisTask.getCollaborator().split(",");
                    if (split.length > 1) {
                        List<String> names = new ArrayList<>();
                        for (String s : split) {
                            names.add(userMap.get(Long.valueOf(s)));
                        }
                        hisTask.setCollaborator(StringUtils.join(names, ","));
                    }
                }
                FlowHisTask flowHisTask = new FlowHisTask();
                BeanUtils.copyProperties(hisTask, flowHisTask);
                flowHisTaskList.add(flowHisTask);
            }
        }
        return ResponseDTO.ok(flowHisTaskList);
    }

    /**
     * 根据taskId查询代表任务
     *
     * @param taskId
     * @return
     */
    @GetMapping("/getTaskById/{taskId}")
    public ResponseDTO<Task> getTaskById(@PathVariable("taskId") Long taskId) {
        return ResponseDTO.ok(taskService.getById(taskId));
    }

    /**
     * 根据taskId查询代表任务
     *
     * @param taskId
     * @return
     */
    @GetMapping("/getTaskDetail/{taskId}")
    public ResponseDTO<FlowTaskVO> getTaskDetailById(@PathVariable("taskId") Long taskId) {
        return ResponseDTO.ok(flowTaskService.getTaskDetailById(taskId));
    }

    /**
     * 根据taskId查询代表任务
     *
     * @param taskId
     * @return
     */
    @GetMapping("/getDoneTaskDetail/{taskId}")
    public ResponseDTO<FlowTaskVO> getDoneTaskDetail(@PathVariable("taskId") Long taskId) {
        return ResponseDTO.ok(flowTaskService.getDoneTaskDetailById(taskId));
    }

    /**
     * 查询跳转任意节点列表
     */
    @SaCheckPermission("flow:execute:goAnyNode")
    @GetMapping("/anyNodeList/{instanceId}")
    public ResponseDTO<List<Node>> anyNodeList(@PathVariable("instanceId") Long instanceId) {
        Instance instance = insService.getById(instanceId);
        List<Node> nodeList = nodeService.list(FlowEngine.newNode().setDefinitionId(instance.getDefinitionId()));
        nodeList.removeIf(node -> NodeType.isGateWay(node.getNodeType()));
        return ResponseDTO.ok(nodeList);
    }

    /**
     * 处理非办理的流程交互类型
     *
     * @param WarmFlowInteractiveTypeVO 要转办用户
     * @return 是否成功
     */
    @PostMapping("/interactiveType")
    public ResponseDTO<Boolean> interactiveType(WarmFlowInteractiveTypeVO WarmFlowInteractiveTypeVO) {
        return ResponseDTO.ok(hhDefService.interactiveType(WarmFlowInteractiveTypeVO));
    }

    /**
     * 交互类型可以选择的用户
     *
     * @param warmFlowInteractiveTypeVO 交互类型请求类
     * @return 是否成功
     */
    @GetMapping("/interactiveTypeEmployeeEntity")
    public  ResponseDTO<PageResult<EmployeeEntity>> interactiveTypeEmployeeEntity(WarmFlowInteractiveTypeVO warmFlowInteractiveTypeVO, @RequestParam(defaultValue = "1") int pageNum,
                                                                                  @RequestParam(defaultValue = "10") int pageSize) {

        long userId =  SmartRequestUtil.getRequestUserId();
        Integer operatorType = warmFlowInteractiveTypeVO.getOperatorType();
        // 构建分页对象
        Page<EmployeeEntity> pageParam = new Page<>(pageNum, pageSize);
        // 根据操作类型查询分页数据
        Page<EmployeeEntity> page;

        Long taskId = warmFlowInteractiveTypeVO.getTaskId();
        List<User> users = flowUserservice.listByAssociatedAndTypes(taskId);
        if (!Objects.equals(CooperateType.REDUCTION_SIGNATURE.getKey(), operatorType)) {
            List<String> userIds = StreamUtils.toList(users, User::getProcessedBy);
            warmFlowInteractiveTypeVO.setUserIds(userIds);
            page = employeeService.selectNotUserList(pageParam,warmFlowInteractiveTypeVO);
        } else {
            List<String> userIds = StreamUtils.toList(users, User::getProcessedBy);
            warmFlowInteractiveTypeVO.setUserIds(userIds);
            page = employeeService.selectUserList(pageParam,warmFlowInteractiveTypeVO);
            // 过滤当前用户（内存过滤，适用于小数据量）
            List<EmployeeEntity> filteredList = page.getRecords().stream()
                    .filter(employee -> !Objects.equals(employee.getEmployeeId(), userId))
                    .collect(Collectors.toList());
            page.setRecords(filteredList);}
        // 转换为自定义分页响应对象
        PageResult<EmployeeEntity> pageResult = new PageResult<>();
        pageResult.setList(page.getRecords());
        pageResult.setTotal(page.getTotal());
        pageResult.setPageNum( page.getCurrent());
        pageResult.setPageSize( page.getSize());
        pageResult.setPages( page.getPages());
        return ResponseDTO.ok(pageResult);
    }

    /**
     * 激活流程
     *
     * @param instanceId
     * @return
     */
    @GetMapping("/active/{instanceId}")
    public ResponseDTO<Boolean> active(@PathVariable("instanceId") Long instanceId) {
        return ResponseDTO.ok(insService.active(instanceId));
    }

    /**
     * 挂起流程
     *
     * @param instanceId
     * @return
     */
    @GetMapping("/unActive/{instanceId}")
    public ResponseDTO<Boolean> unActive(@PathVariable("instanceId") Long instanceId) {
        return ResponseDTO.ok(insService.unActive(instanceId));
    }

    /**
     * 获取权限
     *
     * @param userId  用户编号
     * @param deptId  部门编号
     * @param employee 登陆用户
     * @return 权限列表
     */
    private List<String> permissionList(String userId, Long deptId, EmployeeEntity employee) {
        List<RoleSelectedVO> roles = roleEmployeeService.getRoleInfoListByEmployeeId(employee.getEmployeeId());
        List<String> permissionList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(roles)) {
            permissionList = StreamUtils.toList(roles, role -> "role:" + role.getRoleId());
        }
        permissionList.add(userId);
        if (Objects.nonNull(deptId)) {
            permissionList.add("dept:" + deptId);
        }
        return permissionList;
    }

    /**
     * 根据ID反显姓名
     *
     * @param ids 需要反显姓名的用户ID
     * @return {@link ResponseDTO< List<EmployeeEntity>>}
     * @author liangli
     * @date 2024/8/21 17:08
     **/
    @GetMapping(value = "/idReverseDisplayName/{ids}")
    public ResponseDTO<List<EmployeeEntity>> idReverseDisplayName(@PathVariable Long[] ids) {
        return ResponseDTO.ok(flowTaskService.idReverseDisplayName(ids));
    }

}
