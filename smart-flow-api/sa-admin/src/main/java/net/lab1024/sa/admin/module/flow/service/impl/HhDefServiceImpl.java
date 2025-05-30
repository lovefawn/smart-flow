package net.lab1024.sa.admin.module.flow.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.flow.adapter.WarmFlowAdapter;
import net.lab1024.sa.admin.module.flow.service.HhDefService;
import net.lab1024.sa.admin.module.flow.domain.vo.WarmFlowInteractiveTypeVO;
import net.lab1024.sa.base.common.util.SpringUtils;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.service.NodeService;
import org.dromara.warm.flow.core.service.SkipService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 流程定义serviceImpl
 *
 * @author warm
 * @since 2023/5/29 13:09
 */
@Service
public class HhDefServiceImpl implements HhDefService {

    @Resource
    private DefService defService;

    @Resource
    private NodeService nodeService;

    @Resource
    private SkipService skipService;

    private static final List<WarmFlowAdapter> WARM_FLOW_ADAPTERS = new ArrayList<>();

    /**
     * 初始化方法
     */
    @PostConstruct
    public void initMethod() {
        Map<String, WarmFlowAdapter> beanNamesForType = SpringUtils.getBeanNamesForType(WarmFlowAdapter.class);
        WARM_FLOW_ADAPTERS.addAll(beanNamesForType.values());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean interactiveType(WarmFlowInteractiveTypeVO warmFlowInteractiveTypeVo) {
        Integer operatorType = warmFlowInteractiveTypeVo.getOperatorType();
        for (WarmFlowAdapter warmFlowAdapter : WARM_FLOW_ADAPTERS) {
            if (warmFlowAdapter.isAdapter(operatorType)) {
                return warmFlowAdapter.adapter(warmFlowInteractiveTypeVo);
            }
        }
        return false;
    }
}
