package com.mingbo.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CostDetail {
    private Long id;
    private Long operationId;
    private Long categoryId;
    private String itemName;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private LocalDate costDate;
    private String payee;
    private String notes;
    private LocalDateTime createdAt;

    // 关联字段（非数据库直接字段）
    private String operationName;
    private String categoryName;
}
