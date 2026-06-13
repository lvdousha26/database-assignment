package com.mingbo.mapper;

import com.mingbo.pojo.ReferenceResource;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 存储相关数据库映射类
 */
@Mapper
public interface StorageMapper {

    /**
     * 存储上传文件元数据
     * @param ref 文件元数据
     */
    @Insert("INSERT INTO tb_file_metadata " +
            "(admin_id, original_name, storage_path, resource_type, description, upload_time, status) " +
            "VALUES (#{adminId}, #{originalName}, #{storagePath}, " +
            "#{resourceType}, #{description}, #{uploadTime}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void saveMetaData(ReferenceResource ref);


    /**
     * 查询范围内元数据
     * @param id 管理员id
     * @param begin 开始序号
     * @param size 项数量
     * @return 范围内元数据记录
     */
    List<ReferenceResource> getMetaData(int id, int begin, int size);

    /**
     * 查询元数据记录数
     * @return 记录数
     */
    @Select("SELECT COUNT(*) FROM tb_file_metadata")
    Long selectTotalCount();

    /**
     * 按id查询记录文件的路径
     * @param id 查询的记录id号
     * @return id对应的记录
     */
    @Select("SELECT * FROM tb_file_metadata WHERE id=#{id}")
    List<ReferenceResource> selectMetaDataById(int id);

    /**
     * 按id删除元数据记录
     * @param id 要删除的记录id
     */
    @Delete("DELETE FROM tb_file_metadata WHERE id=#{id}")
    void deleteMetaDataById(int id);
}
