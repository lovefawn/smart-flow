package net.lab1024.sa.admin.module.flow.service.impl;

import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.flow.enums.TaskAssigneeEnum;
import net.lab1024.sa.admin.module.system.department.domain.form.DepartmentQueryForm;
import net.lab1024.sa.admin.module.system.department.domain.vo.DepartmentVO;
import net.lab1024.sa.admin.module.system.department.service.DepartmentService;
import net.lab1024.sa.admin.module.system.employee.domain.form.EmployeeQueryForm;
import net.lab1024.sa.admin.module.system.employee.domain.vo.EmployeeVO;
import net.lab1024.sa.admin.module.system.employee.service.EmployeeService;
import net.lab1024.sa.admin.module.system.role.domain.form.RoleQueryForm;
import net.lab1024.sa.admin.module.system.role.domain.vo.RoleVO;
import net.lab1024.sa.admin.module.system.role.service.RoleService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.util.SmartDateFormatterEnum;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import net.lab1024.sa.base.common.util.SmartStringUtil;
import org.dromara.warm.flow.core.utils.MathUtil;
import org.dromara.warm.flow.ui.dto.HandlerFunDto;
import org.dromara.warm.flow.ui.dto.HandlerQuery;
import org.dromara.warm.flow.ui.dto.TreeFunDto;
import org.dromara.warm.flow.ui.service.HandlerSelectService;
import org.dromara.warm.flow.ui.vo.HandlerSelectVo;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 流程设计器-获取办理人权限设置列表接口实现类
 *
 * @author warm
 */
@Component
public class HandlerSelectServiceImpl implements HandlerSelectService {
    @Resource
    private EmployeeService employeeService;

    @Resource
    private RoleService roleService;

    @Resource
    private DepartmentService departmentService;

    /**
     * 获取办理人权限设置列表tabs页签，如：用户、角色和部门等，可以返回其中一种或者多种，按业务需求决定
     *
     * @return tabs页签
     */
    @Override
    public List<String> getHandlerType() {
        return Arrays.asList("用户", "角色", "部门");
    }

    /**
     * 获取用户列表、角色列表、部门列表等，可以返回其中一种或者多种，按业务需求决定
     *
     * @param query 查询参数
     * @return 结果
     */
    @Override
    public HandlerSelectVo getHandlerSelect(HandlerQuery query) {

        if ("角色".equals(query.getHandlerType())) {
            return getRole(query);
        }

        if ("部门".equals(query.getHandlerType())) {
            return getDept(query);
        }

        if ("用户".equals(query.getHandlerType())) {
            return getUser(query);
        }

        return new HandlerSelectVo();
    }

    /**
     * 获取角色列表
     *
     * @param query 查询条件
     * @return HandlerSelectVo
     */
    private HandlerSelectVo getRole(HandlerQuery query) {
        RoleQueryForm queryForm = new RoleQueryForm();
        queryForm.setRoleCode(query.getHandlerCode());
        queryForm.setRoleName(query.getHandlerName());
        if(SmartStringUtil.isNotEmpty(query.getBeginTime()))
        queryForm.setStartTime(SmartLocalDateUtil.parseDate(query.getBeginTime(), SmartDateFormatterEnum.YMD));
        if(SmartStringUtil.isNotEmpty(query.getEndTime()))
        queryForm.setEndTime(SmartLocalDateUtil.parseDate(query.getEndTime(), SmartDateFormatterEnum.YMD));
        // 处理分页参数，避免空指针异常
        queryForm.setPageNum(query.getPageNum() != null ? (long)query.getPageNum() : 1L);
        queryForm.setPageSize(query.getPageSize() != null ? (long)query.getPageSize() : 10L);
        // 查询角色列表
        PageResult<RoleVO> pageResult = roleService.queryPage(queryForm);

        // 业务系统数据，转成组件内部能够显示的数据, total是业务数据总数，用于分页显示
        HandlerFunDto<RoleVO> handlerFunDto = new HandlerFunDto<>(pageResult.getList(), pageResult.getTotal())
                // 以下设置获取内置变量的Function
                .setStorageId(role -> TaskAssigneeEnum.ROLE.getCode() + role.getRoleId()) // 前面拼接role:  是为了防止用户、角色的主键重复
                .setHandlerCode(RoleVO::getRoleCode) // 权限编码
                .setHandlerName(RoleVO::getRoleName) // 权限名称
                .setCreateTime(role -> SmartLocalDateUtil.format(role.getCreateTime(), SmartDateFormatterEnum.YMD));

        return getHandlerSelectVo(handlerFunDto);
    }

