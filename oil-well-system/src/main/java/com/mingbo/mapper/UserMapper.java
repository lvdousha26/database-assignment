package com.mingbo.mapper;

import com.mingbo.pojo.PasswordDTO;
import com.mingbo.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from tb_user where username = #{username} and password = #{password}")
    User selectByUsernameAndPassword(String username, String password);

    @Select("select * from tb_user where id = #{id}")
    User selectById(Long id);

    @Select("select * from tb_user where username = #{username}")
    User selectByName(String username);

    @Insert("insert into tb_user (username, password, role, gender, phone, status) " +
            "values (#{username}, #{password}, #{role}, #{gender}, #{phone}, #{status})")
    void insert(User user);

    void updateUserAvatarByUsername(User user);

    void updatePasswordByUsername(PasswordDTO passwordDTO);

    void updateUsername(@Param("oldUsername") String oldUsername, @Param("newUsername") String newUsername);

    void deleteBatchIds(@Param("ids") Long[] ids);

    void deleteById(Long id);

    @Select("select * from tb_user order by id asc")
    List<User> listAll();

    @Update("update tb_user set role = #{role} where id = #{id}")
    void updateRole(Long id, String role);

    @Update("update tb_user set status = #{status} where id = #{id}")
    void updateStatus(Long id, Integer status);
}
