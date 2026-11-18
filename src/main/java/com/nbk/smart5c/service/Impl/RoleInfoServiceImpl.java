package com.nbk.smart5c.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbk.smart5c.entity.RoleInfo;
import com.nbk.smart5c.mapper.RoleInfoMapper;
import com.nbk.smart5c.pojo.bo.RoleInfoSaveBO;
import com.nbk.smart5c.pojo.bo.RoleInfoUpdateBO;
import com.nbk.smart5c.pojo.qo.RoleInfoQO;
import com.nbk.smart5c.pojo.vo.DropDownListVO;
import com.nbk.smart5c.pojo.vo.RoleInfoVO;
import com.nbk.smart5c.service.RoleInfoService;
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
 * @apiNote 角色信息表业务逻辑层
 * @since 2026-01-27 15:25:38
 */
@Service
@Primary
public class RoleInfoServiceImpl extends ServiceImpl<RoleInfoMapper, RoleInfo> implements RoleInfoService<RoleInfoMapper, RoleInfo> {

    private final static String FILE_NAME = "nbk_role_info(角色信息表)";

    private final static List<String> TITLE = Arrays.asList(
            "id(primary key)",
            "role_code(角色编码)",
            "role_name(角色名称(系统管理员、技术专工、检测员、只读用户))",
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
    public Boolean save(RoleInfoSaveBO info) {
        RoleInfo entity = new RoleInfo();
        BeanUtils.copyProperties(info, entity);
        return super.save(entity);
    }

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     */
    @Override
    public RoleInfo queryById(String id) {
        return super.getById(id);
    }

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     */
    @Override
    public IPage<RoleInfoVO> queryAndPaging(RoleInfoQO query) {
        return null;
    }

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    @Override
    public List<RoleInfoVO> queryAll(RoleInfoQO query) {
        return super.baseMapper.queryAll(query);
    }

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     */
    @Override
    public Boolean updateById(RoleInfoUpdateBO info) {
        RoleInfo entity = new RoleInfo();
        BeanUtils.copyProperties(info, entity);
        return super.updateById(entity);
    }

    /**
     * 数据导出
     *
     * @param query 查询条件
     */
    @Override
    public void export(RoleInfoQO query, HttpServletResponse response) {
        // 查询用户数据
        List<RoleInfoVO> result = baseMapper.queryAll(query);
        List<List<String>> data = new ArrayList<>(result.size());
        for (RoleInfoVO item : result) {
            data.add(Arrays.asList(
                    item.getId() + "",
                    item.getRoleCode(),
                    item.getRoleName(),
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

    @Override
    public List<DropDownListVO> getRoleDownList() {
        return super.baseMapper.getRoleDownList();
    }
}
