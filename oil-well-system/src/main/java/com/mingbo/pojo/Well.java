package com.oilwell.pojo;

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
public class Well {
    private Long id;
    private String wellName;
    private String wellType;
    private String wellStatus;
    private String fieldName;
    private String layer;
    private BigDecimal depth;
    private String operator;
    private LocalDate drillingDate;
    private String address;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
