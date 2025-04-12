package net.lab1024.sa.admin.module.system.role.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.module.system.department.domain.form.DepartmentQueryForm;
import net.lab1024.sa.admin.module.system.department.domain.vo.DepartmentVO;
import net.lab1024.sa.admin.module.system.role.domain.form.RoleQueryForm;
import net.lab1024.sa.admin.module.system.role.domain.vo.RoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import net.lab1024.sa.admin.module.system.role.domain.entity.RoleEntity;

import java.util.List;

/**
 * 角色 dao
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2022-02-26 21:34:01
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Mapper
public interface RoleDao extends BaseMapper<RoleEntity> {

    /**
     * 根据角色名称查询
     */
    RoleEntity getByRoleName(@Param("roleName") String roleName);

    /**
     * 根据角色编码
     */
    RoleEntity getByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<RoleVO> queryPage(Page page, @Param("queryForm") RoleQueryForm queryForm);

}
