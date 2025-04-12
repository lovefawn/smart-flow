package net.lab1024.sa.admin.module.flow.service;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.flow.dao.FlowDefinitionDao;
import net.lab1024.sa.admin.module.flow.domain.form.FlowDefinitionAddForm;
import net.lab1024.sa.admin.module.flow.domain.form.FlowDefinitionQueryForm;
import net.lab1024.sa.admin.module.flow.domain.form.FlowDefinitionUpdateForm;
import net.lab1024.sa.admin.module.flow.domain.vo.FlowDefinitionVO;
import net.lab1024.sa.admin.module.flow.domain.vo.FlowTaskVO;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.warm.flow.core.entity.Definition;
import org.dromara.warm.flow.core.enums.PublishStatus;
import org.dromara.warm.flow.core.service.DefService;
import org.dromara.warm.flow.core.utils.page.Page;
import org.dromara.warm.flow.orm.entity.FlowDefinition;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 职务表 Service
 *
 * @Author kaiyun
 * @Date 2024-06-23 23:31:38
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */

@Service
public class FlowDefinitionService {

    @Resource
    private DefService defService;

    @Resource
    private FlowDefinitionDao flowDefinitionDao;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<FlowDefinitionVO> queryPage(FlowDefinitionQueryForm queryForm) {
        FlowDefinition flowDefinition = SmartBeanUtil.copy(queryForm, FlowDefinition.class);
        Page<Definition> page = Page.pageOf(queryForm.getPageNum().intValue(), queryForm.getPageSize().intValue());
        page = defService.orderByCreateTime().desc().page(flowDefinition, page);
        // 转换为自定义分页响应对象
        PageResult<FlowDefinitionVO> pageResult = new PageResult<>();
        pageResult.setList(SmartBeanUtil.copyList(page.getList(), FlowDefinitionVO.class));
        pageResult.setTotal(page.getTotal());
        pageResult.setPageNum( (long)page.getPageNum());
        pageResult.setPageSize( page.getTotal());
        pageResult.setPages( (long)page.getPageSize());
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(FlowDefinitionAddForm addForm) {
        FlowDefinition flowDefinition = SmartBeanUtil.copy(addForm, FlowDefinition.class);
        flowDefinitionDao.insert(flowDefinition);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(FlowDefinitionUpdateForm updateForm) {
        FlowDefinition flowDefinition = SmartBeanUtil.copy(updateForm, FlowDefinition.class);
        flowDefinitionDao.updateById(flowDefinition);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     *
     * @param idList
     * @return
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        flowDefinitionDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long positionId) {
        if (null == positionId){
            return ResponseDTO.ok();
        }

        flowDefinitionDao.deleteById(positionId);
        return ResponseDTO.ok();
    }

    /**
     * 分页查询
     *
     * @return
     */
    public List<FlowDefinitionVO> queryList() {
        List<FlowDefinitionVO> list = flowDefinitionDao.queryList(Boolean.FALSE);
        return list;
    }

    private LambdaQueryWrapper<FlowDefinition> buildQueryWrapper(FlowDefinition flowDefinition) {
        LambdaQueryWrapper<FlowDefinition> wrapper = Wrappers.lambdaQuery();
        wrapper.like(StringUtils.isNotBlank(flowDefinition.getFlowCode()), FlowDefinition::getFlowCode, flowDefinition.getFlowCode());
        wrapper.like(StringUtils.isNotBlank(flowDefinition.getFlowName()), FlowDefinition::getFlowName, flowDefinition.getFlowName());
        wrapper.like(StringUtils.isNotBlank(flowDefinition.getCategory()), FlowDefinition::getCategory, flowDefinition.getCategory());

        wrapper.orderByDesc(FlowDefinition::getCreateTime);
        return wrapper;
    }
}
