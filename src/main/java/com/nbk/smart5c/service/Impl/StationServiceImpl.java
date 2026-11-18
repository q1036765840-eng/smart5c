package com.nbk.smart5c.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbk.smart5c.entity.Station;
import com.nbk.smart5c.mapper.StationMapper;
import com.nbk.smart5c.pojo.bo.StationSaveBO;
import com.nbk.smart5c.pojo.bo.StationUpdateBO;
import com.nbk.smart5c.pojo.qo.StationQO;
import com.nbk.smart5c.pojo.vo.DropDownListVO;
import com.nbk.smart5c.pojo.vo.StationVO;
import com.nbk.smart5c.service.StationService;
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
 * @apiNote 区站信息表业务逻辑层
 * @since 2026-01-27 15:25:38
 */
@Service
@Primary
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements StationService<StationMapper, Station> {

    private final static String FILE_NAME = "nbk_station(区站信息表)";

    private final static List<String> TITLE = Arrays.asList(
            "id(primary key)",
            "line_id(线路id)",
            "station_code(区站编码)",
            "station_name(区站名)",
            "description(区站描述)",
            "created_user(创建人)",
            "created_time(创建时间)",
            "updated_user(修改人)",
            "updated_time(更新时间)"
    );

    /**
     * 新增数据
     *
     * @param info 新增数据信息
     */
    @Override
    public Boolean save(StationSaveBO info) {
        Station entity = new Station();
        BeanUtils.copyProperties(info, entity);
        return super.save(entity);
    }

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     */
    @Override
    public Station queryById(String id) {
        return super.getById(id);
    }

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     */
    @Override
    public IPage<StationVO> queryAndPaging(StationQO query) {
        return null;
    }

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    @Override
    public List<StationVO> queryAll(StationQO query) {
        return super.baseMapper.queryAll(query);
    }

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     */
    @Override
    public Boolean updateById(StationUpdateBO info) {
        Station entity = new Station();
        BeanUtils.copyProperties(info, entity);
        return super.updateById(entity);
    }

    /**
     * 数据导出
     *
     * @param query 查询条件
     */
    @Override
    public void export(StationQO query, HttpServletResponse response) {
        // 查询用户数据
        List<StationVO> result = baseMapper.queryAll(query);
        List<List<String>> data = new ArrayList<>(result.size());
        for (StationVO item : result) {
            data.add(Arrays.asList(
                    item.getId() + "",
                    item.getLineId() + "",
                    item.getStationCode(),
                    item.getStationName(),
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
    public List<DropDownListVO> getStationDownList(Long lineId) {
        return super.baseMapper.getStationDownList(lineId);
    }
}
