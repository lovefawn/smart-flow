package net.lab1024.sa.admin.module.flow.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.flow.domain.form.FlowDefinitionQueryForm;
import net.lab1024.sa.admin.module.flow.domain.vo.FlowDefinitionVO;
import net.lab1024.sa.admin.module.system.position.domain.entity.PositionEntity;
import net.lab1024.sa.admin.module.system.position.domain.form.PositionQueryForm;
import net.lab1024.sa.admin.module.system.position.domain.vo.PositionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dromara.warm.flow.orm.entity.FlowDefinition;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 职务表 Dao
 *
 * @Author kaiyun
 * @Date 2024-06-23 23:31:38
 * @Copyright <a href="https://1024lab.net">1024创新实验室</a>
 */

@Mapper
@Component
public interface FlowDefinitionDao extends BaseMapper<FlowDefinition> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<FlowDefinitionVO> queryPage(Page page, @Param("queryForm") FlowDefinitionQueryForm queryForm);


    /**
     * 查询
     * @param deletedFlag
     * @return
     */
    List<FlowDefinitionVO> queryList(@Param("deletedFlag") Boolean deletedFlag);
}
