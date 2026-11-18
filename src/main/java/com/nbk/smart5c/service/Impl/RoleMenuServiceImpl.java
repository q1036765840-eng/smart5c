package com.nbk.smart5c.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbk.smart5c.entity.RoleMenu;
import com.nbk.smart5c.mapper.RoleMenuMapper;
import com.nbk.smart5c.pojo.bo.RoleMenuSaveBO;
import com.nbk.smart5c.pojo.bo.RoleMenuUpdateBO;
import com.nbk.smart5c.pojo.qo.RoleMenuQO;
import com.nbk.smart5c.pojo.vo.RoleMenuVO;
import com.nbk.smart5c.service.RoleMenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 角色和菜单关联表业务逻辑层
 * @since 2026-01-27 20:44:51
 */
@Service
@Primary
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService<RoleMenuMapper, RoleMenu> {

    private final static String FILE_NAME = "nbk_role_menu(角色和菜单关联表)";

    private final static List<String> TITLE = Arrays.asList(
            "role_id(角色ID)",
            "menu_id(菜单ID)"
    );

    /**
     * 新增数据
     *
     * @param info 新增数据信息
     */
    @Override
    public Boolean save(RoleMenuSaveBO info) {
        RoleMenu entity = new RoleMenu();
        BeanUtils.copyProperties(info, entity);
        return super.save(entity);
    }

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     */
    @Override
    public RoleMenu queryById(String id) {
        return super.getById(id);
    }

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     */
    @Override
    public IPage<RoleMenuVO> queryAndPaging(RoleMenuQO query) {
        return null;
    }

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    @Override
    public List<RoleMenuVO> queryAll(RoleMenuQO query) {
        return super.baseMapper.queryAll(query);
    }

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     */
    @Override
    public Boolean updateById(RoleMenuUpdateBO info) {
        RoleMenu entity = new RoleMenu();
        BeanUtils.copyProperties(info, entity);
        return super.updateById(entity);
    }

    /**
     * 数据导出
     *
     * @param query 查询条件
     */
    @Override
    public void export(RoleMenuQO query, HttpServletResponse response) {
        // 查询用户数据
        List<RoleMenuVO> result = baseMapper.queryAll(query);
        List<List<String>> data = new ArrayList<>(result.size());
        for (RoleMenuVO item : result) {
            data.add(Arrays.asList(
                    item.getRoleId() + "",
                    item.getMenuId() + ""
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
