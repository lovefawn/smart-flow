package net.lab1024.sa.admin.module.business.oa.leave.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * OA 请假申请表 列表VO
 *
 * @Author lf
 * @Date 2025-05-31 20:00:10
 * @Copyright lf
 */

@Data
public class LeaveVO {


    @Schema(description = "主键")
    private Long leaveId;

    @Schema(description = "请假类型")
    private String type;

    @Schema(description = "请假原因")
    private String reason;

    /**
     * 开始时间
     */
    private LocalDate startTime;

    /**
     * 结束时间
     */
    private LocalDate endTime;

    @Schema(description = "请假天数")
    private Integer day;

    @Schema(description = "流程节点名称")
    private String nodeName;

    @Schema(description = "流程状态（0待提交 1审批中 2 审批通过 3自动通过 4终止 5作废 6撤销 7取回  8已完成 9已退回 10失效）")
    private String flowStatus;

    @Schema(description = "创建者")
    private String createUserName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 流程实例的id
     */
    @Schema(description = "流程实例的id")
    private Long instanceId;

}