    /**
     * 获取用户列表
     *
     * @param query 查询条件
     * @return HandlerSelectVo
     */
    private HandlerSelectVo getDept(HandlerQuery query) {
        DepartmentQueryForm queryForm = new DepartmentQueryForm();
        queryForm.setName(query.getHandlerName());
        if(SmartStringUtil.isNotEmpty(query.getBeginTime()))
        queryForm.setStartTime(SmartLocalDateUtil.parseDate(query.getBeginTime(), SmartDateFormatterEnum.YMD));
        if(SmartStringUtil.isNotEmpty(query.getEndTime()))
        queryForm.setEndTime(SmartLocalDateUtil.parseDate(query.getEndTime(), SmartDateFormatterEnum.YMD));
        // 处理分页参数，避免空指针异常
        queryForm.setPageNum(query.getPageNum() != null ? (long)query.getPageNum() : 1L);
        queryForm.setPageSize(query.getPageSize() != null ? (long)query.getPageSize() : 10L);
        // 查询部门列表
        PageResult<DepartmentVO> deptPage = departmentService.queryPage(queryForm);
        // 业务系统数据，转成组件内部能够显示的数据, total是业务数据总数，用于分页显示
        HandlerFunDto<DepartmentVO> handlerFunDto = new HandlerFunDto<>(deptPage.getList(), deptPage.getTotal())
                .setStorageId(dept ->TaskAssigneeEnum.DEPT.getCode() + dept.getDepartmentId()) // 前面拼接dept:  是为了防止用户、部门的主键重复
                .setHandlerName(DepartmentVO::getName) // 权限名称
                .setCreateTime(dept -> SmartLocalDateUtil.format(dept.getCreateTime(), SmartDateFormatterEnum.YMD));

        return getHandlerSelectVo(handlerFunDto);

    }

    /**
     * 获取用户列表, 同时构建左侧部门树状结构
     *
     * @param query 查询条件
     * @return HandlerSelectVo
     */
    private HandlerSelectVo getUser(HandlerQuery query) {
        EmployeeQueryForm employeeQueryForm = new EmployeeQueryForm();
        if (SmartStringUtil.isNotEmpty(query.getHandlerCode()))
            employeeQueryForm.setKeyword(query.getHandlerCode());
        else if (SmartStringUtil.isNotEmpty(query.getHandlerName()))
            employeeQueryForm.setKeyword(query.getHandlerName());
        // 办理人用户选择列表，需要展示左侧树状部门，所以可能会通过部门id
        if (MathUtil.isNumeric(query.getGroupId())) {
            employeeQueryForm.setDepartmentId(Long.valueOf(query.getGroupId()));
        }
        if(SmartStringUtil.isNotEmpty(query.getBeginTime()))
        employeeQueryForm.setStartTime(SmartLocalDateUtil.parseDate(query.getBeginTime(), SmartDateFormatterEnum.YMD));
        if(SmartStringUtil.isNotEmpty(query.getEndTime()))
        employeeQueryForm.setEndTime(SmartLocalDateUtil.parseDate(query.getEndTime(), SmartDateFormatterEnum.YMD));
        // 处理分页参数，避免空指针异常
        employeeQueryForm.setPageNum(query.getPageNum() != null ? (long)query.getPageNum() : 1L);
        employeeQueryForm.setPageSize(query.getPageSize() != null ? (long)query.getPageSize() : 10L);
        // 查询用户列表
        PageResult<EmployeeVO>  employeeVOPageResult = employeeService.queryEmployeePage(employeeQueryForm);

        // 查询部门列表，构建树状结构
        List<DepartmentVO> deptList = departmentService.listAll();

        // 业务系统数据，转成组件内部能够显示的数据, total是业务数据总数，用于分页显示
        HandlerFunDto<EmployeeVO> handlerFunDto = new HandlerFunDto<>(employeeVOPageResult.getList(), employeeVOPageResult.getTotal())
                .setStorageId(employee -> employee.getEmployeeId().toString())
                .setHandlerCode(EmployeeVO::getLoginName) // 权限编码
                .setHandlerName(EmployeeVO::getActualName) // 权限名称
                .setCreateTime(employee -> SmartLocalDateUtil.format(employee.getCreateTime(), SmartDateFormatterEnum.YMD_HMS))
                .setGroupName(employee -> employee.getDepartmentName() != null ? employee.getDepartmentName() : "");

        // 业务系统机构，转成组件内部左侧树列表能够显示的数据
        TreeFunDto<DepartmentVO> treeFunDto = new TreeFunDto<>(deptList)
                .setId(dept -> dept.getDepartmentId().toString()) // 左侧树ID
                .setName(DepartmentVO::getName) // 左侧树名称
                .setParentId(dept -> dept.getParentId().toString()); // 左侧树父级ID

        return getHandlerSelectVo(handlerFunDto, treeFunDto);
    }
}
