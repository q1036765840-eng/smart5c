package com.nbk.smart5c.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.nbk.smart5c.common.enums.TreeTypeEnum;
import com.nbk.smart5c.entity.Device;
import com.nbk.smart5c.mapper.DeviceMapper;
import com.nbk.smart5c.pojo.bo.DeviceSaveBO;
import com.nbk.smart5c.pojo.bo.DeviceUpdateBO;
import com.nbk.smart5c.pojo.qo.DeviceQO;
import com.nbk.smart5c.pojo.vo.DeviceVO;
import com.nbk.smart5c.service.DeviceService;
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
 * @apiNote 设备信息表业务逻辑层
 * @since 2026-01-27 15:25:38
 */
@Service
@Primary
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService<DeviceMapper, Device> {

    @Autowired
    private DeviceMapper deviceMapper;
    private final static String FILE_NAME = "nbk_device(设备信息表)";

    private final static List<String> TITLE = Arrays.asList(
            "id(主键)",
            "station_id(主键)",
            "device_code(主键)",
            "device_type(主键)",
            "installation_date(安装日期)",
            "manufacturer(制造商)",
            "pillar_no(杆号)",
            "model(型号)",
            "status(状态)",
            "last_maintenance_date(上次维护日期)",
            "next_maintenance_date(下次维护日期)",
            "parameters(参数)",
            "created_user(创建人)",
            "updated_user(更新人)",
            "created_time(创建时间)",
            "updated_time(更新时间)"
    );

    /**
     * 新增数据
     *
     * @param info 新增数据信息
     */
    @Override
    public Boolean save(DeviceSaveBO info) {
        Device entity = new Device();
        BeanUtils.copyProperties(info, entity);
        return super.save(entity);
    }

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     */
    @Override
    public Device queryById(String id) {
        return super.getById(id);
    }

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     */
    @Override
    public IPage<Device> queryAndPaging(DeviceQO query) {
        LambdaQueryWrapper<Device> deviceQueryWrapper = new LambdaQueryWrapper<>();
        deviceQueryWrapper.eq(ObjectUtil.isNotNull(query.getStationId()), Device::getStationId, query.getStationId());
        deviceQueryWrapper.eq(ObjectUtil.isNotNull(query.getDeviceStatus()), Device::getStatus, query.getDeviceStatus());
        if (ObjectUtil.isNotNull(query.getTreeId())) {
            List<Long> deviceIdList = getDeviceIdByTreeTypeAndId(query.getTreeType(), query.getTreeId());
            if (CollUtil.isNotEmpty(deviceIdList)) {
                deviceQueryWrapper.in(Device::getId, deviceIdList);
            }
        }
        return super.page(new Page<>(query.getPageNo(), query.getPageSize()), deviceQueryWrapper);
    }

    @Override
    public List<Long> getDeviceIdByTreeTypeAndId(Integer treeType, Long id) {
        List<Long> deviceIdList = new ArrayList<>();
        if (TreeTypeEnum.SECTION.getCode().equals(treeType)) {
            deviceIdList = deviceMapper.getDeviceIdBySectionId(id);
        } else if (TreeTypeEnum.LINE.getCode().equals(treeType)) {
            deviceIdList = deviceMapper.getDeviceIdByLineId(id);
        } else if (TreeTypeEnum.STATION.getCode().equals(treeType)) {
            deviceIdList = deviceMapper.getDeviceIdByStationId(id);
        } else if (TreeTypeEnum.PILLAR.getCode().equals(treeType)) {
            deviceIdList = Lists.newArrayList(id);
        }
        return deviceIdList;
    }

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    @Override
    public List<DeviceVO> queryAll(DeviceQO query) {
        return super.baseMapper.queryAll(query);
    }

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     */
    @Override
    public Boolean updateById(DeviceUpdateBO info) {
        Device entity = new Device();
        BeanUtils.copyProperties(info, entity);
        return super.updateById(entity);
    }

    /**
     * 数据导出
     *
     * @param query 查询条件
     */
    @Override
    public void export(DeviceQO query, HttpServletResponse response) {
        // 查询用户数据
        List<DeviceVO> result = baseMapper.queryAll(query);
        List<List<String>> data = new ArrayList<>(result.size());
        for (DeviceVO item : result) {
            data.add(Arrays.asList(
                    item.getId() + "",
                    item.getStationId() + "",
                    item.getDeviceCode(),
                    item.getDeviceType(),
                    item.getInstallationDate() + "",
                    item.getManufacturer(),
                    item.getPillarNo(),
                    item.getModel(),
                    item.getStatus() + "",
                    item.getLastMaintenanceDate() + "",
                    item.getNextMaintenanceDate() + "",
                    item.getParameters(),
                    item.getCreatedUser(),
                    item.getUpdatedUser(),
                    Objects.isNull(item.getCreatedTime()) ? "" : item.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
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
