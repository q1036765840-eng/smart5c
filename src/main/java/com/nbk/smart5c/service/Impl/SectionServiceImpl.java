package com.nbk.smart5c.service.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbk.smart5c.entity.Section;
import com.nbk.smart5c.mapper.DeviceMapper;
import com.nbk.smart5c.mapper.LineMapper;
import com.nbk.smart5c.mapper.SectionMapper;
import com.nbk.smart5c.mapper.StationMapper;
import com.nbk.smart5c.pojo.bo.SectionSaveBO;
import com.nbk.smart5c.pojo.bo.SectionUpdateBO;
import com.nbk.smart5c.pojo.qo.SectionQO;
import com.nbk.smart5c.pojo.vo.DropDownListVO;
import com.nbk.smart5c.pojo.vo.SectionVO;
import com.nbk.smart5c.pojo.vo.TreeVO;
import com.nbk.smart5c.service.SectionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 * @apiNote 机务段信息表业务逻辑层
 * @since 2026-01-27 15:25:38
 */
@Service
@Primary
public class SectionServiceImpl extends ServiceImpl<SectionMapper, Section> implements SectionService<SectionMapper, Section> {

    @Autowired
    private LineMapper lineMapper;

    @Autowired
    private StationMapper stationMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    private final static String FILE_NAME = "nbk_section(机务段信息表)";

    private final static List<String> TITLE = Arrays.asList(
            "id(主键)",
            "section_code(机务段编码)",
            "section_name(机务段名称)",
            "description(机务段描述)",
            "created_user(创建人)",
            "created_time(创建时间)",
            "updated_user(更新人)",
            "updated_time(修改时间)"
    );

    /**
     * 新增数据
     *
     * @param info 新增数据信息
     */
    @Override
    public Boolean save(SectionSaveBO info) {
        Section entity = new Section();
        BeanUtils.copyProperties(info, entity);
        return super.save(entity);
    }

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     */
    @Override
    public Section queryById(String id) {
        return super.getById(id);
    }

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     */
    @Override
    public IPage<SectionVO> queryAndPaging(SectionQO query) {
        return null;
    }

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    @Override
    public List<SectionVO> queryAll(SectionQO query) {
        return super.baseMapper.queryAll(query);
    }

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     */
    @Override
    public Boolean updateById(SectionUpdateBO info) {
        Section entity = new Section();
        BeanUtils.copyProperties(info, entity);
        return super.updateById(entity);
    }

    /**
     * 数据导出
     *
     * @param query 查询条件
     */
    @Override
    public void export(SectionQO query, HttpServletResponse response) {
        // 查询用户数据
        List<SectionVO> result = baseMapper.queryAll(query);
        List<List<String>> data = new ArrayList<>(result.size());
        for (SectionVO item : result) {
            data.add(Arrays.asList(
                    item.getId() + "",
                    item.getSectionCode(),
                    item.getSectionName(),
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
    public List<DropDownListVO> getSectionDownList() {
        return super.baseMapper.getSectionDownList();
    }

    @Override
    public List<TreeVO> getSectionTree() {
        List<TreeVO> sectionTreeList = super.baseMapper.getSectionTree();
        if (CollUtil.isEmpty(sectionTreeList)) {
            return sectionTreeList;
        }
        for (TreeVO section : sectionTreeList) {
            List<TreeVO> lineList = lineMapper.getLineTreeList(section.getId());
            if (CollUtil.isNotEmpty(lineList)) {
                for (TreeVO line : lineList) {
                    List<TreeVO> stationList = stationMapper.getStationTreeList(line.getId());
                    setPillarList(stationList);
                    line.setChildren(stationList);
                }
            }
            section.setChildren(lineList);
        }
        List<TreeVO> resultTreeList = new ArrayList<>();
        for (TreeVO treeVO : sectionTreeList) {
            if (treeVO.getLabel().contains("未知")) {
                resultTreeList.addAll(treeVO.getChildren());
            } else {
                resultTreeList.add(treeVO);
            }
        }
        return resultTreeList;
    }

    private void setPillarList(List<TreeVO> stationList) {
        if (CollUtil.isNotEmpty(stationList)) {
            for (TreeVO station : stationList) {
                List<TreeVO> pillarList = deviceMapper.getPillarTreeList(station.getId());
                station.setChildren(pillarList);
            }
        }
    }
}
