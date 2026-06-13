package com.mingbo.service;

import com.mingbo.exception.AuthorityRoleErrorException;
import com.mingbo.exception.OperationInvalidException;
import com.mingbo.pojo.*;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 权限管理业务层逻辑类
 */
public interface AuthorityService {
    /**
     * 添加权限请求申请
     * @param authorityRequest 申请信息
     * @throws DataAccessException 数据库错误时抛出，如申请人id非法，申请信息过长
     * @throws AuthorityRoleErrorException 管理员相关信息异常时抛出，如id非管理员，不存在此人，以及id查询结果出现多个的情况（一般不发生）
     */
    void addRequest(AuthorityRequest authorityRequest) throws DataAccessException, AuthorityRoleErrorException, OperationInvalidException;

    /**
     * 按页查看收到的申请记录
     * @param authorityRequestDTO 页面信息，包括申请人id，当前页数和页面展示的记录数
     * @return 一页记录以及记录总数信息
     * @throws DataAccessException 当数据库出现问题抛出此错误，比如id不合法
     */
    PageVO<AuthorityRequest> getReceivedRequests(GeneralRequestDTO authorityRequestDTO) throws DataAccessException;

    AuthorityRequest getRequestById(long id) throws DataAccessException;

    /**
     * 按页查看发出的申请记录
     * @param authorityRequestDTO 页面信息，包括申请人id，当前页数和页面展示的记录数
     * @return 一页记录以及记录总数信息
     * @throws DataAccessException 当数据库出现问题抛出此错误，比如id不合法
     */
    PageVO<AuthorityRequest> getSentRequests(GeneralRequestDTO authorityRequestDTO) throws DataAccessException;

    /**
     * 批准权限申请
     * @param id 批注的申请id
     * @return 成功执行信息
     * @throws DataAccessException 数据库出现问题时抛出，如记录不存在
     * @throws OperationInvalidException 记录已经处理完毕时抛出，防止重复操作
     */
    Object acceptRequest(int id) throws DataAccessException, OperationInvalidException;

    /**
     * 拒绝权限申请
     * @param id 要拒绝的申请id
     * @return 成功执行拒绝操作信息
     * @throws DataAccessException 数据库出现问题时抛出。如记录不存在
     * @throws OperationInvalidException 记录已经处理完毕时抛出，防止重复操作
     */
    Object rejectRequest(int id) throws DataAccessException, OperationInvalidException;

    PageResult<User> getAuthorizedUserByPage(String username, int pageSize, int currentPage) throws DataAccessException;

    int isAuthorizedUser(long id) throws DataAccessException;

    Object setStatus(@PathVariable int status, @PathVariable long id) throws DataAccessException;
}
