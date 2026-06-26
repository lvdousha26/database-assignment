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
            "(user_id, admin_id, status, perm_create, perm_read, perm_update, perm_delete, created_at, updated_at) " +
            "VALUES (#{userId}, #{adminId}, #{status}, #{permCreate}, #{permRead}, #{permUpdate}, #{permDelete}, #{createdAt}, #{updatedAt})")
    void addAuthority(Authority authority);

    List<Authority> getAuthoritiesByPage(String username, int offset, int pageSize) throws DataAccessException;

    @Select("SELECT COUNT(*) FROM tb_authorization")
    long getAuthoritiesCount(String username) throws DataAccessException;

    @Select("SELECT * FROM tb_authorization_request WHERE id=#{id}")
    AuthorityRequest getRequestById(long id) throws DataAccessException;

    @Select("SELECT COUNT(*) FROM tb_authorization WHERE admin_id=#{adminId} AND user_id=#{userId}")
    byte getIfUserAuthorized(Long adminId, Long userId);

    @Select("SELECT * FROM tb_authorization WHERE user_id=#{userId} AND admin_id=#{adminId}")
    Authority getAuthorityByUserAndAdmin(Long userId, Long adminId);

    @Select("SELECT * FROM tb_authorization WHERE user_id=#{userId} AND status=1")
    List<Authority> getActiveAuthoritiesByUserId(Long userId);

    @Update("UPDATE tb_authorization SET status=#{status} WHERE user_id=#{id}")
    void setStatus(long id, int status);

    @Update("UPDATE tb_authorization SET status=#{status}, perm_create=#{permCreate}, perm_read=#{permRead}, perm_update=#{permUpdate}, perm_delete=#{permDelete} WHERE user_id=#{userId}")
    void updateUserAuthority(long userId, int status, Integer permCreate, Integer permRead, Integer permUpdate, Integer permDelete);
}
