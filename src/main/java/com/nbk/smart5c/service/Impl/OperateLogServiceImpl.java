package com.nbk.smart5c.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbk.smart5c.entity.OperateLog;
import com.nbk.smart5c.mapper.OperateLogMapper;
import com.nbk.smart5c.pojo.bo.OperateLogSaveBO;
import com.nbk.smart5c.pojo.bo.OperateLogUpdateBO;
import com.nbk.smart5c.pojo.qo.OperateLogQO;
import com.nbk.smart5c.pojo.vo.OperateLogVO;
import com.nbk.smart5c.service.OperateLogService;
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
 * @apiNote 操作日志表业务逻辑层
 * @since 2026-01-27 15:25:38
 */
@Service
@Primary
public class OperateLogServiceImpl extends ServiceImpl<OperateLogMapper, OperateLog> implements OperateLogService<OperateLogMapper, OperateLog> {

    private final static String FILE_NAME = "nbk_operate_log(操作日志表)";

    private final static List<String> TITLE = Arrays.asList(
            "id(主键)",
            "operate_user(操作人)",
            "operate_time(操作时间)",
            "operate_type(操作类型(0:删除,1:新增,2:修改))",
            "class_name(操作类名)",
            "method_name(操作方法名)",
            "method_params(操作方法参数)",
            "return_value(操作方法返回值)",
            "cost_time(操作耗时)",
            "operate_desc(操作详细描述)"
    );

    /**
     * 新增数据
     *
     * @param info 新增数据信息
     */
    @Override
    public Boolean save(OperateLogSaveBO info) {
        OperateLog entity = new OperateLog();
        BeanUtils.copyProperties(info, entity);
        return super.save(entity);
    }

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     */
    @Override
    public OperateLog queryById(String id) {
        return super.getById(id);
    }

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     */
    @Override
    public IPage<OperateLogVO> queryAndPaging(OperateLogQO query) {
        return null;
    }

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    @Override
    public List<OperateLogVO> queryAll(OperateLogQO query) {
        return super.baseMapper.queryAll(query);
    }

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     */
    @Override
    public Boolean updateById(OperateLogUpdateBO info) {
        OperateLog entity = new OperateLog();
        BeanUtils.copyProperties(info, entity);
        return super.updateById(entity);
    }

    /**
     * 数据导出
     *
     * @param query 查询条件
     */
    @Override
    public void export(OperateLogQO query, HttpServletResponse response) {
        // 查询用户数据
        List<OperateLogVO> result = baseMapper.queryAll(query);
        List<List<String>> data = new ArrayList<>(result.size());
        for (OperateLogVO item : result) {
            data.add(Arrays.asList(
                    item.getId() + "",
                    item.getOperateUser(),
                    Objects.isNull(item.getOperateTime()) ? "" : item.getOperateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    item.getOperateType() + "",
                    item.getClassName(),
                    item.getMethodName(),
                    item.getMethodParams(),
                    item.getReturnValue(),
                    item.getCostTime() + "",
                    item.getOperateDesc()
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
