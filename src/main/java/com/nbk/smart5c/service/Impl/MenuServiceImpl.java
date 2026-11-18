package com.nbk.smart5c.service.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbk.smart5c.entity.Menu;
import com.nbk.smart5c.mapper.MenuMapper;
import com.nbk.smart5c.pojo.bo.MenuSaveBO;
import com.nbk.smart5c.pojo.bo.MenuUpdateBO;
import com.nbk.smart5c.pojo.qo.MenuQO;
import com.nbk.smart5c.pojo.vo.MenuVO;
import com.nbk.smart5c.service.MenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 菜单权限表业务逻辑层
 * @since 2026-01-27 20:44:51
 */
@Service
@Primary
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService<MenuMapper, Menu> {
    /**
     * 新增数据
     *
     * @param info 新增数据信息
     */
    @Override
    public Boolean save(MenuSaveBO info) {
        Menu entity = new Menu();
        BeanUtils.copyProperties(info, entity);
        return super.save(entity);
    }

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     */
    @Override
    public Menu queryById(String id) {
        return super.getById(id);
    }

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     */
    @Override
    public IPage<MenuVO> queryAndPaging(MenuQO query) {
        return null;
    }

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    @Override
    public List<MenuVO> queryAll(MenuQO query) {
        return super.baseMapper.queryAll(query);
    }

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     */
    @Override
    public Boolean updateById(MenuUpdateBO info) {
        Menu entity = new Menu();
        BeanUtils.copyProperties(info, entity);
        return super.updateById(entity);
    }

    /**
     * 数据导出
     *
     * @param query 查询条件
     */
    @Override
    public void export(MenuQO query, HttpServletResponse response) {

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

    @Override
    public List<Long> getTopMenuId() {
        QueryWrapper<Menu> menuInfoQueryWrapper = new QueryWrapper<>();
        menuInfoQueryWrapper.lambda().isNull(Menu::getParentId);
        List<Menu> menuInfoList = super.list(menuInfoQueryWrapper);
        return menuInfoList.stream().map(Menu::getId).collect(Collectors.toList());
    }

    @Override
    public List<Menu> getMenuInfoTree(List<Long> menuIdList) {
        List<Menu> menuInfoList = super.baseMapper.selectBatchIds(menuIdList);
        return setLevelMenu(menuInfoList);
    }

    private List<Menu> setLevelMenu(List<Menu> menuInfoList) {
        if (CollUtil.isEmpty(menuInfoList)) {
            return menuInfoList;
        }
        menuInfoList.stream().forEach(menu -> {
            QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(Menu::getParentId, menu.getId());
            List<Menu> childList = super.list(queryWrapper);
            if (CollUtil.isNotEmpty(childList)) {
                setLevelMenu(childList);
            }
            menu.setChildren(childList);
        });
        return menuInfoList;
    }
}
