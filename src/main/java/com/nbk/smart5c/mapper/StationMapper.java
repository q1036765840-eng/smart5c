package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.Station;
import com.nbk.smart5c.pojo.qo.StationQO;
import com.nbk.smart5c.pojo.vo.DropDownListVO;
import com.nbk.smart5c.pojo.vo.StationVO;
import com.nbk.smart5c.pojo.vo.TreeVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 区站信息表持久层
 * @since 2026-01-27 15:25:38
 */
public interface StationMapper extends MPJBaseMapper<Station> {

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果集
     */
    List<StationVO> queryAndPaging(@Param("query") StationQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 查询结果集
     */
    List<StationVO> queryAll(StationQO query);


    @Select("select id, station_name name from nbk_station where line_id=#{lineId}")
    List<DropDownListVO> getStationDownList(@Param("lineId") Long lineId);

    @Select("select s.id, s.station_name label, 3 as type from nbk_station s where s.line_id=#{lineId} and exists (select 1 from nbk_inspection_data i where i.station_id = s.id)")
    List<TreeVO> getStationTreeList(@Param("lineId") Long lineId);

    @Select("select * from nbk_station where  line_id=#{lineId} and station_name=#{stationName} for update")
    Station getStationByLineIdAndName(@Param("lineId") Long lineId, @Param("stationName") String stationName);
}
