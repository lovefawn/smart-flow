package net.lab1024.sa.admin.module.flow.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 职务表 更新表单
 *
 * @Author kaiyun
 * @Date 2024-06-23 23:31:38
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */

@Data
public class FlowDefinitionUpdateForm extends FlowDefinitionAddForm {

    @Schema(description = "流程ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "流程ID 不能为空")
    private Long id;

}