package net.lab1024.sa.admin.module.flow.service;

import net.lab1024.sa.admin.module.flow.domain.vo.WarmFlowInteractiveTypeVO;

/**
 * 流程定义service
 *
 * @author warm
 * @since 2023/5/29 13:09
 */
public interface HhDefService {

    Boolean interactiveType(WarmFlowInteractiveTypeVO warmFlowInteractiveTypeVo);
}
