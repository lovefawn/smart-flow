package net.lab1024.sa.admin.module.flow.handler;

import cn.hutool.core.util.ObjectUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.flow.annoation.ConditionalOnEnable;
import net.lab1024.sa.admin.module.flow.enums.TaskAssigneeEnum;
import net.lab1024.sa.admin.module.system.employee.domain.entity.EmployeeEntity;
import net.lab1024.sa.admin.module.system.employee.service.EmployeeService;
import net.lab1024.sa.admin.module.system.role.domain.form.RoleQueryForm;
import net.lab1024.sa.admin.module.system.role.domain.vo.RoleVO;
import net.lab1024.sa.admin.module.system.role.service.RoleEmployeeService;
import net.lab1024.sa.admin.module.system.role.service.RoleService;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.handler.PermissionHandler;
import org.dromara.warm.flow.core.service.impl.TaskServiceImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 办理人权限处理器
 *
 * @author AprilWind
 */
@ConditionalOnEnable
@RequiredArgsConstructor
@Component
@Slf4j
public class WorkflowPermissionHandler implements PermissionHandler {
    @Resource
    private EmployeeService userService;
    @Resource
    private RoleEmployeeService roleEmployeeService;
    /**
     * 审批前获取当前办理人，办理时会校验的该权限集合
     * 返回当前用户权限集合
     */
    @Override
    public List<String> permissions() {
        long userId =  SmartRequestUtil.getRequestUserId();
        EmployeeEntity loginUser = userService.getById(userId);
        if (ObjectUtil.isNull(loginUser)) {
            return new ArrayList<>();
        }
        List<RoleVO> roleList = roleEmployeeService.getRoleIdList(userId);

        // 使用一个流来构建权限列表
        return Stream.of(
                // 角色权限前缀
                        roleList.stream()
                    .map(role -> TaskAssigneeEnum.ROLE.getCode() + role.getRoleId()),

                // 岗位权限前缀
                Stream.ofNullable(loginUser.getDepartmentId())
                    .map(post -> TaskAssigneeEnum.POST.getCode() + post),

                // 用户和部门权限
                Stream.of(String.valueOf(loginUser.getEmployeeId()),
                    TaskAssigneeEnum.DEPT.getCode() + loginUser.getEmployeeId()
                )
            )
            .flatMap(stream -> stream)
            .collect(Collectors.toList());
    }

    /**
     * 获取当前办理人
     *
     * @return 当前办理人
     */
    @Override
    public String getHandler() {
        return String.valueOf(SmartRequestUtil.getRequestUserId());
    }

}
