package com.mingbo.service;

import com.mingbo.exception.OperationInvalidException;
import com.mingbo.pojo.Authority;
import com.mingbo.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    @Autowired
    private InfoService infoService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorityService authorityService;

    public void requireCreate() {
        check("create");
    }

    public void requireRead() {
        check("read");
    }

    public void requireUpdate() {
        check("update");
    }

    public void requireDelete() {
        check("delete");
    }

    public void requireAdmin() {
        long userId = infoService.getOperateUser();
        User user = userService.getById(userId);
        if (user == null || !"管理员".equals(user.getRole())) {
            throw new OperationInvalidException("仅管理员可执行此操作");
        }
    }

    private void check(String perm) {
        long userId = infoService.getOperateUser();
        User user = userService.getById(userId);
        if (user != null && "管理员".equals(user.getRole())) {
            return;
        }
        Authority auth = authorityService.getMyPermissions(userId);
        if (auth == null) {
            throw new OperationInvalidException("无操作权限，请先申请权限");
        }
        boolean ok;
        switch (perm) {
            case "create":
                ok = auth.getPermCreate() == 1;
                break;
            case "read":
                ok = auth.getPermRead() == 1;
                break;
            case "update":
                ok = auth.getPermUpdate() == 1;
                break;
            case "delete":
                ok = auth.getPermDelete() == 1;
                break;
            default:
                ok = false;
        }
        if (!ok) {
            throw new OperationInvalidException("无此操作权限，请联系管理员授权");
        }
    }
}
