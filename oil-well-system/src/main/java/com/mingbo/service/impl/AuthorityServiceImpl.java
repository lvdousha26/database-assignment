package com.mingbo.service.impl;

import com.mingbo.exception.AuthorityRoleErrorException;
import com.mingbo.exception.OperationInvalidException;
import com.mingbo.mapper.AuthorityMapper;
import com.mingbo.mapper.AuthorityRequestMapper;
import com.mingbo.mapper.UserMapper;
import com.mingbo.pojo.*;
import com.mingbo.service.AuthorityService;
import com.mingbo.service.InfoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 */
@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    private AuthorityRequestMapper authorityRequestMapper;

    @Autowired
    private AuthorityMapper authorityMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private InfoService infoService;

    @Transactional
    @Override
    public void addRequest(AuthorityRequest authorityRequest)
            throws DataAccessException, AuthorityRoleErrorException, OperationInvalidException {
        authorityRequest.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        List<String> userRole = authorityRequestMapper.
                getUserRole(authorityRequest.getAdminId());
        if (userRole.isEmpty()) {
            throw new AuthorityRoleErrorException("未找到相关人员");
        } else if (userRole.size() > 1) {
            throw new AuthorityRoleErrorException("发生了未知错误");
        } else {
            String role = userRole.get(0);
            if (!role.equals("管理员")) {
                throw new AuthorityRoleErrorException("申请对象并非管理员，请联系相关人员进行核实");
            }
        }
        int countOfRequestNotYetResponded =
                authorityRequestMapper.getNotRespondedRequestCount(
                        authorityRequest.getUserId(), authorityRequest.getAdminId());
        if (countOfRequestNotYetResponded > 0) {
            throw new OperationInvalidException("已有未答复申请，请勿重复申请");
        }
        authorityRequestMapper.addAuthorityRequest(authorityRequest);
    }

    @Transactional
    @Override
    public PageVO<AuthorityRequest> getReceivedRequests(GeneralRequestDTO authorityRequestDTO)
            throws DataAccessException {

        System.out.println(authorityRequestDTO);
        int currentPage = authorityRequestDTO.getCurrentPage();
        int pageSize = authorityRequestDTO.getPageSize();
        int userId = authorityRequestDTO.getUserId();

        int begin = (currentPage - 1) * pageSize;

        List<AuthorityRequest> rows =
                authorityRequestMapper.getReceivedRequestsByPage(userId, begin, pageSize);

        //6. 查询总记录数
        Long totalCount = authorityRequestMapper.selectTotalCount();

        //7. 封装PageBean对象
        PageVO<AuthorityRequest> pageBean = new PageVO<>();
        pageBean.setRows(rows);
        pageBean.setTotalCount(totalCount);
        System.out.println(pageBean);
        return pageBean;
    }

    @Override
    public AuthorityRequest getRequestById(long id) throws DataAccessException {
        return authorityMapper.getRequestById(id);
    }

    @Override
    public PageVO<AuthorityRequest> getSentRequests(GeneralRequestDTO authorityRequestDTO) throws DataAccessException {
        int currentPage = authorityRequestDTO.getCurrentPage();
        int pageSize = authorityRequestDTO.getPageSize();
        int userId = authorityRequestDTO.getUserId();

        int begin = (currentPage - 1) * pageSize;

        List<AuthorityRequest> rows =
                authorityRequestMapper.getSentRequestsByPage(userId, begin, pageSize);

        //6. 查询总记录数
        Long totalCount = authorityRequestMapper.selectTotalCount();

        //7. 封装PageBean对象
        PageVO<AuthorityRequest> pageBean = new PageVO<>();
        pageBean.setRows(rows);
        pageBean.setTotalCount(totalCount);
        return pageBean;
    }

    @Transactional
    @Override
    public Object acceptRequest(int id) throws DataAccessException {
        if (getRequestStatus(id) != -1) {
            throw new OperationInvalidException("Request already processed.");
        }
        authorityRequestMapper.setRequestStatus(id, 1);
        AuthorityRequest request = authorityRequestMapper.getAuthorityRequestById(id);
        Authority existing = authorityMapper.getAuthorityByUserAndAdmin(
                (long) request.getUserId(), (long) request.getAdminId());

        if (existing != null) {
            // OR 逻辑：新权限累加到现有权限上
            authorityMapper.updateUserAuthority(
                    request.getUserId(), 1,
                    existing.getPermCreate() | request.getPermCreate(),
                    existing.getPermRead() | request.getPermRead(),
                    existing.getPermUpdate() | request.getPermUpdate(),
                    existing.getPermDelete() | request.getPermDelete()
            );
        } else {
            Authority authority = Authority.builder()
                    .userId(request.getUserId())
                    .adminId(request.getAdminId())
                    .status((byte) 1)
                    .permCreate(request.getPermCreate())
                    .permRead(request.getPermRead())
                    .permUpdate(request.getPermUpdate())
                    .permDelete(request.getPermDelete())
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .updatedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            authorityMapper.addAuthority(authority);
        }
        authorityRequestMapper.updateAuthorityRequestProcessedTime(
                id, new Timestamp(System.currentTimeMillis()));
        return "Request Accepted";
    }

    @Transactional
    @Override
    public Object rejectRequest(int id) throws DataAccessException, OperationInvalidException {
        if (getRequestStatus(id) != -1) {
            throw new OperationInvalidException("Request already processed.");
        }
        authorityRequestMapper.setRequestStatus(id, 0);
        authorityRequestMapper.updateAuthorityRequestProcessedTime(
                id, new Timestamp(System.currentTimeMillis()));
        return "Request Rejected";
    }

    @Override
    public PageResult<Authority> getAuthorizedUserByPage(String username, int pageSize, int currentPage) throws DataAccessException {
        PageResult<Authority> pageBean = new PageResult<>();
        pageBean.setTotal(authorityMapper.getAuthoritiesCount(username));

        int begin = (currentPage - 1) * pageSize;

        List<Authority> rows =
                authorityMapper.getAuthoritiesByPage(username, begin, pageSize);

        pageBean.setRecords(rows);
        return pageBean;
    }

    @Override
    public int isAuthorizedUser(long id) throws DataAccessException {
        return authorityMapper.getIfUserAuthorized(id, infoService.getOperateUser());
    }

    @Override
    public Object setStatus(int status, long id) throws DataAccessException {
        authorityMapper.setStatus(id, status);
        return "操作成功";
    }

    @Transactional
    @Override
    public Object updateUserAuthority(long userId, int status, Integer permCreate, Integer permRead, Integer permUpdate, Integer permDelete) throws DataAccessException {
        authorityMapper.updateUserAuthority(userId, status, permCreate, permRead, permUpdate, permDelete);
        return "操作成功";
    }

    @Override
    public Authority getMyPermissions(long userId) throws DataAccessException {
        List<Authority> list = authorityMapper.getActiveAuthoritiesByUserId(userId);
        if (list.isEmpty()) {
            return null;
        }
        // OR 聚合所有授权记录的权限字段
        Authority result = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            Authority a = list.get(i);
            result.setPermCreate(result.getPermCreate() | a.getPermCreate());
            result.setPermRead(result.getPermRead() | a.getPermRead());
            result.setPermUpdate(result.getPermUpdate() | a.getPermUpdate());
            result.setPermDelete(result.getPermDelete() | a.getPermDelete());
        }
        return result;
    }

    private byte getRequestStatus(int id) {
        return authorityRequestMapper.getRequestStatus(id);
    }
}

