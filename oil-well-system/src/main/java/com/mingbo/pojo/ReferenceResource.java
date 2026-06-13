package com.mingbo.pojo;

import lombok.*;

import java.sql.Timestamp;

/**
 * 文件元数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ReferenceResource {
    private int id;
    private int adminId;
    private String originalName;
    private String storagePath;
    private String resourceType;
    private String description;
    private Timestamp uploadTime;
    private byte status;
}
