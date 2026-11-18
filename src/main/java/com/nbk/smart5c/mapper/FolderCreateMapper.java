package com.nbk.smart5c.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.nbk.smart5c.entity.FolderCreate;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 文件夹创建记录表持久层
 * @since 2026-06-30 18:45:01
 */
public interface FolderCreateMapper extends MPJBaseMapper<FolderCreate> {
    @Select("select * from nbk_folder_create where folder_path=#{folderPath}")
    FolderCreate queryByFilePath(@Param("folderPath") String folderPath);

    @Select("select count(*) from nbk_folder_create where folder_path = #{folderPath}")
    long countByFilePath(@Param("folderPath") String folderPath);

    @Select("select count(*) from nbk_folder_create where folder_path = #{folderPath} and created_user = #{createUser}")
    long countByFilePathAndCreateUser(@Param("folderPath") String folderPath, @Param("createUser") String createUser);

    @Select("select count(*) from nbk_folder_create where folder_path like concat(#{folderPath},'%') and created_user <> #{createUser}")
    long countByFilePathNotCreateUser(@Param("folderPath") String folderPath, @Param("createUser") String createUser);

    @Delete("delete from nbk_folder_create where folder_path = #{folderPath} and created_user = #{createUser}")
    void deleteCreateRecord(@Param("folderPath") String folderPath, @Param("createUser") String createUser);
}
