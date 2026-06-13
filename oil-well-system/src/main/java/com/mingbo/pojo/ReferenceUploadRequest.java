package com.mingbo.pojo;

import lombok.*;

/**
 * 上传文件附带数据描述
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ReferenceUploadRequest {
    private int id;
    private String resourceType;
    private String description;
}
