package com.mingbo.mapper;

import com.mingbo.pojo.Authority;
import com.mingbo.pojo.AuthorityRequest;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * 权限数据库管理相关映射类
 */
@Mapper
public interface AuthorityMapper {

    /**
     * 添加权限记录
     * @param authority 权限分享信息
     */
    @Insert("INSERT INTO tb_authorization " +
            "VALUES (#{userId}, #{adminId}, #{status}, #{createdAt}, #{updatedAt})")
    void addAuthority(Authority authority);

    List<Authority> getAuthoritiesByPage(String username, int offset, int pageSize) throws DataAccessException;

    @Select("SELECT COUNT(*) FROM tb_authorization, tb_user WHERE tb_authorization.admin_id = tb_user.id AND tb_user.username=#{username}")
    long getAuthoritiesCount(String username) throws DataAccessException;

    @Select("SELECT * FROM tb_authorization_request WHERE id=#{id}")
    AuthorityRequest getRequestById(long id) throws DataAccessException;

    @Select("SELECT COUNT(*) FROM tb_authorization WHERE admin_id=#{adminId} AND user_id=#{userId}")
    byte getIfUserAuthorized(Long adminId, Long userId);

    @Update("UPDATE tb_authorization SET status=#{status} WHERE user_id=#{id}")
    void setStatus(long id, int status);
}
