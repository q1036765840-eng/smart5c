package com.nbk.smart5c.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbk.smart5c.entity.UserRole;
import com.nbk.smart5c.mapper.UserRoleMapper;
import com.nbk.smart5c.pojo.bo.UserRoleSaveBO;
import com.nbk.smart5c.pojo.bo.UserRoleUpdateBO;
import com.nbk.smart5c.pojo.qo.UserRoleQO;
import com.nbk.smart5c.pojo.vo.UserRoleVO;
import com.nbk.smart5c.service.UserRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 用户角色信息表业务逻辑层
 * @since 2026-01-27 15:25:38
 */
@Service
@Primary
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService<UserRoleMapper, UserRole> {

    private final static String FILE_NAME = "nbk_user_role(用户角色信息表)";

    private final static List<String> TITLE = Arrays.asList(
            "id(主键)",
            "user_id(用户id)",
            "role_id(角色id)",
            "created_user(创建人)",
            "created_time(创建时间)",
            "updated_user(修改人)",
            "updated_time(修改时间)"
    );

    /**
     * 新增数据
     *
     * @param info 新增数据信息
     */
    @Override
    public Boolean save(UserRoleSaveBO info) {
        UserRole entity = new UserRole();
        BeanUtils.copyProperties(info, entity);
        return super.save(entity);
    }

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     */
    @Override
    public UserRole queryById(String id) {
        return super.getById(id);
    }

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     */
    @Override
    public IPage<UserRoleVO> queryAndPaging(UserRoleQO query) {
        return null;
    }

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    @Override
    public List<UserRoleVO> queryAll(UserRoleQO query) {
        return super.baseMapper.queryAll(query);
    }

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     */
    @Override
    public Boolean updateById(UserRoleUpdateBO info) {
        UserRole entity = new UserRole();
        BeanUtils.copyProperties(info, entity);
        return super.updateById(entity);
    }

    /**
     * 数据导出
     *
     * @param query 查询条件
     */
    @Override
    public void export(UserRoleQO query, HttpServletResponse response) {
        // 查询用户数据
        List<UserRoleVO> result = baseMapper.queryAll(query);
        List<List<String>> data = new ArrayList<>(result.size());
        for (UserRoleVO item : result) {
            data.add(Arrays.asList(
                    item.getId() + "",
                    item.getUserId() + "",
                    item.getRoleId() + "",
                    item.getCreatedUser(),
                    Objects.isNull(item.getCreatedTime()) ? "" : item.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    item.getUpdatedUser(),
                    Objects.isNull(item.getUpdatedTime()) ? "" : item.getUpdatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
    }

    /**
     * 模板导出
     */
    @Override
    public void exportTemplate(HttpServletResponse response) {

    }

    /**
     * 数据导入
     */
    @Override
    public void analysis(MultipartFile file) {

    }
}
