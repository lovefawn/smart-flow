package net.lab1024.sa.admin.module.business.oa.leave.service;

import java.util.List;
import net.lab1024.sa.admin.module.business.oa.leave.dao.LeaveDao;
import net.lab1024.sa.admin.module.business.oa.leave.domain.entity.LeaveEntity;
import net.lab1024.sa.admin.module.business.oa.leave.domain.form.LeaveAddForm;
import net.lab1024.sa.admin.module.business.oa.leave.domain.form.LeaveQueryForm;
import net.lab1024.sa.admin.module.business.oa.leave.domain.form.LeaveUpdateForm;
import net.lab1024.sa.admin.module.business.oa.leave.domain.vo.LeaveVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * OA 请假申请表 Service
 *
 * @Author lf
 * @Date 2025-05-31 20:00:10
 * @Copyright lf
 */

@Service
public class LeaveService {

    @Resource
    private LeaveDao leaveDao;

    /**
     * 分页查询
     */
    public PageResult<LeaveVO> queryPage(LeaveQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<LeaveVO> list = leaveDao.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(LeaveAddForm addForm) {
        LeaveEntity leaveEntity = SmartBeanUtil.copy(addForm, LeaveEntity.class);
        leaveDao.insert(leaveEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     */
    public ResponseDTO<String> update(LeaveUpdateForm updateForm) {
        LeaveEntity leaveEntity = SmartBeanUtil.copy(updateForm, LeaveEntity.class);
        leaveDao.updateById(leaveEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        leaveDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        leaveDao.deleteById(id);
        return ResponseDTO.ok();
    }

    public LeaveVO getDetail(Long id) {
        LeaveEntity leaveEntity = leaveDao.selectById(id);
        return SmartBeanUtil.copy(leaveEntity, LeaveVO.class);
    }
}
