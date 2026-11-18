package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.Device;
import com.nbk.smart5c.pojo.qo.DeviceQO;
import com.nbk.smart5c.pojo.vo.DeviceVO;
import com.nbk.smart5c.pojo.vo.TreeVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 设备信息表持久层
 * @since 2026-01-27 15:25:38
 */
public interface DeviceMapper extends MPJBaseMapper<Device> {

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果集
     */
    List<DeviceVO> queryAndPaging(@Param("query") DeviceQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 查询结果集
     */
    List<DeviceVO> queryAll(DeviceQO query);


    @Select("select d.id, d.device_code label,4 as type from nbk_device d where d.station_id=#{stationId} and exists (select 1 from nbk_inspection_data i where i.device_id = d.id)")
    List<TreeVO> getPillarTreeList(@Param("stationId") Long stationId);

    /**
     *
     * @param sectionId 供电段id
     * @return
     */
    @Select({
            "select distinct d.id from nbk_device d inner join nbk_station s on d.station_id = s.id",
            "inner join nbk_line l on s.line_id = l.id",
            "inner join nbk_section se on l.section_id = se.id",
            "where se.id=#{sectionId}"
    })
    List<Long> getDeviceIdBySectionId(@Param("sectionId") Long sectionId);

    /**
     *
     * @param lineId 线路id
     * @return
     */
    @Select({
            "select distinct d.id from nbk_device d inner join nbk_station s on d.station_id = s.id",
            "inner join nbk_line l on s.line_id = l.id",
            "where l.id=#{lineId}"
    })
    List<Long> getDeviceIdByLineId(@Param("lineId") Long lineId);

    /**
     *
     * @param stationId 区站id
     * @return
     */
    @Select({
            "select distinct d.id from nbk_device d inner join nbk_station s on d.station_id = s.id",
            "where s.id=#{stationId}"
    })
    List<Long> getDeviceIdByStationId(@Param("stationId") Long stationId);

    @Select("select * from nbk_device where station_id=#{stationId} and device_code=#{deviceCode} for update")
    Device getDeviceByCode(@Param("stationId") Long stationId, @Param("deviceCode") String deviceCode);
}
