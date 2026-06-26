package com.mingbo.mapper;

import com.mingbo.pojo.AuthorityRequest;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.sql.Timestamp;
import java.util.List;

/**
 * 权限相关持久层SQL映射
 */
@Mapper
public interface AuthorityRequestMapper {

    /**
     * 添加申请信息
     * @param authorityRequest 申请信息
     */
    @Insert("INSERT INTO tb_authorization_request(" +
            "user_id, admin_id, request_message, perm_create, perm_read, perm_update, perm_delete, created_at)" +
            "VALUES (#{userId}, #{adminId}, #{requestMessage}, #{permCreate}, #{permRead}, #{permUpdate}, #{permDelete}, #{createdAt})")
    void addAuthorityRequest(AuthorityRequest authorityRequest);

    /**
     * 查询id身份
     * @param id 要查询的id
     * @return 查询结果
     */
    @Select("SELECT role FROM tb_user WHERE id=#{id}")
    List<String> getUserRole(int id);

    /**
     * 查询所提供id用户收到的申请记录信息
     * @param id 要查询的id
     * @param begin 开始项
     * @param size 页项数
     * @return id收到的申请记录
     */
    List<AuthorityRequest> getReceivedRequestsByPage(int id, int begin, int size);

    @Select("SELECT COUNT(*) FROM tb_authorization_request WHERE " +
            "user_id=#{userId} AND admin_id=#{adminId} AND status=-1")
    int getNotRespondedRequestCount(int userId, int adminId);

    /**
     * 查询所提供id用户发出的申请记录信息
     * @param id 要查询的id
     * @param begin 开始项
     * @param size 页项数
     * @return id发送的申请记录
     */
    List<AuthorityRequest> getSentRequestsByPage(int id, int begin, int size);

    @Select("SELECT COUNT(*) FROM tb_authorization_request")
    Long selectTotalCount();

    /**
     * 修改申请记录状态
     * @param id 申请记录id
     * @param status 目标状态
     */
    @Update("UPDATE tb_authorization_request SET status=#{status} WHERE id=#{id}")
    void setRequestStatus(int id, int status);

    /**
     * 依据id查找申请记录
     * @param id 要查找的记录id
     * @return 申请记录信息
     */
    @Select("SELECT * FROM tb_authorization_request WHERE id=#{id}")
    AuthorityRequest getAuthorityRequestById(int id);

    /**
     * 依据id查询记录状态信息
     * @param id 要查询的id
     * @return 申请记录状态
     */
    @Select("SELECT status FROM tb_authorization_request WHERE id=#{id}")
    byte getRequestStatus(int id);

    /**
     * 修改上申请处理时间
     * @param id 要修改的申请记录
     * @param processedTime 处理时间
     */
    @Update("UPDATE tb_authorization_request SET processed_at=#{processedTime} " +
            "WHERE id=#{id}")
    void updateAuthorityRequestProcessedTime(int id, Timestamp processedTime);
}
