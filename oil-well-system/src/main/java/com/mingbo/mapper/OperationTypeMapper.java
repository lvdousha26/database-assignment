package com.mingbo.mapper;

import com.mingbo.pojo.OperationType;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OperationTypeMapper {

    @Select("select * from tb_operation_type where id = #{id}")
    OperationType selectById(Long id);

    @Select("select * from tb_operation_type order by id")
    List<OperationType> selectAll();

    @Insert("insert into tb_operation_type (type_name, description) values (#{typeName}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(OperationType operationType);

    @Update("update tb_operation_type set type_name=#{typeName}, description=#{description} where id=#{id}")
    void update(OperationType operationType);

    @Delete("delete from tb_operation_type where id = #{id}")
    void deleteById(Long id);
}
