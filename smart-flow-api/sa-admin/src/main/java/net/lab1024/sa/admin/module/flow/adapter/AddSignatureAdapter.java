package net.lab1024.sa.admin.module.flow.adapter;

import net.lab1024.sa.admin.module.flow.domain.vo.WarmFlowInteractiveTypeVO;
import net.lab1024.sa.admin.module.system.employee.domain.entity.EmployeeEntity;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.dromara.warm.flow.core.dto.FlowParams;
import org.dromara.warm.flow.core.enums.CooperateType;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 加签适配器
 */
@Component
public class AddSignatureAdapter extends AbstractWarmFlowAdapter implements WarmFlowAdapter {
    @Override
    public boolean isAdapter(Integer warmFlowType) {
        return Objects.equals(CooperateType.ADD_SIGNATURE.getKey(), warmFlowType);
    }

    @Override
    public boolean adapter(WarmFlowInteractiveTypeVO obj) {
        Long taskId = obj.getTaskId();
        String userId =  String.valueOf(SmartRequestUtil.getRequestUserId());
        EmployeeEntity employee = new EmployeeEntity();
        employee.setEmployeeId(Long.parseLong(userId));
        FlowParams flowParams = new FlowParams()
                .handler(userId)
                .permissionFlag(permissionList(employee, userId))
                .addHandlers(obj.getAddHandlers())
                .message(this.type(obj.getOperatorType()));

        return super.taskService.addSignature(taskId, flowParams);
    }
}
