package com.mingbo.controller;

import com.mingbo.exception.AuthorityRoleErrorException;
import com.mingbo.exception.OperationInvalidException;
import com.mingbo.pojo.Authority;
import com.mingbo.pojo.AuthorityRequest;
import com.mingbo.pojo.GeneralRequestDTO;
import com.mingbo.pojo.PageResult;
import com.mingbo.pojo.Result;
import com.mingbo.pojo.User;
import com.mingbo.service.AuthorityService;
import com.mingbo.service.InfoService;
import com.mingbo.service.MessageService;
import com.mingbo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * 权限申请相关表现类
 */
@RestController
@RequestMapping("/authority")
public class AuthorityController {

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Value("${authority.request.message}")
    private String requestMessage;

    @Value("${authority.response.accept}")
    private String acceptMessage;
    @Autowired
    private InfoService infoService;

    /**
     * 添加申请
     * @param authorityRequest 申请数据，包括申请人userId，申请管理员对象adminId和相关说明requestMessage
     * @return Result消息，如果操作成功则返回，若出现错误则返回错误信息
     */
    @PostMapping
    public Result addRequest (AuthorityRequest authorityRequest) {
        try {
            int authorizedUser = authorityService.isAuthorizedUser(authorityRequest.getAdminId());
            if (authorizedUser != 0) {
                throw new OperationInvalidException("您已经获得访问权限，无需再次申请");
            }
            User userPublicInfo = userService.getUserPublicInfo(
                    (long) authorityRequest.getUserId());

            String message = requestMessage.replaceFirst(
                    "%USER%", userPublicInfo.getUsername());
            message = message.replaceFirst(
                    "%ID%", String.valueOf(authorityRequest.getUserId()));

            authorityService.addRequest(authorityRequest);
            messageService.sendSystemMessage(authorityRequest.getAdminId(), message);
            return Result.success();
        } catch (DataAccessException |
                 AuthorityRoleErrorException |
                 OperationInvalidException e) {
            //System.out.println(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 按页获取收到的申请记录
     * @param authorityRequestDTO 查询人id，页码和页项数
     * @return 收到的申请记录页
     */
    @GetMapping("/received")
    public Result getReceivedRequest (GeneralRequestDTO authorityRequestDTO) {
        try {
            return Result.success(authorityService.getReceivedRequests(authorityRequestDTO));
        } catch (DataAccessException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 按页获取发出的申请记录
     * @param authorityRequestDTO 查询人id，页码和页项数
     * @return 发送的申请记录页
     */
    @GetMapping("/sent")
    public Result getSentRequest (GeneralRequestDTO authorityRequestDTO) {
        try {
            return Result.success(authorityService.getSentRequests(authorityRequestDTO));
        } catch (DataAccessException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 回复权限申请
     * @param id 申请记录id
     * @param status 申请回复代码，1代表接受，0代表拒绝
     * @return 申请状态修改执行情况
     */
    @PutMapping
    public Result responseToRequest (int id, int status) {
        String message;
        User user = userService.getUserPublicInfo(infoService.getOperateUser());
        AuthorityRequest authorityRequest = authorityService.getRequestById(id);
        try {
            switch (status) {
                case 1:
                    message = acceptMessage.replaceFirst("%USER%", user.getUsername());
                    message = message.replaceFirst("%ID%", String.valueOf(user.getId()));
                    messageService.sendSystemMessage(authorityRequest.getUserId(), message);
                    return Result.success(authorityService.acceptRequest(id));
                case 0:
                    return Result.success(authorityService.rejectRequest(id));
                default:
                    throw new IllegalArgumentException("Invalid status for response to request");
            }
        } catch (DataAccessException | IllegalArgumentException |OperationInvalidException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询已授权用户
     */
    @GetMapping("/users")
    public Result getAuthorizedUsers(
            @RequestParam(defaultValue = "") String username,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int currentPage) {
        try {
            PageResult<?> result = authorityService.getAuthorizedUserByPage(username, pageSize, currentPage);
            return Result.success(result);
        } catch (DataAccessException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户授权（收回/修改权限）
     */
    @PutMapping("/user/{userId}")
    public Result updateUserAuthority(
            @PathVariable long userId,
            @RequestParam(defaultValue = "1") int status,
            @RequestParam(required = false) Integer permCreate,
            @RequestParam(required = false) Integer permRead,
            @RequestParam(required = false) Integer permUpdate,
            @RequestParam(required = false) Integer permDelete) {
        try {
            return Result.success(authorityService.updateUserAuthority(userId, status, permCreate, permRead, permUpdate, permDelete));
        } catch (DataAccessException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户的权限状态
     */
    @GetMapping("/my")
    public Result getMyPermissions() {
        try {
            Authority auth = authorityService.getMyPermissions(infoService.getOperateUser());
            return Result.success(auth);
        } catch (DataAccessException e) {
            return Result.error(e.getMessage());
        }
    }
}
