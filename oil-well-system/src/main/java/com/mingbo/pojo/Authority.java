package com.mingbo.pojo;

import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Authority {
    private long userId;
    private long adminId;
    private byte status;
    private int permCreate;
    private int permRead;
    private int permUpdate;
    private int permDelete;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    // 用户信息（联表查询）
    private String username;
    private String avatar;
}
