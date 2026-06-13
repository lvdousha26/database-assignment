package com.mingbo.pojo;

import lombok.Data;

@Data
public class AdminInfo {
    private String id;
    private String username; // 直接使用username而不是name
    private String avatar;
}
