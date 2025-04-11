package net.lab1024.sa.admin.module.flow.adapter;

import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.system.employee.domain.entity.EmployeeEntity;
import net.lab1024.sa.admin.module.system.role.domain.vo.RoleSelectedVO;
import net.lab1024.sa.admin.module.system.role.service.RoleEmployeeService;
import org.dromara.warm.flow.core.enums.CooperateType;
import org.dromara.warm.flow.core.service.TaskService;
import org.dromara.warm.flow.core.utils.StreamUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class AbstractWarmFlowAdapter {
    @Resource
    protected TaskService taskService;

    @Resource
    private RoleEmployeeService roleEmployeeService;

    /**
     * 获取权限
     *
     * @return 权限列表
     */
    protected List<String> permissionList(EmployeeEntity employee, String userId) {
        List<RoleSelectedVO> roles = roleEmployeeService.getRoleInfoListByEmployeeId(employee.getEmployeeId());
        List<String> permissionList = StreamUtils.toList(roles, role -> "role:" + role.getRoleId());
        permissionList.add(userId);
        permissionList.add("dept:" + employee.getEmployeeId());
        return permissionList;
    }

    /**
     * 根据类型获取描述
     *
     * @param type 流程类型
     * @return value
     */
    protected String type(Integer type) {
        return CooperateType.getValueByKey(type);
    }
}
