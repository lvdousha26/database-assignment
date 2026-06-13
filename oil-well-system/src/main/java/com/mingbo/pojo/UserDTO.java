package com.mingbo.pojo;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserDTO {
    //员工用户名
    private String username;

    private Integer currentPage;
    private Integer pageSize;
}
