package com.mingbo.pojo;

import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Message {
    private long senderId;
    private long receiverId;
    private String message;
    private Timestamp sentTime;
}
