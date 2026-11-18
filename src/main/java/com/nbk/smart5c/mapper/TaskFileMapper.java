package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.TaskFile;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 任务文件表持久层
 * @since 2026-05-19 19:52:09
 */
public interface TaskFileMapper extends MPJBaseMapper<TaskFile> {

    @Delete("delete from nbk_task_file where task_id=#{taskId}")
    void deleteByTaskId(@Param("taskId") Long taskId);

    @Delete({
            "<script>",
            "delete from nbk_task_file where task_id in",
            "<foreach collection='taskIds' item='taskId' open='(' separator=',' close=')'>",
            "#{taskId}",
            "</foreach>",
            "</script>"
    })
    void deleteByTaskIds(@Param("taskIds") List<Long> taskIds);

    @Select("select file_path from nbk_task_file where task_id=#{taskId}")
    List<String> getByTaskId(@Param("taskId") Long taskId);
}
