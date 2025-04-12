package net.lab1024.sa.admin.module.flow.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 职务表 新建表单
 *
 * @Author kaiyun
 * @Date 2024-06-23 23:31:38
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */

@Data
public class FlowDefinitionAddForm {

    @Schema(description = "流程编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "流程编码不能为空")
    private String flowCode;

    @Schema(description = "流程名称")
    @NotBlank(message = "流程名称不能为空")
    private String flowName;

    @Schema(description = "流程类别")
    private String category;

    /**
     * 审批表单是否自定义（Y是 N否）
     */
    @Schema(description = "审批表单是否自定义")
    @NotBlank(message = "审批表单是否自定义不能为空")
    private String formCustom;

    /**
     * 审批表单路径
     */
    @Schema(description = "审批表单路径")
    private String formPath;

    /**
     * 监听器类型
     */
    @Schema(description = "监听器类型")
    private String listenerType;

    /**
     * 监听器路径
     */
    @Schema(description = "监听器路径")
    private String listenerPath;

}