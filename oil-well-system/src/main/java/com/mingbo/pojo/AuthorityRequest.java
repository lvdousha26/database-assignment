package com.mingbo.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 权限申请相关，业务层与持久层信息DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorityRequest {

    //消息id
    private int id;

    //申请人id
    private int userId;

    //申请人名字
    private String username;

    //申请对象管理员id
    private int adminId;

    //申请对象管理员名字
    private String adminName;

    //申请事由
    private String requestMessage;

    //申请消息状态
    private byte status;

    //申请权限：增删改查
    private int permCreate;
    private int permRead;
    private int permUpdate;
    private int permDelete;

    //申请信息创建时间
    private Timestamp createdAt;

    //申请答复时间
    private Timestamp processedAt;
}
