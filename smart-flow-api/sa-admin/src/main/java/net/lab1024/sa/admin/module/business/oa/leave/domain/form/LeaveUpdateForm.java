package net.lab1024.sa.admin.module.business.oa.leave.domain.form;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * OA 请假申请表 更新表单
 *
 * @Author lf
 * @Date 2025-05-31 20:00:10
 * @Copyright lf
 */

@Data
public class LeaveUpdateForm {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键 不能为空")
    private Long leaveId;

    @Schema(description = "请假类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "请假类型 不能为空")
    private String type;

    @Schema(description = "请假原因", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请假原因 不能为空")
    private String reason;

    /**
     * 开始时间
     */
    private LocalDate startTime;

    /**
     * 结束时间
     */
    private LocalDate endTime;
    /**
     * 请假天数
     */
    private Integer day;

}