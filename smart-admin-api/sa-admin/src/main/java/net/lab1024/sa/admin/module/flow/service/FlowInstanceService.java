package net.lab1024.sa.admin.module.flow.service;
import net.lab1024.sa.admin.module.flow.domain.vo.FlowInstanceVO;
import org.dromara.warm.flow.orm.entity.FlowInstance;
import java.util.List;
import java.util.Map;

/**
 * 流程实例 服务层
 *
 * @author may
 */
public interface FlowInstanceService {


    /**
     * 根据业务id查询流程实例详细信息
     *
     * @param businessId 业务id
     * @return 结果
     */
    FlowInstanceVO queryByBusinessId(Long businessId);

    /**
     * 按照业务id查询流程实例
     *
     * @param businessId 业务id
     * @return 结果
     */
    FlowInstance selectInstByBusinessId(String businessId);

    /**
     * 按照实例id查询流程实例
     *
     * @param instanceId 实例id
     * @return 结果
     */
    FlowInstance selectInstById(Long instanceId);

    /**
     * 按照实例id查询流程实例
     *
     * @param instanceIds 实例id
     * @return 结果
     */
    List<FlowInstance> selectInstListByIdList(List<Long> instanceIds);

    /**
     * 按照业务id删除流程实例
     *
     * @param businessIds 业务id
     * @return 结果
     */
    boolean deleteByBusinessIds(List<Long> businessIds);

    /**
     * 按照实例id删除流程实例
     *
     * @param instanceIds 实例id
     * @return 结果
     */
    boolean deleteByInstanceIds(List<Long> instanceIds);

    /**
     * 按照实例id更新状态
     *
     * @param instanceId 实例id
     * @param status     状态
     */
    void updateStatus(Long instanceId, String status);

    /**
     * 设置流程变量
     *
     * @param instanceId 实例id
     * @param variable   流程变量
     */
    void setVariable(Long instanceId, Map<String, Object> variable);
}
