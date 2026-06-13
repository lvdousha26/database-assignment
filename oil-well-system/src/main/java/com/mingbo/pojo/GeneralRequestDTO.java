package com.mingbo.pojo;

import lombok.*;

/**
 * 按页查询申请记录信息类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GeneralRequestDTO {
    private int currentPage;
    private int pageSize;
    private int userId;
}
