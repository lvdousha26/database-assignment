package com.mingbo.pojo;

import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Message {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String message;
    private Integer checked;
    private Timestamp sentTime;
    private String senderName;
    private String receiverName;
}
