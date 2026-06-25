package com.mingbo.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Dynamic {
    private Long id;
    private Long userId;
    private String content;
    private String images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
