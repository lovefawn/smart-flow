package net.lab1024.sa.admin.module.flow.event;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 删除流程监听
 *
 * @author AprilWind
 */
@Data
public class ProcessDeleteEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 流程定义编码
     */
    private String flowCode;

    /**
     * 业务id
     */
    private String businessId;

}
