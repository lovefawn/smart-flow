package net.lab1024.sa.admin.module.flow.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import net.lab1024.sa.admin.constant.AdminSwaggerTagConst;
import net.lab1024.sa.admin.module.flow.domain.form.FlowDefinitionQueryForm;
import net.lab1024.sa.admin.module.flow.domain.vo.FlowDefinitionVO;
import net.lab1024.sa.admin.module.flow.service.FlowDefinitionService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartResponseUtil;
import net.lab1024.sa.base.common.util.SmartStringUtil;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.service.ChartService;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.orm.entity.FlowDefinition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 流程定义Controller
 *
 * @author hh
 * @date 2023-04-11
 */
@Validated
@RestController
@Tag(name = AdminSwaggerTagConst.Flow.FLOW_DEFINITION)
@RequestMapping("/flow/definition")
public class DefController {
    @Resource
    private FlowDefinitionService flowDefinitionService;
    
    @Resource
    private DefService defService;

    @Resource
    private ChartService chartService;

    /**
     * 分页查询流程定义列表
     */
    @Operation(summary = "分页查询 @author kaiyun")
    @PostMapping("/queryPage")
    public ResponseDTO<PageResult<FlowDefinitionVO>> queryPage(@RequestBody @Valid FlowDefinitionQueryForm queryForm) {
        return ResponseDTO.ok(flowDefinitionService.queryPage(queryForm));
    }
    
    /**
     * 获取流程定义详细信息
     */
    @Operation(summary = "获取流程定义详细信息 @author kaiyun")
    @SaCheckPermission("flow:definition:query")
    @GetMapping(value = "/{id}")
    public ResponseDTO<Definition> getInfo(@PathVariable("id") Long id) {
        return ResponseDTO.ok(defService.getById(id));
    }

    /**
     * 新增流程定义
     */
    @Operation(summary = "添加 @author kaiyun")
    @SaCheckPermission("flow:definition:add")
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Boolean> add(@RequestBody FlowDefinition flowDefinition) {
        return ResponseDTO.ok(defService.saveAndInitNode(flowDefinition));
    }

    /**
     * 发布流程定义
     */
    @Operation(summary = "发布流程定义 @author kaiyun")
    @SaCheckPermission("flow:definition:publish")
    @GetMapping("/publish/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Boolean> publish(@PathVariable("id") Long id) {
        return ResponseDTO.ok(defService.publish(id));
    }

    /**
     * 取消发布流程定义
     */
    @Operation(summary = "取消发布流程定义 @author kaiyun")
    @SaCheckPermission("flow:definition:publish")
    @GetMapping("/unPublish/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Void> unPublish(@PathVariable("id") Long id) {
        defService.unPublish(id);
        return ResponseDTO.ok();
    }

    /**
     * 修改流程定义
     */
    @Operation(summary = "更新 @author kaiyun")
    @SaCheckPermission("flow:definition:edit")
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Boolean> edit(@RequestBody FlowDefinition flowDefinition) {
        return ResponseDTO.ok(defService.updateById(flowDefinition));
    }

    /**
     * 删除流程定义
     */
    @Operation(summary = "删除 @author kaiyun")
    @SaCheckPermission("flow:definition:remove")
    @GetMapping("/delete/{ids}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Boolean> remove(@PathVariable List<Long> ids) {
        return ResponseDTO.ok(defService.removeDef(ids));
    }

    /**
     * 复制流程定义
     */
    @Operation(summary = "复制流程定义 @author kaiyun")
    @SaCheckPermission("flow:definition:publish")
    @GetMapping("/copyDef/{id}")
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Boolean> copyDef(@PathVariable("id") Long id) {
        return ResponseDTO.ok(defService.copyDef(id));
    }

    @Operation(summary = "流程定义导入 @author kaiyun")
    @SaCheckPermission("flow:definition:importDefinition")
    @PostMapping("/importDefinition")
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<Void> importDefinition(MultipartFile file) throws Exception {
        defService.importIs(file.getInputStream());
        return ResponseDTO.ok();
    }

    @Operation(summary = "流程定义导出 @author kaiyun")
    @SaCheckPermission("flow:definition:exportDefinition")
    @PostMapping("/exportDefinition/{id}")
    public void exportDefinition(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        // 要导出的字符串
        String content = defService.exportJson(id);

        if (SmartStringUtil.isNotEmpty(content)) {
            SmartResponseUtil.setDownloadFileHeader(response, "flowDefinition" + id + ".json", (long) content.getBytes().length);
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM + ";charset=utf-8");
            response.getOutputStream().write(content.getBytes());
        }
    }

    /**
     * 查询流程图
     *
     * @param definitionId
     *
     * @return
     */
    @Operation(summary = "查询流程图 @author kaiyun")
    @GetMapping("/chartDef/{definitionId}")
    public ResponseDTO<String> chartDef(@PathVariable("definitionId") Long definitionId) {
        return ResponseDTO.ok(chartService.chartDef(definitionId));
    }

    /**
     * 查询流程图
     *
     * @param instanceId
     * @return
     */
    @Operation(summary = "查询流程图 @author kaiyun")
    @GetMapping("/flowChart/{instanceId}")
    public ResponseDTO<String> flowChart(@PathVariable("instanceId") Long instanceId) throws IOException {
        return ResponseDTO.ok(chartService.chartIns(instanceId));
    }

    /**
     * 激活流程
     *
     * @param definitionId
     * @return
     */
    @Operation(summary = "激活流程 @author kaiyun")
    @GetMapping("/active/{definitionId}")
    public ResponseDTO<Boolean> active(@PathVariable("definitionId") Long definitionId) {
        return ResponseDTO.ok(defService.active(definitionId));
    }

    /**
     * 挂起流程
     *
     * @param definitionId
     * @return
     */
    @Operation(summary = "挂起流程 @author kaiyun")
    @GetMapping("/unActive/{definitionId}")
    public ResponseDTO<Boolean> unActive(@PathVariable("definitionId") Long definitionId) {
        return ResponseDTO.ok(defService.unActive(definitionId));
    }
}
