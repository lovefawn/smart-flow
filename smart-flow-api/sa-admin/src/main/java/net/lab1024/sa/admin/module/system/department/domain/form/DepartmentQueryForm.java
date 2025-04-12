package net.lab1024.sa.admin.module.system.department.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import net.lab1024.sa.base.common.domain.PageParam;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

@Data
public class DepartmentQueryForm extends PageParam {

    @Schema(description = "部门名称")
    @Length(max = 20, message = "搜索词最多20字符")
    private String name;

    @Schema(description = "开始时间")
    private LocalDate startTime;

    @Schema(description = "结束时间")
    private LocalDate endTime;



}
