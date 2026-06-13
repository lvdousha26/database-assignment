package com.mingbo.mapper;

import com.mingbo.pojo.AdminInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMapper {

    /**
     * 分页查询管理员(简化为只查询id,username,avatar)
     */
    List<AdminInfo> selectAdminsByPage(
            @Param("offset") int offset,
            @Param("pageSize") int pageSize,
            @Param("search") String search);

    /**
     * 统计管理员数量
     */
    long countAdmins(@Param("search") String search);

    /**
     * 查询可申请的管理员(status=1)
     */
    List<AdminInfo> selectAvailableAdmins(
            @Param("offset") int offset,
            @Param("pageSize") int pageSize,
            @Param("search") String search,
            @Param("userId") int userId);

    /**
     * 统计可申请的管理员数量(status=1)
     */
    long countAvailableAdmins(@Param("search") String search);
}
