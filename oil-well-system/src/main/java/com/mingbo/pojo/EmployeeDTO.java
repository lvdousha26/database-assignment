package com.mingbo.pojo;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EmployeeDTO {
    private Integer currentPage;
    private Integer pageSize;
}
