package net.lab1024.sa.admin.module.business.oa.leave.domain.form;

import net.lab1024.sa.base.common.domain.PageParam;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OA 请假申请表 分页查询表单
 *
 * @Author lf
 * @Date 2025-05-31 20:00:10
 * @Copyright lf
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class LeaveQueryForm extends PageParam {

    @Schema(description = "请假类型")
    private String type;

    @Schema(description = "开始时间")
    private LocalDate startTimeBegin;

    @Schema(description = "开始时间")
    private LocalDate startTimeEnd;

    @Schema(description = "请假原因")
    private String reason;

    @Schema(description = "流程状态")
    private String flowStatus;

}
