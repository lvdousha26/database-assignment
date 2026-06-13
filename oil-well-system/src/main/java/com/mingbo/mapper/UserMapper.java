package com.oilwell.mapper;

import com.oilwell.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("select * from tb_user where username = #{username} and password = #{password}")
    User selectByUsernameAndPassword(String username, String password);

    @Select("select * from tb_user where id = #{id}")
    User selectById(Long id);

    @Insert("insert into tb_user (username, password, role, gender, phone, status) " +
            "values (#{username}, #{password}, #{role}, #{gender}, #{phone}, #{status})")
    void insert(User user);
}
