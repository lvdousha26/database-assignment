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
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
