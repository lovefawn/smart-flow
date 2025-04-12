package net.lab1024.sa.admin.module.flow.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import net.lab1024.sa.admin.module.flow.domain.vo.FlowTaskVO;
import net.lab1024.sa.admin.module.system.employee.domain.entity.EmployeeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dromara.warm.flow.core.entity.HisTask;
import org.dromara.warm.flow.core.entity.Task;
import org.dromara.warm.flow.orm.entity.FlowHisTask;
import org.dromara.warm.flow.orm.entity.FlowTask;

import java.util.List;

/**
 * warm-flow工作流Mapper接口
 *
 * @author ruoyi
 * @date 2024-03-07
 */
@Mapper
public interface WarmFlowMapper {
    /**
     * 分页查询待办任务
     *
     * @param task 条件实体
     */
    IPage<FlowTaskVO> toDoPage(IPage<FlowTaskVO> page, @Param("task") Task task);

    /**
     * 获取最新的已办任务
     *
     * @param hisTask
     * @return
     */
    IPage<FlowHisTask> donePage(IPage<FlowHisTask> page, @Param("hisTask") HisTask hisTask);

    /**
     * 分页获取抄送任务
     * @param flowTask
     * @return
     */
    IPage<FlowHisTask> copyPage(IPage<FlowHisTask> page, @Param("task") FlowTask flowTask);

    /**
     * 根据ID反显姓名
     *
     * @param ids 需要反显姓名的用户ID
     * @return {@link List<EmployeeEntity>}
     * @author liangli
     * @date 2024/8/21 17:11
     **/
    List<EmployeeEntity> idReverseDisplayName(@Param("ids") Long[] ids);
}
