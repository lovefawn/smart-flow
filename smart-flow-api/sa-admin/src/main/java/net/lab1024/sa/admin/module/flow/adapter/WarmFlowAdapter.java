package net.lab1024.sa.admin.module.flow.adapter;

import net.lab1024.sa.admin.module.flow.domain.vo.WarmFlowInteractiveTypeVO;

public interface WarmFlowAdapter {
    boolean isAdapter(Integer warmFlowType);

    boolean adapter(WarmFlowInteractiveTypeVO obj);
}
