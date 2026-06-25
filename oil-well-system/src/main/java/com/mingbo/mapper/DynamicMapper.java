package com.mingbo.mapper;

import com.mingbo.pojo.Dynamic;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DynamicMapper {

    @Insert("insert into tb_dynamic (user_id, content, images) values (#{userId}, #{content}, #{images})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Dynamic dynamic);

    @Select("select * from tb_dynamic where id = #{id}")
    Dynamic selectById(Long id);

    @Select("select * from tb_dynamic order by created_at desc")
    List<Dynamic> selectAll();

    @Select("select * from tb_dynamic where user_id = #{userId} order by created_at desc")
    List<Dynamic> selectByUserId(Long userId);

    @Delete("delete from tb_dynamic where id = #{id}")
    void deleteById(Long id);
}
