package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.FileUploadRecords;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 文件上传记录表持久层
 * @since 2026-05-15 11:02:04
 */
@Mapper
public interface FileUploadRecordsMapper extends MPJBaseMapper<FileUploadRecords> {

    @Select("select count(*) from nbk_file_upload_records where upload_path = #{filePath} and created_user <> #{createUser}")
    long countByFilePathAndCreateUser(@Param("filePath") String filePath, @Param("createUser") String createUser);

    @Delete("delete from nbk_file_upload_records where #{filePath} like concat(upload_path,'%') and created_user = #{createUser}")
    void deleteUploadRecord(@Param("filePath") String filePath, @Param("createUser") String createUser);
}
