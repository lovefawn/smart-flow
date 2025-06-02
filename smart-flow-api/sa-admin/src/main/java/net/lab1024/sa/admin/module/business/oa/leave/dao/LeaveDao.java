package net.lab1024.sa.admin.module.business.oa.leave.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.oa.leave.domain.entity.LeaveEntity;
import net.lab1024.sa.admin.module.business.oa.leave.domain.form.LeaveQueryForm;
import net.lab1024.sa.admin.module.business.oa.leave.domain.vo.LeaveVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * OA 请假申请表 Dao
 *
 * @Author lf
 * @Date 2025-05-31 20:00:10
 * @Copyright lf
 */

@Mapper
public interface LeaveDao extends BaseMapper<LeaveEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<LeaveVO> queryPage(Page page, @Param("queryForm") LeaveQueryForm queryForm);

}
