package com.oilwell.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CostCategory {
    private Long id;
    private String categoryName;
    private Long parentId;
    private String description;
    private LocalDateTime createdAt;

    // 父类别名称（非数据库字段）
    private String parentName;
}
