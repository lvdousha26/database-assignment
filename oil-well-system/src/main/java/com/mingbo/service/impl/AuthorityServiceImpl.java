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
        Authority authority = new Authority(
                request.getUserId(),
                request.getAdminId(),
                (byte) 1,
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()));
        authorityMapper.addAuthority(authority);
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
    public PageResult<User> getAuthorizedUserByPage(String username, int pageSize, int currentPage) throws DataAccessException {
        PageResult<User> pageBean = new PageResult<>();
        pageBean.setTotal(authorityMapper.getAuthoritiesCount(username));

        int begin = (currentPage - 1) * pageSize;

        List<Authority> rows =
                authorityMapper.getAuthoritiesByPage(username, begin, pageSize);

        List<User> users = new ArrayList<>();
        for (Authority authority : rows) {
            User user = userMapper.selectById(authority.getUserId());
            users.add(user);
        }
        pageBean.setRecords(users);
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

    private byte getRequestStatus(int id) {
        return authorityRequestMapper.getRequestStatus(id);
    }
}

