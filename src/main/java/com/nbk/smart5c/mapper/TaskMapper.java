package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.Task;
import com.nbk.smart5c.pojo.qo.TaskQO;
import com.nbk.smart5c.pojo.vo.TaskPullDownVO;
import com.nbk.smart5c.pojo.vo.TaskVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 任务表持久层
 * @since 2026-04-07 16:41:49
 */
public interface TaskMapper extends MPJBaseMapper<Task> {

    /**
     * 分页查询
     *
     * @param query 查询条件
     * @return 分页查询结果集
     */
    List<TaskVO> queryAndPaging(@Param("query") TaskQO query);

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 查询结果集
     */
    List<TaskVO> queryAll(TaskQO query);


    @Update("update nbk_task set import_nums=#{importNums} where id=#{taskId}")
    void updateImportNumsByTaskId(@Param("taskId") Long taskId, @Param("importNums") int importNums);

    @Select("select id, task_name name, task_status from nbk_task")
    List<TaskPullDownVO> taskPulldownList();

    @Update("update nbk_task set start_check_time=current_timestamp where id=#{taskId} and start_check_time is null")
    void updateTaskStartTime(@Param("taskId") Long taskId);

    @Select({"<script>",
            "select * from nbk_task where task_status=2 and id in",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>",
    })
    List<Task> getAnalyzingTaskList(@Param("ids") List<Long> ids);

    @Update("update nbk_task t set review_nums=(select count(d.id) from nbk_inspection_data d where d.task_id = t.id AND d.confirm_status > 0) where t.id=#{taskId}")
    void updateTaskReviewNums(@Param("taskId") Long taskId);

    @Select("select count(id) from nbk_task where task_status = 2 and id != #{taskId}")
    long getJudgeAnalyzingTask(@Param("taskId") Long taskId);

    @Select("select count(id) from nbk_task where task_status = 2")
    long countAnalyzingTask();
}
