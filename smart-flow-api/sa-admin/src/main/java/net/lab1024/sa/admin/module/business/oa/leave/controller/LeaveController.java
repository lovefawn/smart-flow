package net.lab1024.sa.admin.module.business.oa.leave.controller;

import net.lab1024.sa.admin.module.business.oa.leave.domain.form.LeaveAddForm;
import net.lab1024.sa.admin.module.business.oa.leave.domain.form.LeaveQueryForm;
import net.lab1024.sa.admin.module.business.oa.leave.domain.form.LeaveUpdateForm;
import net.lab1024.sa.admin.module.business.oa.leave.domain.vo.LeaveVO;
import net.lab1024.sa.admin.module.business.oa.leave.service.LeaveService;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * OA 请假申请表 Controller
 *
 * @Author lf
 * @Date 2025-05-31 20:00:10
 * @Copyright lf
 */

@RestController
@Tag(name = "OA 请假申请表")
public class LeaveController {

    @Resource
    private LeaveService leaveService;

    @Operation(summary = "分页查询 @author lf")
    @PostMapping("/leave/queryPage")
    @SaCheckPermission("leave:query")
    public ResponseDTO<PageResult<LeaveVO>> queryPage(@RequestBody @Valid LeaveQueryForm queryForm) {
        return ResponseDTO.ok(leaveService.queryPage(queryForm));
    }

    @Operation(summary = "添加 @author lf")
    @PostMapping("/leave/add")
    @SaCheckPermission("leave:add")
    public ResponseDTO<String> add(@RequestBody @Valid LeaveAddForm addForm) {
         addForm.setCreateUserId(Long.valueOf(SmartRequestUtil.getRequestUserId()));
        return leaveService.add(addForm);
    }

    @Operation(summary = "更新 @author lf")
    @PostMapping("/leave/update")
    @SaCheckPermission("leave:update")
    public ResponseDTO<String> update(@RequestBody @Valid LeaveUpdateForm updateForm) {
        return leaveService.update(updateForm);
    }

    @Operation(summary = "批量删除 @author lf")
    @PostMapping("/leave/batchDelete")
    @SaCheckPermission("leave:delete")
    public ResponseDTO<String> batchDelete(@RequestBody ValidateList<Long> idList) {
        return leaveService.batchDelete(idList);
    }

    @Operation(summary = "单个删除 @author lf")
    @GetMapping("/leave/delete/{id}")
    @SaCheckPermission("leave:delete")
    public ResponseDTO<String> batchDelete(@PathVariable Long id) {
        return leaveService.delete(id);
    }

    @GetMapping("/leave/{id}")
    public ResponseDTO<LeaveVO> getDetail(@PathVariable Long id) {
        return ResponseDTO.ok(leaveService.getDetail(id));
    }
}
