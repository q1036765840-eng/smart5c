package com.nbk.smart5c.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbk.smart5c.entity.Line;
import com.nbk.smart5c.mapper.LineMapper;
import com.nbk.smart5c.pojo.bo.LineSaveBO;
import com.nbk.smart5c.pojo.bo.LineUpdateBO;
import com.nbk.smart5c.pojo.qo.LineQO;
import com.nbk.smart5c.pojo.vo.DropDownListVO;
import com.nbk.smart5c.pojo.vo.LineVO;
import com.nbk.smart5c.service.LineService;
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
 * @apiNote 线路信息表业务逻辑层
 * @since 2026-01-27 15:25:38
 */
@Service
@Primary
public class LineServiceImpl extends ServiceImpl<LineMapper, Line> implements LineService<LineMapper, Line> {

    private final static String FILE_NAME = "nbk_line(线路信息表)";

    private final static List<String> TITLE = Arrays.asList(
            "id(主键)",
            "section_id(区域id)",
            "line_code(线路编码)",
            "line_name(线路名称)",
            "description(描述)",
            "created_user(创建人)",
            "created_time(创建时间)",
            "updated_user(更新人)",
            "updated_time(更新时间)"
    );

    /**
     * 新增数据
     *
     * @param info 新增数据信息
     */
    @Override
    public Boolean save(LineSaveBO info) {
        Line entity = new Line();
        BeanUtils.copyProperties(info, entity);
        return super.save(entity);
    }

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     */
    @Override
    public Line queryById(String id) {
        return super.getById(id);
    }

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     */
    @Override
    public IPage<LineVO> queryAndPaging(LineQO query) {
        return null;
    }

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    @Override
    public List<LineVO> queryAll(LineQO query) {
        return super.baseMapper.queryAll(query);
    }

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     */
    @Override
    public Boolean updateById(LineUpdateBO info) {
        Line entity = new Line();
        BeanUtils.copyProperties(info, entity);
        return super.updateById(entity);
    }

    /**
     * 数据导出
     *
     * @param query 查询条件
     */
    @Override
    public void export(LineQO query, HttpServletResponse response) {
        // 查询用户数据
        List<LineVO> result = baseMapper.queryAll(query);
        List<List<String>> data = new ArrayList<>(result.size());
        for (LineVO item : result) {
            data.add(Arrays.asList(
                    item.getId() + "",
                    item.getSectionId() + "",
                    item.getLineCode(),
                    item.getLineName(),
                    item.getDescription(),
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
    public List<DropDownListVO> getLineDownList(Long sectionId) {
        return super.baseMapper.getLineDownList(sectionId);
    }
}
