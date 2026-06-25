package com.mingbo.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Operation {
    private Long id;
    private Long wellId;
    private Long operationTypeId;
    private String operationName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String teamName;
    private String teamLeader;
    private Integer teamMembers;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联字段（非数据库直接字段）
    private String wellName;
    private String wellType;
    private String operationTypeName;
}
