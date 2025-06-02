package net.lab1024.sa.admin.module.flow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import net.lab1024.sa.admin.module.flow.domain.form.ApproveTaskInsForm;
import net.lab1024.sa.admin.module.flow.domain.form.CompleteTaskForm;
import net.lab1024.sa.admin.module.flow.domain.form.StartProcessForm;
import net.lab1024.sa.admin.module.flow.domain.vo.FlowTaskVO;
import net.lab1024.sa.admin.module.flow.domain.vo.StartProcessReturnVO;
import net.lab1024.sa.admin.module.system.employee.domain.entity.EmployeeEntity;
import org.dromara.warm.flow.core.entity.HisTask;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.orm.entity.FlowHisTask;
import org.dromara.warm.flow.orm.entity.FlowTask;

import java.util.List;

/**
 * 流程执行service
 *
 * @author warm
 * @since 2023/5/29 13:09
 */
public interface FlowTaskService {

    /**
     * 分页查询待办任务
     *
     * @param task 条件实体
     * @return
     */
    IPage<FlowTaskVO> toDoPage(IPage<FlowTaskVO> page, Task task);

    /**
     * 获取已办任务
     *
     * @param hisTask
     * @return
     */
    IPage<FlowHisTask> donePage(IPage<FlowHisTask> page, HisTask hisTask);

    IPage<FlowHisTask> copyPage(IPage<FlowHisTask> page, FlowTask flowTask);

    /**
     * 根据ID反显姓名
     *
     * @param ids 需要反显姓名的用户ID
     * @return {@link List<EmployeeEntity>}
     * @author liangli
     * @date 2024/8/21 17:11
     **/
    List<EmployeeEntity> idReverseDisplayName(Long[] ids);

    StartProcessReturnVO startWorkFlow(StartProcessForm startProcessForm);

    boolean completeTask(CompleteTaskForm completeTaskForm);

    List<FlowTask> selectByInstId(Long instanceId);

    boolean approveTaskByInstance(ApproveTaskInsForm approveTaskInsForm);


    FlowTaskVO getTaskDetailById(Long taskId);

    FlowTaskVO getDoneTaskDetailById(Long taskId);
}
