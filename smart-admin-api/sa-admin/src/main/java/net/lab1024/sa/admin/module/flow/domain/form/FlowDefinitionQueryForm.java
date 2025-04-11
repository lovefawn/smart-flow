package net.lab1024.sa.admin.module.flow.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;

@Data
public class FlowDefinitionQueryForm extends PageParam {
    private String flowCode;
    private String flowName;
    private String category;
    private String version;

    @Schema(hidden = true)
    private Boolean deletedFlag;
}
